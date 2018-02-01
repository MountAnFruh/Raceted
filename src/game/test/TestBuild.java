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
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
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
    private static final Trigger CAMERA_ZOOMIN = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false);
    private static final Trigger CAMERA_ZOOMOUT = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true);
    private static final Trigger PLACE_TRAP = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private static final Trigger CAMERA_DRAG = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    
    // define mappings
    private static final String MAPPING_CAMERA_LEFT = "Camera_Left";
    private static final String MAPPING_CAMERA_RIGHT = "Camera_Right";
    private static final String MAPPING_CAMERA_DOWN = "Camera_Down";
    private static final String MAPPING_CAMERA_UP = "Camera_Up";
    private static final String MAPPING_CAMERA_DRAG = "Camera_Drag";
    private static final String MAPPING_CAMERA_ZOOMIN = "Camera_Zoomin";
    private static final String MAPPING_CAMERA_ZOOMOUT = "Camera_Zoomout";
    private static final String MAPPING_PLACE_TRAP = "Place_Trap";
    
    private BulletAppState bulletAppState;
    
    private Node rockNode;
    private RigidBodyControl rockControl;
    
    private CameraNode camNode;
    private boolean isDragging;
    private Spatial terrain;

    public static void main(String[] args) {
        TestBuild testBuild = new TestBuild();
        testBuild.start();
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
        sphereGeo.setCullHint(CullHint.Never);
        sphereGeo.setMaterial(mat);
        
//        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
//        CapsuleCollisionShape capsule = new CapsuleCollisionShape(3f,0f);
//        compoundShape.addChildShape(capsule, new Vector3f(0, 1, 0));

        CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(sphereGeo);
        
        rockControl = new RigidBodyControl(collShape);
        sphereGeo.addControl(rockControl);
        rootNode.attachChild(sphereGeo);
        
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
        inputManager.addMapping(MAPPING_PLACE_TRAP, PLACE_TRAP);
        
        inputManager.addListener(this, MAPPING_CAMERA_LEFT, MAPPING_CAMERA_RIGHT
                , MAPPING_CAMERA_DOWN, MAPPING_CAMERA_UP, MAPPING_CAMERA_DRAG
                , MAPPING_CAMERA_ZOOMIN, MAPPING_CAMERA_ZOOMOUT, MAPPING_PLACE_TRAP);
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
        terrain.setCullHint(CullHint.Never);
        rootNode.attachChild(terrain);
        bulletAppState.getPhysicsSpace().add(terrain);
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
        Vector3f camPosition = camNode.getLocalTranslation();
        camNode.setLocalTranslation(camPosition.add(cam.getDirection().mult(-value * 10.0f)));
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
        } else if(name.equals(MAPPING_PLACE_TRAP)) {
            if(isPressed) {
                Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
                Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
                direction.subtractLocal(origin).normalizeLocal();

                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();
                terrain.collideWith(ray, results);

                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();

                    Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture texture = assetManager.loadTexture("Textures/Tile/Stone.jpg");
                    mat.setTexture("ColorMap", texture);

                    Sphere sphere = new Sphere(20,20,3);
                    Geometry sphereGeo = new Geometry("Rocksphere",sphere);
                    sphereGeo.setCullHint(CullHint.Never);
                    sphereGeo.setMaterial(mat);
                    sphereGeo.setLocalTranslation(closest.getContactPoint().add(0, 10, 0));
                    
                    CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(sphereGeo);
        
                    rockControl = new RigidBodyControl(collShape);
                    sphereGeo.addControl(rockControl);
                    rootNode.attachChild(sphereGeo);

                    bulletAppState.getPhysicsSpace().add(rockControl);
                }
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
