/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.SimpleApplication;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author rober
 */
public abstract class AbstractScreenBuilder extends ScreenBuilder{

    protected Nifty nifty;
    protected SimpleApplication app;
    
    public AbstractScreenBuilder(String string, Nifty nifty, SimpleApplication app, ScreenController controller) {
        super(string);
        this.nifty = nifty;
        this.app = app;
        init(controller);
    }

    abstract void init(ScreenController controller);
}
