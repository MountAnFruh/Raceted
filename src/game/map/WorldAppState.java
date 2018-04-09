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
import java.util.List;

/**
 *
 * @author Andreas
 */
public class WorldAppState extends AbstractAppState {
    
    public static final String SKYBOXNAME = "skybox";
    public static final String TERRAINNODENAME = "terrain";
    
    private final BulletAppState bulletAppState;
    private final List<Light> lights = new ArrayList<>();
    private final Node terrainNode = new Node(TERRAINNODENAME);
    
    private Node rootNode;
    private AssetManager assetManager;
    private Camera cam;
    
    private Material matTerrain;
    private RigidBodyControl landscapeControl;

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
        
        rootNode.attachChild(terrainNode);
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
    
    public void setTexture(String terrainName, int textNumber, Texture texture, float scale) {
        checkTextureNumber(textNumber);
        TerrainQuad terrain = (TerrainQuad) terrainNode.getChild(terrainName);
        if(terrain != null) {
            String txtName = "Tex" + textNumber;
            texture.setWrap(Texture.WrapMode.Repeat);
            Material material = terrain.getMaterial();
            material.setTexture("Tex" + textNumber, texture);
            material.setFloat(txtName + "Scale", scale);
        }
    }
    
    public void loadTerrain(String name, Texture alphamapText, Texture heightmapText, Vector3f moved, Vector3f scale) {
        // First, we load up our textures and the heightmap texture for the terrain

        Material mat = matTerrain.clone();
        
        // ALPHA map (for splat textures)
        mat.setTexture("Alpha", alphamapText);
        
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
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
        TerrainQuad terrain = new TerrainQuad(name, 65, 513, heightmap.getHeightMap());
        terrain.setCullHint(Spatial.CullHint.Never);
        terrain.setLocalTranslation(moved);
        
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        
        terrain.setMaterial(mat);
        
        terrain.setLocalScale(scale);
        
        unloadTerrain(name);
        
        terrainNode.attachChild(terrain);
        
        if(bulletAppState != null) {
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
    
    public void changeTexture(String terrainName, Vector3f location, int textNumber) {
        checkTextureNumber(textNumber);
        
        ColorRGBA color = ColorRGBA.White;
        switch(textNumber) {
            case 1: color = ColorRGBA.Red; break;
            case 2: color = ColorRGBA.Green; break;
            case 3: color = ColorRGBA.Blue; break;
        }
        
        changeTexture(terrainName, location, color);
    }
    
    public void changeTexture(String terrainName, Vector3f location, ColorRGBA color) {
        TerrainQuad terrain = (TerrainQuad) terrainNode.getChild(terrainName);
        if(terrain != null) {
            if(terrain.getMaterial(location) != null) {
                Material mat = terrain.getMaterial(location);
                Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
                Image image = texture.getImage();

                ImageUtils.manipulatePixel(image, Math.round(location.getX()), Math.round(location.getZ()), color, true);
            }
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
        for(Spatial spatial : terrainNode.getChildren()) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            if(terrain.getMaterial(location) != null) {
                Material mat = terrain.getMaterial(location);
                Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
                Image image = texture.getImage();

                ImageUtils.manipulatePixel(image, Math.round(location.getX()), Math.round(location.getZ()), color, true);
            }
        }
    }
    
    private void refreshPhysicsControl(String name) {
        Spatial spatial = terrainNode.getChild(name);
        bulletAppState.getPhysicsSpace().remove(spatial);
        spatial.removeControl(landscapeControl);
        landscapeControl = new RigidBodyControl(0.0f);
        spatial.addControl(landscapeControl);
        bulletAppState.getPhysicsSpace().add(spatial);
    }
    
    public void adjustHeights(List<Vector2f> xz, List<Float> delta) {
        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            refreshPhysicsControl(terrain.getName());
            terrain.adjustHeight(xz, delta);
        }
    }
    
    public void adjustHeight(Vector2f xz, float delta) {
        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            refreshPhysicsControl(terrain.getName());
            terrain.adjustHeight(xz, delta);
        }
    }
    
    public void setHeights(List<Vector2f> xz, List<Float> delta) {
        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            refreshPhysicsControl(terrain.getName());
            terrain.setHeight(xz, delta);
        }
    }
    
    public void setHeight(Vector2f xz, float delta) {
        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            refreshPhysicsControl(terrain.getName());
            terrain.setHeight(xz, delta);
        }
    }
    
    public void unloadAllTerrains() {
        for(Spatial terrain : new ArrayList<>(terrainNode.getChildren())) {
            unloadTerrain(terrain.getName());
        }
    }
    
    public void unloadTerrain(String name) {
        Spatial terrain = terrainNode.getChild(name);
        if(terrain != null) {
            terrainNode.detachChild(terrain);
            if(bulletAppState != null) {
                bulletAppState.getPhysicsSpace().remove(terrain);
            }
        }
    }
    
    public boolean isTerrainLoaded() {
        return terrainNode.getChildren().size() > 0;
    }
    
    public boolean isTerrainLoaded(String terrainName) {
        for(Spatial spatial : terrainNode.getChildren()) {
            if(spatial.getName().equals(terrainName)) {
                return true;
            }
        }
        return false;
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
        unloadAllTerrains();
        
        setSky(null);
        
        for(Light light : new ArrayList<>(lights)) {
            removeLight(light);
        }
    }
    
}
