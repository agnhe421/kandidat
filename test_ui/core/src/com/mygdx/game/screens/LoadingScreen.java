package com.mygdx.game.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.MyGdxGame;

import java.util.Map;

import javafx.application.Application;

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class LoadingScreen implements Screen {

    private final MyGdxGame app;
    private ShapeRenderer shapeRenderer;
    private float progress;
    private Image splashImg;

    public LoadingScreen(final MyGdxGame app)
    {
        this.app = app;
        this.shapeRenderer = new ShapeRenderer();
    }

    private void queueAsset()
    {
        app.assets.load("badlogic.jpg", Texture.class);
        app.assets.load("ui/uiskin.atlas", TextureAtlas.class);
    }

    @Override
    public void show() {
        System.out.println("LOADING");
        shapeRenderer.setProjectionMatrix(app.camera.combined); // viktigt för att shaprenderer ska ha relativ storlek
        this.progress = 0f;
        queueAsset();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(32, app.camera.viewportHeight / 2 - 8, app.camera.viewportWidth - 64, 16);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(32, app.camera.viewportHeight / 2 - 8, progress * (app.camera.viewportWidth - 64), 16);
        shapeRenderer.end();

        app.batch.begin();
        app.font24.draw(app.batch,"Screen: LOADING", 20, 20);
        app.batch.end();
    }

    private void update(float delta){
        progress = MathUtils.lerp(progress, app.assets.getProgress(),0.1f);
        if(app.assets.update() && progress >= app.assets.getProgress() - 0.01f) // retunerar false tills alla assets är inladdade
        {
            app.setScreen(app.mainMenyScreen); // Kan vara splashscreen också
        }
    }

    @Override
    public void resize(int width, int height) {

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
    }
}
