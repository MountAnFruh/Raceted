/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.network;

import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import game.utils.NetworkUtils;

/**
 *
 * @author Robbo13
 */
public class NetworkServer {
    
    private Server server;

    public NetworkServer() {
        
    }
    
    public void start() throws IOException {
        server = Network.createServer(NetworkUtils.PORT);
        server.start();
    }
}
