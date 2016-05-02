package com.qualcomm.vuforia.samples.libGDX.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;
import com.qualcomm.vuforia.samples.Vuforia.VuforiaCamera;
import com.qualcomm.vuforia.samples.libGDX.LaunchGame;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

public class ChooseBallScreen extends InputAdapter implements ApplicationListener, GestureDetector.GestureListener, Screen {

    VuforiaCamera cam;
    CameraInputController inputController;
    ModelBatch modelBatch;
    DirectionalShadowLight shadowLight;
    ModelBatch shadowBatch;
    Model model;
    Array<ModelInstance> instances;
    Environment environment;

    int currentBall = 0;
    Array<String> ballNames;

    AssetManager assets;
    boolean loading;

    float rotation = 0;

    private Stage stage;
    Label LabelScore;

    FPSLogger fps;

    BitmapFont fontH1;
    BitmapFont fontH3;
    Image swipe;
    Image accept;

    String choosenBall = "";
    ImageButton readyButon;
    boolean playerReady = false;

    private final LaunchGame app;

    public ChooseBallScreen(final LaunchGame app,Array<String> ballNames){
        this.app = app;
        this.ballNames = ballNames;

        this.assets = PropertiesSingleton.getInstance().getAssets();

        this.create();
    }

    @Override
    public void create () {
        modelBatch = new ModelBatch();

        fps = new FPSLogger();
        Gdx.app.log("height: " + Gdx.graphics.getHeight() + "", "width: " + Gdx.graphics.getWidth() + "");

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f,
                -.2f));
        environment.shadowMap = shadowLight;

        shadowBatch = new ModelBatch(new DepthShaderProvider());

        cam = new VuforiaCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        instances = new Array<ModelInstance>();
        //       instances.add(new ModelInstance(model, "floor"));
//        instances.add(ball = new Ball(model, "ball"));
//        ball.setPosition(1f, 1f, 1f);





        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));

        initFonts();
//        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(fontH1, Color.WHITE);
        Label.LabelStyle labelStyle2 = new Label.LabelStyle(fontH3, Color.WHITE);
        Label labelTitle = new Label("CHOOSE BALL ", labelStyle);
        LabelScore = new Label("apple", labelStyle2);
//        labelTitle.setScale(Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        Gdx.app.log("width: " + Gdx.graphics.getWidth(), "height: " + Gdx.graphics.getHeight());
        labelTitle.setPosition(Gdx.graphics.getHeight() / 2 - labelTitle.getWidth() / 2, Gdx.graphics.getHeight() - labelTitle.getHeight() * 2);

        LabelScore.setPosition(Gdx.graphics.getHeight() - (LabelScore.getWidth() + Gdx.graphics.getHeight() / 50), LabelScore.getHeight() / 2);


        swipe = new Image(new Sprite(new Texture(Gdx.files.internal("swipe2.png"))));
        swipe.addAction(Actions.sequence(Actions.fadeOut(0.00000001f), Actions.delay(2), Actions.fadeIn(1)));
//        swipe.setSize((stage.getWidth()) / 6, stage.getHeight() / 6);
        swipe.setPosition(Gdx.graphics.getHeight() / 2 - swipe.getWidth() / 2, Gdx.graphics.getHeight() / 2 - swipe.getHeight() / 2);
        stage.addActor(swipe);


        accept = new Image(new Sprite(new Texture(Gdx.files.internal("accept.png"))));
//        accept.addAction(Actions.sequence(Actions.fadeOut(0.00000001f), Actions.delay(2), Actions.fadeIn(1)));
        accept.setSize((stage.getWidth()) / 10, stage.getWidth() / 10);
        accept.setPosition(Gdx.graphics.getHeight() / 2 - accept.getWidth() / 2, accept.getHeight());
//        stage.addActor(accept);



        stage.addActor(LabelScore);
        stage.addActor(labelTitle);

        TextureAtlas atlasVote = new TextureAtlas("Buttons.pack");

        Skin voteSkin = new Skin(atlasVote);

        ImageButton.ImageButtonStyle voteButtonStyle = new ImageButton.ImageButtonStyle();  //Instaciate
        voteButtonStyle.up = voteSkin.getDrawable("notready");  //Set image for not pressed button
        voteButtonStyle.checked = voteSkin.getDrawable("ready");  //Set image for not pressed button

        readyButon = new ImageButton(voteButtonStyle);


        readyButon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//
                choosenBall = ballNames.get(currentBall);
                playerReady = true;

                Gdx.app.log("click", "");

                PropertiesSingleton.getInstance().setChoosenBall(choosenBall);

                app.setScreen(new PlayScreen(app));


            }
        });

        readyButon.setSize((stage.getWidth()) / 6, stage.getWidth() / 6);

        readyButon.setPosition(Gdx.graphics.getHeight() / 2 - readyButon.getWidth() / 2, 0);
        stage.addActor(readyButon);

        for(int i = 0; i<ballNames.size; i++)
        {
            Model tmpModel = assets.get("3d/balls/"+ballNames.get(i)+".g3dj", Model.class);

            for(int k = 0; k<tmpModel.meshes.size;k++)
//                tmpModel.meshes.get(k).scale(30,30,30);
//            String id = tmpModel.nodes.get(0).id;
//            Node node = tmpModel.getNode(id);
//            node.scale.set(30f, 30f, 30f);
//            node.translation.set(0, 15f, 0);
//            tmpModel.calculateTransforms();
            instances.add(new ModelInstance(tmpModel));
        }



        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new GestureDetector(0.0f, 0.0f, 0.0f, 5f, this));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render () {


        fps.log();
        Gdx.gl.glClearColor(0, 0, 0, 0f);


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);



        final float delta = Math.min(1f / 10f, Gdx.graphics.getDeltaTime());

        rotation = rotation + 0.5f;

        if (rotation >= 360)
            rotation = 0;

        if(!loading)
            instances.get(currentBall).transform.setToRotation(0, 1, 0, rotation);

        cam.update();

        modelBatch.begin(cam);
        if(!loading)
            modelBatch.render(instances.get(currentBall), environment);
        modelBatch.end();


        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
        if(!loading)
            shadowBatch.render(instances.get(currentBall));
        shadowBatch.end();
        shadowLight.end();

//            Gdx.app.log("current", "" + currentBall);

        stage.act();
        stage.draw();




    }


    @Override
    public void dispose () {
        modelBatch.dispose();
        model.dispose();
        stage.dispose();
    }

    public void resume () {
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        render();

    }

    public void resize (int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    public void pause () {
    }

    @Override
    public boolean touchDown(float v, float v1, int i, int i1) {

        return false;
    }

    @Override
    public boolean tap(float v, float v1, int i, int i1) {
        return false;
    }

    @Override
    public boolean longPress(float v, float v1) {
        return false;
    }

    float translateX = 0;

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        swipe.addAction(Actions.fadeOut(1));

        if(instances != null) {

            if(playerReady == true)
            {
                readyButon.setChecked(false);
                Gdx.app.log("toggle",playerReady + "");
            }


            if (velocityX > 0) {
//            translateX = translateX+20;
//            instances.get(0).transform.setToTranslation(translateX, 1, 0);

//            instances.get(0).nodes.get(0).translation.set(translateX, 0, 0);

                if (currentBall != 0)
                    currentBall--;

                Gdx.app.log("+", currentBall + "");

                CharSequence tmp;
                tmp = ballNames.get(currentBall);
                LabelScore.setText(tmp);


            } else {
//            translateX = translateX-20;
//            instances.get(0).transform.setToTranslation(translateX, 1, 0);
//            instances.get(0).nodes.get(0).translation.set(translateX, 0, 0);


                if (currentBall < (ballNames.size-1))
                    currentBall++;

                Gdx.app.log("-", currentBall + "");

                if (ballNames.get(currentBall) != null)
                {
                    CharSequence tmp;
                    tmp = ballNames.get(currentBall);
                    LabelScore.setText(tmp);
                }

            }

        }
        return true;
    }

    @Override
    public boolean pan(float v, float v1, float v2, float v3) {
        return false;
    }

    @Override
    public boolean panStop(float v, float v1, int i, int i1) {
        return false;
    }

    @Override
    public boolean zoom(float v, float v1) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23) {
        return false;
    }

    // Hur man lägger till egna ttf fonts i Libgdx
    private void initFonts(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/candy.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 20;
        params.color = Color.WHITE;
        fontH1 = generator.generateFont(params);

        params.size = 12;
        params.color = Color.WHITE;
        fontH3 = generator.generateFont(params);
        generator.dispose();
    }
}