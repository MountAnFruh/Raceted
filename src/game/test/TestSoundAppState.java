/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import game.utils.AudioPlayer;

/**
 *
 * @author Kevin
 */
public class TestSoundAppState extends AbstractAppState implements ActionListener {

    private RenderManager renderManager;

    // define triggers
    private static final Trigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_A);
    private static final Trigger TRIGGER_UP = new KeyTrigger(KeyInput.KEY_W);
    private static final Trigger TRIGGER_DOWN = new KeyTrigger(KeyInput.KEY_S);
    private static final Trigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_D);
    private static final Trigger TRIGGER_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    private static final Trigger TRIGGER_RESET = new KeyTrigger(KeyInput.KEY_RETURN);
    private static final Trigger TRIGGER_DMG = new KeyTrigger(KeyInput.KEY_F);

    // define mappings
    private static final String MAPPING_LEFT = "Left";
    private static final String MAPPING_UP = "Up";
    private static final String MAPPING_DOWN = "Down";
    private static final String MAPPING_RIGHT = "Right";
    private static final String MAPPING_SPACE = "Space";
    private static final String MAPPING_RESET = "Reset";
    private static final String MAPPING_DMG = "F";

    private static final float DEFAULT_JUMP_COOLDOWN = 1.0f;

    private final BulletAppState bulletAppState;
    private final Node rootNode;
    private final Spatial terrain;

    private FlyByCamera deathCam;
    private ChaseCamera chaseCam;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera cam;

    private boolean forward, left, backward, right, jump;
    private boolean onGround;

    private RigidBodyControl rockControl;
    private Geometry sphereGeo;

    private float jumpCooldown = 0;

    private static final float MAX_DMG = 100f;
    private static final float SPIKE_DMG_REDUCE = 3f;
    private float dmg = 0;
    private Explosion expl;

    private AudioPlayer player = new AudioPlayer();
    private boolean rocksound = false;

    public TestSoundAppState(BulletAppState bulletAppState, Node rootNode, Spatial terrain, RenderManager renderManager) {
        this.bulletAppState = bulletAppState;
        this.rootNode = rootNode;
        this.terrain = terrain;
        this.renderManager = renderManager;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.inputManager = app.getInputManager();
        this.assetManager = app.getAssetManager();
        this.cam = app.getCamera();

        initInput();
        initPlayer();
        initCamera();
    }

    private void initInput() {
        inputManager.addMapping(MAPPING_DMG, TRIGGER_DMG);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_RIGHT);
        inputManager.addMapping(MAPPING_UP, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_SPACE, TRIGGER_SPACE);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_RESET);
        inputManager.addListener(this, MAPPING_DMG, MAPPING_LEFT, MAPPING_RIGHT, MAPPING_UP,
                MAPPING_DOWN, MAPPING_SPACE, MAPPING_RESET);
    }

    private void cleanupInput() {
        inputManager.removeListener(this);
    }

    private void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Textures/Tile/Stone.jpg");
        mat.setTexture("ColorMap", texture);

        Sphere sphere = new Sphere(20, 20, 3);
        sphere.setBound(new BoundingSphere());
        sphere.updateBound();
        sphereGeo = new Geometry("Rocksphere", sphere);
        sphereGeo.setMaterial(mat);

        CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(sphereGeo);

        sphereGeo.setLocalTranslation(0, 50, 0);
        rockControl = new RigidBodyControl(collShape);
        sphereGeo.addControl(rockControl);

        bulletAppState.getPhysicsSpace().add(rockControl);
        rootNode.attachChild(sphereGeo);
        rockControl.setGravity(new Vector3f(0, -30.0f, 0));
    }

    private void cleanupPlayer() {
        bulletAppState.getPhysicsSpace().remove(rockControl);
        rootNode.detachChild(sphereGeo);
    }

    private void initCamera() {
        chaseCam = new ChaseCamera(cam, sphereGeo, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));

        deathCam = new FlyByCamera(cam);
        deathCam.setMoveSpeed(0);
        deathCam.setZoomSpeed(0);
        deathCam.setDragToRotate(false);
        deathCam.setRotationSpeed(0);
    }

    private void cleanupCamera() {
        chaseCam.setEnabled(false);
        deathCam.setEnabled(false);
    }

    @Override
    public void update(float tpf) {
        if (expl != null) {
            expl.updateExplotion(tpf);
        }
        onGround = terrain.collideWith(sphereGeo.getWorldBound(), new CollisionResults()) != 0;
        float divisor = 150;
        Vector3f speedVector = rockControl.getLinearVelocity();
        Vector3f lookVector = new Vector3f(0, 0, 0);
        if (left) {
            lookVector.addLocal(cam.getLeft());                 /////////// LEFT  
        }
        if (right) {
            lookVector.addLocal(cam.getLeft().negate());        /////////// RIGHT
        }
        if (forward) {
              if (!rocksound) {
                player.playDaSound(assetManager, "Sounds/Effects/Rock.ogg", true);
                rocksound = true;
            }
            lookVector.addLocal(cam.getDirection());            /////////// UP
        }
        if (backward) {
            if (!rocksound) {
                player.playDaSound(assetManager, "Sounds/Effects/Rock.ogg", true);
                rocksound = true;
            }
            lookVector.addLocal(cam.getDirection().negate());   /////////// DOWN 
        }
        lookVector = lookVector.normalize();
        rockControl.applyImpulse(lookVector.divide(divisor).setY(0), new Vector3f(0, 2, 0));
        if (jump) {
            if (onGround && jumpCooldown <= 0) {
                rockControl.applyImpulse(rockControl.getGravity().negate().divide(2), Vector3f.ZERO);
                jumpCooldown = DEFAULT_JUMP_COOLDOWN;
            }
        }
        if (jumpCooldown > 0) {
            jumpCooldown -= tpf;
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

    public Geometry getGeometry() {
        return sphereGeo;
    }

    public RigidBodyControl getControl() {
        return rockControl;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case MAPPING_DMG:
                if (isPressed) {
                    causeDmg(20, DMGArt.GRUBE);
                }
                break;
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
        if (name.equals(MAPPING_RESET)) {
            if (isPressed) {
                rockControl.setPhysicsLocation(Vector3f.ZERO);
                rockControl.setPhysicsRotation(new Matrix3f());
                rockControl.setLinearVelocity(Vector3f.ZERO);
                rockControl.setAngularVelocity(Vector3f.ZERO);
            }
        }
    }

    public void causeDmg(float dmg, DMGArt art) {
        switch (art) {
            case SPIKE:
                this.dmg += dmg / SPIKE_DMG_REDUCE;
                break;
            default:
                this.dmg += dmg;
                break;
        }
        System.out.println((this.dmg > MAX_DMG) + " - " + this.dmg + "/" + MAX_DMG);
        if (this.dmg > MAX_DMG) {
            System.out.println("destroy");
            destroyStone();
        }
    }

    private void destroyStone() {
        sphereGeo.getLocalTranslation();
        chaseCam.setEnabled(false);
        deathCam.setEnabled(true);
        try {
            rootNode.detachChild(sphereGeo);
        } catch (Exception e) {
        }
        expl = new Explosion(sphereGeo.getWorldTranslation(), assetManager, renderManager, rootNode);
        expl.explode();
        System.out.println("destroy");
        dmg = 0;
        // noch zum hinzufügen
    }
}
