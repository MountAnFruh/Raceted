/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Network;
import java.io.IOException;
import java.net.ConnectException;
import main.utils.NetworkUtils;

/**
 *
 * @author Robbo13
 */
public class NetworkClient implements ClientStateListener {
    
    private Client client;

    public NetworkClient() {
        
    }
    
    /**
     * Starts the connection to the server
     * @throws ConnectException when the server could not be reached
     * @throws IOException 
     */
    public void startConnection() throws ConnectException, IOException {
        client = Network.connectToServer(NetworkUtils.IP_ADDRESS, NetworkUtils.PORT);
        //TODO: Add Message Listeners
        //client.addMessageListener(listener);
        client.addClientStateListener(this);
        client.start();
    }

    @Override
    public void clientConnected(Client c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clientDisconnected(Client c, DisconnectInfo info) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
