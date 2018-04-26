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
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import game.test.InitTestCar;
import game.test.InitTestRock;
import game.test.InitTestTerrain;

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
        audioSource.play();
        app.stop();
    }

    public void startGame() {
        mainScreen.goToScreen("chooser");
    }

    public void backtomain() {
        audioSource.play();
        mainScreen.goToScreen("start");
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

    public void playwith(String character) {
        audioSource.stop();
        switch (character) {
            case "Cart":
                System.out.println("testCar");

                InitTestCar testCar = new InitTestCar(app);
                mainScreen.goToScreen("hud");
                mainScreen.setCurrentGame(testCar);
                break;

            case "Rock":
                System.out.println("testRock");

                InitTestRock testRock = new InitTestRock(app);
                mainScreen.goToScreen("hud");
                mainScreen.setCurrentGame(testRock);
                break;

            case "Terrain":
                System.out.println("testTerrain");

                InitTestTerrain testTerrain = new InitTestTerrain(nifty, app);
                mainScreen.goToScreen("hud");
                mainScreen.setCurrentGame(testTerrain);
                break;
        }
    }

    public void update(float tpf) {
        
        if (mainScreen.getCurrentGame() instanceof InitTestTerrain) {
            ((InitTestTerrain)mainScreen.getCurrentGame()).update(tpf);
        }
    }

}
