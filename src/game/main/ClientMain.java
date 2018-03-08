/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.ConnectException;
import game.gui.StartScreen;
import game.network.NetworkClient;
import game.utils.ImageUtils;

/**
 *
 * @author Robbo13
 */
public class ClientMain extends SimpleApplication {

    private final NetworkClient client = new NetworkClient();
    private StartScreen startScreen;
    private AudioNode audioSource;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setSettingsDialogImage(ImageUtils.RACETED_TEXT);
        settings.setResolution(1920, 1080);
        settings.setSamples(16);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        settings.setFullscreen(device.isFullScreenSupported());
        settings.setTitle("Raceted");

        ClientMain app = new ClientMain();
        app.setShowSettings(true);
        app.setSettings(settings);
        app.setDisplayFps(true);
        app.setDisplayStatView(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        initAppStates();
        audioSource = new AudioNode(assetManager, "Sounds/Musics/Main.ogg", AudioData.DataType.Buffer);
        audioSource.play();
        flyCam.setDragToRotate(true);
        try {
            client.startConnection();
        } catch (ConnectException ex) {
            System.out.println("Server not running!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initAppStates() {
        startScreen = new StartScreen();
        stateManager.attach(startScreen);
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
