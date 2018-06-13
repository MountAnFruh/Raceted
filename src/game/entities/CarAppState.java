/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture;
import game.gui.GUIAppState;
import game.main.appstates.GameAppState;
import beans.DMGArt;
import beans.PlayerInfo;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Spatial;
import game.main.appstates.TrapPlaceAppState;
import java.util.List;

/**
 *
 * @author Florian Rottmann
 */
public class CarAppState extends CharacterAppState {
    
    private static final float MAX_SPEED = 300f;
    private static final float SPIKE_DMG_GAIN = 2f;
    
    private int steer;
    private float wheelRadius;
    private VehicleControl carControl;
    private Node vehicleNode;
    private float steeringValue;
    private float accelerationValue;
    
    public CarAppState(BulletAppState bulletAppState, GameAppState gameAppState,
            int maxHP, Vector3f spawnPoint, Quaternion spawnRotation,
            Node terrainNode, PlayerInfo playerInfo) {
        this(null, gameAppState, bulletAppState, maxHP, spawnPoint, spawnRotation, terrainNode, playerInfo);
    }

    public CarAppState(GUIAppState guiAppState, GameAppState gameAppState,
            BulletAppState bulletAppState, int maxHP, Vector3f spawnPoint,
            Quaternion spawnRotation, Node terrainNode, PlayerInfo playerInfo) {
        super(guiAppState, gameAppState, bulletAppState, maxHP, spawnPoint, spawnRotation, terrainNode, playerInfo);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }
    
    @Override
    public void initCamera() {
//        cam.setLocation(getLocation());
//        cam.setRotation(getRotation());
////        camNode = new CameraNode("CameraNode", cam);
////        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
////        vehicleNode.attachChild(camNode);
////        camNode.setLocalTranslation(0, 5, -15);
//
//        //Work in Progresser als af
//        chaseCam = new ChaseCamera(cam, geometry, inputManager);
//        chaseCam.setInvertVerticalAxis(true);
//        chaseCam.setSmoothMotion(true);
//        chaseCam.setTrailingEnabled(true);
//        chaseCam.setMaxVerticalRotation(FastMath.PI / 16);
//        chaseCam.setDefaultVerticalRotation(FastMath.PI / 16);
//        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
//        chaseCam.setDefaultDistance(7);
//        chaseCam.setTrailingSensitivity(10);
//        chaseCam.setZoomSensitivity(1000);
//        chaseCam.setEnabled(true);
        cam.setLocation(getLocation());
        cam.setRotation(getRotation());
//        camNode = new CameraNode("CameraNode", cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        vehicleNode.attachChild(camNode);
//        camNode.setLocalTranslation(0, 5, -15);

        //Work in Progresser als af
        chaseCam = new ChaseCamera(cam, geometry, inputManager);
        //chaseCam.setSmoothMotion(true);
        //chaseCam.setTrailingEnabled(true);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
        chaseCam.setDefaultDistance(14);
        chaseCam.setEnabled(true);
        chaseCam.setMaxVerticalRotation(FastMath.PI / 16);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 16);
        //chaseCam.setTrailingSensitivity(10);
        chaseCam.setZoomSensitivity(1000);
    }
    
    @Override
    protected void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/chassis.png");
//        mat.getAdditionalRenderState().setWireframe(true);
        mat.setTexture("ColorMap", tex);
        
        vehicleNode = new Node("vehicleNode");
        geometry = (Geometry) assetManager.loadModel("Models/geruestedit1.obj");
        geometry.setCullHint(Geometry.CullHint.Never);
        geometry.setMaterial(mat);
        vehicleNode.attachChild(geometry);
        vehicleNode.setShadowMode(RenderQueue.ShadowMode.Cast);
        
        BoundingBox box = (BoundingBox) geometry.getModelBound();

        //Create a hull collision shape for the chassis
        CollisionShape vehicleHull = CollisionShapeFactory.createDynamicMeshShape(geometry);
        vehicleHull.setScale(new Vector3f(1f, 1f, 0.8f));

        //Create a vehicle control
        carControl = new VehicleControl(vehicleHull, 500);
        
        
        //Hier sollte man noch'n paar neue Zahlen draufschreiben, hmmm...
        carControl.setSuspensionCompression(0.2f  * 2.0f * FastMath.sqrt(120.0f));
        carControl.setSuspensionDamping(0.3f  * 2.0f * FastMath.sqrt(120.0f));
        carControl.setSuspensionStiffness(120.0f);
        carControl.setMaxSuspensionForce(10000);
        
        vehicleNode.addControl(carControl);
        
//        //Create four wheels and add them at their locations
//        //note that our fancy car actually goes backwards..
//        Vector3f wheelDirection = new Vector3f(0, -1, 0);
//        Vector3f wheelAxle = new Vector3f(-1, 0, 0);
//
//        Geometry wheel_fr = (Geometry) assetManager.loadModel("Models/wheel_rf.obj");
//        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        tex = assetManager.loadTexture("Textures/wheel.png");
////        mat.getAdditionalRenderState().setWireframe(true);
//        mat.setTexture("ColorMap", tex);
//        wheel_fr.setMaterial(mat);
//        wheel_fr.center();
//        box = (BoundingBox) wheel_fr.getModelBound();
//        wheelRadius = box.getZExtent();
//        System.out.println(box.getYExtent());
//        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
//        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
//        carControl.addWheel(wheel_fr, box.getCenter().add(0, -front_wheel_h, 0),
//                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);
//
//        Geometry wheel_fl = (Geometry) assetManager.loadModel("Models/wheel_lf.obj");
//        wheel_fl.setMaterial(mat);
//        wheel_fl.center();
//        box = (BoundingBox) wheel_fl.getModelBound();
//        carControl.addWheel(wheel_fl, box.getCenter().add(0, -front_wheel_h, 0),
//                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);
//
//        Geometry wheel_br = (Geometry) assetManager.loadModel("Models/wheel_rr.obj");
//        wheel_br.setMaterial(mat);
//        wheel_br.center();
//        wheelRadius = box.getZExtent();
//        System.out.println(box.getYExtent());
//        box = (BoundingBox) wheel_br.getModelBound();
//        carControl.addWheel(wheel_br, box.getCenter().add(0, -back_wheel_h, 0),
//                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);
//
//        Geometry wheel_bl = (Geometry) assetManager.loadModel("Models/wheel_lr.obj");
//        wheel_bl.setMaterial(mat);
//        wheel_bl.center();
//        box = (BoundingBox) wheel_bl.getModelBound();
//        carControl.addWheel(wheel_bl, box.getCenter().add(0, -back_wheel_h, 0),
//                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);
//
////        carControl.getWheel(2).setFrictionSlip(4);
////        carControl.getWheel(3).setFrictionSlip(4);


        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radius1 = 0.4f;
        float radius2 = 0.3f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 0.85f;
        float zOff = 0.8f;

        Cylinder wheelMesh1 = new Cylinder(16, 16, radius1, radius1 * 2f, true);
        Cylinder wheelMesh2 = new Cylinder(16, 16, radius2, radius2 * 2f, true);
        

        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        tex = assetManager.loadTexture("Textures/wheel.png");
//        mat.getAdditionalRenderState().setWireframe(true);
        mat.setTexture("ColorMap", tex);
        
        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh2);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        carControl.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius2, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh2);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        carControl.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius2, true);

        yOff += 0.1f;
        zOff += 1;
        xOff += 0.15f;
        
        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh1);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        carControl.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius1, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh1);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        carControl.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius1, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        rootNode.attachChild(vehicleNode);
        
        bulletAppState.getPhysicsSpace().add(carControl);
        carControl.setPhysicsLocation(spawnPoint);
        carControl.setPhysicsRotation(spawnRotation);
    }
    
    @Override
    protected void cleanupPlayer() {
        bulletAppState.getPhysicsSpace().remove(carControl);
        rootNode.detachChild(vehicleNode);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        steer = 0;
        if(left) steer++;
        if(right) steer--;
        accelerationValue = 0;
        if(forward) accelerationValue += 2000;
        if(backward)
            if(carControl.getCurrentVehicleSpeedKmHour() >= 0) {
                carControl.brake(40f);
                System.out.println("Brakes");
            }
        
            else if (carControl.getCurrentVehicleSpeedKmHour() < 0)
            {
                accelerationValue -= 2000;
                System.out.println("Decel");
            }
        
        steeringValue = steer * 0.5f * FastMath.pow(((MAX_SPEED - carControl.getCurrentVehicleSpeedKmHour()) / MAX_SPEED) , 2);
        carControl.steer(steeringValue);
        carControl.accelerate(accelerationValue * ((MAX_SPEED - carControl.getCurrentVehicleSpeedKmHour()) / MAX_SPEED));
        
        for (List<Spatial> spatials : gameAppState.getPlacedTraps().values()) {
            for(Spatial spatial : spatials) {
                if(geometry.collideWith(spatial.getWorldBound(), new CollisionResults()) > 0) {
                    if(spatial.getUserData(TrapPlaceAppState.DMG_ART_KEY) == DMGArt.BUSHES.name()) {
                        Vector3f oldVelocity = carControl.getLinearVelocity();
                        float vx = carControl.getLinearVelocity().x;
                        float vy = carControl.getLinearVelocity().y;
                        float vz = carControl.getLinearVelocity().z;
                        if(Math.abs(vx) > 10) {
                            oldVelocity.setX(vx > 0 ? 10 : -10);
                            carControl.setLinearVelocity(oldVelocity);
                        }
                        if(Math.abs(vy) > 10) {
                            oldVelocity.setY(vy > 0 ? 10 : -10);
                            carControl.setLinearVelocity(oldVelocity);
                        }
                        if(Math.abs(vz) > 10) {
                            oldVelocity.setZ(vz > 0 ? 10 : -10);
                            carControl.setLinearVelocity(oldVelocity);
                        }
                    }
                }
            }
        }
        
    }

    public VehicleControl getControl() {
        return carControl;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name, isPressed, tpf);
        if(this.isEnabled()) {
            switch(name) {
                case MAPPING_RESET:
                    if (isPressed) {
                        carControl.setPhysicsLocation(spawnPoint);
                        carControl.setPhysicsRotation(spawnRotation);
                        carControl.setLinearVelocity(Vector3f.ZERO);
                        carControl.setAngularVelocity(Vector3f.ZERO);
                        carControl.resetSuspension();
                    }
                    break;
                default: break;
            }
    //        System.out.println(carControl.getCurrentVehicleSpeedKmHour() + ", " + accelerationValue + ", " + (MAX_SPEED - carControl.getCurrentVehicleSpeedKmHour()));
        }
    }

    @Override
    public void causeDmg(double dmg, DMGArt art) {
        switch (art) {
            case SPIKE:
                super.causeDmg((int)(dmg * SPIKE_DMG_GAIN));
                break;
            case BOUNCE: break;
            case TRAFFICCONE: break;
            case BUSHES: break;
            default:
                super.causeDmg((int)dmg);
                break;
        }
    }

    @Override
    public Vector3f getLocation() {
        return carControl.getPhysicsLocation();
    }

    @Override
    public void setLocation(Vector3f location) {
        carControl.setPhysicsLocation(location);
    }

    @Override
    public Quaternion getRotation() {
        return carControl.getPhysicsRotation();
    }

    @Override
    public void setRotation(Quaternion rotation) {
        carControl.setPhysicsRotation(rotation);
    }
    
}
