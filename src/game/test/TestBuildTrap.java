/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import game.main.appstates.TrapPlaceAppState;
import game.main.appstates.WorldAppState;

/**
 * 
 * @author Robbo13
 */
public class TestBuildTrap extends SimpleApplication {

    private BulletAppState bulletAppState;
    private WorldAppState worldAppState;
    private TrapPlaceAppState trapPlaceAppState;
    
    private boolean terrainInitialized = false;
    
    public static void main(String[] args) {
        TestBuildTrap testBuild = new TestBuildTrap();
        testBuild.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        bulletAppState = new BulletAppState();
        worldAppState = new WorldAppState(bulletAppState);
        stateManager.attach(bulletAppState);
        stateManager.attach(worldAppState);
        trapPlaceAppState = new TrapPlaceAppState(bulletAppState, worldAppState);
        stateManager.attach(trapPlaceAppState);
        //bulletAppState.setDebugEnabled(true);
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
    public void simpleUpdate(float tpf) {
        if(worldAppState.isInitialized() && !terrainInitialized) {
            initTerrain();
            terrainInitialized = true;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
}
