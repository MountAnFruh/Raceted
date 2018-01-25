/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;

/**
 *
 * @author Robbo13
 */
public class TestRock extends SimpleApplication implements AnalogListener, ActionListener {

    private BulletAppState bulletAppState;
    private Node rockNode;
    private ChaseCamera chaseCam;
    private RigidBodyControl rockControl;
    private Spatial terrain;

    public static void main(String[] args) {
        TestRock testRock = new TestRock();
        testRock.start(JmeContext.Type.Display);
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.setDebugEnabled(true);

        setupKeys();

        initLight();
        initSky();
        initTerrain();

        buildPlayer();

        initCamera();
    }

    private void initCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, rockNode, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
    }

    private void setupKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
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
        Geometry sphereGeo = new Geometry("Rocksphere", sphere);
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

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
//        System.out.println(rockNode.collideWith(terrain, new CollisionResults()));
        if (name.equals("Space")) {
            float y = rockNode.getLocalTranslation().y;
            if (isPressed && rockNode.getLocalTranslation().y > -2.2 && rockNode.getLocalTranslation().y < -1.7) {
                rockControl.applyImpulse(new Vector3f(0f, 10f, 0f), Vector3f.ZERO);
            }
        } else if (name.equals("Reset")) {
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
        if (name.equals("Left")) {
            Vector3f speedVector = cam.getLeft().normalize();
            rockControl.applyImpulse(speedVector.divide(50).setY(0), new Vector3f(0, 2, 0));
        } else if (name.equals("Right")) {
            Vector3f speedVector = cam.getLeft().negate().normalize();
            rockControl.applyImpulse(speedVector.divide(50).setY(0), new Vector3f(0, 2, 0));
        } else if (name.equals("Up")) {
            Vector3f speedVector = cam.getDirection().normalize();
            rockControl.applyImpulse(speedVector.divide(50).setY(0), new Vector3f(0, 2, 0));
        } else if (name.equals("Down")) {
            Vector3f speedVector = cam.getDirection().negate().normalize();
            rockControl.applyImpulse(speedVector.divide(50).setY(0), new Vector3f(0, 2, 0));
        }
    }
}
