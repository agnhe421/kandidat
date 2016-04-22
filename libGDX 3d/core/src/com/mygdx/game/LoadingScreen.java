package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;


/**
 * Created by sofiekhullar on 16-03-02.
 */
public class LoadingScreen implements Screen {
    // App reference
    private final BaseGame app;
    // Progressbar
    private ShapeRenderer shapeRenderer;
    private float progress;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    private Stage stage;

    public LoadingScreen(final BaseGame app)
    {
        this.app = app;
        this.shapeRenderer = new ShapeRenderer();
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

        shapeRenderer.setProjectionMatrix(app.camera.combined); // viktigt för att shaprenderer ska ha relativ storlek

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/greek.jpg"))));
        background.setPosition(0, 0);
        background.setSize((stage.getWidth()), stage.getHeight());
        stage.addActor(background);

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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(32, h / 6, w - 72, 70);

        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(32, h / 6, progress * (w - 72), 70);
        shapeRenderer.end();
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
        shapeRenderer.dispose();
        stage.dispose();
    }
}
