/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import java.io.IOException;
import game.network.NetworkServer;

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
        try {
            server.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
