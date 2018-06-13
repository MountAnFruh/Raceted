/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import game.gui.GUIAppState;
import game.main.appstates.GameAppState;
import game.utils.ImageUtils;

/**
 *
 * @author Andreas Fruhwirt
 */
public class ClientMain extends SimpleApplication {
    
    private GUIAppState guiAppState;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setSettingsDialogImage(ImageUtils.RACETED_TEXT);
        settings.setSamples(16);
        //GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //settings.setFullscreen(device.isFullScreenSupported());
        
        settings.setFullscreen(true);
        settings.setResolution(1920, 1080);
//      settings.setResolution(640, 480);
        
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
        guiAppState = new GUIAppState();
        stateManager.attach(guiAppState);
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
