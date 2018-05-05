/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map;

import beans.MapData;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    private final Map<String,MapData> mapDatas = new HashMap<>();
    
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
        if(!lights.contains(light)) {
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
     * @param mappingText Textur von den Mappings der Map
     * @param moved Verschiebt das Terrain um den Vektor
     * @param scale Vergrößert das Terrain um den Vektor
     */
    public void loadTerrain(String name, Texture alphamapText, Texture heightmapText, @Nullable Texture mappingText, Vector3f moved, Vector3f scale) {
        // First, we load up our textures and the heightmap texture for the terrain

        Material mat = matTerrain.clone();
        
        // ALPHA map (for splat textures)
        alphamapText = alphamapText.clone();
        alphamapText.setImage(ImageUtils.copyImage(alphamapText.getImage()));
        mat.setTexture("Alpha", alphamapText);
        
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);
            heightmap = new ImageBasedHeightMap(ImageUtils.copyImage(heightmapText.getImage()), 1f);
            heightmap.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(mappingText != null) {
            Image mappingImg = mappingText.getImage();
            loadMapping(name, moved, scale, mappingImg);
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
        
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        
        terrain.setMaterial(mat);
        
        unloadTerrain(name);
        
        terrainNode.attachChild(terrain);
        
        terrain.setLocalScale(scale);
        setTranslation(name, moved);
        
        if(bulletAppState != null) {
            landscapeControl = new RigidBodyControl(0.0f);
            terrain.addControl(landscapeControl);
            bulletAppState.getPhysicsSpace().add(terrain);
        }
    }
    
    private void loadMapping(String name, Vector3f moved, Vector3f scale, Image mappingImage) {
        Map<ColorRGBA,List<Vector3f>> boundCheckpoints = null;
        List<Vector3f> boundStart = null;
        for(int i = 0;i < mappingImage.getWidth();i++) {
            for(int j = 0;j < mappingImage.getHeight();j++) {
                ColorRGBA color = new ColorRGBA();
                ImageUtils.manipulatePixel(mappingImage, i, j, color, false);
                Vector2f coordinate = new Vector2f(i,mappingImage.getHeight() - 1 - j);
                if(color.r == 0 && color.g == 0) {
                    // Ist eine Checkpoint-Markierung
                    if(boundCheckpoints == null) {
                        boundCheckpoints = new HashMap<>();
                    }
                    if(boundCheckpoints.get(color) == null) {
                        boundCheckpoints.put(color, new ArrayList<>());
                        boundCheckpoints.get(color).add(new Vector3f(TERRAINSIZE + 1, Float.MAX_VALUE, TERRAINSIZE + 1));
                        boundCheckpoints.get(color).add(new Vector3f(-1,-1,-1));
                    }
                    float minX = boundCheckpoints.get(color).get(0).x;
                    float minZ = boundCheckpoints.get(color).get(0).z;
                    float minY, maxY;
                    float maxX = boundCheckpoints.get(color).get(1).x;
                    float maxZ = boundCheckpoints.get(color).get(1).z;
                    if(coordinate.x < minX) minX = coordinate.x;
                    if(coordinate.y < minZ) minZ = coordinate.y;
                    if(coordinate.x > maxX) maxX = coordinate.x;
                    if(coordinate.y > maxZ) maxZ = coordinate.y;
                    minY = -1; maxY = TERRAINSIZE + 1;
                    boundCheckpoints.get(color).get(0).set(new Vector3f(minX,minY,minZ));
                    boundCheckpoints.get(color).get(1).set(new Vector3f(maxX, maxY, maxZ));
                } else if(color.r == 0 && color.b == 0) {
                    // Ist eine Start-Ziel-Markierung
                    if(boundStart == null) {
                        boundStart = new ArrayList<>();
                        boundStart.add(new Vector3f(TERRAINSIZE + 1, TERRAINSIZE + 1, TERRAINSIZE + 1));
                        boundStart.add(new Vector3f(-1,-1,-1));
                    }
                    float minX = boundStart.get(0).x;
                    float minZ = boundStart.get(0).z;
                    float minY,maxY;
                    float maxX = boundStart.get(1).x;
                    float maxZ = boundStart.get(1).z;
                    minY = -1; maxY = TERRAINSIZE + 1;
                    if(coordinate.x < minX) minX = coordinate.x;
                    if(coordinate.y < minZ) minZ = coordinate.y;
                    if(coordinate.x > maxX) maxX = coordinate.x;
                    if(coordinate.y > maxZ) maxZ = coordinate.y;
                    boundStart.get(0).set(new Vector3f(minX,minY,minZ));
                    boundStart.get(1).set(new Vector3f(maxX, maxY, maxZ));
                } else {
                    // TODO: Andere Markierungen hinzufügen
                }
            }
        }
        MapData mapData = new MapData();
        if(boundCheckpoints != null) {
            mapData.setCheckpoints(new ArrayList<>());
            ArrayList<ColorRGBA> colorsList = new ArrayList<>();
            colorsList.addAll(boundCheckpoints.keySet());
            colorsList.sort(Comparator.comparing(ColorRGBA::getBlue));
            for(ColorRGBA color : colorsList) {
                Vector3f min = boundCheckpoints.get(color).get(0);
                Vector3f max = boundCheckpoints.get(color).get(1);
                min = min.mult(scale).add(moved);
                max = max.mult(scale).add(moved);
                BoundingBox boundBox = new BoundingBox(min,max);
                mapData.getCheckpoints().add(boundBox);
            }
        }
        if(boundStart != null) {
            Vector3f min = boundStart.get(0);
            Vector3f max = boundStart.get(1);
            min = min.mult(scale).add(moved);
            max = max.mult(scale).add(moved);
            BoundingBox boundBox = new BoundingBox(min,max);
            mapData.setStart(boundBox);
            System.out.println("START: " + boundBox.getMin(null) + " - " + boundBox.getMax(null));
        }
        System.out.println("Mapping loaded!");
        mapDatas.put(name, mapData);
    }
    
    private boolean insideBoundingBox(Vector3f location, BoundingBox boundBox) {
        float locX = location.getX();
        float locY = location.getY();
        float locZ = location.getZ();
        float minX = boundBox.getMin(null).getX();
        float minY = boundBox.getMin(null).getY();
        float minZ = boundBox.getMin(null).getZ();
        float maxX = boundBox.getMax(null).getX();
        float maxY = boundBox.getMax(null).getY();
        float maxZ = boundBox.getMax(null).getZ();
        if(locX >= minX && locX <= maxX &&
           locY >= minY && locY <= maxY &&
           locZ >= minZ && locZ <= maxZ) {
            return true;
        }
        return false;
    }
    
    /**
     * Liefert einen String im Format <code>[Terrain-Name];[Checkpoint-Index/Start]</code>
     * wobei bei 0 der Vektor im Start-Feld ist und bei >=1 der Vektor in
     * einem Checkpoint sich befindet. Wenn der Vektor in keinem Checkpoint/Start ist,
     * dann wird <code>null</code> zurückgeliefert.
     * @param location Position relativ zu (0,0,0)
     * @return [Terrain-Name];[Checkpoint-Index/Start]
     */
    public String insideCheckpointOrStart(Vector3f location) {
        for(String name : mapDatas.keySet()) {
            MapData mapData = mapDatas.get(name);
            for(int i = 0;i < mapData.getCheckpoints().size();i++) {
                BoundingBox boundBox = mapData.getCheckpoints().get(i);
                if(insideBoundingBox(location, boundBox)) {
                    return name + ";" + (i + 1); 
                }
            }
            BoundingBox boundStart = mapData.getStart();
            if(insideBoundingBox(location, boundStart)) {
                return name + ";0"; 
            }
        }
        return null;
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
            Vector3f diff = getCoordsRelativeToTerrain(location, terrain).divide(terrain.getLocalScale());
            
            if(diff.getX() >= 0 && diff.getX() <= TERRAINSIZE) {
                if(diff.getZ() >= 0 && diff.getZ() <= TERRAINSIZE) {
                    Material mat = terrain.getMaterial(location);
                    Texture texture = (Texture) mat.getTextureParam("Alpha").getTextureValue();
                    Image image = texture.getImage();
                    
                    ImageUtils.manipulatePixel(image, Math.round(diff.getX()), terrain.getTerrainSize() - Math.round(diff.getZ()), color, true);
                }
            }
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
            changeTexture(terrain.getName(), location, color);
        }
    }
    
    private Vector3f getCoordsRelativeToTerrain(Vector3f location, TerrainQuad terrain) {
        return location.subtract(getTranslation(terrain.getName()));
    }
    
    private void refreshPhysicsControl(String name) {
        Spatial spatial = terrainNode.getChild(name);
        if(spatial != null) {
            bulletAppState.getPhysicsSpace().remove(spatial);
            spatial.removeControl(landscapeControl);
            landscapeControl = new RigidBodyControl(0.0f);
            spatial.addControl(landscapeControl);
            bulletAppState.getPhysicsSpace().add(spatial);
        }
    }
    
    /**
     * Ändert die Höhe von bestimmten Positionen um einen Delta-Wert
     * @param x1
     * @param x2
     * @param z1
     * @param z2
     * @param delta Delta-Werte von den Positionen
     */
    public void adjustHeights(float x1, float x2, float z1, float z2, float delta) {
        // TODO: Fix adjust Heights
//        for(Spatial spatial : new ArrayList<>(terrainNode.getChildren())) {
//            TerrainQuad terrain = (TerrainQuad) spatial;
//            Vector3f scale = terrain.getLocalScale();
//            List<Vector2f> realXZs = new ArrayList<>();
//            List<Float> deltas = new ArrayList<>();
//            for(float x = x1; x <= x2;x += scale.getX()) {
//                for(float z = z1; z <= z2;z += scale.getZ()) {
//                    Vector3f diff = getCoordsRelativeToTerrain(new Vector3f(x,0,z),terrain);
//                    diff = diff.subtract(new Vector3f(TERRAINSIZE, 0, TERRAINSIZE).mult(terrain.getLocalScale()).divide(2));
//                    realXZs.add(new Vector2f(diff.getX(), diff.getZ()));
//                    deltas.add(delta * scale.getY());
//                }
//            }
//            System.out.println("name: " + spatial.getName());
//            System.out.println("xzs:  " + Arrays.toString(realXZs.toArray()));
//            System.out.println("del:  " + Arrays.toString(deltas.toArray()));
//            terrain.adjustHeight(realXZs, deltas);
//            refreshPhysicsControl(spatial.getName());
//        }
        throw new NotImplementedException();
    }
    
    /**
     * Ändert die Position von einem Terrain
     * @param name Name vom Terrain
     * @param translation Die lokale Position
     */
    public void setTranslation(String name, Vector3f translation) {
        Spatial terrain = terrainNode.getChild(name);
        if(terrain != null) {
            terrain.setLocalTranslation(translation.add(new Vector3f(TERRAINSIZE, 0, TERRAINSIZE).mult(terrain.getLocalScale()).divide(2)));
        }
    }
    
    /**
     * Holt die jetzige Position von einem Terrain
     * @param name Name vom Terrain
     * @return Die lokale Position
     */
    public Vector3f getTranslation(String name) {
        Spatial terrain = terrainNode.getChild(name);
        if(terrain != null) {
            return terrain.getLocalTranslation().subtract(new Vector3f(TERRAINSIZE, 0, TERRAINSIZE).mult(terrain.getLocalScale()).divide(2));
        }
        return null;
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

    /**
     * @return Gibt die Terrain-Node zurück
     */
    public Node getTerrainNode() {
        return terrainNode;
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        unloadAllTerrains();
        
        setSky(null);
        
        for(Light light : new ArrayList<>(lights)) {
            removeLight(light);
        }
    }
    
}
