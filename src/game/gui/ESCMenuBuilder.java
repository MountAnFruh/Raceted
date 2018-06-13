/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.SimpleApplication;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.ScreenController;
import static game.gui.GameHUDBuilder.PLAYER_TEXT;

/**
 *
 * @author Robert Schmölzer
 */
public class ESCMenuBuilder extends AbstractScreenBuilder {
    
    public static final String PLAYER_TEXT = "player_text";
    public static final String PLAYER_TEXT_FORMAT = "Spieler %d\n";

    public ESCMenuBuilder(String string, SimpleApplication app, ScreenController controller) {
        super(string, app, controller);
    }

    @Override
    public void init(ScreenController controller) {
        controller(controller);

        layer(new LayerBuilder("foreground") {
            {
                childLayoutVertical();

                panel(new PanelBuilder("panel_top") {
                    {
                        childLayoutCenter();

                        height("35%");
                        width("75%");
                    }
                });

                panel(new PanelBuilder("panel_mid") {
                    {
                        childLayoutVertical();
                        alignCenter();
                        //backgroundColor("#0f08");
                        height("30%");
                        width("75%");
                        // add text

                        panel(new PanelBuilder("panel_mid_top") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#88f8");
                                height("50%");
                                width("50%");

                                control(new ButtonBuilder("back_to_game", "Back to game") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        visibleToMouse(true);
                                        interactOnClick("backtohud()");
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_mid_down") {
                            {
                                childLayoutCenter();
                                valignCenter();
                                //backgroundColor("#88f8");
                                height("50%");
                                width("50%");

                                control(new ButtonBuilder("back_to_main_menu", "Exit Game") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("50%");
                                        visibleToMouse(true);
                                        interactOnClick("backtomain()");
                                    }
                                });

                            }
                        });

                    }
                });
                
                panel(new PanelBuilder("panel_right") {
                    {
                        childLayoutVertical();
                        //backgroundColor("#00f8");
                        height("100%");
                        width("33%");

                        panel(new PanelBuilder("panel_top_right") {
                            {
                                childLayoutCenter();
                                //backgroundColor("#44f8");
                                height("50%");
                                width("100%");

                                // add image
                                image(new ImageBuilder() {
                                    {
                                        filename("Textures/Images/raceted_icon.png");
                                        valignTop();
                                        alignRight();
                                    }
                                });
                                
                                //Passende Position finden
                                text(new TextBuilder(PLAYER_TEXT) {
                                    {
                                        text(String.format(PLAYER_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignLeft();
                                        height("50%");
                                        width("30%");
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_bottom_right") {
                            {
                                childLayoutCenter();
                                //backgroundColor("#44f8");
                                height("50%");
                                width("100%");
                                valignBottom();
                                alignRight();

                                // add image
                                image(new ImageBuilder() {
                                    {
                                        filename("Textures/Images/raceted_icon.png");
                                        valignBottom();
                                        alignRight();
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
