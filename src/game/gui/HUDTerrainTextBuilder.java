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
public class HUDTerrainTextBuilder extends AbstractScreenBuilder {

    public HUDTerrainTextBuilder(String string, SimpleApplication app, ScreenController controller) {
        super(string, app, controller);
    }

    @Override
    public void init(ScreenController controller) {
        controller(controller);

        layer(new LayerBuilder("foreground") {
            {
                childLayoutVertical();

                panel(new PanelBuilder("panel_left") {
                    {
                        childLayoutHorizontal();
                        //backgroundColor("#00f8");
                        height("15%");
                        width("100%");

                        panel(new PanelBuilder("panel_top_left") {
                            {
                                childLayoutCenter();
                                //backgroundColor("#44f8");
                                height("100%");
                                width("25%");

                                text("Press [1]: Load/Unload Map 1\nPress [2]: Load/Unload Map 2\n"
                                        + "Press [3]: Load/Unload Map 3\n\nPress [4]: Load/Unload Real 1st Map\n\nPress [0]: Unload Map\n"
                                        + "Press [SPACE]: Jump (billig)");
                            }
                        });

                        panel(new PanelBuilder("panel_top_center") {
                            {
                                childLayoutCenter();
                                //backgroundColor("#44f8");
                                height("100%");
                                width("65%");

                            }
                        });
                        panel(new PanelBuilder("panel_top_right") {
                            {
                                childLayoutCenter();
                                //backgroundColor("#44f8");
                                height("100%");
                                width("10%");

                                // add image
                                image(new ImageBuilder() {
                                    {
                                        filename("Textures/Images/raceted_icon.png");
                                        valignCenter();
                                        alignCenter();
                                        height("50%");
                                        width("30%");
                                    }
                                });
                            }
                        });

                    }
                });
            }

        });
    }

}
