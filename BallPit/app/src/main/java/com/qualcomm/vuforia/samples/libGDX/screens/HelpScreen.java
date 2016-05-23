package com.qualcomm.vuforia.samples.libGDX.screens;

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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


/**
 * Created by Agnes on 2016-05-16.
 */
public class HelpScreen implements Screen {

    // App reference
    private final BaseGame app;

    // Stage vars
    private Stage stage, stageBackground;
    private Skin skin;

    // Buttons
    private TextButton buttonBack;
    private Table container;

    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    public HelpScreen(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }
    @Override
    public void show() {
        System.out.println("Help menu");
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

        initScrollMenu();

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

    private void initScrollMenu(){

        // inizializzazione della tabella
       container = new Table();
       container.setFillParent(true);
       stage.addActor(container);

        Table table = new Table();
        stage.addActor(table);

        final ScrollPane scroll = new ScrollPane(table, skin);
        scroll.setupFadeScrollBars(0f, 0f);

        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };

        table.pad(20).defaults().space(5);

        Image image1 = new Image(new Sprite(new Texture(Gdx.files.internal("img/Tutorial1.png"))));
        table.add(image1);//.height(Gdx.graphics.getWidth() / 3).width(app.VIRTUAL_HEIGHT);

        Image image2 = new Image(new Sprite(new Texture(Gdx.files.internal("img/Tutorial2.png"))));
        table.add(image2);//.height(Gdx.graphics.getWidth() / 3).width(app.VIRTUAL_HEIGHT);

        Image image3 = new Image(new Sprite(new Texture(Gdx.files.internal("img/Tutorial3.png"))));
        table.add(image3);//.height(Gdx.graphics.getWidth() / 3).width(app.VIRTUAL_HEIGHT);

        Image image4 = new Image(new Sprite(new Texture(Gdx.files.internal("img/Tutorial4.png"))));
        table.add(image4);//.height(Gdx.graphics.getWidth() / 3).width(app.VIRTUAL_HEIGHT);

        buttonBack = new TextButton("Back", skin, "default8");
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.0f), moveBy(0, 0, 1.f, Interpolation.pow5Out))));
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().addAction(Actions.sequence(Actions.delay(0.0f), Actions.parallel(fadeOut(0.1f), moveBy(0, 0, 0.5f, Interpolation.pow5Out)),
                        Actions.run(new Runnable() {
                            public void run() {
                                app.setScreen(new MainMenyScreen(app));

                            }
                        })));
            }
        });
        container.add(scroll).padTop(10).padBottom(10);//.height(Gdx.graphics.getHeight() / 2);//.expandY().fill().colspan(1);
        container.row();
        container.add(buttonBack).expandX().padTop(10).padBottom(10).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);
        container.row();


    }

}
