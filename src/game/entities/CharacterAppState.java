/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.gui.GUIAppState;
import game.main.appstates.GameAppState;
import beans.DMGArt;
import beans.PlayerInfo;
import com.jme3.scene.Spatial;
import game.main.appstates.TrapPlaceAppState;
import game.utils.AudioPlayer;
import java.util.List;
import sonst.Explosion;

/**
 *
 * @author Andreas Fruhwirt
 */
public abstract class CharacterAppState extends AbstractAppState implements ActionListener {
    
    protected static final Vector3f GRAVITY = new Vector3f(0, -20, 0);
    
    protected static final float DEFAULT_JUMP_COOLDOWN = 1.0f;
    
    // define triggers
    protected static final Trigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_A);
    protected static final Trigger TRIGGER_UP = new KeyTrigger(KeyInput.KEY_W);
    protected static final Trigger TRIGGER_DOWN = new KeyTrigger(KeyInput.KEY_S);
    protected static final Trigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_D);
    protected static final Trigger TRIGGER_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    protected static final Trigger TRIGGER_RESET = new KeyTrigger(KeyInput.KEY_RETURN);

    // define mappings
    protected static final String MAPPING_LEFT = "Left";
    protected static final String MAPPING_UP = "Up";
    protected static final String MAPPING_DOWN = "Down";
    protected static final String MAPPING_RIGHT = "Right";
    protected static final String MAPPING_SPACE = "Space";
    protected static final String MAPPING_RESET = "Reset";
    
    protected final GUIAppState guiAppState;
    protected final GameAppState gameAppState;
    protected final PlayerInfo playerInfo;
    protected final AudioPlayer audioPlayer;
    
    protected BulletAppState bulletAppState;
    protected AssetManager assetManager;
    protected RenderManager renderManager;
    protected InputManager inputManager;
    protected Node rootNode;
    protected FlyByCamera flyCam;
    protected Camera cam;
    protected Node guiNode;
    protected Node terrainNode;
    protected Geometry geometry;
    
    protected Explosion explosion;
    protected ChaseCamera chaseCam;
    
    protected boolean forward, left, backward, right, jump;
    protected boolean onGround, dead;
    protected int maxHP, hp;
    protected float jumpCooldown = 0;
    protected Vector3f spawnPoint;
    protected Quaternion spawnRotation;
    protected long timeDriven = 0;
    protected long timeDied = 0;
    protected float timeExplosionPlayed = 0;
    
    public CharacterAppState(GUIAppState guiAppState, GameAppState gameAppState,
            BulletAppState bulletAppState, int maxHP, Vector3f spawnPoint,
            Quaternion spawnRotation, Node terrainNode, PlayerInfo playerInfo,
            AudioPlayer audioPlayer) {
        this.guiAppState = guiAppState;
        this.gameAppState = gameAppState;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.playerInfo = playerInfo;
        this.audioPlayer = audioPlayer;
        setSpawnPoint(new Vector3f(spawnPoint));
        setSpawnRotation(new Quaternion(spawnRotation));
        this.terrainNode = terrainNode;
        this.bulletAppState = bulletAppState;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        SimpleApplication simpleApp = (SimpleApplication) app;
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        bulletAppState.getPhysicsSpace().setGravity(GRAVITY);
        renderManager = simpleApp.getRenderManager();
        assetManager = simpleApp.getAssetManager();
        inputManager = simpleApp.getInputManager();
        flyCam = simpleApp.getFlyByCamera();
        cam = simpleApp.getCamera();
        guiNode = simpleApp.getGuiNode();
        rootNode = simpleApp.getRootNode();
        
        initPlayer();
        
        initCamera();
        
        initInput();
    }
    
    protected void initInput() {
        if (inputManager.hasMapping(SimpleApplication.INPUT_MAPPING_EXIT)) {
            inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        }
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_RIGHT);
        inputManager.addMapping(MAPPING_UP, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_SPACE, TRIGGER_SPACE);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_RESET);
        inputManager.addListener(this, MAPPING_LEFT, MAPPING_RIGHT, MAPPING_UP,
                MAPPING_DOWN, MAPPING_SPACE, MAPPING_RESET);
    }
    
    protected abstract void initPlayer();
    
    protected void initCamera() {
        cam.setLocation(getLocation());
        chaseCam = new ChaseCamera(cam, geometry, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
        chaseCam.setEnabled(true);
    }
    
    @Override
    public void update(float tpf) {
        //TODO: onGround fixen
        // onGround = terrainNode.collideWith(geometry.getWorldBound(), new CollisionResults()) != 0;
        if(audioPlayer != null) {
            if(audioPlayer.isSoundPlaying("explosion")) {
                timeExplosionPlayed += tpf;
                if(timeExplosionPlayed > 2.0f) {
                    audioPlayer.stopSound("explosion");
                }
            }
        }
        if(hp > 0) {
            timeDriven += tpf * 1_000_000_000;
            if (jumpCooldown > 0) {
                jumpCooldown -= tpf;
            }
            if(jumpCooldown < 0) {
                jumpCooldown = 0;
            }
        } else {
            timeDied += tpf * 1_000_000_000;
            if (explosion != null) {
                explosion.updateExplosion(tpf);
            }
            if(timeDied > 2_000_000_000) {
                explosion.stopExplode();
                gameAppState.changeNextPlayerOrMode();
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
        cleanupInput();
        cleanupPlayer();
        cleanupCamera();
    }
    
    protected void cleanupInput() {
        inputManager.removeListener(this);
        inputManager.deleteMapping(MAPPING_LEFT);
        inputManager.deleteMapping(MAPPING_RIGHT);
        inputManager.deleteMapping(MAPPING_UP);
        inputManager.deleteMapping(MAPPING_DOWN);
        inputManager.deleteMapping(MAPPING_SPACE);
        inputManager.deleteMapping(MAPPING_RESET);
    }
    
    protected void cleanupPlayer() {
        rootNode.detachChild(geometry);
    }

    protected void cleanupCamera() {
        chaseCam.setEnabled(false);
        chaseCam.cleanupWithInput(inputManager);
        cam.setLocation(Vector3f.ZERO);
        cam.setRotation(Quaternion.IDENTITY);
    }
    
    public void healHP(int pHP) {
        this.hp += pHP;
        if (this.hp > this.maxHP) {
            this.hp = this.maxHP;
        }
    }
    
    public void causeDmg(int dmg) {
        this.hp -= dmg;
        if (this.hp < 0) {
            this.hp = 0;
            onDeath();
        }
    }
    
    public abstract void causeDmg(double dmg, DMGArt art);
    
    public abstract Vector3f getLocation();
    
    public abstract void setLocation(Vector3f location);
    
    public abstract Quaternion getRotation();
    
    public abstract void setRotation(Quaternion rotation);
    
    public void onDeath() {
        if(!dead) {
            /**
             * Explosion effect. Uses Texture from jme3-test-data library!
             */
    //        ParticleEmitter debrisEffect = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
    //        Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    //        debrisMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/Debris.png"));
    //        debrisEffect.setMaterial(debrisMat);
    //        debrisEffect.setImagesX(3);
    //        debrisEffect.setImagesY(3); // 3x3 texture animation
    //        debrisEffect.setRotateSpeed(4);
    //        debrisEffect.setSelectRandomImage(true);
    //        debrisEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
    //        debrisEffect.setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
    //        debrisEffect.setGravity(0f, 6f, 0f);
    //        debrisEffect.getParticleInfluencer().setVelocityVariation(.60f);
    //        rootNode.attachChild(debrisEffect);
    //        debrisEffect.emitAllParticles();
            explosion = new Explosion(this.getLocation(), assetManager, renderManager, rootNode);
            explosion.explode();
            this.cleanupPlayer();
            dead = true;
            if(audioPlayer != null) {
                timeExplosionPlayed = 0.0f;
                audioPlayer.playSound("explosion", "Sounds/Effects/Explosion.ogg", false, 0.2f);
                // audioPlayer.playSound("raceted", "Sounds/Effects/Raceted.ogg", false, 2f);
            }
            if(playerInfo != null) {
                playerInfo.setDied(true);
            }
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(this.isEnabled()) {
            switch (name) {
                case MAPPING_LEFT:
                    left = isPressed;
                    break;
                case MAPPING_RIGHT:
                    right = isPressed;
                    break;
                case MAPPING_UP:
                    forward = isPressed;
                    break;
                case MAPPING_DOWN:
                    backward = isPressed;
                    break;
                case MAPPING_SPACE:
                    jump = isPressed;
                    break;
                default:
                    break;
            }
        }
    }

    public void setSpawnPoint(Vector3f spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void setSpawnRotation(Quaternion spawnRotation) {
        this.spawnRotation = spawnRotation;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public long getTimeDriven() {
        return timeDriven;
    }

    public boolean isDead() {
        return dead;
    }

    public int getHP() {
        return hp;
    }

    public int getMaxHP() {
        return maxHP;
    }
    
}
