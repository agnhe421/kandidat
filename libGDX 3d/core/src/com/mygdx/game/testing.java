package com.mygdx.game;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class testing extends BaseBulletTest {

    test.Ball ball;
    AssetManager assets;
    boolean loading;

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


        Gdx.input.setInputProcessor(this);



    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        shoot(screenX, screenY);
        Gdx.app.log("SHOOT", "SHOOT");

        if (assets.update() && loading) {
            Model ship = assets.get("football2.g3dj", Model.class);
//            String id = ship.nodes.get(0).id;
//
//            ball = new test.Ball(ship, id);
//
//            Node node = ball.getNode(id);
//            ball.transform.set(node.globalTransform);
////        node.translation.set(0, 2, 0);
//            node.scale.set(0.1f, 0.1f, 0.1f);
////        node.rotation.idt();
//            ball.calculateTransforms();

            disposables.add(ship);
            world.addConstructor("ball", new BulletConstructor(ship, 1f, new btSphereShape(0.8f)));
            world.add("ball", 0, 0.5f, 0.5f);
            Gdx.app.log("Loaded", "LOADED");
            loading = false;
        }
        return true;
    }


    }