package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class MainMenyScreen implements Screen {
    // App reference
    private final BaseGame app;
    // Stage vars
    private Stage stage, stageBackground;
    private Skin skin;
    // Buttons
    private TextButton buttonPlay, buttonSetting, buttonSkipServer;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    int size_x = 280;
    int size_y = 60;

    public MainMenyScreen(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }
    @Override
    public void show() {
        System.out.println("Main menu");
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/Background.jpg"))));
        background.setPosition(0, 0);
        background.setSize((stageBackground.getWidth()), stageBackground.getHeight());
        stageBackground.addActor(background);

        initButtons();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stageBackground.draw();

        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            System.out.println("Back key was pressed");
            Gdx.app.exit();
        }
    }

          //  new Timer().schedule(app.setScreen(app.settingScreen), 1); }

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
        stageBackground.dispose();
    }

    private void initButtons()
    {
        Table table = new Table(skin);
        stage.addActor(table);
        table.setFillParent(true);

        buttonPlay = new TextButton("Play",skin, "default8");
        buttonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.0f), moveBy(150, 0, 1.f, Interpolation.pow5Out))));
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().addAction(Actions.sequence(Actions.delay(0.0f), Actions.parallel(fadeOut(0.1f), moveBy(-150, 0, 0.5f, Interpolation.pow5Out)),
                        Actions.run(new Runnable() {
                            public void run() {
                                app.setScreen(new ConnectionMenuScreen(app));

                            }
                        })));
            }
        });



        buttonSetting = new TextButton("Settings", skin, "default8");
        buttonSetting.addAction(sequence(alpha(0), parallel(fadeIn(.0f), moveBy(150, 0, 1.5f, Interpolation.pow5Out))));
        buttonSetting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().addAction(Actions.sequence(Actions.delay(0.0f), Actions.parallel(fadeOut(0.1f), moveBy(-150, 0, 0.5f, Interpolation.pow5Out)),
                        Actions.run(new Runnable() {
                            public void run() {
                                app.setScreen(app.settingScreen);

                            }
                        })));
            }
        });


        buttonSkipServer = new TextButton("Skip Server", skin, "default8");
        buttonSkipServer.addAction(sequence(alpha(0), parallel(fadeIn(.0f), moveBy(150, 0, 2.f, Interpolation.pow5Out))));
        buttonSkipServer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().addAction(Actions.sequence(Actions.delay(0.0f), Actions.parallel(fadeOut(0.1f), moveBy(-150, 0, 0.5f, Interpolation.pow5Out)),
                        Actions.run(new Runnable() {
                            public void run() {
                                app.setScreen(new GameScreen(app));

                            }
                        })));
            }
        });



        table.add(buttonPlay).expandX().left().padLeft(-170).padBottom(10).size(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/5);
        table.row();
        table.add(buttonSetting).bottom().left().padLeft(-170).padBottom(10).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);
        table.row();
        table.add(buttonSkipServer).bottom().left().padLeft(-170).padBottom(10).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);

        stage.addActor(table);
    }
}
