package com.qualcomm.vuforia.samples.libGDX.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;
import com.qualcomm.vuforia.samples.libGDX.bullet.BaseBulletTest;
import com.qualcomm.vuforia.samples.libGDX.bullet.BulletConstructor;
import com.qualcomm.vuforia.samples.libGDX.bullet.BulletEntity;
import com.qualcomm.vuforia.samples.libGDX.LaunchGame;


public class PlayScreen extends BaseBulletTest implements Screen {

    AssetManager assets;
    boolean loading;
    BulletEntity player;

    boolean removed = false;


    ModelInstance instance;
    ModelInstance IslandModelinstance;

    Model arrowInstance;

    private AnimationController controller;

    //2x 14 utan jordnötter
    //1x 16
    //1x 29 extra stark
    //1x 11 med biff


    private final LaunchGame app;

    String choosenIsland;
    String choosenBall;

    public PlayScreen(final LaunchGame app){
        this.app = app;
        this.assets = PropertiesSingleton.getInstance().getAssets();

        this.choosenIsland = PropertiesSingleton.getInstance().getChoosenIsland();
        this.choosenBall = PropertiesSingleton.getInstance().getChoosenBall();
        this.create();
    }

    @Override
    public void create () {
        super.create();


        Model ship = assets.get("3d/balls/"+choosenBall+".g3dj", Model.class);
//        ship.meshes.get(0).scale(0.05f, 0.05f, 0.05f);


        disposables.add(ship);
        world.addConstructor("ball", new BulletConstructor(ship, 1024, new btSphereShape(7f)));
        player = world.add("ball", 0, 200f, 0f);

        player.body.setRollingFriction(1000);


        final Model island = assets.get("3d/islands/"+choosenIsland+".g3db", Model.class);
        disposables.add(island);
        final BulletConstructor sceneConstructor = new BulletConstructor(island, 0f, new btBvhTriangleMeshShape(
                island.meshParts));
        sceneConstructor.bodyInfo.setRestitution(0.25f);
        world.addConstructor("island", sceneConstructor);
        world.add("island", 0, 0, 0);


        arrowInstance = assets.get("3d/misc/"+"arrow"+".g3db", Model.class);
        arrowInstance.meshes.get(0).scale(0.1f, 0.1f, 0.1f);



        Gdx.input.setInputProcessor(this);

    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if(removed == true)
        {
            shoot(screenX, screenY);
        }
        else {


            Gdx.app.log("SHOOT", "SHOOT");


//        camera.combined.getTranslation(tmpV2);
//
//        camera.position.set(tmpV2);
//        camera.update();


//        camera.combined.getPickRay ?????????????????????????
            Ray ray = camera.getPickRay(screenX, screenY);

            Vector3 rayFrom = new Vector3();
            Vector3 rayTo = new Vector3();

            rayFrom.set(ray.origin);
            rayTo.set(ray.direction).scl(1000f).add(rayFrom); // 50 meters max from the origin

            // Because we reuse the ClosestRayResultCallback, we need reset it's values

            ClosestRayResultCallback rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
            rayTestCB.setCollisionObject(null);
            rayTestCB.setClosestHitFraction(1f);
            rayTestCB.setRayFromWorld(rayFrom);
            rayTestCB.setRayToWorld(rayTo);

            world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);


            if (rayTestCB.hasHit()) {

                rayTestCB.getHitPointWorld(tmpV1);

                Gdx.app.log("BANG", "BANG");

//                Model model;
//                ModelBuilder modelBuilder = new ModelBuilder();
//                modelBuilder.begin();
//                modelBuilder.part("ball", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
//                        new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED)))
//                        .sphere(1f, 1f, 1f, 10, 10);
//                model = modelBuilder.end();

                instance = new ModelInstance(arrowInstance, tmpV1);
                // You use an AnimationController to um, control animations.  Each control is tied to the model instance
                controller = new AnimationController(instance);

                // Pick the current animation by name
                controller.setAnimation("Cube|CubeAction", -1);


                Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) player.body).getCenterOfMassPosition().x), 0, (tmpV1.z - ((btRigidBody) player.body).getCenterOfMassPosition().z));

                float normFactor = 5000 / vec.len();
                Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
                player.body.activate();
                ((btRigidBody) player.body).applyCentralImpulse(normVec);


            }

        }
        return true;
    }

    boolean up, down, left, right;
    @Override
    public boolean keyDown (int keycode) {
        switch(keycode) {
            case Input.Keys.UP: up = true; break;
            case Input.Keys.DOWN: down = true; break;
            case Input.Keys.LEFT: left = true; break;
            case Input.Keys.RIGHT: right = true; break;
            default: return false;
        }
        Gdx.app.log("RAY pick", "RAY pick");


        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        super.keyUp(keycode);
        switch(keycode) {
            case Input.Keys.UP: up = false; break;
            case Input.Keys.DOWN: down = false; break;
            case Input.Keys.LEFT: left = false; break;
            case Input.Keys.RIGHT: right = false; break;
            default: return false;
        }

        Gdx.app.log("RAY pick", "RAY pick");

//        ball.acceleration.set((right? 1 : 0)+(left?-1: 0), 0f, (up ? -1 : 0) + (down?1:0)).scl(2);
//        player.transform.translate(ball.position);


        return true;
    }

    @Override
    public void render () {
        super.render();

        if(instance != null && removed == false) {
            modelBatch.begin(camera);
            modelBatch.render(instance);
//            modelBatch.render(IslandModelinstance);
            modelBatch.end();

            controller.update(Gdx.graphics.getDeltaTime());
        }


        if((removed == false))
        if( ((btRigidBody) player.body).getCenterOfMassPosition().y < 0)
        {
//            world.entities.removeIndex(1);
//            removed = true;
        }
    }

    public static btConvexHullShape createConvexHullShape (final Model model, boolean optimize) {
        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
        if (!optimize) return shape;
        // now optimize the shape
        final btShapeHull hull = new btShapeHull(shape);
        hull.buildHull(shape.getMargin());
        final btConvexHullShape result = new btConvexHullShape(hull);
        // delete the temporary shape
        shape.dispose();
        hull.dispose();
        return result;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
    render();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
//        if (rayTestCB != null) rayTestCB.dispose();
//        rayTestCB = null;
        super.dispose();
    }



}