/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author rober
 */
public abstract class AbstractInit {

    protected SimpleApplication app;
    protected AppStateManager stateManager;
    protected AssetManager assetManager;
    protected BulletAppState bulletAppState;
    protected Spatial terrain;
    protected Node rootNode;
    protected FlyByCamera flyCam;

    public AbstractInit(SimpleApplication app) {
        this.app = app;

        stateManager = app.getStateManager();
        assetManager = app.getAssetManager();
        rootNode = app.getRootNode();
        flyCam = app.getFlyByCamera();

        rootNode.detachAllChildren();

        bulletAppState = new BulletAppState();

        stateManager.attachAll(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
    }
    
    public void update(float tpf)
    {
        
    }

    public abstract void close();

}
