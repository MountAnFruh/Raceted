/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.SimpleApplication;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;

/**
 *
 * @author Kevin
 */
public class ChooseBuilder extends ScreenBuilder {

    private Nifty nifty;
    private SimpleApplication app;

    public ChooseBuilder(String string, Nifty nifty, SimpleApplication app) {
        super(string);
        this.nifty = nifty;
        this.app = app;
        init();
    }

    public void init() {
        controller(new GUIScreenController(nifty, app));
        layer(new LayerBuilder("background") {
            {
                childLayoutCenter();
                //backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {
                    {
                        filename("Textures/Images/BritishMuseum.jpg");
                    }
                });

            }
        });

        layer(new LayerBuilder("foreground") {
            {
                childLayoutVertical();
                //backgroundColor("#0000");

                // panel added
                // panel added
                panel(new PanelBuilder("panel_top") {
                    {
                        childLayoutCenter();
                        alignCenter();
                        //backgroundColor("#f008");
                        height("25%");
                        width("75%");

                        // add text
                        image(new ImageBuilder() {
                            {
                                filename("Textures/Images/raceted_title.png");
                            }
                        });

                    }
                });

                panel(new PanelBuilder("panel_mid") {
                    {
                        childLayoutVertical();
                        alignCenter();
                        //backgroundColor("#0f08");
                        height("50%");
                        width("75%");
                        // add text

                        panel(new PanelBuilder("panel_center_left") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#88f8");
                                height("50%");
                                width("50%");

                                control(new ButtonBuilder("Cart", "cart") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        visibleToMouse(true);
                                        interactOnClick("quitGame()");
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_center_right") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#88f8");
                                height("50%");
                                width("50%");

                                control(new ButtonBuilder("Rock", "rock") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        visibleToMouse(true);
                                        interactOnClick("startGame()");
                                    }
                                });

                            }
                        });

                    }
                });

                panel(new PanelBuilder("panel_bottom") {
                    {
                        childLayoutVertical();
                        alignCenter();
                        //backgroundColor("#0f08");
                        height("25%");
                        width("75%");
                        // add text

                        control(new ButtonBuilder("BackButton", "back") {
                            {
                                alignCenter();
                                valignCenter();
                                height("25%");
                                width("50%");
                                visibleToMouse(true);
                                interactOnClick("backtomain()");
                            }
                        });

                    }
                });

            }
        });

    }

}
