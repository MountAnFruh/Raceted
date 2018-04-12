/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import game.entities.CarAppState;
import game.map.WorldAppState;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GLContext;

/**
 *
 * @author Robbo13
 */
public class TestTerrain extends SimpleApplication implements ActionListener {
    
    // define triggers
    private static final Trigger CREATE_MOUNTAIN = new KeyTrigger(KeyInput.KEY_SPACE);
    private static final Trigger SWITCH_MAP_0 = new KeyTrigger(KeyInput.KEY_0);
    private static final Trigger SWITCH_MAP_1 = new KeyTrigger(KeyInput.KEY_1);
    private static final Trigger SWITCH_MAP_2 = new KeyTrigger(KeyInput.KEY_2);
    private static final Trigger SWITCH_MAP_3 = new KeyTrigger(KeyInput.KEY_3);
    
    // define mappings
    private static final String MAPPING_CREATE_MOUNTAIN = "Mountain_Create";
    private static final String MAPPING_SWITCH_MAP_0 = "Switch_Map_0";
    private static final String MAPPING_SWITCH_MAP_1 = "Switch_Map_1";
    private static final String MAPPING_SWITCH_MAP_2 = "Switch_Map_2";
    private static final String MAPPING_SWITCH_MAP_3 = "Switch_Map_3";
    
    private final float grassScale = 64;
    private final float dirtScale = 16;
    private final float roadScale = 128;

    private BulletAppState bulletAppState;
    private CarAppState carAppState;

    private WorldAppState worldAppState;
    
    private BitmapText informationText;

    public static void main(String[] args) {
        TestTerrain testTerrain = new TestTerrain();
        testTerrain.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        worldAppState = new WorldAppState(bulletAppState);
        stateManager.attach(bulletAppState);
        stateManager.attach(worldAppState);
        //bulletAppState.setDebugEnabled(true);
        
        initHUD();
        initInput();
        
        carAppState = new CarAppState(bulletAppState);
        stateManager.attach(carAppState);
        
        flyCam.setEnabled(false);
    }
    
    private void initHUD() {
        /** Write text on the screen (HUD) */
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        informationText = new BitmapText(guiFont, false);
        informationText.setSize(guiFont.getCharSet().getRenderedSize());
        informationText.setText("Press [1]: Load/Unload Map 1\nPress [2]: Load/Unload Map 2\n"
                + "Press [3]: Load/Unload Map 3\nPress [0]: Unload Map\n"
                + "Press [SPACE]: Create Mountain");
        informationText.setLocalTranslation(0, settings.getHeight() - informationText.getLineHeight(), 0);
        guiNode.attachChild(informationText);
    }
    
    private void initInput() {
        inputManager.addMapping(MAPPING_CREATE_MOUNTAIN, CREATE_MOUNTAIN);
        inputManager.addMapping(MAPPING_SWITCH_MAP_0, SWITCH_MAP_0);
        inputManager.addMapping(MAPPING_SWITCH_MAP_1, SWITCH_MAP_1);
        inputManager.addMapping(MAPPING_SWITCH_MAP_2, SWITCH_MAP_2);
        inputManager.addMapping(MAPPING_SWITCH_MAP_3, SWITCH_MAP_3);
        inputManager.addListener(this, MAPPING_CREATE_MOUNTAIN,
                MAPPING_SWITCH_MAP_1, MAPPING_SWITCH_MAP_2, MAPPING_SWITCH_MAP_0,
                MAPPING_SWITCH_MAP_3);
    }
    
    private void initTerrain() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        worldAppState.addLight(ambientLight);
        
        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        worldAppState.setSky(sky);
        
        Texture alphaMap = assetManager.loadTexture("Textures/Maps/test-maps/testalphamap2.png");
        Texture heightMap = assetManager.loadTexture("Textures/Maps/test-maps/testheightmap2.png");
        worldAppState.loadTerrain("test_terrain",alphaMap, heightMap, Vector3f.ZERO, new Vector3f(2f,0.5f,2f));
        
        carAppState.getControl().setPhysicsLocation(new Vector3f(0,100,0));
        
        Texture grass = assetManager.loadTexture("Textures/Tile/Gras.jpg");
        worldAppState.setTexture("test_terrain",1,grass,grassScale);
        
        Texture dirt = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
        worldAppState.setTexture("test_terrain",2,dirt,dirtScale);
        
        Texture rock = assetManager.loadTexture("Textures/Tile/Road.jpg");
        worldAppState.setTexture("test_terrain",3,rock,roadScale);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(worldAppState.isInitialized() && !worldAppState.isTerrainLoaded()) initTerrain();
        
        if(carAppState != null) {
            BoundingBox boundBox = (BoundingBox) carAppState.getGeometry().getModelBound();

            int x = (int)(carAppState.getControl().getPhysicsLocation().x - boundBox.getXExtent())/2 + 256;
            int z = 256 - (int)(carAppState.getControl().getPhysicsLocation().z)/2;

            int textNumber = 1;
            worldAppState.changeTexture(new Vector3f(x,0,z), textNumber);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        Texture alphaMap, heightMap;
        if(isPressed) {
            switch(name) {
                case MAPPING_CREATE_MOUNTAIN:
                    Geometry geom = carAppState.getGeometry();
                    List<Vector2f> locations = new ArrayList<>();
                    List<Float> heights = new ArrayList<>();
                    BoundingBox boundBox = (BoundingBox) geom.getModelBound();
                    Vector3f location = carAppState.getControl().getPhysicsLocation();
                    float defaultHeightDelta = 20f;
                    for(int x = 0;x < (int)Math.ceil(boundBox.getXExtent());x++) {
                        for(int z = 0;z < (int)Math.ceil(boundBox.getZExtent());z++) {
                            Vector2f locXZ = new Vector2f(location.getX() + x,location.getZ() + z);
                            locations.add(locXZ);
                            heights.add(defaultHeightDelta);
                        }
                    }
                    worldAppState.adjustHeights(locations, heights);

                    carAppState.getControl().setPhysicsLocation(carAppState.getControl().getPhysicsLocation()
                            .add(new Vector3f(0,defaultHeightDelta/2,0)));
                    break;
                case MAPPING_SWITCH_MAP_0:
                    worldAppState.unloadAllTerrains();
                    break;
                case MAPPING_SWITCH_MAP_1:
                    alphaMap = assetManager.loadTexture("Textures/Maps/test-maps/testalphamap1.png");
                    heightMap = assetManager.loadTexture("Textures/Maps/test-maps/testheightmap1.png");
                    if(!worldAppState.isTerrainLoaded("test_terrain")) {
                        loadTerrain("test_terrain",alphaMap, heightMap,
                                Vector3f.ZERO, new Vector3f(2f,0.2f,2f));
                    } else {
                        worldAppState.unloadTerrain("test_terrain");
                    }
                    break;
                case MAPPING_SWITCH_MAP_2:
                    alphaMap = assetManager.loadTexture("Textures/Maps/test-maps/testalphamap2.png");
                    heightMap = assetManager.loadTexture("Textures/Maps/test-maps/testheightmap2.png");

                    if(!worldAppState.isTerrainLoaded("test_terrain_2")) {
                        worldAppState.loadTerrain("test_terrain_2",alphaMap, heightMap,
                            new Vector3f(0,0,1024), new Vector3f(2f,0.5f,2f));

                        Texture dirt = assetManager.loadTexture("Textures/Tile/Sand.jpg");
                        worldAppState.setTexture("test_terrain_2",2,dirt,dirtScale);

                        Texture grass = assetManager.loadTexture("Textures/Tile/Concrete.jpg");
                        worldAppState.setTexture("test_terrain_2",1,grass,grassScale);

                        Texture rock = assetManager.loadTexture("Textures/Tile/Road.jpg");
                        worldAppState.setTexture("test_terrain_2",3,rock,roadScale);
                    } else {
                        worldAppState.unloadTerrain("test_terrain_2");
                    }

                    break;
                case MAPPING_SWITCH_MAP_3:
                    alphaMap = assetManager.loadTexture("Textures/Maps/test-maps/testalphamap3.png");
                    heightMap = assetManager.loadTexture("Textures/Maps/test-maps/testheightmap3.png");
                    if(!worldAppState.isTerrainLoaded("test_terrain")) {
                        loadTerrain("test_terrain",alphaMap, heightMap,
                                Vector3f.ZERO, new Vector3f(2f,0.2f,2f));
                    } else {
                        worldAppState.unloadTerrain("test_terrain");
                    }
                    break;
            }
        }
    }
    
    public void loadTerrain(String terrainName, Texture alphaMap, Texture heightMap,
            Vector3f moved, Vector3f scale) {
        worldAppState.loadTerrain(terrainName,alphaMap, heightMap,
                        moved, scale);
        
        Texture grass = assetManager.loadTexture("Textures/Tile/Gras.jpg");
        worldAppState.setTexture(terrainName,1,grass,grassScale);

        Texture dirt = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
        worldAppState.setTexture(terrainName,2,dirt,dirtScale);

        Texture rock = assetManager.loadTexture("Textures/Tile/Road.jpg");
        worldAppState.setTexture(terrainName,3,rock,roadScale);
    }
    
}
