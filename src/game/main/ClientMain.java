/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import game.main.appstates.GameAppState;
import game.utils.ImageUtils;

/**
 *
 * @author Robbo13
 */
public class ClientMain extends SimpleApplication {
    
    private GameAppState gameAppState;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setSettingsDialogImage(ImageUtils.RACETED_TEXT);
        //settings.setResolution(1920, 1080);
        settings.setSamples(16);
        //GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //settings.setFullscreen(device.isFullScreenSupported());
        
        // DELETE AFTER TESTS
        settings.setFullscreen(false);
        settings.setResolution(640, 480);
        
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
        gameAppState = new GameAppState(GameAppState.Character.ROCK,
                GameAppState.Level.LEVEL1);
        stateManager.attach(gameAppState);
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
