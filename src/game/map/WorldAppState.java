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
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 *
 * @author Andreas
 */
public class WorldAppState extends AbstractAppState {
    
    private Node rootNode;
    private AssetManager assetManager;
    private Camera cam;
    private BulletAppState bulletAppState;
    
    private TerrainQuad terrain;
    private Material matTerrain;
    private RigidBodyControl landscapeControl;
    
    private Texture heightmapText = null;
    private Texture alphamapText = null;
    private Texture[] alphaTextures = new Texture[3];
    
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
    
    public void initTest() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        addLight(ambientLight);
        
        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        setSky(sky);
        
        Texture alphaMap = assetManager.loadTexture("Textures/test/testalphamap.png");
        Texture heightMap = assetManager.loadTexture("Textures/test/testheightmap.png");
        loadTerrain(alphaMap, heightMap, -100.0f);
        
        Texture grass = assetManager.loadTexture("Textures/Tile/Gras.jpg");
        setTexture(0,grass,1.0f);
        
        Texture dirt = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
        setTexture(1,dirt,1.0f);
        
        Texture rock = assetManager.loadTexture("Textures/Tile/Road.jpg");
        setTexture(2,rock,1.0f);
    }
    
    public void addLight(Light light) {
        rootNode.addLight(light);
    }
    
    public void setSky(Spatial sky) {
        sky.setName("skybox");
        int index = rootNode.getChildIndex(rootNode.getChild("skybox"));
        if(index != -1) {
            rootNode.attachChildAt(sky, index);
        } else {
            rootNode.attachChild(sky);
        }
    }
    
    public void setTexture(int index, Texture texture, float scale) {
        if(index < 0 || index >= 3) {
            throw new RuntimeException("Index darf nur zwischen 0 oder 2 sein!");
        }
        String txtName = "Tex" + (index+1);
        texture.setWrap(Texture.WrapMode.Repeat);
        alphaTextures[index] = texture;
        matTerrain.setTexture("Tex" + (index+1), texture);
        matTerrain.setFloat(txtName + "Scale", scale);
    }
    
    public void loadTerrain(Texture alphamapText, Texture heightmapText, float yMoved) {
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
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        terrain.setCullHint(Spatial.CullHint.Never);
        terrain.setLocalTranslation(0, yMoved, 0);
        
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        
        terrain.setMaterial(matTerrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 0.5f, 2f);
        rootNode.attachChild(terrain);
        
        if(bulletAppState != null) {
            landscapeControl = new RigidBodyControl(0.0f);
            terrain.addControl(landscapeControl);
            bulletAppState.getPhysicsSpace().add(terrain);
        }
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
    }
    
}
