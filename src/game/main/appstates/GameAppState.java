/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main.appstates;

import beans.PlayerInfo;
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
import com.sun.istack.internal.NotNull;
import game.entities.CarAppState;
import game.entities.CharacterAppState;
import game.entities.RockAppState;
import game.gui.GUIAppState;
import beans.DMGArt;
import com.jme3.collision.CollisionResults;
import com.jme3.input.FlyByCamera;
import game.utils.AudioPlayer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andreas Fruhwirt
 */
public class GameAppState extends AbstractAppState implements ActionListener {

    protected static final Trigger TRIGGER_MODE = new KeyTrigger(KeyInput.KEY_T);
    protected static final Trigger TRIGGER_ESC = new KeyTrigger(KeyInput.KEY_ESCAPE);

    private static final String MAPPING_MODE = "Change_Mode";
    private static final String MAPPING_ESC = "ESC_Menu";

    private static final int INITHP = 10_000;
    private static final int BASICDMG = 100;

    private final Map<PlayerInfo, List<Spatial>> placedTraps = new HashMap<>();
    private final List<PlayerInfo> playerInfos;
    private final Level level;
    private final GUIAppState guiAppState;

    private PlayerInfo currentPlayer;

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

    public GameAppState(GUIAppState guiAppState, AudioPlayer audioPlayer, @NotNull List<PlayerInfo> playerInfos, Level level) {
        this.playerInfos = playerInfos;
        this.audioPlayer = audioPlayer;
        for (PlayerInfo playerInfo : playerInfos) {
            placedTraps.put(playerInfo, new ArrayList<>());
        }
        this.guiAppState = guiAppState;
        this.worldAppState = new WorldAppState(bulletAppState);
        this.level = level;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        SimpleApplication simpleApp = (SimpleApplication) app;
        simpleApp.getFlyByCamera().setEnabled(false);
        this.assetManager = simpleApp.getAssetManager();
        this.inputManager = simpleApp.getInputManager();
        inputManager.setCursorVisible(false);
        
        this.stateManager = stateManager;

        this.bulletAppState = new BulletAppState();
        //bulletAppState.setDebugEnabled(true);
        this.worldAppState = new WorldAppState(bulletAppState);
        //this.trapPlaceAppState = new TrapPlaceAppState(bulletAppState, worldAppState);
        this.audioPlayer = new AudioPlayer(assetManager);

        initInput();

        this.currentPlayer = playerInfos.get(0);
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
        float[] scales = {64f, 64f, 64f};
        spawnRotation = new Quaternion();
        switch (level) {
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
                alphaMap = null;
                heightMap = null;
                mappingMap = null;
                break;
        }
        String name = level.name();
        worldAppState.loadTerrain(name, alphaMap, heightMap, mappingMap, Vector3f.ZERO, new Vector3f(1.2f, 0.1f, 1.2f));
        for (int i = 1; i <= 3; i++) {
            worldAppState.setTexture(level.name(), i, textures[i - 1], scales[i - 1]);
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
        if (worldAppState.isInitialized() && !terrainInitialized) {
            loadLevel();
            terrainInitialized = true;
        }
        if (worldAppState.isInitialized()) {
            guiAppState.getController().setCurrentPlayerNumber(getCurrentPlayerNumber());
            guiAppState.getController().setPointsInGameHUDAndTrapPlaceHUD(currentPlayer.getPoints());
            List<PlayerInfo> pointsRanking = new ArrayList<>(playerInfos);
            Collections.sort(pointsRanking, Comparator.comparing(PlayerInfo::getPoints));
            int pointsIndex = pointsRanking.indexOf(currentPlayer);
//            guiAppState.getController().setPlacePointsInGameHUDAndTrapPlaceHUD(pointsIndex + 1);
            if (currMode == Mode.DRIVEMODE) {
                if (characterAppState.isInitialized()) {
                    if (!started) {
                        currRound = 1;
                        currCheckpoint = 0;
                        started = true;
                    }
                    long checkpointNanos = characterAppState.getTimeDriven();
                    LocalTime currTime = LocalTime.ofNanoOfDay(checkpointNanos);
                    currentPlayer.setDrivenTime(currTime);
                    guiAppState.getController().setHPInGameHUD(characterAppState.getHP(),
                            characterAppState.getMaxHP());
                    guiAppState.getController().setTimeLevelInGameHUD(currTime);
                    guiAppState.getController().setRoundInGameHUD(currRound);
                    List<PlayerInfo> ranking = new ArrayList<>(playerInfos);
                    Collections.sort(ranking, Comparator.comparing(PlayerInfo::isDied).thenComparing(PlayerInfo::getDrivenTime));
                    int index = ranking.indexOf(currentPlayer);
                    guiAppState.getController().setPlaceTimeInGameHUD(index + 1);

                    for (List<Spatial> spatials : placedTraps.values()) {
                        for (Spatial spatial : spatials) {
                            if (characterAppState.getGeometry().collideWith(spatial.getWorldBound(), new CollisionResults()) > 0) {
                                String text = spatial.getUserData(TrapPlaceAppState.DMG_ART_KEY);
                                DMGArt art = DMGArt.valueOf(text);
                                characterAppState.causeDmg(BASICDMG, art);
                            }
                        }
                    }

                    if (worldAppState.outsideLevel(level.name(), characterAppState.getLocation())) {
                        if (!characterAppState.isDead()) {
                            System.out.println("RACETED!"); // TODO: Irgendeinen Text am Screen anzeigen lassen
                            characterAppState.causeDmg(INITHP, DMGArt.OUTSIDELEVEL);
                        }
                        return;
                    }

                    String value = worldAppState.insideCheckpointOrStart(characterAppState.getLocation());
                    if (value != null) {
                        String[] parts = value.split(";");
                        String mapName = parts[0];
                        int checkpoint = Integer.parseInt(parts[1]);
                        List<BoundingBox> checkpoints = worldAppState.getCheckpoints(mapName);
                        int checkpointCount = checkpoints.size() + 1;
                        if (mapName.equals(level.name())) {
                            int nextCheckpoint = (currCheckpoint + 1) % checkpointCount;
                            if (checkpoint == nextCheckpoint) {
                                currCheckpoint = nextCheckpoint;
                                BoundingBox checkpointbb;
                                if (currCheckpoint == 0) {
                                    currRound++;
                                    System.out.println("*** NEW ROUND: " + currRound);
                                    if (currRound >= level.getRoundCount()) {
                                        changeNextPlayerOrMode();
                                        return;
                                    }
                                    checkpointbb = worldAppState.getStart(mapName);
                                } else {
                                    checkpointbb = checkpoints.get(currCheckpoint - 1);
                                }
                                System.out.println("TIME: " + currTime.format(DateTimeFormatter.ISO_TIME));

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
            } else {

            }
        }
    }

    public void changeNextPlayerOrMode() {
        int currPlayerIndex = getCurrentPlayerNumber() - 1;

        // NEXT PLAYER OR FIRST PLAYER AGAIN
        PlayerInfo nextPlayer = playerInfos.get(currPlayerIndex + 1 >= playerInfos.size() ? 0 : currPlayerIndex + 1);
        currentPlayer = nextPlayer;

        if (currPlayerIndex >= playerInfos.size() - 1) {
            // NEXT MODE
            if (currMode == Mode.DRIVEMODE) {
                changeMode(Mode.TRAPMODE);
            } else if(currMode == Mode.TRAPMODE) {
                changeMode(Mode.DRIVEMODE);
            }
        } else {
            changeMode(currMode);
        }
        started = false;
    }

    private void calculatePoints() {
        boolean everyBodyDied = true;
        boolean everyBodyLived = true;
        for (PlayerInfo playerInfo : playerInfos) {
            if (playerInfo.isDied()) {
                everyBodyLived = false;
            } else {
                everyBodyDied = false;
            }
        }
        if (everyBodyDied) {
            return;
        }
        if (everyBodyLived) {
            return;
        }
//        LocalTime minTime = LocalTime.MAX;
//        for(PlayerInfo playerInfo : playerInfos) {
//            if(!playerInfo.isDied()) {
//                if(playerInfo.getDrivenTime().isBefore(minTime)) {
//                    minTime = playerInfo.getDrivenTime();
//                }
//            } else {
//                minTime = LocalTime.of(0, 0, 0, 0);
//            }
//        }
        for (PlayerInfo playerInfo : playerInfos) {
            List<PlayerInfo> ranking = new ArrayList<>(playerInfos);
            Collections.sort(ranking, Comparator.comparing(PlayerInfo::isDied).thenComparing(PlayerInfo::getDrivenTime));
            int placed_time = ranking.indexOf(playerInfo);
            if (!playerInfo.isDied()) {
//                LocalTime time = playerInfo.getDrivenTime();
//                int subWithMin = time.getSecond() - minTime.getSecond();
//                playerInfo.setPoints(playerInfo.getPoints() + placed_time * subWithMin);
                playerInfo.setPoints(playerInfo.getPoints() + (playerInfos.size() - placed_time));
            }
        }
    }

    private void changeMode(Mode mode) {
        System.out.println("Activated " + mode);
        currMode = mode;
        switch (mode) {
            case TRAPMODE:
                guiAppState.goToScreen(GUIAppState.TRAP_PLACE_HUD);
                stateManager.detach(trapPlaceAppState);
                Vector3f position = characterAppState.getLocation();
                trapPlaceAppState = new TrapPlaceAppState(bulletAppState, worldAppState, this, guiAppState, new Vector2f(position.x, position.z));
                stateManager.attach(trapPlaceAppState);
                stateManager.detach(characterAppState);
                started = false;
                break;
            case DRIVEMODE:
                guiAppState.goToScreen(GUIAppState.GAME_HUD);
                stateManager.detach(characterAppState);
                switch (currentPlayer.getCharacter()) {
                    case ROCK:
                        characterAppState = new RockAppState(bulletAppState, this, INITHP, spawnPoint, spawnRotation, worldAppState.getTerrainNode(), currentPlayer);
                        break;
                    case CAR:
                        characterAppState = new CarAppState(bulletAppState, this, INITHP, spawnPoint, spawnRotation, worldAppState.getTerrainNode(), currentPlayer);
                        break;
                }
                stateManager.attach(characterAppState);
                stateManager.detach(trapPlaceAppState);
                break;
        }
    }

    public void setTrap(int id) {
        trapPlaceAppState.setTrap(id);
    }

    public void toggleHUD() {
        String currentHUD = null;
        if (currMode == Mode.TRAPMODE) {
            currentHUD = GUIAppState.TRAP_PLACE_HUD;
        } else if (currMode == Mode.DRIVEMODE) {
            currentHUD = GUIAppState.GAME_HUD;
        }
        if (guiAppState.getCurrentScreenName().equals(GUIAppState.ESC_MENU)) {
            switch (currMode) {
                case TRAPMODE:
                    inputManager.setCursorVisible(true);
                    trapPlaceAppState.setEnabled(true);
                    break;
                case DRIVEMODE:
                    inputManager.setCursorVisible(false);
                    characterAppState.setEnabled(true);
                    break;
            }
            inputEnabled = true;
            bulletAppState.setEnabled(true);
            guiAppState.goToScreen(currentHUD);
        } else if (guiAppState.getCurrentScreenName().equals(currentHUD)) {
            switch (currMode) {
                case TRAPMODE:
                    trapPlaceAppState.setEnabled(false);
                    break;
                case DRIVEMODE:
                    characterAppState.setEnabled(false);
                    break;
            }
            inputEnabled = false;
            bulletAppState.setEnabled(false);
            inputManager.setCursorVisible(true);
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
        if (inputEnabled) {
            if (name.equals(MAPPING_MODE) && isPressed) {
                if (currMode == Mode.DRIVEMODE) {
                    changeMode(Mode.TRAPMODE);
                } else {
                    changeMode(Mode.DRIVEMODE);
                }
            }
        }
        if (name.equals(MAPPING_ESC) && isPressed) {
            this.toggleHUD();
        }
    }

    public PlayerInfo getCurrentPlayer() {
        return currentPlayer;
    }

    public Map<PlayerInfo, List<Spatial>> getPlacedTraps() {
        return placedTraps;
    }

    public Level getLevel() {
        return level;
    }

    public int getCurrentPlayerNumber() {
        for (int i = 1; i <= playerInfos.size(); i++) {
            if (playerInfos.get(i - 1) == currentPlayer) {
                return i;
            }
        }
        return -1;
    }

    public enum Mode {
        TRAPMODE, DRIVEMODE
    };

    public enum Character {
        ROCK, CAR
    };

    public enum Level {
        LEVEL1(1);

        private int roundCount = 1;

        private Level(int roundCount) {
            this.roundCount = roundCount;
        }

        public int getRoundCount() {
            return roundCount;
        }
    };

}
