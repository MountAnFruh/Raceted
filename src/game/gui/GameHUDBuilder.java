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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Kevin
 */
public class GameHUDBuilder extends AbstractScreenBuilder {
    
    public static final String PLACE_TIME_TEXT = "place_time_text";
    public static final String TIME_LEVEL_TEXT = "time_level_text";
    public static final String ROUND_TEXT = "round_text";
    public static final String PLAYER_TEXT = "player_text";
    public static final String PLACE_POINTS_TEXT = "place_points_text";
    public static final String POINTS_TEXT = "points_text";
    
    public static final String PLACE_TIME_TEXT_FORMAT = "Platz %d\n";
    public static final String TIME_LEVEL_TEXT_FORMAT_PRE = "Level-Zeit: ";
    public static final String ROUND_TEXT_FORMAT = "Runde %d\n";
    public static final String PLAYER_TEXT_FORMAT = "Spieler %d\n";
    public static final String PLACE_POINTS_TEXT_FORMAT = "Platz %d\n";
    public static final String POINTS_TEXT_FORMAT = "Punkte: %06d";
    
    public GameHUDBuilder(String string, SimpleApplication app, ScreenController controller) {
        super(string, app, controller);
    }

    @Override
    public void init(ScreenController controller) {
        controller(controller);

        layer(new LayerBuilder("foreground") {
            {
                childLayoutHorizontal();

                panel(new PanelBuilder("panel_left") {
                    {
                        childLayoutVertical();
                        //backgroundColor("#00f8");
                        height("100%");
                        width("33%");

                        panel(new PanelBuilder("panel_top_left") {
                            {
                                childLayoutCenter();
                                height("15%");
                                width("100%");

                                text(new TextBuilder(PLACE_TIME_TEXT) {
                                    {
                                        text(String.format(PLACE_TIME_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignLeft();
                                        height("50%");
                                        width("30%");
                                    }
                                });

                                text(new TextBuilder(TIME_LEVEL_TEXT) {
                                    {
                                        text(TIME_LEVEL_TEXT_FORMAT_PRE + "00:00.000");
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignLeft();
                                        height("70%");
                                        width("30%");
                                    }
                                });

                            }
                        });
                    }
                });

                panel(new PanelBuilder("panel_mid") {
                    {
                        childLayoutVertical();
                        //backgroundColor("#00f8");
                        height("100%");
                        width("33%");

                        panel(new PanelBuilder("panel_top_mid") {
                            {
                                childLayoutCenter();
                                height("15%");
                                width("100%");

                                text(new TextBuilder(ROUND_TEXT) {
                                    {
                                        text(String.format(ROUND_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignLeft();
                                        height("50%");
                                        width("100%");
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
