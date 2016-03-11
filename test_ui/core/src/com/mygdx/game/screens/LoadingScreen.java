package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyGdxGame;

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class LoadingScreen implements Screen {
    // App reference
    private final MyGdxGame app;
    // Stage vars
    private Rectangle viewport;
    // Progressbar
    private ShapeRenderer shapeRenderer;
    private float progress;
    // Texture
    private Texture startImage;
    private Texture backgroundImage;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    public LoadingScreen(final MyGdxGame app)
    {
        this.app = app;
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new Rectangle();

    }

    private void queueAsset()
    {
        app.assets.load("img/background1.jpg", Texture.class);
        app.assets.load("img/badlogic.jpg", Texture.class);
        app.assets.load("ui/uiskin.atlas", TextureAtlas.class);
        app.assets.load("ui/menuskin.pack", TextureAtlas.class);
    }

    @Override
    public void show() {
        System.out.println("LOADING");
        startImage = new Texture(Gdx.files.internal("img/badlogic.jpg")); // Kan göras på ett bättre sätt?
        backgroundImage = new Texture(Gdx.files.internal("img/background1.jpg"));

        shapeRenderer.setProjectionMatrix(app.camera.combined); // viktigt för att shaprenderer ska ha relativ storlek

        this.progress = 0f;
        queueAsset();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // set viewport
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                (int) viewport.width, (int) viewport.height);
        update(delta);

        app.batch.begin();
        app.batch.draw(backgroundImage, Gdx.graphics.getHeight()/ 2 - backgroundImage.getHeight()/2, Gdx.graphics.getWidth() / 2 -backgroundImage.getWidth()/2);
        app.font50.draw(app.batch, "Screen: LOADING", 30, 30);
        app.batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(32, h/6, w - 72, 70);

        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(32, h/6, progress * (w - 72), 70);
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
        //calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);

        if(aspectRatio > app.ASPECT_RATIO)
        {
            scale = (float)height/(float)app.VIRTUAL_HEIGHT;
            crop.x = (width - app.VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < app.ASPECT_RATIO)
        {
            scale = (float)width/(float)app.VIRTUAL_WIDTH;
            crop.y = (height - app.VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)app.VIRTUAL_WIDTH;
        }

        float w = (float)app.VIRTUAL_WIDTH*scale;
        float h = (float)app.VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
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
