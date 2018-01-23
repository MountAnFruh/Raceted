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
import utils.NetworkUtils;

/**
 *
 * @author Robbo13
 */
public class ServerMain extends SimpleApplication {
    
    private Server server;
    
    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain();
        serverMain.start(JmeContext.Type.Headless);
    }
    
    private void initServer() {
        try {
            server = Network.createServer(NetworkUtils.PORT);
            server.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void simpleInitApp() {
        initServer();
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
