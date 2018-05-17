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
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Kevin
 */
public class StartBuilder extends AbstractScreenBuilder {

    public StartBuilder(String string, SimpleApplication app, ScreenController controller) {
        super(string, app, controller);
    }

    @Override
    public void init(ScreenController controller) {
        controller(controller);
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
                        childLayoutCenter();
                        alignCenter();
                        //backgroundColor("#0f08");
                        height("50%");
                        width("75%");
                        // add text
                        text(new TextBuilder() {
                            {
                                text("Raceted ist ein Racer, bei dem es rein ums gewinnen geht! \n"
                                    +"Durch das Platzieren von Fallen kann man den Gegner den Weg zum Ziel erschweren und sich somit einen Vorteil gegenüber diese verschaffen.");
                                font("Interface/Fonts/Default.fnt");
                                wrap(true);
                                height("100%");
                                width("100%");
                            }
                        });

                    }
                });

                panel(new PanelBuilder("panel_bottom") {
                    {
                        childLayoutHorizontal();
                        alignCenter();
                        //backgroundColor("#00f8");
                        height("25%");
                        width("75%");

                        panel(new PanelBuilder("panel_bottom_left") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#44f8");
                                height("50%");
                                width("50%");

                                control(new ButtonBuilder("StartButton", "Start") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        interactOnClick("startGame()");
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_bottom_right") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#88f8");
                                height("50%");
                                width("50%");

                                control(new ButtonBuilder("QuitButton", "quit") {
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
                    }
                }); // panel added
            }
        });

    }

}
