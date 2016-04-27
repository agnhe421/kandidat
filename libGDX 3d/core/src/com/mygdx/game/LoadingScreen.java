package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;


/**
 * Created by sofiekhullar on 16-03-02.
 */
public class

        LoadingScreen implements Screen {
    // App reference
    private final BaseGame app;
    // Progressbar
    private float progress;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    private Stage stage;

    //loading bar
    private Image image;
    private Image loadingBar1, loadingBar2, loadingBar3,
            loadingBar4, loadingBar5, loadingBar6, loadingBar7,
            loadingBar8, loadingBar9, loadingBar10, loadingBar11;
    private Skin skin;

    public LoadingScreen(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight(), app.camera));
    }

    private void queueAsset()
    {

        app.assets.load("img/greek.jpg", Texture.class);
        app.assets.load("img/badlogic.jpg", Texture.class);
        app.assets.load("ui/uiskin.atlas", TextureAtlas.class);
        app.assets.load("ui/TextUI.pack", TextureAtlas.class);
        app.assets.load("ui/Buttons.pack", TextureAtlas.class);

    }

    @Override
    public void show() {
        System.out.println("LOADING");
        Gdx.input.setInputProcessor(stage);


        //ladda in loadingBar bilder som ligger i en atlas fil
        app.assets.load("ui/loading.pack", TextureAtlas.class);
        app.assets.finishLoading(); //vänta tills loading.pack har laddats in innnan den fortsätter

        //ladda in loadingBar som skin
        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/loading.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/loading.json"));


        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/greek.jpg"))));
        background.setPosition(0, 0);
        background.setSize((stage.getWidth()), stage.getHeight());
        stage.addActor(background);


        //tilldela alla bilder i atlas filen till enskilda filer
        loadingBar1 = new Image(skin, "bar_1");
        loadingBar2 = new Image(skin, "bar_2");
        loadingBar3 = new Image(skin, "bar_3");
        loadingBar4 = new Image(skin, "bar_4");
        loadingBar5 = new Image(skin, "bar_5");
        loadingBar6 = new Image(skin, "bar_6");
        loadingBar7 = new Image(skin, "bar_7");
        loadingBar8 = new Image(skin, "bar_8");
        loadingBar9 = new Image(skin, "bar_9");
        loadingBar10 = new Image(skin, "bar_10");
        loadingBar11 = new Image(skin, "bar_11");

        queueAsset();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();

        app.batch.begin();
        app.font40.draw(app.batch, "Screen: LOADING", 30, 30);
        app.batch.end();

        //läsa in funktionen selectLoadingBarPicture när assets har laddats i 10%
        //och sedan rita ut det
        if(app.assets.getProgress() >= 0.1f){
            image = selectLoadingBarPicture();
            image.setPosition(w / 28, h / 10); //random värden, bättre sätt?
            image.setSize(w/2,h/12);   //skala så det passar på skärmen, finns bättre sätt?
            stage.addActor(image);
        }
    }

    private void update(float delta){
        progress = MathUtils.lerp(progress, app.assets.getProgress(),0.1f);
        if(app.assets.update() && progress >= app.assets.getProgress() - 0.01f) // retunerar false tills alla assets är inladdade
        {
            app.setScreen(app.mainMenyScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Image selectLoadingBarPicture(){

        //ladda upp bild 1 i loadingbar när det har gått 10%, bild2 20% osv
        if(app.assets.getProgress() >= 0.1f && app.assets.getProgress() < 0.2f){
            return loadingBar1;
        }else if(app.assets.getProgress() >= 0.2f && app.assets.getProgress() < 0.3f){
            return loadingBar2;
        }else if(app.assets.getProgress() >= 0.3f && app.assets.getProgress() < 0.4f){
            return loadingBar3;
        }else if(app.assets.getProgress() >= 0.4f && app.assets.getProgress() < 0.5f){
            return loadingBar4;
        }else if(app.assets.getProgress() >= 0.5f && app.assets.getProgress() < 0.6f){
            return loadingBar5;
        }else if(app.assets.getProgress() >= 0.6f && app.assets.getProgress() < 0.7f){
            return loadingBar6;
        }else if(app.assets.getProgress() >= 0.7f && app.assets.getProgress() < 0.8f){
            return loadingBar7;
        }else if(app.assets.getProgress() >= 0.8f && app.assets.getProgress() < 0.9f){
            return loadingBar8;
        }else if(app.assets.getProgress() >= 0.9f && app.assets.getProgress() < 0.95f){
            return loadingBar9;
        }else if(app.assets.getProgress() >= 0.95f && app.assets.getProgress() < 0.98f){
            return loadingBar10;
        }else if(app.assets.getProgress() >= 0.98f ){
            return loadingBar11;}
        else{
            return loadingBar11;
        }
    }
}