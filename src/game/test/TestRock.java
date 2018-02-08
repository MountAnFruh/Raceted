/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import game.entities.RockAppState;

/**
 *
 * @author Robbo13
 */
public class TestRock extends SimpleApplication {
    
    public final static float MAX_DMG = 100f;
    public final static float SPIKE_DMG_REDUCE = 3f;

    private RockAppState rockAppState;
    private BulletAppState bulletAppState;
    private Spatial terrain;
    
    private float dmg = 0;

    public static void main(String[] args) {
        TestRock testRock = new TestRock();
        testRock.start(JmeContext.Type.Display);
    }

    public void causeDmg(float dmg, DMGArt art)
    {
        switch(art)
        {
            case SPIKE:
                this.dmg += dmg / SPIKE_DMG_REDUCE;
                break;
            default:
                this.dmg += dmg;
                break;
        }
        if(dmg > MAX_DMG)destroyStone();
    }

    private void destroyStone()
    {
        dmg = 0;
        // noch zum hinzufügen
    }
    
    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attachAll(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        flyCam.setEnabled(false);

        initLight();
        initSky();
        initTerrain();

        rockAppState = new RockAppState(bulletAppState, rootNode, terrain);
        stateManager.attach(rockAppState);
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

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {

    }
}
