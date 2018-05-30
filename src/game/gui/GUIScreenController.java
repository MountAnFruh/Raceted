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

    private final GameAppState.Level level = GameAppState.Level.LEVEL1;
    private final int playerCount = 1;
    private final Nifty nifty;
    private final AssetManager assetManager;
    private final AppStateManager stateManager;
    private final GUIAppState guiAppState;
    private final List<PlayerInfo> playerInfos = new ArrayList<>();
    
    private GameAppState gameAppState;
    private AudioNode audioSource;
    private SimpleApplication app;
    private InitTestTrap trapapp;
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
        app.stop();
    }

    public void startGame() {
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
        if (trapapp != null) {
            trapapp.getTrapPlaceAppState().setTrap(1);

            Element e = nifty.getScreen("trap_chooser").findElementById(panelname + "_1");
            System.out.println(e.getId());
            e.getRenderer(PanelRenderer.class).setBackgroundColor(bgColor);

            e = nifty.getScreen("trap_chooser").findElementById(panelname + "_3");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(null);

            e = nifty.getScreen("trap_chooser").findElementById(panelname + "_2");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(null);
        }
    }

    public void trap2() {
        if (trapapp != null) {
            trapapp.getTrapPlaceAppState().setTrap(2);

            Element e = nifty.getScreen("trap_chooser").findElementById(panelname + "_2");
            System.out.println(e.getId());
            e.getRenderer(PanelRenderer.class).setBackgroundColor(bgColor);

            e = nifty.getScreen("trap_chooser").findElementById(panelname + "_1");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(null);

            e = nifty.getScreen("trap_chooser").findElementById(panelname + "_3");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(null);
        }
    }

    public void trap3() {
        if (trapapp != null) {
            trapapp.getTrapPlaceAppState().setTrap(3);

            Element e = nifty.getScreen("trap_chooser").findElementById(panelname + "_3");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(bgColor);

            e = nifty.getScreen("trap_chooser").findElementById(panelname + "_1");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(null);

            e = nifty.getScreen("trap_chooser").findElementById(panelname + "_2");
            e.getRenderer(PanelRenderer.class).setBackgroundColor(null);
        }
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

    public void playwith(GameAppState.Character character) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setCharacter(character);
        playerInfos.add(playerInfo);
        if(playerInfos.size() >= playerCount) {
            trapapp = null;
            audioSource.stop();
            gameAppState = new GameAppState(guiAppState, playerInfos, level);
            stateManager.attach(gameAppState);
        }
    }

//    public void update(float tpf) {
//        if (tester != null) {
//            tester.update(tpf);
//        }
//    }

}
