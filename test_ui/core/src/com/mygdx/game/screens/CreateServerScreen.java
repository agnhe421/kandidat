package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Agnes on 2016-04-13.
 */
public class CreateServerScreen implements Screen{

    private final MyGdxGame app;


    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    private Stage stage, stageBackground;
    private Skin skin;

    private TextButton buttonBack, buttonReady;
    private String player1 = "Player 1", serverName = "Server name", playerList = "";
    public CreateServerScreen(final MyGdxGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }


    @Override
    public void show() {

        System.out.println("Create server screen");
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/blurr.jpg"))));
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

        GlyphLayout player = new GlyphLayout();
        player.setText(app.font40, player1);

        GlyphLayout Server = new GlyphLayout();
        Server.setText(app.font40, player1);

        GlyphLayout glyphLayoutlist = new GlyphLayout();
        glyphLayoutlist.setText(app.font40, playerList);

        float fmx = player.width/2, fmy = player.height/2;
        float fex = Server.width/2, fey = Server.height/2;

        float x = w/2, y = h/2;

        app.batch.begin();

        app.font40.draw(app.batch, serverName, x - fex, y + fey + 120);
        app.font40.draw(app.batch, player1, x - fmx, y + fmy + 50);
        app.font40.draw(app.batch, "Screen: createServer", 30, 30);
        app.font40.draw(app.batch, playerList, 75, y + fmy + 250);

        app.batch.end();
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

    private void initButtons()
    {
        Table table = new Table(skin);
        stage.addActor(table);
        // table.setDebug(true);
        table.setFillParent(true);

        buttonBack = new TextButton("Back", skin, "default8");
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.connectionMenuScreen);
            }
        });

        buttonReady = new TextButton("Ready", skin, "default8");
        buttonReady.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonReady.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.connectionMenuScreen);
            }
        });

        table.add(buttonBack).padRight(5);
        table.add(buttonReady);
        table.row();

        stage.addActor(table);
    }





}
