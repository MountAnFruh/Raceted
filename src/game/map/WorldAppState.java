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
    
    public static final String TERRAINNODENAME = "terrain";
    public static final String SKYBOXNAME = TERRAINNODENAME + "_skybox";
    public static final int TERRAINSIZE = 512;
    
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
    
    /**
     * Fügt Licht zum Terrain hinzu.
     * @param light Licht-Objekt zum Hinzufügen
     */
    public void addLight(Light light) {
        if(lights.contains(light)) {
            lights.add(light);
            rootNode.addLight(light);
        }
    }
    
    /**
     * Entfernt das Licht vom Terrain
     * @param light Licht-Objekt zum Entfernen
     */
    public void removeLight(Light light) {
        if(lights.contains(light)) {
            lights.remove(light);
            rootNode.removeLight(light);
        }
    }
    
    /**
     * Setzt die Skybox vom Terrain
     * @param sky Skybox-Modell oder <code>null</code> um die jetzige Skybox zu entfernen
     */
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
    
    /**
     * Setzt die Textur für ein bestimmtes Terrain (Terrain muss vorher geladen worden sein!)
     * @param terrainName Name vom Terrain
     * @param textNumber Nummer von der Textur (1-3 für RGB)
     * @param texture Textur-Objekt
     * @param scale Ein Skalierwert (Größer = Rausgezoomt)
     */
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
    
    /**
     * Lädt das Terrain mit bestimmten Daten
     * @param name Name vom Terrain
     * @param alphamapText Textur von der Alphamap
     * @param heightmapText Textur von der Höhenmap
     * @param moved Verschiebt das Terrain um den Vektor
     * @param scale Vergrößert das Terrain um den Vektor
     */
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
        TerrainQuad terrain = new TerrainQuad(name, 65, TERRAINSIZE + 1, heightmap.getHeightMap());
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
            throw new RuntimeException("Texture-Number darf nur zwischen 1 und 3"
                    + " sein. maximal 3 Texturen für RGB (siehe alphamap)!");
        }
    }
    
    /**
     * Ändert die Textur von einer bestimmten Position auf dem Terrain
     * @param terrainName Name vom Terrain
     * @param location Position relativ zu (0,0,0)
     * @param textNumber Nummer von der Textur (1-3 für RGB)
     */
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
    
    /**
     * Ändert die Textur von einer bestimmten Position auf dem Terrain
     * @param terrainName Name vom Terrain
     * @param location Position relativ zu (0,0,0)
     * @param color Farbe (RGB)
     */
    public void changeTexture(String terrainName, Vector3f location, ColorRGBA color) {
        TerrainQuad terrain = (TerrainQuad) terrainNode.getChild(terrainName);
        if(terrain != null) {
            Material mat = terrain.getMaterial(location);
            Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
            Image image = texture.getImage();

            ImageUtils.manipulatePixel(image, Math.round(location.getX()), Math.round(location.getZ()), color, true);
        }
    }
    
    /**
     * Ändert die Textur von einer bestimmten Position
     * @param location Position relativ zu (0,0,0)
     * @param textNumber Nummer von der Textur (1-3 für RGB)
     */
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
    
    /**
     * Ändert die Textur von einer bestimmten Position
     * @param location Position relativ zu (0,0,0)
     * @param color Farbe (RGB)
     */
    public void changeTexture(Vector3f location, ColorRGBA color) {
        for(Spatial spatial : terrainNode.getChildren()) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            
            Vector3f diff = getCoordsRelativeToTerrain(location, terrain);
            
            if(diff.getX() >= 0 && diff.getX() <= TERRAINSIZE) {
                if(diff.getZ() >= 0 && diff.getZ() <= TERRAINSIZE) {
                    Material mat = terrain.getMaterial(location);
                    Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
                    Image image = texture.getImage();

                    ImageUtils.manipulatePixel(image, Math.round(diff.getX()), Math.round(diff.getZ()), color, true);
                }
            }
        }
    }
    
    private Vector3f getCoordsRelativeToTerrain(Vector3f location, TerrainQuad terrain) {
        Vector3f terrainTranslation2D = terrain.getLocalTranslation().divide(terrain.getLocalScale());
        Vector3f diff = terrainTranslation2D.add(location);
        return diff;
    }
    
    private void refreshPhysicsControl(String name) {
        Spatial spatial = terrainNode.getChild(name);
        bulletAppState.getPhysicsSpace().remove(spatial);
        spatial.removeControl(landscapeControl);
        landscapeControl = new RigidBodyControl(0.0f);
        spatial.addControl(landscapeControl);
        bulletAppState.getPhysicsSpace().add(spatial);
    }
    
    /**
     * Ändert die Höhe von bestimmten Positionen um einen Delta-Wert
     * @param xzs Positionen, wo die Höhe geändert werden muss
     * @param delta Delta-Werte von den Positionen
     */
    public void adjustHeights(List<Vector2f> xzs, List<Float> delta) {
        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
            for(int i = 0;i < xzs.size();i++) {
                Vector2f xz = (Vector2f) xzs.get(i);
                TerrainQuad terrain = (TerrainQuad) spatial;
                Vector3f diff = getCoordsRelativeToTerrain(new Vector3f(xz.getX(), 0, xz.getY()), terrain);
                System.out.println(diff.getX() + " " + diff.getZ());
                if(diff.getX() >= 0 && diff.getX() <= TERRAINSIZE) {
                    if(diff.getZ() >= 0 && diff.getZ() <= TERRAINSIZE) {
                        refreshPhysicsControl(terrain.getName());
                        terrain.adjustHeight(xz, delta.get(i));
                    }
                }
            }
        }
    }
    
    /**
     * Ändert die Höhe von einer bestimmten Position um einen Delta-Wert
     * @param xz Position, wo die Höhe geändert werden muss
     * @param delta Delta-Wert von der Höhe der Position
     */
    public void adjustHeight(Vector2f xz, float delta) {
        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            refreshPhysicsControl(terrain.getName());
            terrain.adjustHeight(xz, delta);
        }
    }
    
    /**
     * Entfernt alle Terrains
     */
    public void unloadAllTerrains() {
        for(Spatial terrain : new ArrayList<>(terrainNode.getChildren())) {
            unloadTerrain(terrain.getName());
        }
    }
    
    /**
     * Entfernt das Terrain mit einem bestimmten Namen
     * @param name Name vom Terrain
     */
    public void unloadTerrain(String name) {
        Spatial terrain = terrainNode.getChild(name);
        if(terrain != null) {
            terrainNode.detachChild(terrain);
            if(bulletAppState != null) {
                bulletAppState.getPhysicsSpace().remove(terrain);
            }
        }
    }
    
    /**
     * @return Ist irgendein Terrain geladen?
     */
    public boolean isTerrainLoaded() {
        return terrainNode.getChildren().size() > 0;
    }
    
    /**
     * @return Ist das Terrain mit einem bestimmten Namen geladen?
     * @param terrainName Name vom Terrain
     */
    public boolean isTerrainLoaded(String terrainName) {
        for(Spatial spatial : terrainNode.getChildren()) {
            if(spatial.getName().equals(terrainName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Gibt das BulletAppState zurück
     */
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
