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
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author rober
 */
public class TrapPlaceHUDBuilder extends AbstractScreenBuilder {
    
    public static final String PLAYER_TEXT = "player_text";
    public static final String PLACE_POINTS_TEXT = "place_points_text";
    public static final String POINTS_TEXT = "points_text";
    public static final String TRAP_COUNT_TEXT = "trap_count_text";
    
    public static final String PLAYER_TEXT_FORMAT = "Spieler %d\n";
    public static final String PLACE_POINTS_TEXT_FORMAT = "Platz %d\n";
    public static final String POINTS_TEXT_FORMAT = "Punkte: %06d";
    public static final String TRAP_COUNT_TEXT_FORMAT = "%02d / %02d Fallenpunkte";
    
    public TrapPlaceHUDBuilder(String string, SimpleApplication app, ScreenController controller) {
        super(string, app, controller);
    }

    @Override
    public void init(ScreenController controller) {
        controller(controller);

        /*layer(new LayerBuilder("background") {
            {
                childLayoutHorizontal();

                panel(new PanelBuilder("bg_panel_left") {
                    {
                        childLayoutHorizontal();
                        alignCenter();
                        //backgroundColor("#0f08");
                        height("100%");
                        width("10%");
                        // add text

                        panel(new PanelBuilder("bg_panel_left_center") {
                            {
                                childLayoutVertical();
                                valignCenter();
                                height("50%");
                                width("100%");

                                panel(new PanelBuilder("bg_panel_left_center_1") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
                                        height("33%");
                                        width("100%");
                                        backgroundColor(new Color(255, 80, 0, 0));
                                        visible(false);

                                    }
                                });

                                panel(new PanelBuilder("bg_panel_left_center_2") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
                                        height("33%");
                                        width("100%");
                                        backgroundColor(new Color(255, 80, 0, 0));
                                        visible(false);
                                    }
                                });

                                panel(new PanelBuilder("bg_panel_left_center_3") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
                                        height("33%");
                                        width("100%");
                                        backgroundColor(new Color(255, 80, 0, 0));
                                        visible(false);

                                    }
                                });

                            }
                        });

                    }
                });
            }
        });*/
        layer(new LayerBuilder("foreground") {
            {
                childLayoutHorizontal();

                panel(new PanelBuilder("panel_left") {
                    {
                        childLayoutHorizontal();
                        alignCenter();
                        //backgroundColor("#0f08");
                        height("100%");
                        width("10%");
                        // add text

                        panel(new PanelBuilder("panel_left_center") {
                            {
                                childLayoutVertical();
                                valignCenter();
                                height("50%");
                                width("100%");

                                panel(new PanelBuilder("panel_left_center_1") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
                                        height("33%");
                                        width("50%");

                                        panel(new PanelBuilder("panel_left_center_2_1") {
                                            {
                                                childLayoutCenter();
                                                valignCenter();
                                                height("60%");
                                                width("100%");
                                                image(new ImageBuilder("falle1") {
                                                    {
                                                        filename("Textures/Images/pylon_klein.png");
                                                        alignCenter();
                                                        valignCenter();
                                                        height("100%");
                                                        width("100%");
                                                        interactOnClick("trap1()");
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("panel_left_center_2") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
                                        height("33%");
                                        width("50%");

                                        panel(new PanelBuilder("panel_left_center_2_2") {
                                            {
                                                childLayoutCenter();
                                                valignCenter();
                                                height("60%");
                                                width("100%");
                                                image(new ImageBuilder("falle2") {
                                                    {
                                                        filename("Textures/Images/pylon_klein.png");
                                                        alignCenter();
                                                        valignCenter();
                                                        height("100%");
                                                        width("100%");
                                                        interactOnClick("trap2()");
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });

                                panel(new PanelBuilder("panel_left_center_3") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
                                        height("33%");
                                        width("50%");

                                        panel(new PanelBuilder("panel_left_center_2_3") {
                                            {
                                                childLayoutCenter();
                                                valignCenter();
                                                height("60%");
                                                width("100%");
                                                image(new ImageBuilder("falle3") {
                                                    {
                                                        filename("Textures/Images/pylon_klein.png");
                                                        alignCenter();
                                                        valignCenter();
                                                        height("100%");
                                                        width("100%");
                                                        interactOnClick("trap3()");
                                                    }
                                                });
                                            }
                                        });

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
                                //childLayoutCenter();
                                childLayoutVertical();
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
                                //Passende Position finden
                                text(new TextBuilder(PLACE_POINTS_TEXT) {
                                    {
                                        text(String.format(PLACE_POINTS_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignLeft();
                                        height("50%");
                                        width("30%");
                                    }
                                });
                                //Passende Position finden
                                text(new TextBuilder(TRAP_COUNT_TEXT) {
                                    {
                                        text(String.format(TRAP_COUNT_TEXT_FORMAT, 0, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignLeft();
                                        height("50%");
                                        width("30%");
                                    }
                                });
                                //Passende Position finden
                                text(new TextBuilder(POINTS_TEXT) {
                                    {
                                        text(String.format(POINTS_TEXT_FORMAT, 0));
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
