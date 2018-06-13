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
 * @author Kevin Albrich
 */
public class GameHUDBuilder extends AbstractScreenBuilder {

    public static final String PLACE_TIME_TEXT = "place_time_text";
    public static final String TIME_LEVEL_TEXT = "time_level_text";
    public static final String ROUND_TEXT = "round_text";
    public static final String PLAYER_TEXT = "player_text";
    public static final String PLACE_POINTS_TEXT = "place_points_text";
    public static final String POINTS_TEXT = "points_text";
    public static final String HP_TEXT = "hp_text";

    public static final String PLACE_TIME_TEXT_FORMAT = "Platz %d\n";
    public static final String TIME_LEVEL_TEXT_FORMAT_PRE = "Level-Zeit: ";
    public static final String ROUND_TEXT_FORMAT = "Runde %d\n";
    public static final String PLAYER_TEXT_FORMAT = "Spieler %d\n";
    public static final String PLACE_POINTS_TEXT_FORMAT = "Platz %d\n";
    public static final String POINTS_TEXT_FORMAT = "Punkte: %06d\n";
    public static final String HP_TEXT_FORMAT = "HP: %05d/%05d\n";

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
                        //    backgroundColor("#00f8");
                        height("100%");
                        width("50%");

                        panel(new PanelBuilder("panel_top_left") {
                            {
                                childLayoutVertical();
                                // backgroundColor("#00a8");
                                height("50%");
                                width("100%");
                                valignTop();
                                text(new TextBuilder(ROUND_TEXT) {
                                    {
                                        text(String.format(ROUND_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignCenter();
                                        alignLeft();
                                        height("10%");
                                        width("40%");
                                    }
                                });

                                text(new TextBuilder(TIME_LEVEL_TEXT) {
                                    {
                                        text(TIME_LEVEL_TEXT_FORMAT_PRE + "00:00.000");
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignCenter();
                                        alignLeft();
                                        height("10%");
                                        width("60%");
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_bottom_left") {
                            {
                                childLayoutVertical();
                                //  backgroundColor("#00b8");
                                height("50%");
                                width("100%");
                                valignBottom();

                                text(new TextBuilder() {
                                    {
                                        valignBottom();
                                        alignLeft();
                                        height("80%");
                                        width("50%");
                                    }
                                });

                                text(new TextBuilder(PLACE_TIME_TEXT) {
                                    {
                                        text(String.format(PLACE_TIME_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignBottom();
                                        alignLeft();
                                        height("10%");
                                        width("30%");
                                    }
                                }
                                );

                                text(
                                        new TextBuilder(POINTS_TEXT) {
                                    {
                                        text(String.format(POINTS_TEXT_FORMAT, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignBottom();
                                        alignLeft();
                                        height("10%");
                                        width("50%");
                                    }
                                }
                                );

                            }
                        });
                    }

                });

                panel(
                        new PanelBuilder("panel_right") {
                    {
                        childLayoutVertical();
                        //backgroundColor("#00f8");
                        height("100%");
                        width("50%");

                        panel(new PanelBuilder("panel_top_right") {
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
                                        valignTop();
                                        alignRight();
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_bottom_right") {
                            {
                                childLayoutVertical();
                                //backgroundColor("#44f8");
                                height("50%");
                                width("50%");
                                valignTop();
                                alignRight();

                                text(new TextBuilder() {
                                    {

                                        valignCenter();
                                        alignRight();
                                        height("80%");
                                        width("50%");
                                    }
                                });
                                //Passende Position finden
                                text(new TextBuilder(PLAYER_TEXT) {
                                    {
                                        text(String.format(PLAYER_TEXT_FORMAT, 0, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignCenter();
                                        alignRight();
                                        height("10%");
                                        width("50%");
                                    }
                                });
                                //Passende Position finden
                                text(new TextBuilder(HP_TEXT) {
                                    {
                                        text(String.format(HP_TEXT_FORMAT, 0, 0));
                                        font("Interface/Fonts/ErasBoldITC.fnt");
                                        valignTop();
                                        alignRight();
                                        height("10%");
                                        width("70%");
                                    }
                                });
                            }
                        });
                    }
                }
                ); // panel added
            }
        });
    }

}
