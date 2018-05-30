/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import game.entities.CarAppState;
import game.entities.CharacterAppState;
import game.entities.RockAppState;
import game.gui.GUIAppState;
import game.utils.AudioPlayer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Robbo13
 */
public class GameAppState extends AbstractAppState implements ActionListener {
    
    private static final Trigger TRIGGER_MODE = new KeyTrigger(KeyInput.KEY_T);
    protected static final Trigger TRIGGER_ESC = new KeyTrigger(KeyInput.KEY_ESCAPE);
    
    private static final String MAPPING_MODE = "Change_Mode";
    private static final String MAPPING_ESC = "ESC_Menu";
    
    private static final int INITHP = 200;
    
    private final Character character;
    private final Level level;
    private final GUIAppState guiAppState;
    
    private long startNanos;
    
    private BulletAppState bulletAppState;
    private WorldAppState worldAppState;
    private AudioPlayer audioPlayer;
    private CharacterAppState characterAppState;
    private TrapPlaceAppState trapPlaceAppState;
    private AssetManager assetManager;
    private InputManager inputManager;
    
    private AppStateManager stateManager;
    
    private Quaternion spawnRotation;
    private Vector3f spawnPoint;
    
    private int currCheckpoint = 0;
    private int currRound = 1;
    
    private boolean inputEnabled = true;
    private boolean terrainInitialized = false;
    private boolean started = false;
    private Mode currMode = null;
    
    public GameAppState(GUIAppState guiAppState, Character character, Level level) {
        this.guiAppState = guiAppState;
        this.worldAppState = new WorldAppState(bulletAppState);
        this.character = character;
        this.level = level;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        SimpleApplication simpleApp = (SimpleApplication) app;
        simpleApp.getFlyByCamera().setEnabled(false);
        this.assetManager = simpleApp.getAssetManager();
        this.inputManager = simpleApp.getInputManager();
        this.stateManager = stateManager;
        
        this.bulletAppState = new BulletAppState();
        this.worldAppState = new WorldAppState(bulletAppState);
        //this.trapPlaceAppState = new TrapPlaceAppState(bulletAppState, worldAppState);
        this.audioPlayer = new AudioPlayer(assetManager);
        
        initInput();
        
        stateManager.attach(bulletAppState);
        stateManager.attach(worldAppState);
    }
    
    protected void initInput() {
        inputManager.addMapping(MAPPING_MODE, TRIGGER_MODE);
        inputManager.addMapping(MAPPING_ESC, TRIGGER_ESC);
        inputManager.addListener(this, MAPPING_MODE, MAPPING_ESC);
    }
    
    private void loadLevel() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        worldAppState.addLight(ambientLight);
        
        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        worldAppState.setSky(sky);
        
        Texture alphaMap, heightMap, mappingMap;
        Texture[] textures = new Texture[3];
        float[] scales = { 64f, 64f, 64f };
        spawnRotation = new Quaternion();
        switch(level) {
            case LEVEL1:
                alphaMap = assetManager.loadTexture("Textures/Maps/firstalphamap.png");
                heightMap = assetManager.loadTexture("Textures/Maps/firstheightmap.png");
                mappingMap = assetManager.loadTexture("Textures/Maps/firstmap.png");
                textures[0] = assetManager.loadTexture("Textures/Tile/Road.jpg");
                textures[1] = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
                textures[2] = assetManager.loadTexture("Textures/Tile/Gras.jpg");
                spawnRotation.fromAngles(0, (float) Math.toRadians(180), 0);
                break;
            default:
                alphaMap = null; heightMap = null; mappingMap = null;
                break;
        }
        String name = level.name();
        worldAppState.loadTerrain(name, alphaMap, heightMap, mappingMap, Vector3f.ZERO, new Vector3f(1.2f,0.1f,1.2f));
        for(int i = 1;i <= 3;i++) {
            worldAppState.setTexture(level.name(),i,textures[i-1],scales[i-1]);
        }
        BoundingBox startBox = worldAppState.getStart(name);
        spawnPoint = new Vector3f(startBox.getCenter());
        Vector2f spawnPoint2D = new Vector2f(spawnPoint.x, spawnPoint.z);
        float height = worldAppState.getHeight(spawnPoint2D);
        spawnPoint.setY(height);
        
        changeMode(Mode.DRIVEMODE);
    }
    
    @Override
    public void update(float tpf) {
        // Initialisiere Terrain wenn alles bereit ist.
        if(worldAppState.isInitialized() && !terrainInitialized) {
            loadLevel();
            terrainInitialized = true;
        }
        if(currMode == Mode.DRIVEMODE) {
            if(worldAppState.isInitialized() && characterAppState.isInitialized()) {
                if(!started) {
                    startNanos = LocalTime.now().toNanoOfDay();
                    started = true;
                }
                String value = worldAppState.insideCheckpointOrStart(characterAppState.getLocation());
                if(value != null) {
                    String[] parts = value.split(";");
                    String mapName = parts[0];
                    int checkpoint = Integer.parseInt(parts[1]);
                    List<BoundingBox> checkpoints = worldAppState.getCheckpoints(mapName);
                    int checkpointCount = checkpoints.size() + 1;
                    if(mapName.equals(level.name())) {
                        int nextCheckpoint = (currCheckpoint + 1) % checkpointCount;
                        if(checkpoint == nextCheckpoint) {
                            currCheckpoint = nextCheckpoint;
                            BoundingBox checkpointbb;
                            if(currCheckpoint == 0) {
                                currRound++;
                                System.out.println("*** NEW ROUND: " + currRound);
                                checkpointbb = worldAppState.getStart(mapName);
                            } else {
                                checkpointbb = checkpoints.get(currCheckpoint - 1);
                            }
                            long checkpointNanos = LocalTime.now().toNanoOfDay() - startNanos;
                            LocalTime chTime = LocalTime.ofNanoOfDay(checkpointNanos);
                            System.out.println("TIME: " + chTime.format(DateTimeFormatter.ISO_TIME));

                            Vector3f spawnPoint = new Vector3f(checkpointbb.getCenter());
                            Vector2f spawnPoint2D = new Vector2f(spawnPoint.x, spawnPoint.z);
                            float height = worldAppState.getHeight(spawnPoint2D);
                            spawnPoint.setY(height);
                            characterAppState.setSpawnPoint(spawnPoint);
                            characterAppState.setSpawnRotation(characterAppState.getRotation());

                        }
                    }
                }
            }
        }
    }
    
    private void changeMode(Mode mode) {
        System.out.println("Activated " + mode);
        currMode = mode;
        switch(mode) {
            case TRAPMODE:
                guiAppState.goToScreen(GUIAppState.TRAP_PLACE_HUD);
                stateManager.detach(trapPlaceAppState);
                Vector3f position = characterAppState.getLocation();
                trapPlaceAppState = new TrapPlaceAppState(bulletAppState, worldAppState, new Vector2f(position.x, position.z));
                stateManager.attach(trapPlaceAppState);
                stateManager.detach(characterAppState);
                started = false;
                break;
            case DRIVEMODE:
                guiAppState.goToScreen(GUIAppState.GAME_HUD);
                stateManager.detach(characterAppState);
                switch(character) {
                    case ROCK:
                        characterAppState = new RockAppState(bulletAppState, INITHP, spawnPoint, spawnRotation, worldAppState.getTerrainNode());
                        break;
                    case CAR:
                        characterAppState = new CarAppState(bulletAppState, INITHP, spawnPoint, spawnRotation, worldAppState.getTerrainNode());
                        break;
                }
                stateManager.attach(characterAppState);
                stateManager.detach(trapPlaceAppState);
                break;
        }
    }
    
    public void toggleHUD() {
        String currentHUD = null;
        if(currMode == Mode.TRAPMODE) {
            currentHUD = GUIAppState.TRAP_PLACE_HUD;
        } else if(currMode == Mode.DRIVEMODE) {
            currentHUD = GUIAppState.GAME_HUD;
        }
        if(guiAppState.getCurrentScreenName().equals(GUIAppState.ESC_MENU)) {
            switch(currMode) {
                case TRAPMODE:
                    trapPlaceAppState.setEnabled(true);
                    break;
                case DRIVEMODE:
                    characterAppState.setEnabled(true);
                    break;
            }
            inputEnabled = true;
            bulletAppState.setEnabled(true);
            guiAppState.goToScreen(currentHUD);
        } else if(guiAppState.getCurrentScreenName().equals(currentHUD)) {
            switch(currMode) {
                case TRAPMODE:
                    trapPlaceAppState.setEnabled(false);
                    break;
                case DRIVEMODE:
                    characterAppState.setEnabled(false);
                    break;
            }
            inputEnabled = false;
            bulletAppState.setEnabled(false);
            guiAppState.goToScreen(GUIAppState.ESC_MENU);
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        // Add cleanup
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(inputEnabled) {
            if(name.equals(MAPPING_MODE) && isPressed) {
                if(currMode == Mode.DRIVEMODE) {
                    changeMode(Mode.TRAPMODE);
                } else {
                    changeMode(Mode.DRIVEMODE);
                }
            }
        }
        if(name.equals(MAPPING_ESC) && isPressed) {
            this.toggleHUD();
        }
    }
    
    public enum Mode { TRAPMODE, DRIVEMODE};
    
    public enum Character { ROCK, CAR };
    
    public enum Level { LEVEL1 };
    
}
