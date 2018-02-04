/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.entities;

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
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Robbo13
 */
public class RockAppState extends AbstractAppState implements ActionListener {
    
    // define triggers
    private static final Trigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_A);
    private static final Trigger TRIGGER_UP = new KeyTrigger(KeyInput.KEY_W);
    private static final Trigger TRIGGER_DOWN = new KeyTrigger(KeyInput.KEY_S);
    private static final Trigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_D);
    private static final Trigger TRIGGER_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    private static final Trigger TRIGGER_RESET = new KeyTrigger(KeyInput.KEY_RETURN);
    
    // define mappings
    private static final String MAPPING_LEFT = "Left";
    private static final String MAPPING_UP = "Up";
    private static final String MAPPING_DOWN = "Down";
    private static final String MAPPING_RIGHT = "Right";
    private static final String MAPPING_SPACE = "Space";
    private static final String MAPPING_RESET = "Reset";
    
    private static final float DEFAULT_JUMP_COOLDOWN = 1.0f;
    
    private final BulletAppState bulletAppState;
    private final Node rootNode;
    private final Spatial terrain;
    
    private ChaseCamera chaseCam;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera cam;
    
    private boolean forward, left, backward, right, jump;
    private boolean onGround;
    
    private RigidBodyControl rockControl;
    private Geometry sphereGeo;

    private float jumpCooldown = 0;

    public RockAppState(BulletAppState bulletAppState, Node rootNode, Spatial terrain) {
        this.bulletAppState = bulletAppState;
        this.rootNode = rootNode;
        this.terrain = terrain;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.inputManager = app.getInputManager();
        this.assetManager = app.getAssetManager();
        this.cam = app.getCamera();
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached

        initInput();

        initPlayer();

        initCamera();
    }

    private void initInput() {
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_RIGHT);
        inputManager.addMapping(MAPPING_UP, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_SPACE, TRIGGER_SPACE);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_RESET);
        inputManager.addListener(this, MAPPING_LEFT, MAPPING_RIGHT, MAPPING_UP
                , MAPPING_DOWN, MAPPING_SPACE, MAPPING_RESET);
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
        rockControl.setGravity(new Vector3f(0,-30.0f,0));
    }
    
    private void initCamera() {
        chaseCam = new ChaseCamera(cam, sphereGeo, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
    }
    
    @Override
    public void update(float tpf) {
        onGround = terrain.collideWith(sphereGeo.getWorldBound(), new CollisionResults()) != 0;
        float divisor = 150;
        Vector3f speedVector = rockControl.getLinearVelocity();
        Vector3f lookVector = new Vector3f(0,0,0);
        if(left) {
            lookVector.addLocal(cam.getLeft());                 /////////// LEFT  
        }
        if(right) {
            lookVector.addLocal(cam.getLeft().negate());        /////////// RIGHT
        }
        if(forward) {
            lookVector.addLocal(cam.getDirection());            /////////// UP
        }
        if(backward) {
            lookVector.addLocal(cam.getDirection().negate());   /////////// DOWN 
        }
        lookVector = lookVector.normalize();
        rockControl.applyImpulse(lookVector.divide(divisor).setY(0), new Vector3f(0, 2, 0));
        if(jump) {
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
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case MAPPING_LEFT: left = isPressed; break;
            case MAPPING_RIGHT: right = isPressed; break;
            case MAPPING_UP: forward = isPressed; break;
            case MAPPING_DOWN: backward = isPressed; break;
            case MAPPING_SPACE: jump = isPressed; break;
            default: break;
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
    
}
