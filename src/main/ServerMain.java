/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import java.io.IOException;
import main.network.NetworkServer;
import main.utils.NetworkUtils;

/**
 *
 * @author Robbo13
 */
public class ServerMain extends SimpleApplication {
    
    private NetworkServer server = new NetworkServer();
    
    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain();
        serverMain.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        server.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        super.simpleRender(rm);
    }
    
}
