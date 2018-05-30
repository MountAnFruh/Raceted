/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.main.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import java.util.LinkedList;

/**
 *
 * @author Robbo13
 */
public class TrapPlaceAppState extends AbstractAppState implements ActionListener, AnalogListener {

    // define triggers
    private static final Trigger CAMERA_LEFT = new MouseAxisTrigger(MouseInput.AXIS_X, true);
    private static final Trigger CAMERA_RIGHT = new MouseAxisTrigger(MouseInput.AXIS_X, false);
    private static final Trigger CAMERA_UP = new MouseAxisTrigger(MouseInput.AXIS_Y, false);
    private static final Trigger CAMERA_DOWN = new MouseAxisTrigger(MouseInput.AXIS_Y, true);
    private static final Trigger CAMERA_ZOOMIN = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false);
    private static final Trigger CAMERA_ZOOMOUT = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true);
    private static final Trigger PLACE_TRAP = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private static final Trigger CAMERA_DRAG = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private static final Trigger CAMERA_DRAG2 = new KeyTrigger(KeyInput.KEY_LCONTROL);

    private static final Trigger CHOOSE_TRAP1 = new KeyTrigger(KeyInput.KEY_1);
    private static final Trigger CHOOSE_TRAP2 = new KeyTrigger(KeyInput.KEY_2);
    private static final Trigger CHOOSE_TRAP3 = new KeyTrigger(KeyInput.KEY_3);
    private static final Trigger DELETE_TRAP = new KeyTrigger(KeyInput.KEY_DELETE);

    // define mappings
    private static final String MAPPING_CAMERA_LEFT = "Camera_Left";
    private static final String MAPPING_CAMERA_RIGHT = "Camera_Right";
    private static final String MAPPING_CAMERA_DOWN = "Camera_Down";
    private static final String MAPPING_CAMERA_UP = "Camera_Up";
    private static final String MAPPING_CAMERA_DRAG = "Camera_Drag";
    private static final String MAPPING_CAMERA_DRAG2 = "Camera_Drag2";
    private static final String MAPPING_CAMERA_ZOOMIN = "Camera_Zoomin";
    private static final String MAPPING_CAMERA_ZOOMOUT = "Camera_Zoomout";
    private static final String MAPPING_PLACE_TRAP = "Place_Trap";

    private static final String MAPPING_CHOOSE_TRAP1 = "Place_Trap1";
    private static final String MAPPING_CHOOSE_TRAP2 = "Place_Trap2";
    private static final String MAPPING_CHOOSE_TRAP3 = "Place_Trap3";
    private static final String MAPPING_DELETE_TRAP = "Delete_Trap";

    private final BulletAppState bulletAppState;
    private final WorldAppState worldAppState;
    private final Vector2f firstLookSpot;

    protected AssetManager assetManager;
    protected RenderManager renderManager;
    protected InputManager inputManager;
    protected Node rootNode;
    protected FlyByCamera flyCam;
    protected Camera cam;
    protected Node guiNode;

    private int trap = 1;

    private CameraNode camNode;
    private boolean isDragging;

    private Spatial Trap1 = null;
    private Spatial Trap2 = null;
    private Spatial Trap3 = null;
    private Spatial teaGeom = null;
    private LinkedList<Spatial> llplacedtraps = new LinkedList<Spatial>();

    private boolean deletemode = false;

    public TrapPlaceAppState(BulletAppState bulletAppState, WorldAppState worldAppState, Vector2f firstLookSpot) {
        this.bulletAppState = bulletAppState;
        this.worldAppState = worldAppState;
        this.firstLookSpot = firstLookSpot;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        SimpleApplication simpleApp = (SimpleApplication) app;

        this.renderManager = simpleApp.getRenderManager();
        this.assetManager = simpleApp.getAssetManager();
        this.inputManager = simpleApp.getInputManager();
        this.flyCam = simpleApp.getFlyByCamera();
        this.cam = simpleApp.getCamera();
        this.guiNode = simpleApp.getGuiNode();
        this.rootNode = simpleApp.getRootNode();

        initInput();

        initCamera();

        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");

        Trap1 = assetManager.loadModel("Models/pylon.obj");
        Trap1.setMaterial(mat);
        Trap1.setCullHint(Geometry.CullHint.Never);
        Trap2 = assetManager.loadModel("Models/Stachel.obj");
        Trap2.setMaterial(mat);
        Trap2.setCullHint(Geometry.CullHint.Never);
        Trap3 = assetManager.loadModel("Models/bushes.obj");
        Trap3.setMaterial(mat);
        Trap3.setCullHint(Geometry.CullHint.Never);
        //mat.setColor("Color", new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        teaGeom = Trap2;
        rootNode.attachChild(teaGeom);
    }

    private void initCamera() {
        camNode = new CameraNode("MainCamera", cam);
        camNode.move(firstLookSpot.x, 100, firstLookSpot.y);
        camNode.lookAt(new Vector3f(firstLookSpot.x, 0, firstLookSpot.y), Vector3f.UNIT_Y);
        rootNode.attachChild(camNode);
        inputManager.setCursorVisible(true);
    }

    private void initInput() {
        inputManager.addMapping(MAPPING_CAMERA_LEFT, CAMERA_LEFT);
        inputManager.addMapping(MAPPING_CAMERA_RIGHT, CAMERA_RIGHT);
        inputManager.addMapping(MAPPING_CAMERA_UP, CAMERA_UP);
        inputManager.addMapping(MAPPING_CAMERA_DOWN, CAMERA_DOWN);
        inputManager.addMapping(MAPPING_CAMERA_DRAG, CAMERA_DRAG);
        inputManager.addMapping(MAPPING_CAMERA_DRAG2, CAMERA_DRAG2);

        inputManager.addMapping(MAPPING_CAMERA_ZOOMIN, CAMERA_ZOOMIN);
        inputManager.addMapping(MAPPING_CAMERA_ZOOMOUT, CAMERA_ZOOMOUT);
        inputManager.addMapping(MAPPING_PLACE_TRAP, PLACE_TRAP);

        inputManager.addMapping(MAPPING_CHOOSE_TRAP1, CHOOSE_TRAP1);
        inputManager.addMapping(MAPPING_CHOOSE_TRAP2, CHOOSE_TRAP2);
        inputManager.addMapping(MAPPING_CHOOSE_TRAP3, CHOOSE_TRAP3);
        inputManager.addMapping(MAPPING_DELETE_TRAP, DELETE_TRAP);

        inputManager.addListener(this, MAPPING_CAMERA_LEFT, MAPPING_CAMERA_RIGHT,
                MAPPING_CAMERA_DOWN, MAPPING_CAMERA_UP, MAPPING_CAMERA_DRAG, MAPPING_CAMERA_DRAG2,
                MAPPING_CAMERA_ZOOMIN, MAPPING_CAMERA_ZOOMOUT, MAPPING_PLACE_TRAP, MAPPING_CHOOSE_TRAP1,
                MAPPING_CHOOSE_TRAP2, MAPPING_CHOOSE_TRAP3, MAPPING_DELETE_TRAP);
    }

    private void moveCamera(float value, boolean sideways) {
        float cameraMoveSpeed = 100.0f;
        Vector3f vel = new Vector3f();
        Vector3f pos = camNode.getLocalTranslation().clone();

        if (sideways) {
            cam.getLeft(vel).setY(0);
        } else {
            cam.getUp(vel).setY(0);
        }
        vel.multLocal(value * cameraMoveSpeed);

        pos.addLocal(vel);

        camNode.setLocalTranslation(pos.getX(), pos.getY(), pos.getZ());
    }

    private void zoomCamera(float value) {
        Vector3f camPosition = camNode.getLocalTranslation();
        camNode.setLocalTranslation(camPosition.add(cam.getDirection().mult(-value * 10.0f)));
    }

    @Override
    public void update(float tpf) {

    }

    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
        cleanupInput();
        cleanupTEA();
        cleanupCamera();
    }

    protected void cleanupInput() {
        inputManager.removeListener(this);
        inputManager.deleteMapping(MAPPING_CAMERA_LEFT);
        inputManager.deleteMapping(MAPPING_CAMERA_RIGHT);
        inputManager.deleteMapping(MAPPING_CAMERA_UP);
        inputManager.deleteMapping(MAPPING_CAMERA_DOWN);
        inputManager.deleteMapping(MAPPING_CAMERA_DRAG);
        inputManager.deleteMapping(MAPPING_CAMERA_DRAG2);

        inputManager.deleteMapping(MAPPING_CAMERA_ZOOMIN);
        inputManager.deleteMapping(MAPPING_CAMERA_ZOOMOUT);
        inputManager.deleteMapping(MAPPING_PLACE_TRAP);

        inputManager.deleteMapping(MAPPING_CHOOSE_TRAP1);
        inputManager.deleteMapping(MAPPING_CHOOSE_TRAP2);
        inputManager.deleteMapping(MAPPING_CHOOSE_TRAP3);
        inputManager.deleteMapping(MAPPING_DELETE_TRAP);
    }

    protected void cleanupTEA() {
        rootNode.detachChild(teaGeom);
    }

    protected void cleanupCamera() {
        camNode.setEnabled(false);
        rootNode.detachChild(camNode);
        inputManager.setCursorVisible(false);
        cam.setLocation(Vector3f.ZERO);
        cam.setRotation(Quaternion.IDENTITY);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(this.isEnabled()) {
            if (name.equals(MAPPING_DELETE_TRAP)) {
                if (deletemode == false) {
                    deletemode = true;
                } else {
                    deletemode = false;
                }
            }
            if (deletemode == true) {
                Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
                Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
                direction.subtractLocal(origin).normalizeLocal();

                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();
                worldAppState.getTerrainNode().collideWith(ray, results);
                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();

                    teaGeom.setLocalTranslation(closest.getContactPoint().add(0, 0, 0));

                    CollisionResults rs = new CollisionResults();
                    for (Spatial sp : llplacedtraps) {
                        // sp.collideWith((Geometry)teaGeom,rs);
                        bulletAppState.getPhysicsSpace().remove(sp.getControl(RigidBodyControl.class));
                        sp.removeFromParent();
    //                  if(rs.size()>0)
    //                  {
    //                      //teaGeom.move(100, 100, 100);
    //                      //sp.removeFromParent();
    //                      //llplacedtraps.remove(sp);
    //                  }       
                    }
                    //----------------
                }
            } else {
                if (name.equals(MAPPING_CAMERA_DRAG) || name.equals(MAPPING_CAMERA_DRAG2)) {
                    if (isPressed) {
                        isDragging = true;
                        inputManager.setCursorVisible(false);
                    } else {
                        isDragging = false;
                        inputManager.setCursorVisible(true);
                    }
                } else if (name.equals(MAPPING_PLACE_TRAP)) {
                    if (isPressed) {
                        Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
                        Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
                        direction.subtractLocal(origin).normalizeLocal();

                        Ray ray = new Ray(origin, direction);
                        CollisionResults results = new CollisionResults();
                        worldAppState.getTerrainNode().collideWith(ray, results);
                        if (results.size() > 0) {
                            Spatial settrap = null;
                            Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
                            //mat.setColor("Color", ColorRGBA.White);
                            //mat.getAdditionalRenderState().setLineWidth(1);
                            //mat.setColor("Diffuse", new ColorRGBA(1, 1, 1, 0.5f));
                            //mat.setBoolean("UseMaterialColors", true);
                            //mat.setColor("m_Color", new ColorRGBA(0, 1, 0, 0.5f));
                            if (teaGeom == Trap1) {
                                settrap = (Spatial) assetManager.loadModel("Models/pylon.obj");
                            }
                            if (teaGeom == Trap2) {
                                settrap = (Spatial) assetManager.loadModel("Models/Stachel.obj");
                            }
                            if (teaGeom == Trap3) {
                                settrap = (Spatial) assetManager.loadModel("Models/bushes.obj");
                            }
                            settrap.setMaterial(mat);
                            settrap.setCullHint(Geometry.CullHint.Never);
                            llplacedtraps.add(settrap);
                            CollisionResult closest = results.getClosestCollision();
                            settrap.setLocalTranslation(closest.getContactPoint().add(0, 0, 0));
                            CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(settrap);
                            RigidBodyControl trapControl = new RigidBodyControl(collShape, 0);
                            settrap.addControl(trapControl);
                            bulletAppState.getPhysicsSpace().add(trapControl);
                            rootNode.attachChild(settrap);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAnalog(String name, float pressed, float tpf) {
        if(this.isEnabled()) {
            if (isDragging) {
                switch (name) {
                    case MAPPING_CAMERA_LEFT:
                        moveCamera(pressed, true);
                        break;
                    case MAPPING_CAMERA_RIGHT:
                        moveCamera(-pressed, true);
                        break;
                    case MAPPING_CAMERA_UP:
                        moveCamera(pressed, false);
                        break;
                    case MAPPING_CAMERA_DOWN:
                        moveCamera(-pressed, false);
                        break;
                    case MAPPING_CAMERA_ZOOMIN:
                        zoomCamera(-pressed);
                        break;
                    case MAPPING_CAMERA_ZOOMOUT:
                        zoomCamera(pressed);
                        break;
                }
            }
            switch (name) {
                case MAPPING_CHOOSE_TRAP1:
                    rootNode.detachChild(teaGeom);
                    teaGeom = Trap1;
                    rootNode.attachChild(teaGeom);
                    break;
                case MAPPING_CHOOSE_TRAP2:
                    rootNode.detachChild(teaGeom);
                    teaGeom = Trap2;
                    rootNode.attachChild(teaGeom);
                    break;
                case MAPPING_CHOOSE_TRAP3:
                    rootNode.detachChild(teaGeom);
                    teaGeom = Trap3;
                    rootNode.attachChild(teaGeom);
                    break;
            }
            if (deletemode == false) {
                Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
                Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
                direction.subtractLocal(origin).normalizeLocal();

                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();
                worldAppState.getTerrainNode().collideWith(ray, results);

                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();
                    teaGeom.setLocalTranslation(closest.getContactPoint().add(0, 0, 0));
                    //----------------
                }
            }
        }
    }

    public String getTrapName() //zeigt ausgewählte falle an
    {
        String str = "";

        if (teaGeom == Trap1) {
            str = "Trap1";
        } else if (teaGeom == Trap2) {
            str = "Trap1";
        } else if (teaGeom == Trap3) {
            str = "Trap3";
        }
        return str;
    }

    public void setTrap(int id) {
        if (id >= 1 && id <= 3) {
            rootNode.detachChild(teaGeom);
            switch (id) {
                case 1:
                    teaGeom = Trap1;
                    break;
                case 2:
                    teaGeom = Trap2;
                    break;
                case 3:
                    teaGeom = Trap3;
                    break;
            }
            rootNode.attachChild(teaGeom);
        }
    }

}
