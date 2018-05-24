/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import game.entities.RockAppState;
import game.test.AbstractInit;
import game.test.InitTestCar;
import game.test.InitTestRock;
import game.test.InitTestTerrain;
import game.test.InitTestTrap;
import java.util.List;

/**
 *
 * @author rober
 */
public class GUIScreenController implements ScreenController {

    private AudioNode audioSource;
    private AssetManager asset;
    private Nifty nifty;
    private SimpleApplication app;
    private MainScreen mainScreen;
    private AbstractInit tester;
    private RockAppState rockapp;
    private InitTestTrap trapapp;
    public final Color bgColor = new Color(255, 16, 0, 50);
    private final String panelname = "panel_left_center_2";

    public GUIScreenController(Nifty nifty, SimpleApplication app) {
        this.nifty = nifty;
        this.app = app;
        asset = app.getAssetManager();
        audioSource = new AudioNode(asset, "Sounds/Effects/Select.ogg", AudioData.DataType.Buffer);

        mainScreen = MainScreen.getTheInstance();
    }

    public GUIScreenController(Nifty nifty) {
        this.nifty = nifty;
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
        mainScreen.goToScreen("chooser");
    }

    public void backtomain() {
//        if(rockapp != null)
//        {
//            rockapp.setRunning(false);
//        }
//        app.getRootNode().detachAllChildren();
//        tester.close();
//        mainScreen.goToScreen("start");
        quitGame();
    }

    public void backtohud() {
        mainScreen.goToScreen("hud");
    }

    public void playwithCart() {
        audioSource.stop();
        playwith("Cart");
    }

    public void playwithRock() {
        audioSource.stop();
        playwith("Rock");
    }

    public void playwithTerrain() {
        audioSource.stop();
        playwith("Terrain");
    }

    public void playwithTraps() {
        audioSource.stop();
        playwith("Traps");
    }

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

    public void playwith(String character) {
        rockapp = null;
        trapapp = null;
        audioSource.stop();
        switch (character) {
            case "Cart":
                System.out.println("testCar");
                InitTestCar testCar = new InitTestCar(app);
                tester = testCar;
                mainScreen.goToScreen("hud");
                mainScreen.setCurrentGame(testCar);
                break;

            case "Rock":
                System.out.println("testRock");

                InitTestRock testRock = new InitTestRock(app);
                tester = testRock;
                mainScreen.goToScreen("hud");
                mainScreen.setCurrentGame(testRock);
                rockapp = testRock.getAppState();
                break;

            case "Terrain":
                System.out.println("testTerrain");

                InitTestTerrain testTerrain = new InitTestTerrain(nifty, app);
                tester = testTerrain;
                mainScreen.goToScreen("hud");
                mainScreen.setCurrentGame(testTerrain);
                break;
            case "Traps":
                System.out.println("testTerrain");

                trapapp = new InitTestTrap(app);
                tester = trapapp;
                mainScreen.goToScreen("trap_chooser");
                mainScreen.setCurrentGame(trapapp);
                break;
        }
    }

    public void update(float tpf) {
        if (tester != null) {
            tester.update(tpf);
        }
    }

}
