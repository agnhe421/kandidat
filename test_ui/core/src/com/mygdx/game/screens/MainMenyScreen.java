package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;

import javax.sound.midi.Sequence;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*; // Bra att importera!

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class MainMenyScreen implements Screen {

    private final MyGdxGame app;
    private Stage stage;
    private Skin skin;
    private TextButton buttonPlay, buttonExit;


    public MainMenyScreen(final MyGdxGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(MyGdxGame.V_HEIGTH ,MyGdxGame.V_WIDTH, app.camera ));
    }
    @Override
    public void show() {
        System.out.println("Main menu");
        Gdx.input.setInputProcessor(stage);
        stage.clear();

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
        app.font24.draw(app.batch, "Screen: MAINMENY", 20, 20);
        app.batch.end();


    }

    public void update(float delta)
    {
        stage.act();
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
        stage.dispose();
    }

    private void initButtons()
    {
        buttonPlay = new TextButton("Play",skin, "default");
        buttonPlay.setPosition(70, 260);
        buttonPlay.setSize(280, 60);
        buttonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.playScreen);
            }
        });

        buttonExit = new TextButton("Exit", skin, "default");
        buttonExit.setPosition(70, 190);
        buttonExit.setSize(280, 60);
        buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(buttonPlay);
        stage.addActor(buttonExit);

    }
}
