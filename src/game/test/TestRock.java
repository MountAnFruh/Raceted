/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;

/** 
 *
 * @author Andreas Fruhwirt
 */
public class TestRock extends SimpleApplication {
    

//    private RockAppState rockAppState;
//    private BulletAppState bulletAppState;
//    private Spatial terrain;

    public static void main(String[] args) {
        TestRock testRock = new TestRock();
        testRock.start(JmeContext.Type.Display);
    }

    
    
    @Override
    public void simpleInitApp() {
        
        InitTestRock initTestRock = new InitTestRock(this);
        
//        bulletAppState = new BulletAppState();
//        stateManager.attachAll(bulletAppState);
//        //bulletAppState.setDebugEnabled(true);
//
//        flyCam.setEnabled(false);
//
//        initLight();
//        initSky();
//        initTerrain();
//
//        rockAppState = new RockAppState(bulletAppState, rootNode, terrain, this.getRenderManager());
//        stateManager.attach(rockAppState);
    }

//    private void initLight() {
//        AmbientLight ambientLight = new AmbientLight();
//        ambientLight.setColor(ColorRGBA.White);
//        rootNode.addLight(ambientLight);
//    }
//
//    private void initSky() {
//        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
//        rootNode.attachChild(sky);
//    }
//
//    private void initTerrain() {
//        terrain = assetManager.loadModel("Scenes/Terrain.j3o");
//        terrain.setLocalTranslation(0, -5, 0);
//        RigidBodyControl landscapeControl = new RigidBodyControl(0.0f);
//        terrain.addControl(landscapeControl);
//        rootNode.attachChild(terrain);
//        bulletAppState.getPhysicsSpace().add(landscapeControl);
//    }

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {

    }
}
