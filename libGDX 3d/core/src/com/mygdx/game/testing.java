package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class testing extends BaseBulletTest {

    AssetManager assets;
    boolean loading;
    BulletEntity player, player2;

    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

    ModelInstance instance;

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


       /* final Model sphere2 = modelBuilder.createSphere(4f, 4f, 4f, 24, 24, material, attributes);
        disposables.add(sphere2);
        world.addConstructor("sphere2", new BulletConstructor(sphere2, 10f, new btSphereShape(2f)));
        // Create the entities
        world.add("sphere2", 0, 5, 5);
*/
        // Load texture
        assets = new AssetManager();
        assets.load("football2.g3dj", Model.class);
        assets.load("apple.g3dj", Model.class);
        loading = true;

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        Gdx.input.setInputProcessor(this);

    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        shoot(x, y);
        Gdx.app.log("TAP", "Tap");
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
       // shoot(screenX, screenY);
       // Gdx.app.log("SHOOT", "SHOOT");

        Ray ray = camera.getPickRay(screenX, screenY);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom); // 50 meters max from the origin

        // Because we reuse the ClosestRayResultCallback, we need reset it's values
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
            modelBuilder.part("ball", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                    new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED)))
                    .sphere(1f, 1f, 1f, 10, 10);
            model = modelBuilder.end();

            instance = new ModelInstance(model,tmpV1);

            Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) player.body).getCenterOfMassPosition().x), 0, (tmpV1.z - ((btRigidBody) player.body).getCenterOfMassPosition().z));

            float normFactor = 3 / vec.len();
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
            Model fotball = assets.get("football2.g3dj", Model.class);
            String id = fotball.nodes.get(0).id;

            Model apple = assets.get("apple.g3dj", Model.class);
            String id2 = apple.nodes.get(0).id;
            Node node = apple.getNode(id2);
            //apple.transform.set(node.globalTransform);
            //node.translation.set(0, 2, 0);
            node.scale.set(0.5f, 0.5f, 0.5f);

//            ball = new test.Ball(ship, id);
//            Node node = ball.getNode(id);
//            ball.transform.set(node.globalTransform);
////        node.translation.set(0, 2, 0);
//            node.scale.set(0.1f, 0.1f, 0.1f);
////        node.rotation.idt();
//            ball.calculateTransforms();

            disposables.add(fotball);
            world.addConstructor("ball", new BulletConstructor(fotball, 1f, new btSphereShape(0.8f)));
            player = world.add("ball", 0, 0.5f, 0.5f);

            disposables.add(apple);
            world.addConstructor("apple", new BulletConstructor(apple, 1f, new btSphereShape(0.5f)));
            player2 = world.add("apple", 0, 0.5f, 0.5f);

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