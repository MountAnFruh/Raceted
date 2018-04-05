/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.SimpleApplication;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author rober
 */
public class GUIScreenController implements ScreenController {

    private Nifty nifty;
    private SimpleApplication app;

    public GUIScreenController(Nifty nifty, SimpleApplication app) {
        this.nifty = nifty;
        this.app = app;
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

        nifty.gotoScreen("chooser");

    }

    public void backtomain() {

        nifty.gotoScreen("start");

    }

    public void playrock() {

    }

    public void playcart() {

    }

}
