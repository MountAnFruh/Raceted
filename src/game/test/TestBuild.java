/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * 
 * @author Robbo13
 */
public class TestBuild extends SimpleApplication implements AnalogListener, ActionListener {
    
    // define triggers
    private static final Trigger CAMERA_LEFT = new MouseAxisTrigger(MouseInput.AXIS_X, true);
    private static final Trigger CAMERA_RIGHT = new MouseAxisTrigger(MouseInput.AXIS_X, false);
    private static final Trigger CAMERA_UP = new MouseAxisTrigger(MouseInput.AXIS_Y, false);
    private static final Trigger CAMERA_DOWN = new MouseAxisTrigger(MouseInput.AXIS_Y, true);
    private static final Trigger CAMERA_DRAG = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private static final Trigger CAMERA_ZOOMIN = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false);
    private static final Trigger CAMERA_ZOOMOUT = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true);
    
    // define mappings
    private static final String MAPPING_CAMERA_LEFT = "Camera_Left";
    private static final String MAPPING_CAMERA_RIGHT = "Camera_Right";
    private static final String MAPPING_CAMERA_DOWN = "Camera_Down";
    private static final String MAPPING_CAMERA_UP = "Camera_Up";
    private static final String MAPPING_CAMERA_DRAG = "Camera_Drag";
    private static final String MAPPING_CAMERA_ZOOMIN = "Camera_Zoomin";
    private static final String MAPPING_CAMERA_ZOOMOUT = "Camera_Zoomout";
    
    private BulletAppState bulletAppState;
    
    private Node rockNode;
    private RigidBodyControl rockControl;
    
    private CameraNode camNode;
    private boolean isDragging;

    public static void main(String[] args) {
        TestBuild testBuild = new TestBuild();
        testBuild.start();
    }


    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
        
        setupKeys();
        
        initLight();
        initSky();
        initTerrain();
        
        buildPlayer();
        initCamera();
    }
    
    private void initCamera() {
        flyCam.setEnabled(false);
        camNode = new CameraNode("MainCamera", cam);
        camNode.move(0, 100, 0);
        camNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        rootNode.attachChild(camNode);
        inputManager.setCursorVisible(true);
        cam.setLocation(new Vector3f(0,100,0));
    }
    
    private void buildPlayer() {
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Textures/Tile/Stone.jpg");
        mat.setTexture("ColorMap", texture);
        
        Sphere sphere = new Sphere(20,20,3);
        Geometry sphereGeo = new Geometry("Rocksphere",sphere);
        sphereGeo.setMaterial(mat);
        
//        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
//        CapsuleCollisionShape capsule = new CapsuleCollisionShape(3f,0f);
//        compoundShape.addChildShape(capsule, new Vector3f(0, 1, 0));

        CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(sphereGeo);
        
        rockNode = new Node("vehicleNode");
        rockControl = new RigidBodyControl(collShape);
        rockNode.addControl(rockControl);
        rockNode.attachChild(sphereGeo);
        rootNode.attachChild(rockNode);
        
        bulletAppState.getPhysicsSpace().add(rockControl);
    }
    
    private void setupKeys() {
        inputManager.addMapping(MAPPING_CAMERA_LEFT, CAMERA_LEFT);
        inputManager.addMapping(MAPPING_CAMERA_RIGHT, CAMERA_RIGHT);
        inputManager.addMapping(MAPPING_CAMERA_UP, CAMERA_UP);
        inputManager.addMapping(MAPPING_CAMERA_DOWN, CAMERA_DOWN);
        inputManager.addMapping(MAPPING_CAMERA_DRAG, CAMERA_DRAG);
        inputManager.addMapping(MAPPING_CAMERA_ZOOMIN, CAMERA_ZOOMIN);
        inputManager.addMapping(MAPPING_CAMERA_ZOOMOUT, CAMERA_ZOOMOUT);
        
        inputManager.addListener(this, MAPPING_CAMERA_LEFT, MAPPING_CAMERA_RIGHT
                , MAPPING_CAMERA_DOWN, MAPPING_CAMERA_UP, MAPPING_CAMERA_DRAG
                , MAPPING_CAMERA_ZOOMIN, MAPPING_CAMERA_ZOOMOUT);
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
        Spatial terrain = assetManager.loadModel("Scenes/Terrain.j3o");
        terrain.setLocalTranslation(0, -5, 0);
        RigidBodyControl landscapeControl = new RigidBodyControl(0.0f);
        terrain.addControl(landscapeControl);
        rootNode.attachChild(terrain);
        bulletAppState.getPhysicsSpace().add(landscapeControl);
    }
    
    private void moveCamera(float value, boolean sideways){
        float cameraMoveSpeed = 100.0f;
        Vector3f vel = new Vector3f();
        Vector3f pos = camNode.getLocalTranslation().clone();

        if (sideways){
            cam.getLeft(vel).setY(0);
        }else{
            cam.getUp(vel).setY(0);
        }
        vel.multLocal(value * cameraMoveSpeed);

        pos.addLocal(vel);

        camNode.setLocalTranslation(pos.getX(),pos.getY(),pos.getZ());
    }
    
    private void zoomCamera(float value){
        // derive fovY value
        float h = cam.getFrustumTop();
        float w = cam.getFrustumRight();
        float aspect = w / h;

        float near = cam.getFrustumNear();

        float fovY = FastMath.atan(h / near)
                / (FastMath.DEG_TO_RAD * .5f);
        float newFovY = fovY + value * 0.1f * 10.0f;
        if (newFovY > 0f) {
            // Don't let the FOV go zero or negative.
            fovY = newFovY;
        }

        h = FastMath.tan( fovY * FastMath.DEG_TO_RAD * .5f) * near;
        w = h * aspect;

        cam.setFrustumTop(h);
        cam.setFrustumBottom(-h);
        cam.setFrustumLeft(-w);
        cam.setFrustumRight(w);
    }

    @Override
    public void simpleUpdate(float tpf) {
            
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
    
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(MAPPING_CAMERA_DRAG)) {
            if(isPressed) {
                isDragging = true;
                inputManager.setCursorVisible(false);
            } else {
                isDragging = false;
                inputManager.setCursorVisible(true);
            }
        }
    }

    @Override
    public void onAnalog(String name, float pressed, float tpf) {
        if(isDragging) {
            switch(name) {
                case MAPPING_CAMERA_LEFT:
                    moveCamera(pressed, true);
                    break;
                case MAPPING_CAMERA_RIGHT:
                    moveCamera(-pressed, true);
                    break;
                case MAPPING_CAMERA_UP:
                    moveCamera(pressed, false);
                    break;
                case MAPPING_CAMERA_DOWN:
                    moveCamera(-pressed, false);
                    break;
                case MAPPING_CAMERA_ZOOMIN:
                    zoomCamera(-pressed);
                    break;
                case MAPPING_CAMERA_ZOOMOUT:
                    zoomCamera(pressed);
                    break;
            }
        }
    }
}
