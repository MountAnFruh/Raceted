/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.FlyByCamera;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.CarAppState;

/**
 *
 * @author rober
 */
public class InitTestCar {
    private SimpleApplication app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private CarAppState carAppState;
    private BulletAppState bulletAppState;
    private Spatial terrain;
    private Node rootNode;
    private FlyByCamera flyCam;

    public InitTestCar(SimpleApplication app) {
        this.app = app;
        
        stateManager = app.getStateManager();
        assetManager = app.getAssetManager();
        rootNode = app.getRootNode();
        flyCam = app.getFlyByCamera();
        
        rootNode.detachAllChildren();
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        flyCam.setEnabled(false);
        
        initLight();
        initSky();
        initTerrain();
        
        carAppState = new CarAppState(bulletAppState);
        stateManager.attach(carAppState);
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
    
    
}
