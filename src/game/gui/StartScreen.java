/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Robbo13
 */
public class StartScreen extends AbstractAppState implements ScreenController {

    private AudioNode audioSource;
    private AssetManager asset;
    private SimpleApplication appi;
    private Nifty nifty;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        SimpleApplication appi = (SimpleApplication) app;
        asset = appi.getAssetManager();
        audioSource = new AudioNode(asset, "Sounds/Musics/Main.ogg", AudioData.DataType.Buffer);
        audioSource.play();
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        app.getGuiViewPort().addProcessor(niftyDisplay);
        appi.getFlyByCamera().setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        nifty.addScreen("start", new ScreenBuilder("start") {
            {
                controller(new StartScreen());
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
                                                interactOnClick("System.out.println(\"Hallo\")");
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

                                        control(new ButtonBuilder("QuitButton", "Quit") {
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
        }.build(nifty));

        nifty.addScreen("hud", new ScreenBuilder("hud") {
            {
                controller(new DefaultScreenController());

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
                        childLayoutHorizontal();
                        //backgroundColor("#0000");

                        // panel added
                        panel(new PanelBuilder("panel_left") {
                            {
                                childLayoutVertical();
                                //backgroundColor("#0f08");
                                height("100%");
                                width("80%");
                                // <!-- spacer -->
                            }
                        });

                        panel(new PanelBuilder("panel_right") {
                            {
                                childLayoutVertical();
                                //backgroundColor("#00f8");
                                height("100%");
                                width("20%");

                                panel(new PanelBuilder("panel_top_right1") {
                                    {
                                        childLayoutCenter();
                                        //backgroundColor("#00f8");
                                        height("15%");
                                        width("100%");
                                    }
                                });

                                panel(new PanelBuilder("panel_top_right2") {
                                    {
                                        childLayoutCenter();
                                        //backgroundColor("#44f8");
                                        height("15%");
                                        width("100%");

                                        // add image
                                        image(new ImageBuilder() {
                                            {
                                                //filename("Interface/tutorial/face1.png");
                                                valignCenter();
                                                alignCenter();
                                                height("50%");
                                                width("30%");
                                            }
                                        });

                                    }
                                });

                                panel(new PanelBuilder("panel_bot_right") {
                                    {
                                        childLayoutCenter();
                                        valignCenter();
                                        //backgroundColor("#88f8");
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

        nifty.gotoScreen("start");
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void quitGame() {
        audioSource = new AudioNode(asset, "Sounds/Musics/Rock.ogg", AudioData.DataType.Buffer);
        audioSource.play();
        System.out.println("asdfghjlllkjhgfds");
        nifty.exit();
        appi.stop();
    }

}
