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
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
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
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.registerMusic("mysound", "Sounds/Musics/Main.mp3");
        // <screen>
        nifty.addScreen(MAINMENUID, new ScreenBuilder("Hello Nifty Screen") {
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
        nifty.gotoScreen(MAINMENUID);
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
     * @param screenID the screenID
     */
    public void gotoScreen(String screenID) {
        nifty.gotoScreen(screenID);
    }
}
