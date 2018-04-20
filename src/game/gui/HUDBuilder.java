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

/**
 *
 * @author Kevin
 */
public class HUDBuilder extends ScreenBuilder {

    private Nifty nifty;
    private SimpleApplication app;

    public HUDBuilder(String string, Nifty nifty, SimpleApplication app) {
        super(string);
        this.nifty = nifty;
        this.app = app;

        init();
    }

    public void init() {
        controller(new GUIScreenController(nifty));

        layer(new LayerBuilder("foreground") {
            {
                childLayoutHorizontal();

                panel(new PanelBuilder("panel_left") {
                    {
                        childLayoutCenter();
                        //backgroundColor("#00f8");
                        height("15%");
                        width("90%");
                    }
                });
                panel(new PanelBuilder("panel_right") {
                    {
                        childLayoutVertical();
                        //backgroundColor("#00f8");
                        height("75%");
                        width("10%");
                        
                        panel(new PanelBuilder("panel_top_right") {
                            {
                                childLayoutCenter();
                                //backgroundColor("#44f8");
                                height("15%");
                                width("100%");

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
                }); // panel added
            }
        });
    }

}
