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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by sofiekhullar on 16-04-13.
 */
public class JoinServerScreen implements Screen{

    // App reference
    private final MyGdxGame app;
    private Stage stage, stageBackground;

    private TextButton buttonBack, buttonConnect;
    private Skin skin;

    private String playerName = "Player 1", chooseServer = "Choose server";

    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    int nr_connected_players = 0, nr_servers = 2;

    public JoinServerScreen(final MyGdxGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {
        System.out.println("Join server");
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("Buttons.json"));

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/background4.jpg"))));
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

        GlyphLayout glyphPlayerName = new GlyphLayout();
        glyphPlayerName.setText(app.font40, playerName);

        GlyphLayout glyphChooseServer = new GlyphLayout();
        glyphChooseServer.setText(app.font40, chooseServer);

        float fpx = glyphPlayerName.width/2, fpy = glyphPlayerName.height/2;
        float fcx = glyphChooseServer.width/2, fcy = glyphChooseServer.height/2;
        float x = w/2, y = h/2;

        app.batch.begin();
        app.font40.draw(app.batch, "Screen: JoinServerScreen", 30, 30);
        app.font40.draw(app.batch, chooseServer, x - fcx, h - fcy);
        app.font40.draw(app.batch, playerName, x - fpx, h - fpy * 5);
        app.batch.end();

        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            System.out.println("Back key was pressed");
            app.setScreen(app.connectionMenuScreen);
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

    private void initButtons()
    {
        //TODO Lägga in buttonConnect i table, oklart varför det inte fungerar nu
        int space = 0;
        Table table = new Table(skin);
        stage.addActor(table);
        table.setFillParent(true);

            buttonConnect = new TextButton("Server 1", skin, "default");
            buttonConnect.setWidth(w - w/3);
            buttonConnect.getLabel().setAlignment(Align.left);
            buttonConnect.setPosition(w / 2 - buttonConnect.getWidth() / 2, h /2 - buttonConnect.getHeight() / 2 - space);
            buttonConnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
            space += 20;
            System.out.print(space);
            buttonConnect.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    buttonConnect.setText("Server 1        Connected: " + nr_connected_players);
                }
            });

        buttonBack = new TextButton("Back",skin, "default");
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonBack.setPosition(w / 2 - buttonBack.getWidth() / 2, h / 6);
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //app.setScreen(app.connectionMenuScreen);
            }
        });


        stage.addActor(buttonConnect);
        stage.addActor(buttonBack);
        //table.add(buttonConnect).  //.left().width(w - w/3).height(40);
        //table.add(buttonBack);

        stage.addActor(table);
    }


    @Override
    public void dispose() {
        stage.dispose();
        stageBackground.dispose();

    }
}
