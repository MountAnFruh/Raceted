/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import game.utils.ImageUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Andreas
 */
public class WorldAppState extends AbstractAppState {
    
    public static final String SKYBOXNAME = "skybox";
    public static final String TERRAINNAME = "terrain";
    
    private final BulletAppState bulletAppState;
    private final List<Light> lights = new ArrayList<>();
    
    private Node rootNode;
    private AssetManager assetManager;
    private Camera cam;
    
    private TerrainQuad terrain;
    private Material matTerrain;
    private RigidBodyControl landscapeControl;
    
    private Texture heightmapText = null;
    private Texture alphamapText = null;
    
    private AbstractHeightMap heightmap = null;

    public WorldAppState() {
        bulletAppState = null;
    }
    
    public WorldAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        SimpleApplication simpleApp = (SimpleApplication) app;
        this.rootNode = simpleApp.getRootNode();
        this.assetManager = simpleApp.getAssetManager();
        this.cam = simpleApp.getCamera();
        
        // TERRAIN TEXTURE material
        matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        matTerrain.setBoolean("useTriPlanarMapping", false);
        
    }
    
    public void addLight(Light light) {
        lights.add(light);
        rootNode.addLight(light);
    }
    
    public void removeLight(Light light) {
        lights.remove(light);
        rootNode.removeLight(light);
    }
    
    public void setSky(Spatial sky) {
        Spatial oldSky = rootNode.getChild(SKYBOXNAME);
        if(oldSky != null) {
            rootNode.detachChild(oldSky);
        }
        
        if(sky != null) {
            sky.setName(SKYBOXNAME);
            rootNode.attachChild(sky);
        }
    }
    
    public void setTexture(int textNumber, Texture texture, float scale) {
        checkTextureNumber(textNumber);
        String txtName = "Tex" + textNumber;
        texture.setWrap(Texture.WrapMode.Repeat);
        matTerrain.setTexture("Tex" + textNumber, texture);
        matTerrain.setFloat(txtName + "Scale", scale);
    }
    
    public void loadTerrain(Texture alphamapText, Texture heightmapText, Vector3f moved, Vector3f scale) {
        // First, we load up our textures and the heightmap texture for the terrain

        // ALPHA map (for splat textures)
        this.alphamapText = alphamapText;
        matTerrain.setTexture("Alpha", alphamapText);

        // HEIGHTMAP image (for the terrain heightmap)
        this.heightmapText = heightmapText;

//        // WIREFRAME material
//        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        matWire.getAdditionalRenderState().setWireframe(true);
//        matWire.setColor("Color", ColorRGBA.Green);

        // CREATE HEIGHTMAP
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);
            heightmap = new ImageBasedHeightMap(heightmapText.getImage(), 1f);
            heightmap.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
         * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
        /**
         * Optimal terrain patch size is 65 (64x64).
         * The total size is up to you. At 1025 it ran fine for me (200+FPS), however at
         * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
         */
        terrain = new TerrainQuad(TERRAINNAME, 65, 513, heightmap.getHeightMap());
        terrain.setCullHint(Spatial.CullHint.Never);
        terrain.setLocalTranslation(moved);
        
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        
        terrain.setMaterial(matTerrain);
        terrain.setLocalScale(scale);
        
        Spatial oldTerrain = rootNode.getChild(TERRAINNAME);
        if(oldTerrain != null) {
            rootNode.detachChild(oldTerrain);
        }
        rootNode.attachChild(terrain);
        
        if(bulletAppState != null) {
            if(oldTerrain != null) {
                bulletAppState.getPhysicsSpace().remove(oldTerrain);
            }
            landscapeControl = new RigidBodyControl(0.0f);
            terrain.addControl(landscapeControl);
            bulletAppState.getPhysicsSpace().add(terrain);
        }
    }
    
    private void checkTextureNumber(int textNumber) throws RuntimeException {
        if(textNumber < 1 || textNumber > 3) {
            throw new RuntimeException("Texture-Number darf nur zwischen 1 und 3 sein (max. 3 Texturen)!");
        }
    }
    
    public void changeTexture(Vector3f location, int textNumber) {
        checkTextureNumber(textNumber);
        
        ColorRGBA color = ColorRGBA.White;
        switch(textNumber) {
            case 1: color = ColorRGBA.Red; break;
            case 2: color = ColorRGBA.Green; break;
            case 3: color = ColorRGBA.Blue; break;
        }
        
        changeTexture(location, color);
    }
    
    public void changeTexture(Vector3f location, ColorRGBA color) {
        Material mat = terrain.getMaterial(location);
        Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
        Image image = texture.getImage();
        
        ImageUtils.manipulatePixel(image, Math.round(location.getX()), Math.round(location.getZ()), color, true);
    }
    
    private void refreshPhysicsControl() {
        bulletAppState.getPhysicsSpace().remove(terrain);
        terrain.removeControl(landscapeControl);
        landscapeControl = new RigidBodyControl(0.0f);
        terrain.addControl(landscapeControl);
        bulletAppState.getPhysicsSpace().add(terrain);
    }
    
    public void adjustHeights(List<Vector2f> xz, List<Float> delta) {
        terrain.adjustHeight(xz, delta);
        refreshPhysicsControl();
    }
    
    public void adjustHeight(Vector2f xz, float delta) {
        terrain.adjustHeight(xz, delta);
        refreshPhysicsControl();
    }
    
    public void setHeights(List<Vector2f> xz, List<Float> delta) {
        terrain.setHeight(xz, delta);
        refreshPhysicsControl();
    }
    
    public void setHeight(Vector2f xz, float delta) {
        terrain.setHeight(xz, delta);
        refreshPhysicsControl();
    }
    
    public void unloadTerrain() {
        Spatial oldTerrain = rootNode.getChild(TERRAINNAME);
        if(oldTerrain != null) {
            rootNode.detachChild(oldTerrain);
            if(bulletAppState != null) {
                bulletAppState.getPhysicsSpace().remove(oldTerrain);
            }
        }
    }
    
    public boolean isTerrainLoaded() {
        return terrain != null;
    }

    public Texture getAlphamapTexture() {
        return alphamapText;
    }

    public Texture getHeightmapTexture() {
        return heightmapText;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }
    
    public TerrainQuad getTerrain() {
        return terrain;
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
        unloadTerrain();
        
        setSky(null);
        
        for(Light light : new ArrayList<>(lights)) {
            removeLight(light);
        }
    }
    
}
