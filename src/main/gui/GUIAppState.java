/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import static de.lessvoid.nifty.NiftyStopwatch.start;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;

/**
 *
 * @author Robbo13
 */
public class GUIAppState extends AbstractAppState {

    public static final String MAINMENUID = "Screen_ID";

    private AssetManager assetManager;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort guiViewPort;

    private Nifty nifty;

    public GUIAppState() {

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.audioRenderer = app.getAudioRenderer();
        this.guiViewPort = app.getGuiViewPort();

        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        // flyCam.setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        // <screen>
        nifty.addScreen("hud", new ScreenBuilder("hud") {
            {
                controller(new DefaultScreenController());

                layer(new LayerBuilder("background") {
                    {
                        childLayoutCenter();
                        backgroundColor("#000f");
                        // <!-- ... -->
                    }
                });

                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutHorizontal();
                        backgroundColor("#0000");

                        // panel added
                        panel(new PanelBuilder("panel_left") {
                            {
                                childLayoutVertical();
                                backgroundColor("#0f08");
                                height("100%");
                                width("80%");
                                // <!-- spacer -->
                            }
                        });

                        panel(new PanelBuilder("panel_right") {
                            {
                                childLayoutVertical();
                                backgroundColor("#00f8");
                                height("100%");
                                width("20%");

                                panel(new PanelBuilder("panel_top_right1") {
                                    {
                                        childLayoutCenter();
                                        backgroundColor("#00f8");
                                        height("15%");
                                        width("100%");
                                    }
                                });

                                panel(new PanelBuilder("panel_top_right2") {
                                    {
                                        childLayoutCenter();
                                        backgroundColor("#44f8");
                                        height("15%");
                                        width("100%");
                                    }
                                });

                                panel(new PanelBuilder("panel_bot_right") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        backgroundColor("#88f8");
                                        height("70%");
                                        width("100%");
                                    }
                                });
                            }
                        }); // panel added
                    }
                });
            }
        }.build(nifty));

        nifty.addScreen("start", new ScreenBuilder("start") {
            {
                controller(new DefaultScreenController());
                layer(new LayerBuilder("background") {
                    {
                        childLayoutCenter();
                        backgroundColor("#000f");
                        // <!-- ... -->
                    }
                });

                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutVertical();
                        backgroundColor("#0000");

                        // panel added
                        panel(new PanelBuilder("panel_top") {
                            {
                                childLayoutCenter();
                                alignCenter();
                                backgroundColor("#f008");
                                height("25%");
                                width("75%");

                                // add text
                                text(new TextBuilder() {
                                    {
                                        text("My Cool Game");
                                        font("Interface/Fonts/Default.fnt");
                                        height("100%");
                                        width("100%");
                                    }
                                });

                            }
                        });

                        panel(new PanelBuilder("panel_mid") {
                            {
                                childLayoutCenter();
                                alignCenter();
                                backgroundColor("#0f08");
                                height("50%");
                                width("75%");
                                // add text
                                text(new TextBuilder() {
                                    {
                                        text("Here goes some text describing the game and the rules and stuff. "
                                                + "Incidentally, the text is quite long and needs to wrap at the end of lines. ");
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
                                backgroundColor("#00f8");
                                height("25%");
                                width("75%");

                                       panel(new PanelBuilder("panel_bottom_left") {{
                    childLayoutCenter();
                    valignCenter();
                    backgroundColor("#44f8");
                    height("50%");
                    width("50%");

                    // add control
                    control(new ButtonBuilder("StartButton", "Start") {{
                      alignCenter();
                      valignCenter();
                      height("50%");
                      width("50%");
                    }});

                }});

                panel(new PanelBuilder("panel_bottom_right") {{
                    childLayoutCenter();
                    valignCenter();
                    backgroundColor("#88f8");
                    height("50%");
                    width("50%");

                    // add control
                    control(new ButtonBuilder("QuitButton", "Quit") {{
                      alignCenter();
                      valignCenter();
                      height("50%");
                      width("50%");
                    }});

                }});
                            }
                        }); // panel added
                    }
                });

            }
        }.build(nifty));
        // </screen>

        nifty.gotoScreen("start"); // start the screen
    }

    @Override

    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    /**
     * Changes the Screen to Screen with ID screenID
     *
     * @param screenID the screenID
     */
    public void gotoScreen(String screenID) {
        nifty.gotoScreen(screenID);
    }
}
