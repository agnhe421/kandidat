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
import com.badlogic.gdx.graphics.Pixmap;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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


import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.qualcomm.vuforia.samples.Network.CreateServer;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;
import com.qualcomm.vuforia.samples.Vuforia.VuforiaCamera;
import com.qualcomm.vuforia.samples.libGDX.LaunchGame;

public class ChooseIslandScreen extends InputAdapter implements ApplicationListener, GestureDetector.GestureListener, Screen {

    VuforiaCamera cam;
    CameraInputController inputController;
    ModelBatch modelBatch;
    DirectionalShadowLight shadowLight;
    ModelBatch shadowBatch;
    Model model;
    Array<ModelInstance> instances;
    Environment environment;

    int currentIsland = 0;
    Array<String> islandNames;
    Array<String> ballNames;

    AssetManager assets;

    float rotation = 0;

    private Stage stage;
    Label LabelScore;

    FPSLogger fps;

    BitmapFont fontH1;
    BitmapFont fontH3;
    Image swipe;
    Image accept;

    String voted;
    Array<ImageButton> voteButtons;

    private final BaseGame app;

    public ChooseIslandScreen(final BaseGame app){
        this.app = app;
        this.islandNames = app.islandNames;
        this.ballNames = app.ballNames;

        this.assets = PropertiesSingleton.getInstance().getAssets();

        Gdx.app.log("CHOOSE ISLANNNNDDDDDDD","CHOOOOOSSEEEE ISLAAAAND");
        this.create();
    }

    @Override
    public void create () {
        if(app.createServerScreen.create != null)
        {
            app.createServerScreen.create.resetUserChoiceState();
            app.createServerScreen.create.startIslandVote();
        }

        modelBatch = new ModelBatch();

        fps = new FPSLogger();
        Gdx.app.log("height: " + Gdx.graphics.getHeight() + "", "width: " + Gdx.graphics.getWidth() + "");

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
//        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
//        environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f,
//                -.2f));
//        environment.shadowMap = shadowLight;

        shadowBatch = new ModelBatch(new DepthShaderProvider());

        cam = new VuforiaCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        instances = new Array<ModelInstance>();
        voteButtons = new Array<ImageButton>();


        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));

        initFonts();
//        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(fontH1, Color.WHITE);
        Label.LabelStyle labelStyle2 = new Label.LabelStyle(fontH3, Color.WHITE);
        Label labelTitle = new Label("CHOOSE ISLAND ", labelStyle);
        LabelScore = new Label("forest", labelStyle2);
//        labelTitle.setScale(Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        Gdx.app.log("width: " + Gdx.graphics.getWidth(), "height: " + Gdx.graphics.getHeight());
        labelTitle.setPosition(Gdx.graphics.getHeight() / 2 - labelTitle.getWidth() / 2, Gdx.graphics.getHeight() - labelTitle.getHeight() * 2);

        LabelScore.setPosition(Gdx.graphics.getHeight()-(LabelScore.getWidth()+Gdx.graphics.getHeight()/50), LabelScore.getHeight()/2);


        swipe = new Image(new Sprite(new Texture(Gdx.files.internal("swipe2.png"))));
        swipe.addAction(Actions.sequence(Actions.fadeOut(0.00000001f), Actions.delay(2), Actions.fadeIn(1)));
//        swipe.setSize((stage.getWidth()) / 6, stage.getHeight() / 6);
        swipe.setPosition(Gdx.graphics.getHeight() / 2 - swipe.getWidth() / 2, Gdx.graphics.getHeight() / 2 - swipe.getHeight() / 2);
        stage.addActor(swipe);


        accept = new Image(new Sprite(new Texture(Gdx.files.internal("accept.png"))));
        accept.setSize((stage.getWidth()) / 10, stage.getWidth() / 10);
        accept.setPosition(Gdx.graphics.getHeight() / 2 - accept.getWidth() / 2, accept.getHeight());




        stage.addActor(LabelScore);
        stage.addActor(labelTitle);

        TextureAtlas atlasVote = new TextureAtlas("Buttons.pack");

        Skin voteSkin = new Skin(atlasVote);

        ImageButton.ImageButtonStyle voteButtonStyle = new ImageButton.ImageButtonStyle();  //Instaciate
        voteButtonStyle.up = voteSkin.getDrawable("notvoted");  //Set image for not pressed button
        voteButtonStyle.imageChecked = voteSkin.getDrawable("voted");  //Set image for pressed


        for(int i = 0; i<islandNames.size; i++)
        {
            Model tmpModel = assets.get("3d/islands/"+islandNames.get(i)+".g3db", Model.class);
//            tmpModel.meshes.get(0).scale(3,3,3);
//            Texture texture2 = new Texture(Gdx.files.internal("3d/islands/"+islandNames.get(i)+".jpg"), Pixmap.Format.RGB888, false);
//            TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, texture2);
//            Material material = tmpModel.materials.get(0);
//            material.set(textureAttribute);
            instances.add(new ModelInstance(tmpModel));
        }

        int numberOfIslands = islandNames.size;
        for(int i = 0; i < numberOfIslands; i++) {
            ImageButton item1Button = new ImageButton(voteButtonStyle);
            item1Button.setSize((stage.getWidth()) / 10, stage.getHeight() / 10);

//        Gdx.input.setInputProcessor(stage);

            item1Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    voted = islandNames.get(currentIsland);
                    if(app.joinServerScreen.join != null)
                    {
                        app.joinServerScreen.join.sendIslandChoice(voted);
                    }
                    else if (app.createServerScreen != null)
                    {
                        app.createServerScreen.create.serverUser.setIslandChoice(voted);
                        app.createServerScreen.create.serverUser.setChosen(true);
                        app.createServerScreen.create.notifyIsland();
                    }
                    //boolean found = false;
                    /*for(int k = 0; k < voted.size; k++)
                    {
                        if(voted.get(k) == islandNames.get(currentIsland))
                            found = true;
                    }

                    if(!found)
                        voted.add(islandNames.get(currentIsland));

                    Gdx.app.log("voted", voted + "");

                    PropertiesSingleton.getInstance().setChoosenIsland(islandNames.get(currentIsland));*/

                    //app.setScreen(new ChooseBallScreen(app));
                }
            });

            item1Button.setPosition(0,-item1Button.getHeight());
            voteButtons.add(item1Button);
            stage.addActor(voteButtons.get(i));
        }
        voteButtons.get(currentIsland).setPosition(Gdx.graphics.getHeight() / 2 - accept.getWidth() / 2, accept.getHeight());


        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new GestureDetector(0.0f, 0.0f, 0.0f, 5f, this));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }






    @Override
    public void render () {

        if(app.joinServerScreen.join != null)
        {
            if(!app.joinServerScreen.join.isAlive())
            {
                app.joinServerScreen.join = null;
                app.mainMenyScreen = new MainMenyScreen(app);
                app.setScreen(app.mainMenyScreen);
            }
            if(app.joinServerScreen.join.getIslandChosenState())
                app.setScreen(new ChooseBallScreen(app));
        }
        if(app.createServerScreen.create != null)
            if(app.createServerScreen.create.checkIslandChosen() && app.createServerScreen.create.getSwitchScreen())
                app.setScreen(new ChooseBallScreen(app));

        fps.log();
        Gdx.gl.glClearColor(0, 0, 0, 0f);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        final float delta = Math.min(1f / 10f, Gdx.graphics.getDeltaTime());

        rotation = rotation + 0.5f;

        if (rotation >= 360)
            rotation = 0;

        instances.get(currentIsland).transform.setToRotation(0, 1, 0, rotation);

        cam.update();

        modelBatch.begin(cam);

        modelBatch.render(instances.get(currentIsland), environment);
        modelBatch.end();


//                shadowLight.begin(Vector3.Zero, cam.direction);
//                shadowBatch.begin(shadowLight.getCamera());
//                if(!loading)
//                shadowBatch.render(instances.get(currentIsland));
//                shadowBatch.end();
//                shadowLight.end();

//            Gdx.app.log("current", "" + currentIsland);

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
//        create();

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

            voteButtons.get(currentIsland).setPosition(0, -voteButtons.get(currentIsland).getHeight());

            if (velocityX > 0) {
//            translateX = translateX+20;
//            instances.get(0).transform.setToTranslation(translateX, 1, 0);

//            instances.get(0).nodes.get(0).translation.set(translateX, 0, 0);

                if (currentIsland != 0)
                    currentIsland--;

                Gdx.app.log("+", currentIsland + "");

                CharSequence tmp;
                tmp = islandNames.get(currentIsland);
                LabelScore.setText(tmp);


            } else {
//            translateX = translateX-20;
//            instances.get(0).transform.setToTranslation(translateX, 1, 0);
//            instances.get(0).nodes.get(0).translation.set(translateX, 0, 0);


                if (currentIsland < (islandNames.size-1))
                    currentIsland++;

                Gdx.app.log("-", currentIsland + "");

                CharSequence tmp;
                tmp = islandNames.get(currentIsland);
                LabelScore.setText(tmp);

            }
            voteButtons.get(currentIsland).setPosition(Gdx.graphics.getHeight() / 2 - accept.getWidth() / 2, accept.getHeight());

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