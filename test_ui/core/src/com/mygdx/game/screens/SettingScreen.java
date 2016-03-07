package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
public class SettingScreen implements Screen {

    private final MyGdxGame app;
    private Stage stage;
    private TextButton buttonBack;
    private Skin skin;

    public SettingScreen(final MyGdxGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(MyGdxGame.V_HEIGTH ,MyGdxGame.V_WIDTH, app.camera )); // kan hatera olika skärmstorlekar
    }

    // Kallas varje gång man vill att denna screen ska visas
    @Override
    public void show() {
        System.out.println("Show");
        Gdx.input.setInputProcessor(stage); // hanterar olika input events

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/uiskin.atlas" , TextureAtlas.class));
        this.skin.add("default-font", app.font24); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

        initButtons();
        }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        stage.draw();

        app.batch.begin();
        app.font24.draw(app.batch, "Screen: SETTING", 20, 20);
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

    private void initButtons() {
        buttonBack = new TextButton("Back", skin, "default");
        buttonBack.setPosition(20,app.camera.viewportHeight - 60);
        buttonBack.setSize(280, 60);
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.mainMenyScreen);
            }
        });

        stage.addActor(buttonBack);
    }
}
