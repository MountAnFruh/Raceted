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
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Kevin
 */
public class ChooseBuilder extends AbstractScreenBuilder {

    public ChooseBuilder(String string, SimpleApplication app, ScreenController controller) {
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
                                height("25%");
                                width("50%");

                                control(new ButtonBuilder("Cart", "cart") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        visibleToMouse(true);
                                        interactOnClick("playwithCart()");

                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_center_right") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#88f8");
                                height("25%");
                                width("50%");

                                control(new ButtonBuilder("Rock", "rock") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        visibleToMouse(true);
                                        interactOnClick("playwithRock()");
                                    }
                                });

                            }
                        });

//                        panel(new PanelBuilder("panel_center_right_down") {
//                            {
//                                childLayoutCenter();
//                                valignCenter();
//                                //backgroundColor("#88f8");
//                                height("25%");
//                                width("50%");
//
//                                control(new ButtonBuilder("Terrain", "terrain") {
//                                    {
//                                        alignCenter();
//                                        valignCenter();
//                                        height("50%");
//                                        width("50%");
//                                        visibleToMouse(true);
//                                        interactOnClick("playwithTerrain()");
//                                    }
//                                });
//
//                            }
//                        });
//                        
//                        panel(new PanelBuilder("panel_center_right_lowest") {
//                            {
//                                childLayoutCenter();
//                                valignCenter();
//                                //backgroundColor("#88f8");
//                                height("25%");
//                                width("50%");
//
//                                control(new ButtonBuilder("Traps", "traps") {
//                                    {
//                                        alignCenter();
//                                        valignCenter();
//                                        height("50%");
//                                        width("50%");
//                                        visibleToMouse(true);
//                                        interactOnClick("playwithTraps()");
//                                    }
//                                });
//
//                            }
//                        });

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
