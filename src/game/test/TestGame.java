/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import game.entities.CarAppState;
import game.map.WorldAppState;

/**
 *
 * @author Robbo13
 */
public class TestGame extends SimpleApplication implements ActionListener {

    // define triggers
    private static final Trigger TEST_KEY = new KeyTrigger(KeyInput.KEY_O);

    // define mappings
    private static final String MAPPING_TEST_KEY = "Test_Map_1";

    private BulletAppState bulletAppState;
    private CarAppState carAppState;

    private WorldAppState worldAppState;

    private BitmapText informationText;

    public static void main(String[] args) {
        TestGame testGame = new TestGame();
        testGame.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        worldAppState = new WorldAppState(bulletAppState);
        stateManager.attach(bulletAppState);
        stateManager.attach(worldAppState);
        //bulletAppState.setDebugEnabled(true);

        initHUD();
        initInput();

        carAppState = new CarAppState(bulletAppState, new Vector3f(512, 100, 512));
        stateManager.attach(carAppState);

        flyCam.setEnabled(false);
    }

    private void initHUD() {
        /**
         * Write text on the screen (HUD)
         */
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        informationText = new BitmapText(guiFont, false);
        informationText.setSize(guiFont.getCharSet().getRenderedSize());
        informationText.setText("Press [1]: Load Map 1\nPress [2]: Load Map 2\n"
                + "Press [3]: Load Map 3\nPress [0]: Unload Map\n"
                + "Press [SPACE]: Create Mountain");
        informationText.setLocalTranslation(0, settings.getHeight() - informationText.getLineHeight(), 0);
        guiNode.attachChild(informationText);
    }

    private void initInput() {
        inputManager.addMapping(MAPPING_TEST_KEY, TEST_KEY);
        inputManager.addListener(this, MAPPING_TEST_KEY);
    }

    private void initTerrain() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        worldAppState.addLight(ambientLight);

        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        worldAppState.setSky(sky);

        Texture alphaMap = assetManager.loadTexture("Textures/Maps/test-maps/testalphamap2.png");
        Texture heightMap = assetManager.loadTexture("Textures/Maps/test-maps/testheightmap2.png");
        worldAppState.loadTerrain("test-map", alphaMap, heightMap, Vector3f.ZERO, new Vector3f(2f, 0.5f, 2f));

        Texture grass = assetManager.loadTexture("Textures/Tile/Gras.jpg");
        worldAppState.setTexture("test-map", 1, grass, 5.0f);

        Texture dirt = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
        worldAppState.setTexture("test-map", 2, dirt, 5.0f);

        Texture rock = assetManager.loadTexture("Textures/Tile/Road.jpg");
        worldAppState.setTexture("test-map", 3, rock, 5.0f);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (worldAppState.isInitialized() && !worldAppState.isTerrainLoaded()) {
            initTerrain();
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(MAPPING_TEST_KEY) && isPressed) {
            System.out.println("game start");
            RoundThread roundthread = new RoundThread();
            Thread thread = new Thread(roundthread);
            thread.start();
        }
    }

    class RoundThread implements Runnable {

        @Override
        public void run() {
            boolean goalreached = true;
            int time = 0;
            int mult = 1;
            String displaytime = "00:00";
            while (!Thread.interrupted()) {
                if (!goalreached) {
                    time = time + (1 * mult);
                    displaytime = time / 60 + ":" + time % 60;
                    if (time == -1) {
                        System.out.println("Round Over");
                        return;
                    }
                } else if (goalreached) {
                    time = 60;
                    mult = -1;
                    goalreached = false;
                }

                displaytime = time / 60 + ":" + time % 60;
                System.out.println(displaytime);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    return;
                }

            }
        }

    }

}
