/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;

/**
 *
 * @author Robbo13
 */
public class ClientMain extends SimpleApplication {

    private Nifty nifty;

    public static void main(String[] args) {
//        ClientMain clientMain = new ClientMain();
//        clientMain.start(JmeContext.Type.Display);

        ClientMain app = new ClientMain();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.registerMusic("mysound", "Sounds/Musics/Main.mp3");
        // <screen>
        nifty.addScreen("Screen_ID", new ScreenBuilder("Hello Nifty Screen") {
            {
                controller(new DefaultScreenController()); // Screen properties

                // <layer>
                layer(new LayerBuilder("Layer_ID") {
                    {
                        childLayoutVertical(); // layer properties, add more...

                        // <panel>
                        panel(new PanelBuilder("Panel_ID") {
                            {
                                childLayoutCenter(); // panel properties, add more...

                                // GUI elements
                                control(new ButtonBuilder("Button_ID", "Hello Nifty") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("5%");
                                        width("15%");
                                    }
                                });

                                //.. add more GUI elements here
                            }
                        });
                        
                        panel(new PanelBuilder("Panel_ID2") {
                            {
                                childLayoutCenter(); // panel properties, add more...

                                // GUI elements
                                control(new ButtonBuilder("Button_ID2", "Hello Nifty") {
                                    {
                                        alignCenter();
                                        valignCenter();
                                        height("5%");
                                        width("15%");
                                    }
                                });

                                //.. add more GUI elements here
                            }
                        });
                        // </panel>
                    }
                });
                // </layer>
            }
        }.build(nifty));
        // </screen>

        nifty.gotoScreen("Screen_ID"); // start the screen
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
