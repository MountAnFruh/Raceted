/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import game.main.appstates.TrapPlaceAppState;
import game.main.appstates.WorldAppState;
import java.util.LinkedList;

/**
 *
 * @author rober
 */
public class InitTestTrap extends AbstractInit {

    private WorldAppState worldAppState;
    private TrapPlaceAppState trapPlaceAppState;
    
    private boolean terrainInitialized = false;
    
    private Camera cam;
    private InputManager inputManager;

    public InitTestTrap(SimpleApplication app) {
        super(app);
        cam = app.getCamera();
        inputManager = app.getInputManager();

        flyCam.setEnabled(false);
        worldAppState = new WorldAppState(bulletAppState);
        stateManager.attach(worldAppState);
        trapPlaceAppState = new TrapPlaceAppState(bulletAppState, worldAppState, new Vector2f(0,0));
        stateManager.attach(trapPlaceAppState);
    }

    private void initTerrain() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        worldAppState.addLight(ambientLight);
        
        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        worldAppState.setSky(sky);
        
        Texture alphaMap, heightMap;
        Texture[] textures = new Texture[3];
        float[] scales = { 64f, 64f, 64f };
        alphaMap = assetManager.loadTexture("Textures/Maps/test-maps/testalphamap1.png");
        heightMap = assetManager.loadTexture("Textures/Maps/test-maps/testheightmap1.png");
        textures[0] = assetManager.loadTexture("Textures/Tile/Road.jpg");
        textures[1] = assetManager.loadTexture("Textures/Tile/Dirt.jpg");
        textures[2] = assetManager.loadTexture("Textures/Tile/Gras.jpg");
        String name = "first_map";
        worldAppState.loadTerrain(name, alphaMap, heightMap, null, Vector3f.ZERO, new Vector3f(1.2f,0.1f,1.2f));
        for(int i = 1;i <= 3;i++) {
            worldAppState.setTexture(name,i,textures[i-1],scales[i-1]);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(worldAppState.isInitialized() && !terrainInitialized) {
            initTerrain();
            terrainInitialized = true;
        }
    }

    @Override
    public void close() {
        stateManager.detach(worldAppState);
    }

    public TrapPlaceAppState getTrapPlaceAppState() {
        return trapPlaceAppState;
    }
    
}
