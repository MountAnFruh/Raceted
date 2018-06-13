/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;

/**
 * 
 * @author Andreas Fruhwirt
 */
public class TestBuildTrap extends SimpleApplication {

    public static void main(String[] args) {
        TestBuildTrap testBuild = new TestBuildTrap();
        testBuild.start();
    }

    @Override
    public void simpleInitApp() {
        InitTestTrap initTestTrap = new InitTestTrap(this);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
}
