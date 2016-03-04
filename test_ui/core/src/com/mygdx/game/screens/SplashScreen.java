package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;

import javafx.application.Application;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*; // Bra att importera!

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class SplashScreen implements Screen {

    private final MyGdxGame app;
    private Stage stage;
    private Image splashImg;

    public  SplashScreen(final MyGdxGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(MyGdxGame.V_HEIGTH ,MyGdxGame.V_WIDTH, app.camera )); // kan hatera olika skärmstorlekar
    }

    // Kallas varje gång man vill att denna screen ska visas
    @Override
    public void show() {
        System.out.println("Show");
        Gdx.input.setInputProcessor(stage); // hanterar olika input events

        Runnable transitionRunnale = new Runnable() {
            @Override
            public void run() {
                app.setScreen(app.mainMenyScreen);
            }
        };

        Texture splashTex = app.assets.get("badlogic.jpg", Texture.class);
        splashImg = new Image(splashTex);
        splashImg.setOrigin(splashImg.getWidth() / 2, splashImg.getHeight() / 2);
        splashImg.setPosition(stage.getWidth() / 2 - 128, stage.getHeight() + 128);
        //splashImg.addAction(sequence(alpha(0f), fadeIn(2f))); // kallar första och avslutar den och sen går vidare
        //splashImg.addAction(sequence(alpha(0f), parallel(moveBy(20,-30, 2f) ,fadeIn(2f))));
        splashImg.addAction(sequence(alpha(0f), scaleBy(0.1f, 0.1f),
                parallel(fadeIn(2f, Interpolation.pow2),
                        scaleBy(0.5f, 0.5f, 2.5f, Interpolation.pow5),
                        moveTo(stage.getWidth() / 2 - 128, stage.getHeight() / 2 - 128, 2f, Interpolation.swing)),
                delay(1.5f), fadeOut(1f), run(transitionRunnale)));

        stage.addActor(splashImg);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        stage.draw();

        app.batch.begin();
        app.font24.draw(app.batch, "Screen: SPLASH", 20, 20);
        app.batch.end();
    }

    public void update(float delta)
    {
        stage.act(delta);
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
        System.out.println("dispose");
        stage.dispose();
    }
}
