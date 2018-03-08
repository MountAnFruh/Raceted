/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import game.entities.RockAppState;
import game.utils.ImageUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robbo13
 */
public class TestTerrain extends SimpleApplication implements ActionListener {
    
    // define triggers
    private static final Trigger CREATE_MOUNTAIN = new KeyTrigger(KeyInput.KEY_SPACE);
    
    // define mappings
    private static final String MAPPING_CREATE_MOUNTAIN = "Mountain_Create";

    private BulletAppState bulletAppState;
    private RockAppState rockAppState;

    private TerrainQuad terrain;
    private RigidBodyControl landscapeControl;
    
    private Material matRock;
    private Material matWire;
    
    private float grassScale = 64;
    private float dirtScale = 16;
    private float rockScale = 128;

    public static void main(String[] args) {
        TestTerrain testTerrain = new TestTerrain();
        testTerrain.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        
        initInput();
        initLight();
        initSky();
        initTerrain();
        
        rockAppState = new RockAppState(bulletAppState, rootNode, terrain, this.getRenderManager());
        stateManager.attach(rockAppState);
        
        flyCam.setMoveSpeed(1000.0f);
    }

    private void initLight() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        rootNode.addLight(ambientLight);
    }

    private void initSky() {
        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        rootNode.attachChild(sky);
    }
    
    private void initInput() {
        inputManager.addMapping(MAPPING_CREATE_MOUNTAIN, CREATE_MOUNTAIN);
        inputManager.addListener(this, MAPPING_CREATE_MOUNTAIN);
    }

    private void initTerrain() {
        // First, we load up our textures and the heightmap texture for the terrain

        // TERRAIN TEXTURE material
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);

        // ALPHA map (for splat textures)
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/test/testalphamap.png"));

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/test/testheightmap.png");

        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/Tile/Gras.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", grassScale);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex2", dirt);
        matRock.setFloat("Tex2Scale", dirtScale);

        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/Tile/Road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex3", rock);
        matRock.setFloat("Tex3Scale", rockScale);

        // WIREFRAME material
        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.getAdditionalRenderState().setWireframe(true);
        matWire.setColor("Color", ColorRGBA.Green);

        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
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
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        terrain.setCullHint(CullHint.Never);
        terrain.setLocalTranslation(0, -100f, 0);
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        terrain.setMaterial(matRock);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 0.5f, 2f);
        rootNode.attachChild(terrain);
        landscapeControl = new RigidBodyControl(0.0f);
        terrain.addControl(landscapeControl);
        bulletAppState.getPhysicsSpace().add(terrain);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Material mat = terrain.getMaterial(rockAppState.getControl().getPhysicsLocation());
        Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
        Image image = texture.getImage();
        
        BoundingSphere boundSphere = (BoundingSphere)rockAppState.getGeometry().getModelBound();
        
        int x = (int)(rockAppState.getControl().getPhysicsLocation().x - boundSphere.getRadius())/2 + 256;
        int z = 256 - (int)(rockAppState.getControl().getPhysicsLocation().z)/2;
        
        System.out.println("Manipulated Pixel at " + x + " " + z);
        
        ImageUtils.manipulatePixel(image, x, z, ColorRGBA.Red, true);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch(name) {
            case MAPPING_CREATE_MOUNTAIN:
                if(isPressed) {
                    Geometry geom = rockAppState.getGeometry();
                    List<Vector2f> locations = new ArrayList<>();
                    List<Float> heights = new ArrayList<>();
                    BoundingSphere boundSphere = (BoundingSphere)geom.getModelBound();
                    Vector3f location = geom.getLocalTranslation();
                    float defaultHeightDelta = 20f;
                    for(int x = (int)Math.floor(-boundSphere.getRadius());x < (int)Math.ceil(boundSphere.getRadius());x++) {
                        for(int z = (int)Math.floor(-boundSphere.getRadius());z < (int)Math.ceil(boundSphere.getRadius());z++) {
                            Vector2f locXZ = new Vector2f(location.getX() + x,location.getZ() + z);
                            locations.add(locXZ);
                            heights.add(defaultHeightDelta);
                        }
                    }
                    System.out.println("Terrain raised!");
                    terrain.adjustHeight(locations, heights);
                    rockAppState.getControl().setPhysicsLocation(rockAppState.getControl().getPhysicsLocation()
                            .add(new Vector3f(0,defaultHeightDelta/2,0)));
                    
                    
                    bulletAppState.getPhysicsSpace().remove(terrain);
                    terrain.removeControl(landscapeControl);
                    landscapeControl = new RigidBodyControl(0.0f);
                    terrain.addControl(landscapeControl);
                    bulletAppState.getPhysicsSpace().add(terrain);
                }
                break;
        }
    }
    
}
