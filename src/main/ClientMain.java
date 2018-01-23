/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;

/**
 * 
 * @author Robbo13
 */
public class ClientMain extends SimpleApplication{

    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.start(JmeContext.Type.Display);
    }

    @Override
    public void simpleInitApp() {
        //TODO: add init code
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
