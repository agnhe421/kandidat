package com.mygdx.game.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MyGdxGame;

import java.util.Map;

import javafx.application.Application;

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class LoadingScreen implements Screen {
    // App reference
    private final MyGdxGame app;
    // Progressbar
    private ShapeRenderer shapeRenderer;
    private float progress;

    // Texture
    private Texture startImageTexture;
    private Texture backgroundImage;

    public LoadingScreen(final MyGdxGame app)
    {
        this.app = app;
        this.shapeRenderer = new ShapeRenderer();
    }

    private void queueAsset()
    {
        app.assets.load("img/b.jpg", Texture.class);
        app.assets.load("img/badlogic.jpg", Texture.class);
        app.assets.load("ui/uiskin.atlas", TextureAtlas.class);
    }

    @Override
    public void show() {
        System.out.println("LOADING");
        startImageTexture = new Texture(Gdx.files.internal("img/badlogic.jpg")); // Kan göras på ett bättre sätt?
        backgroundImage = new Texture(Gdx.files.internal("img/b.jpg"));

        shapeRenderer.setProjectionMatrix(app.camera.combined); // viktigt för att shaprenderer ska ha relativ storlek
        this.progress = 0f;
        queueAsset();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        app.batch.begin();
        app.batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        app.font24.draw(app.batch, "Screen: LOADING", 20, 20);
        app.batch.draw(startImageTexture, Gdx.graphics.getHeight()/ 2, Gdx.graphics.getWidth() / 2);
        app.batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(32, app.camera.viewportHeight / 2 - 150, app.camera.viewportWidth - 64, 16);

        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(32, app.camera.viewportHeight / 2 - 150, progress * (app.camera.viewportWidth - 64), 16);
        shapeRenderer.end();
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
