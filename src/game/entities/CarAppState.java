/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Robbo13
 */
public class CarAppState extends AbstractAppState implements ActionListener {
    
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
    
    private FlyByCamera deathCam;
    private ChaseCamera chaseCam;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera cam;
    
    private boolean jump;
    private boolean onGround;
    private VehicleControl carControl;
    private Node vehicleNode;
    private float steeringValue;
    private float accelerationValue;
    
    private float jumpCooldown = 0;

    public CarAppState(BulletAppState bulletAppState, Node rootNode, Spatial terrain) {
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
        
        initInput();
        
        initPlayer();
        
        initCamera();
    }
    
    private void initCamera() {
//        camNode = new CameraNode("CameraNode", cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        vehicleNode.attachChild(camNode);
//        camNode.setLocalTranslation(0, 5, -15);

        //Work in Progresser als af
        chaseCam = new ChaseCamera(cam, vehicleNode, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setMaxVerticalRotation(FastMath.PI / 16);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 16);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
        chaseCam.setDefaultDistance(7);
        chaseCam.setTrailingSensitivity(10);
        chaseCam.setZoomSensitivity(1000);
    }
    
    private void cleanupCamera() {
        chaseCam.setEnabled(false);
    }
    
    private void initInput() {
        inputManager.addMapping(MAPPING_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(MAPPING_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(MAPPING_UP, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(MAPPING_DOWN, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(MAPPING_SPACE, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(MAPPING_RESET, new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, MAPPING_LEFT, MAPPING_RIGHT,
                MAPPING_UP, MAPPING_DOWN, MAPPING_SPACE, MAPPING_RESET);
    }
    
    private void cleanupInput() {
        inputManager.removeListener(this);
    }
    
    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }
    
    private void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);
        
//        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
//        BoxCollisionShape collBox = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));
//        compoundShape.addChildShape(collBox, new Vector3f(0, 1, 0));
//        
//        Box box = new Box(1.2f, 0.5f, 2.4f);
//        Geometry boxGeo = new Geometry("base", box);
//        boxGeo.setLocalTranslation(new Vector3f(0, 1, 0));
//        boxGeo.setMaterial(mat);
//        
//        vehicleNode = new Node("vehicleNode");
//        vehicleNode.attachChild(boxGeo);
        
        vehicleNode = new Node("vehicleNode");
        Geometry chassis = (Geometry) assetManager.loadModel("Models/Car.obj");
        vehicleNode.attachChild(chassis);
        vehicleNode.setShadowMode(RenderQueue.ShadowMode.Cast);
        BoundingBox box = (BoundingBox) chassis.getModelBound();

        //Create a hull collision shape for the chassis
        CollisionShape vehicleHull = CollisionShapeFactory.createDynamicMeshShape(chassis);
        

        //Create a vehicle control
        carControl = new VehicleControl(vehicleHull, 200);
        
        
        //Hier sollte man noch'n paar neue Zahlen draufschreiben, hmmm...
        carControl.setSuspensionCompression(0.1f  * 2.0f * FastMath.sqrt(200.0f));
        carControl.setSuspensionDamping(0.2f  * 2.0f * FastMath.sqrt(200.0f));
        carControl.setSuspensionStiffness(200.0f);
        carControl.setMaxSuspensionForce(5000);
        
        vehicleNode.addControl(carControl);
        
        //Create four wheels and add them at their locations
        float radius = 0.35f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 1.3f;
        
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);

        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        carControl.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        carControl.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        carControl.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        carControl.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);

        rootNode.attachChild(vehicleNode);
        
        bulletAppState.getPhysicsSpace().add(carControl);
    }
    
    private void cleanupPlayer() {
        bulletAppState.getPhysicsSpace().remove(carControl);
        rootNode.detachChild(vehicleNode);
    }
    
    @Override
    public void update(float tpf) {
        //onGround = terrain.collideWith(vehicleNode.getWorldBound(), new CollisionResults()) != 0;
        if (jump) {
            if (/*onGround &&*/ jumpCooldown <= 0) {
                carControl.applyImpulse(carControl.getGravity().negate().divide(2), Vector3f.ZERO);
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
        cleanupInput();
        cleanupCamera();
        cleanupPlayer();
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch(name) {
            case MAPPING_SPACE: jump = isPressed; break;
            case MAPPING_LEFT:
                if(isPressed) {
                    steeringValue += 0.5f;
                } else {
                    steeringValue -= 0.5f;
                }
                break;
            case MAPPING_RIGHT:
                if(isPressed) {
                    steeringValue -= 0.5f;
                } else {
                    steeringValue += 0.5f;
                }
                break;
            case MAPPING_UP:
                if (isPressed) {
                    accelerationValue += 800;
                } else {
                    accelerationValue -= 800;
                }
                break;
            case MAPPING_DOWN:
                if (isPressed) {
                    accelerationValue -= 800;
                } else {
                    accelerationValue += 800;
                }
                break;
            case MAPPING_RESET:
                if (isPressed) {
                    carControl.setPhysicsLocation(Vector3f.ZERO);
                    carControl.setPhysicsRotation(new Matrix3f());
                    carControl.setLinearVelocity(Vector3f.ZERO);
                    carControl.setAngularVelocity(Vector3f.ZERO);
                    carControl.resetSuspension();
                }
                break;
            default: break;
        }
        carControl.steer(steeringValue);
        carControl.accelerate(accelerationValue);
    }
    
}
