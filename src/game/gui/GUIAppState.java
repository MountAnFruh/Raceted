/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import game.test.AbstractInit;

/**
 *
 * @author Robbo13
 */
public class GUIAppState extends AbstractAppState {

    public static final String START_SCREEN = "start";
    public static final String GAME_HUD = "game_hud";
    public static final String TRAP_PLACE_HUD = "trap_place_hud";
    public static final String ESC_MENU = "esc_menu";
    public static final String CHARACTER_CHOOSER = "chooser";
    
    private Nifty nifty;
    private SimpleApplication app;
    private GUIScreenController controller;
    private String currentScreen = null;
    private NiftyJmeDisplay niftyDisplay;

    @Override
    public void initialize(AppStateManager stateManager, Application appl) {

        app = (SimpleApplication) appl;
        
//        audioSource = new AudioNode(asset, "Sounds/Musics/Main.ogg", AudioData.DataType.Buffer);
//        audioSource.play();

        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                app.getAssetManager(),
                app.getInputManager(),
                app.getAudioRenderer(),
                app.getGuiViewPort()
        );
        nifty = niftyDisplay.getNifty();
        
        app.getGuiViewPort().addProcessor(niftyDisplay);
        app.getFlyByCamera().setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        controller = new GUIScreenController(this, nifty, app);

        nifty.addScreen(START_SCREEN, new StartBuilder(START_SCREEN, app, controller).build(nifty));

        nifty.addScreen(GAME_HUD, new GameHUDBuilder(GAME_HUD, app, controller).build(nifty));

//        nifty.addScreen("hud_terrain_text", new HUDTerrainTextBuilder("hud_terrain_text", app, controller).build(nifty));

        nifty.addScreen(CHARACTER_CHOOSER, new ChooseBuilder(CHARACTER_CHOOSER, app, controller).build(nifty));

        nifty.addScreen(ESC_MENU, new ESCMenuBuilder(ESC_MENU, app, controller).build(nifty));
        
        nifty.addScreen(TRAP_PLACE_HUD, new TrapPlaceHUDBuilder(TRAP_PLACE_HUD, app, controller).build(nifty));

        goToScreen(START_SCREEN);
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
//        controller.update(tpf);
    }

    public String getCurrentScreenName() {
        return currentScreen;
    }

    public GUIScreenController getController() {
        return controller;
    }

    public void goToScreen(String screen) {
        if (screen.equals(GUIAppState.START_SCREEN)) {
            this.app.getRootNode().detachAllChildren();
        }
        nifty.gotoScreen(screen);
        currentScreen = screen;
    }
}
