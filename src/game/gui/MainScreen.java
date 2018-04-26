/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import game.test.AbstractInit;

/**
 *
 * @author Robbo13
 */
public class MainScreen extends AbstractAppState {
    
    
    
    private AudioNode audioSource;
    private AssetManager asset;
    private Nifty nifty;
    private SimpleApplication app;
    private GUIScreenController controller;
    private String currentScreen = "";
    private AbstractInit currentGame = null;
    
    private static MainScreen theInstance;
    
    public static MainScreen getTheInstance()
    {
        if(theInstance==null)
        {
            theInstance = new MainScreen();
        }
        
        return theInstance;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application appl) {
        
        app = (SimpleApplication) appl;
        asset = app.getAssetManager();
//        audioSource = new AudioNode(asset, "Sounds/Musics/Main.ogg", AudioData.DataType.Buffer);
//        audioSource.play();
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        app.getGuiViewPort().addProcessor(niftyDisplay);
        app.getFlyByCamera().setDragToRotate(true);
        
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        
        controller = new GUIScreenController(nifty, app);
        
        nifty.addScreen("start", new StartBuilder("start", nifty, app, controller).build(nifty));
        
        nifty.addScreen("hud", new HUDBuilder("hud", nifty, app, controller).build(nifty));
        
        nifty.addScreen("hud_terrain_text", new HUDTerrainTextBuilder("hud_terrain_text", nifty, app, controller).build(nifty));
        
        nifty.addScreen("chooser", new ChooseBuilder("chooser", nifty, app, controller).build(nifty));
        
        goToScreen("start");
    }

//    @Override
//    public void bind(Nifty nifty, Screen screen) {
//    }
//
//    @Override
//    public void onStartScreen() {
//
//    }
//
//    @Override
//    public void onEndScreen() {
//
//    }
//
//    public void quitGame() {
//        audioSource = new AudioNode(asset, "Sounds/Musics/Rock.ogg", AudioData.DataType.Buffer);
//        audioSource.play();
//        System.out.println("asdfghjlllkjhgfds");
//        nifty.exit();
//        app.stop();
//    }
    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        controller.update(tpf);
    }

    public String getCurrentScreenName() {
        return currentScreen;
    }
    
    public void goToScreen(String screen)
    {
        nifty.gotoScreen(screen);
        currentScreen = screen;
    }

    public void setCurrentGame(AbstractInit currentGame) {
        this.currentGame = currentGame;
    }

    public AbstractInit getCurrentGame() {
        return currentGame;
    }
}
