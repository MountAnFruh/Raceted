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
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import game.test.TestCar;
import game.test.TestRock;

/**
 *
 * @author rober
 */
public class GUIScreenController implements ScreenController {

    private AudioNode audioSource;
    private AssetManager asset;
    private Nifty nifty;
    private SimpleApplication app;

    public GUIScreenController(Nifty nifty, SimpleApplication app) {
        this.nifty = nifty;
        this.app = app;
        asset = app.getAssetManager();
        audioSource = new AudioNode(asset, "Sounds/Effects/Select.ogg", AudioData.DataType.Buffer);

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
        nifty.gotoScreen("chooser");

    }

    public void backtomain() {
        audioSource.play();
        nifty.gotoScreen("start");

    }

    public void playwithCart() {
        audioSource.stop();
        playwith("Cart");
    }

    public void playwith(String character) {
        audioSource.stop();
        switch (character) {
            case "Cart":
                System.out.println("hi");
//                TestCar testCar = new TestCar();
//                testCar.start(JmeContext.Type.Display);

                break;

            case "Rock":
                System.out.println("hi");
//                TestRock testrock = new TestRock();
//                testrock.start(JmeContext.Type.Display);

                break;
        }
    }

}
