/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;

/**
 *
 * @author Robbo13
 */
public class TestRock extends SimpleApplication implements AnalogListener, ActionListener {

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
    
    private BulletAppState bulletAppState;
    private Geometry sphereGeo;
    private ChaseCamera chaseCam;
    private RigidBodyControl rockControl;
    private Spatial terrain;

    private float jumpCooldown = 0;

    public static void main(String[] args) {
        TestRock testRock = new TestRock();
        testRock.start(JmeContext.Type.Display);
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        setupKeys();

        initLight();
        initSky();
        initTerrain();

        buildPlayer();

        initCamera();
    }

    private void initCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, sphereGeo, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
    }

    private void setupKeys() {
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_RIGHT);
        inputManager.addMapping(MAPPING_UP, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_SPACE, TRIGGER_SPACE);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_RESET);
        inputManager.addListener(this, MAPPING_LEFT, MAPPING_RIGHT, MAPPING_UP
                , MAPPING_DOWN, MAPPING_SPACE, MAPPING_RESET);
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

    private void initTerrain() {
        terrain = assetManager.loadModel("Scenes/Terrain.j3o");
        terrain.setLocalTranslation(0, -5, 0);
        RigidBodyControl landscapeControl = new RigidBodyControl(0.0f);
        terrain.addControl(landscapeControl);
        rootNode.attachChild(terrain);
        bulletAppState.getPhysicsSpace().add(landscapeControl);
    }

    private void buildPlayer() {
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
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
        rootNode.attachChild(sphereGeo);

        bulletAppState.getPhysicsSpace().add(rockControl);
        rockControl.setGravity(new Vector3f(0,-30.0f,0));
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (jumpCooldown > 0) {
            jumpCooldown -= tpf;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(MAPPING_RESET)) {
            if (isPressed) {
                rockControl.setPhysicsLocation(Vector3f.ZERO);
                rockControl.setPhysicsRotation(new Matrix3f());
                rockControl.setLinearVelocity(Vector3f.ZERO);
                rockControl.setAngularVelocity(Vector3f.ZERO);
            }
        }
    }

    @Override
    public void onAnalog(String name, float pressed, float tpf) {
        float divisor = 50;
        switch (name) {
            case MAPPING_LEFT: {
                Vector3f speedVector = cam.getLeft().normalize();
                rockControl.applyImpulse(speedVector.divide(divisor).setY(0), new Vector3f(0, 2, 0)); ///////////LEFT
                break;
            }
            case MAPPING_RIGHT: {
                Vector3f speedVector = cam.getLeft().negate().normalize();
                rockControl.applyImpulse(speedVector.divide(divisor).setY(0), new Vector3f(0, 2, 0)); ///////////RIGHT
                break;
            }
            case MAPPING_UP: {
                Vector3f speedVector = cam.getDirection().normalize();
                rockControl.applyImpulse(speedVector.divide(divisor).setY(0), new Vector3f(0, 2, 0)); ///////////UP
                break;
            }
            case MAPPING_DOWN: {
                Vector3f speedVector = cam.getDirection().negate().normalize();
                rockControl.applyImpulse(speedVector.divide(divisor).setY(0), new Vector3f(0, 2, 0)); ///////////DOWN
                break;
            }
            default:
                break;
        }
        boolean onGround = terrain.collideWith(sphereGeo.getWorldBound(), new CollisionResults()) != 0;
        if (name.equals(MAPPING_SPACE)) {
            if (onGround && jumpCooldown <= 0) {
                rockControl.applyImpulse(rockControl.getGravity().negate().divide(2), Vector3f.ZERO);
                jumpCooldown = 1.0f;
            }
        }
    }
}
