/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import game.test.DMGArt;

/**
 *
 * @author Robbo13
 */
public class RockAppState extends CharacterAppState {

    private RigidBodyControl rockControl;

    private static final float SPIKE_DMG_REDUCE = 3f;

    public RockAppState(BulletAppState bulletAppState, int maxHP, Vector3f spawnPoint, Node terrainNode) {
        super(bulletAppState, maxHP, spawnPoint, terrainNode);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    @Override
    protected void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Textures/Tile/Stone.jpg");
        mat.setTexture("ColorMap", texture);

        Sphere sphere = new Sphere(20, 20, 3);
        sphere.setBound(new BoundingSphere());
        sphere.updateBound();
        geometry = new Geometry("Rocksphere", sphere);
        geometry.setMaterial(mat);

        CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(geometry);

        geometry.setLocalTranslation(spawnPoint);
        rockControl = new RigidBodyControl(collShape);
        geometry.addControl(rockControl);

        bulletAppState.getPhysicsSpace().add(rockControl);
        rootNode.attachChild(geometry);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        float divisor = 150;
        Vector3f speedVector = rockControl.getLinearVelocity();
        Vector3f lookVector = new Vector3f(0, 0, 0);
        if (left) {
            lookVector.addLocal(cam.getLeft());                 /////////// LEFT  
        }
        if (right) {
            lookVector.addLocal(cam.getLeft().negate());        /////////// RIGHT
        }
        if (forward) {
            lookVector.addLocal(cam.getDirection());            /////////// UP
        }
        if (backward) {
            lookVector.addLocal(cam.getDirection().negate());   /////////// DOWN 
        }
        lookVector = lookVector.normalize();
        rockControl.applyImpulse(lookVector.divide(divisor).setY(0), new Vector3f(0, 2, 0));
        if (jump) {
            if (onGround && jumpCooldown <= 0) {
                rockControl.applyImpulse(rockControl.getGravity().negate().divide(2), Vector3f.ZERO);
                jumpCooldown = DEFAULT_JUMP_COOLDOWN;
            }
        }
    }

    @Override
    protected void cleanupPlayer() {
        bulletAppState.getPhysicsSpace().remove(rockControl);
        super.cleanupPlayer();
    }

    public RigidBodyControl getControl() {
        return rockControl;
    }

    @Override
    public void causeDmg(int dmg, DMGArt art) {
        switch (art) {
            case SPIKE:
                super.causeDmg((int)(dmg / SPIKE_DMG_REDUCE));
                break;
            default:
                super.causeDmg(dmg);
                break;
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name, isPressed, tpf);
        if (name.equals(MAPPING_RESET)) {
            if (isPressed) {
                rockControl.setPhysicsLocation(spawnPoint);
                rockControl.setPhysicsRotation(new Matrix3f());
                rockControl.setLinearVelocity(Vector3f.ZERO);
                rockControl.setAngularVelocity(Vector3f.ZERO);
            }
        }
    }

}
