/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;

/**
 * 
 * @author Robbo13
 */
public class TestBuild extends SimpleApplication implements AnalogListener, ActionListener {
    
    private BulletAppState bulletAppState;
    
    private Node rockNode;
    private RigidBodyControl rockControl;
    
    private CameraNode camNode;

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
        inputManager.addMapping("Drag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
//        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
//        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
//        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
//        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
//        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Drag");
//        inputManager.addListener(this, "Right");
//        inputManager.addListener(this, "Up");
//        inputManager.addListener(this, "Down");
//        inputManager.addListener(this, "Space");
//        inputManager.addListener(this, "Reset");
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

    @Override
    public void simpleUpdate(float tpf) {
//        Vector3f camLook = new Vector3f(cam.getLocation());
//        camLook.setY(0);
//        cam.lookAt(camLook, Vector3f.UNIT_Y);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
    
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
    }

    @Override
    public void onAnalog(String name, float pressed, float tpf) {
        if(name.equals("Drag")) {
            inputManager.setCursorVisible(false);
        } else {
            inputManager.setCursorVisible(true);
        }
    }
}
