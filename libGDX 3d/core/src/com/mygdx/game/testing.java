package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class testing extends BaseBulletTest {

    test.Ball ball;
    AssetManager assets;
    boolean loading;
    BulletEntity player;

    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

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
        final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;

        final Model sphere = modelBuilder.createSphere(4f, 4f, 4f, 24, 24, material, attributes);
        disposables.add(sphere);
        world.addConstructor("sphere", new BulletConstructor(sphere, 10f, new btSphereShape(2f)));


        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);
        world.add("sphere", 0, 5, 5);


        assets = new AssetManager();
        assets.load("football2.g3dj", Model.class);
        loading = true;


        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

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


        Ray ray = camera.getPickRay(screenX, screenY);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom); // 50 meters max from the origin

        // Because we reuse the ClosestRayResultCallback, we need reset it's values
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);

        world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

        rayTestCB.getHitPointWorld(tmpV1);


        Gdx.app.log("BANG", "BANG");


        Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) player.body).getCenterOfMassPosition().x), 0, (tmpV1.z - ((btRigidBody) player.body).getCenterOfMassPosition().z));
//
        float normFactor = 3 / vec.len();
        Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
        ((btRigidBody) player.body).applyCentralImpulse(normVec);


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
        Gdx.app.log("SHOOT", "SHOOT");

//        ball.acceleration.set((right?1:0)+(left?-1:0), 0f, (up?-1:0)+(down?1:0)).scl(2);
//        player.transform.translate(ball.position);

        Vector3 tmpV = new Vector3();

//        if(up)
//            tmpV.set(2,0,0);
//        else if(down)
//            tmpV.set(-2,0,0);
//        else if(right)
//            tmpV.set(0,0,2);
//        else if(left)
//            tmpV.set(0,0,-2);
//
//        ((btRigidBody)player.body).applyCentralImpulse(tmpV);




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
        Gdx.app.log("SHOOT", "SHOOT");

//        ball.acceleration.set((right? 1 : 0)+(left?-1: 0), 0f, (up ? -1 : 0) + (down?1:0)).scl(2);
//        player.transform.translate(ball.position);

        Vector3 tmpV = new Vector3();

        if(up)
            tmpV.set(2,0,0);
        else if(down)
            tmpV.set(-2,0,0);
        else if(right)
            tmpV.set(0,0,2);
        else if(left)
            tmpV.set(0,0,-2);

        ((btRigidBody)player.body).applyCentralImpulse(tmpV);


        return true;
    }

    @Override
    public void render () {
        super.render();


        if (assets.update() && loading) {
            Model ship = assets.get("football2.g3dj", Model.class);
            String id = ship.nodes.get(0).id;

//            ball = new test.Ball(ship, id);

//            Node node = ball.getNode(id);
//            ball.transform.set(node.globalTransform);
////        node.translation.set(0, 2, 0);
//            node.scale.set(0.1f, 0.1f, 0.1f);
////        node.rotation.idt();
//            ball.calculateTransforms();

            disposables.add(ship);
            world.addConstructor("ball", new BulletConstructor(ship, 1f, new btSphereShape(0.8f)));
            player = world.add("ball", 0, 0.5f, 0.5f);



            Gdx.app.log("Loaded", "LOADED");
            loading = false;
        }
    }


    @Override
    public void dispose () {
        if (rayTestCB != null) rayTestCB.dispose();
        rayTestCB = null;
        super.dispose();
    }



    }