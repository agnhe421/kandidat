package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.libGDX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;


public class testing extends BaseBulletTest {

    AssetManager assets;
    boolean loading;
    BulletEntity player;


    ModelInstance instance;

    //2x 14 utan jordnötter
    //1x 16
    //1x 29 extra stark
    //1x 11 med biff

    @Override
    public void create () {
        super.create();

        final Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        disposables.add(texture);
        final Material material = new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createSpecular(1, 1, 1, 1),
                FloatAttribute.createShininess(8f));
        final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        final Model sphere = modelBuilder.createSphere(4f, 4f, 4f, 24, 24, material, attributes);
        disposables.add(sphere);
        world.addConstructor("sphere", new BulletConstructor(sphere, 10f, new btSphereShape(2f)));

        // Create the entities
//        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
//                0.25f + 0.5f * (float) Math.random(), 1f);
        world.add("sphere", 0, 5, 5);



        assets = new AssetManager();
        assets.load("football2.g3dj", Model.class);
        assets.load("ship.g3db", Model.class);
        loading = true;

        Gdx.input.setInputProcessor(this);



    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        shoot(screenX, screenY);
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

            Model model;
            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part("ball", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                    new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED)))
                    .sphere(1f, 1f, 1f, 10, 10);


            model = modelBuilder.end();

            instance = new ModelInstance(model,tmpV1);



            Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) player.body).getCenterOfMassPosition().x), 0, (tmpV1.z - ((btRigidBody) player.body).getCenterOfMassPosition().z));

            float normFactor = 10 / vec.len();
            Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
            player.body.activate();
            ((btRigidBody) player.body).applyCentralImpulse(normVec);


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

        if(instance != null) {
            modelBatch.begin(camera);
            modelBatch.render(instance);
            modelBatch.end();
        }


        if (assets.update() && loading) {
            Model ship = assets.get("football2.g3dj", Model.class);
            ship.meshes.get(0).scale(5,5,5);

//            ball = new test.Ball(ship, id);

//            Node node = ball.getNode(id);
//            ball.transform.set(node.globalTransform);
////        node.translation.set(0, 2, 0);
//            node.scale.set(0.1f, 0.1f, 0.1f);
////        node.rotation.idt();
//            ball.calculateTransforms();

            disposables.add(ship);
            world.addConstructor("ball", new BulletConstructor(ship, 1f, new btSphereShape(5f)));
            player = world.add("ball", 0, 10f, 0f);


            final Model island = assets.get("ship.g3db", Model.class);

            island.meshes.get(0).scale(300f,100f,300f);

            disposables.add(island);
            world.addConstructor("island", new BulletConstructor(island, 0f, createConvexHullShape(island, false)));
            world.add("island",0,0,0);


            Gdx.app.log("Loaded", "LOADED");
            loading = false;
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
    public void dispose () {
//        if (rayTestCB != null) rayTestCB.dispose();
//        rayTestCB = null;
        super.dispose();
    }



}