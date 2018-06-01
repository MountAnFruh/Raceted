/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import beans.PlayerInfo;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import game.entities.RockAppState;
import game.main.appstates.GameAppState;
import game.test.AbstractInit;
import game.test.InitTestCar;
import game.test.InitTestRock;
import game.test.InitTestTerrain;
import game.test.InitTestTrap;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rober
 */
public class GUIScreenController implements ScreenController {

    private static final GameAppState.Level LEVEL = GameAppState.Level.LEVEL1;
    private static final int PLAYERCOUNT = 2;
    
    private final Nifty nifty;
    private final AssetManager assetManager;
    private final AppStateManager stateManager;
    private final GUIAppState guiAppState;
    private final List<PlayerInfo> playerInfos = new ArrayList<>();
    
    private GameAppState gameAppState;
    private AudioNode audioSource;
    private SimpleApplication app;
    
    public final Color bgColor = new Color(255, 16, 0, 50);
    private final String panelname = "panel_left_center_2";

    public GUIScreenController(GUIAppState guiAppState, Nifty nifty, SimpleApplication app) {
        this.guiAppState = guiAppState;
        this.nifty = nifty;
        this.app = app;
        assetManager = app.getAssetManager();
        stateManager = app.getStateManager();
        
        audioSource = new AudioNode(assetManager, "Sounds/Effects/Select.ogg", AudioData.DataType.Buffer);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void quitGame() {
        setCurrentPlayerNumber(0);
        app.stop();
    }

    public void startGame() {
        setCurrentPlayerNumber(playerInfos.size() + 1);
        guiAppState.goToScreen(GUIAppState.CHARACTER_CHOOSER);
    }

    public void backtomain() {
//        if(rockapp != null)
//        {
//            rockapp.setRunning(false);
//        }
//        app.getRootNode().detachAllChildren();
//        tester.close();
//        mainScreen.goToScreen("start");
//        quitGame();
        stateManager.detach(gameAppState);
        gameAppState = null;
        playerInfos.clear();
        guiAppState.goToScreen(GUIAppState.START_SCREEN);
    }

    public void backtohud() {
        gameAppState.toggleHUD();
    }

    public void playwithCart() {
        audioSource.stop();
        playwith(GameAppState.Character.CAR);
    }

    public void playwithRock() {
        audioSource.stop();
        playwith(GameAppState.Character.ROCK);
    }

//    public void playwithTerrain() {
//        audioSource.stop();
//        playwith("Terrain");
//    }

//    public void playwithTraps() {
//        audioSource.stop();
//        playwith("Traps");
//    }

    public void trap1() {
        gameAppState.setTrap(1);

        Element e = nifty.getScreen("trap_chooser").findElementById(panelname + "_1");
        System.out.println(e.getId());
        e.getRenderer(PanelRenderer.class).setBackgroundColor(bgColor);

        e = nifty.getScreen("trap_chooser").findElementById(panelname + "_3");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(null);

        e = nifty.getScreen("trap_chooser").findElementById(panelname + "_2");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(null);
    }

    public void trap2() {
        gameAppState.setTrap(2);

        Element e = nifty.getScreen("trap_chooser").findElementById(panelname + "_2");
        System.out.println(e.getId());
        e.getRenderer(PanelRenderer.class).setBackgroundColor(bgColor);

        e = nifty.getScreen("trap_chooser").findElementById(panelname + "_1");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(null);

        e = nifty.getScreen("trap_chooser").findElementById(panelname + "_3");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(null);
    }

    public void trap3() {
        gameAppState.setTrap(3);

        Element e = nifty.getScreen("trap_chooser").findElementById(panelname + "_3");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(bgColor);

        e = nifty.getScreen("trap_chooser").findElementById(panelname + "_1");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(null);

        e = nifty.getScreen("trap_chooser").findElementById(panelname + "_2");
        e.getRenderer(PanelRenderer.class).setBackgroundColor(null);
    }
    
    public void setTimeInGameHUD(LocalTime time) {
        Element e = nifty.getScreen(GUIAppState.GAME_HUD).findElementById(GameHUDBuilder.TIME_TEXT);
        e.getRenderer(TextRenderer.class).setText(time.format(DateTimeFormatter.ofPattern("mm:ss.SSS")) + "\n");
    }
    
    public void setRoundInGameHUD(int round) {
        Element e = nifty.getScreen(GUIAppState.GAME_HUD).findElementById(GameHUDBuilder.ROUND_TEXT);
        e.getRenderer(TextRenderer.class).setText("Runde " + round + "\n");
    }
    
    public void setPlaceInGameHUD(int place) {
        Element e = nifty.getScreen(GUIAppState.GAME_HUD).findElementById(GameHUDBuilder.PLACE_TEXT);
        e.getRenderer(TextRenderer.class).setText("Platz " + place + "\n");
    }
    
    public void setCurrentPlayerNumber(int number) {
        String text = "Spieler " + number + "\n";
        Element e = nifty.getScreen(GUIAppState.ESC_MENU).findElementById(ESCMenuBuilder.PLAYER_TEXT);
        if(e != null) e.getRenderer(TextRenderer.class).setText(text);
        e = nifty.getScreen(GUIAppState.GAME_HUD).findElementById(GameHUDBuilder.PLAYER_TEXT);
        if(e != null) e.getRenderer(TextRenderer.class).setText(text);
        e = nifty.getScreen(GUIAppState.TRAP_PLACE_HUD).findElementById(TrapPlaceHUDBuilder.PLAYER_TEXT);
        if(e != null) e.getRenderer(TextRenderer.class).setText(text);
        e = nifty.getScreen(GUIAppState.CHARACTER_CHOOSER).findElementById(ChooseBuilder.PLAYER_TEXT);
        if(e != null) e.getRenderer(TextRenderer.class).setText(text);
    }

    public void playwith(GameAppState.Character character) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setCharacter(character);
        playerInfos.add(playerInfo);
        if(playerInfos.size() >= PLAYERCOUNT) {
            audioSource.stop();
            gameAppState = new GameAppState(guiAppState, playerInfos, LEVEL);
            stateManager.attach(gameAppState);
        } else {
            setCurrentPlayerNumber(playerInfos.size() + 1);
        }
    }

//    public void update(float tpf) {
//        if (tester != null) {
//            tester.update(tpf);
//        }
//    }

}
