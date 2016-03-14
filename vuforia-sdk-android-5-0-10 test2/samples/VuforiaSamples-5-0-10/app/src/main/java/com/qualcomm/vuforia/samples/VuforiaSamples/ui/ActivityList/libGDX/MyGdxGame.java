package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.libGDX;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.DataHolder;

import javax.microedition.khronos.opengles.GL10;

public class MyGdxGame extends InputAdapter implements ApplicationListener {
    public static class Ball extends ModelInstance {
        private static Vector3 tmpV = new Vector3();
        private static Quaternion tmpQ = new Quaternion();

        public Vector3 velocity = new Vector3();
        public Vector3 acceleration = new Vector3();
        public Vector3 position = new Vector3();
        public Quaternion rotation = new Quaternion();

        public Ball(Model model, String nodeId) {
            super(model, nodeId, true);
        }

        public void setPosition(float x, float y, float z) {
            transform.setTranslation(position.set(x, y, z));
        }

        private static final float friction = -1.0f;
        public void update(float delta) {
            velocity.add(tmpV.set(velocity).limit(1f).scl(delta*friction)); //simulate some friction

            velocity.add(tmpV.set(acceleration).scl(delta)).limit(5f);
            final float speed = velocity.len();

            if (speed > 0.2f) {
                position.add(tmpV.set(velocity).scl(delta));

                tmpQ.set(tmpV.set(velocity).scl(-1f/speed).crs(Vector3.Y), speed*delta* MathUtils.radiansToDegrees);
                rotation.mulLeft(tmpQ);

                transform.set(position, rotation);
            }
        }
    }

    PerspectiveCamera cam;
    CameraInputController inputController;
    ModelBatch modelBatch;
    DirectionalShadowLight shadowLight;
    ModelBatch shadowBatch;
    Model model;
    Array<ModelInstance> instances;
    Environment environment;
    Ball ball;

    AssetManager assets;
    boolean loading;

    @Override
    public void create () {
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f,
                -.2f));
        environment.shadowMap = shadowLight;

        shadowBatch = new ModelBatch(new DepthShaderProvider());

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 1, 0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "floor";
        modelBuilder.part("floor", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED)))
                .box(100f, 1f, 100f);
        Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        modelBuilder.manage(texture);
        modelBuilder.node().id = "ball";
        modelBuilder.part("ball", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                new Material("diffuseRed", TextureAttribute.createDiffuse(texture)))
                .sphere(1f, 1f, 1f, 10, 10);
        model = modelBuilder.end();

        instances = new Array<ModelInstance>();
        //       instances.add(new ModelInstance(model, "floor"));
//        instances.add(ball = new Ball(model, "ball"));
//        ball.setPosition(1f, 1f, 1f);


        assets = new AssetManager();
        assets.load("island4_main.g3dj", Model.class);
        loading = true;

        Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController = new CameraInputController(cam)));
    }


    private void doneLoading () {
        Model ship = assets.get("island4_main.g3dj", Model.class);
        ship.meshes.get(0).getVertexAttribute(0).Normal();
        String id = ship.nodes.get(0).id;

        ball = new Ball(ship, id);

//        Node node = ball.getNode(id);
//        ball.transform.set(node.globalTransform);
////        node.translation.set(0, 2, 0);
////        node.scale.set(1, 1, 1);
////        node.rotation.idt();
//        ball.calculateTransforms();

        instances.add(ball);
        ball.setPosition(1f, 1.5f, 1f);



        loading = false;

    }


    @Override
    public void render () {
        if (loading && assets.update())
            doneLoading();


        final float delta = Math.min(1f/10f, Gdx.graphics.getDeltaTime());

        if(!loading) {
            ball.update(delta);

        }

        inputController.update();


        Gdx.gl.glClearColor(0, 0, 0, 0f);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        float[] modelViewMatrix = DataHolder.getInstance().getData();

        if(modelViewMatrix != null)
        {

            Matrix4 temp = new Matrix4(modelViewMatrix);

            cam.combined.set(temp);

//
//                Gdx.app.log("---------------------------------------------","------------------------------------------------------------------");
//                Gdx.app.log("aa", "" + modelViewMatrix[0] + "  " + modelViewMatrix[1] + "  " + modelViewMatrix[2] + "  " + modelViewMatrix[3]);
//                Gdx.app.log("aa", "" + modelViewMatrix[4] + "  " + modelViewMatrix[5] + "  " + modelViewMatrix[6] + "  " + modelViewMatrix[7]);
//                Gdx.app.log("a", "" + modelViewMatrix[8] + "  " + modelViewMatrix[9] + "  " + modelViewMatrix[10] + "  " + modelViewMatrix[11]);
//                Gdx.app.log("aa", "" + modelViewMatrix[12] + "  " + modelViewMatrix[13] + "  " + modelViewMatrix[14] + "  " + modelViewMatrix[15]);
//                Gdx.app.log("---------------------------------------------", "------------------------------------------------------------------");

//                cam.view.mul(temp);

        }

        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(instances);
        shadowBatch.end();
        shadowLight.end();

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
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
        ball.acceleration.set((right?1:0)+(left?-1:0), 0f, (up?-1:0)+(down?1:0)).scl(2);

        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        switch(keycode) {
            case Input.Keys.UP: up = false; break;
            case Input.Keys.DOWN: down = false; break;
            case Input.Keys.LEFT: left = false; break;
            case Input.Keys.RIGHT: right = false; break;
            default: return false;
        }
        ball.acceleration.set((right?1:0)+(left?-1:0), 0f, (up?-1:0)+(down?1:0)).scl(2);
        return true;
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        model.dispose();
    }

    public void resume () {
    }

    public void resize (int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    public void pause () {
    }
}