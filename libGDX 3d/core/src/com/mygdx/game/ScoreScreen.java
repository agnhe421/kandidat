package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by DJ on 2016-03-30.
 */
public class ScoreScreen implements Screen{

    // App reference
    private final BaseGame app;

    private Stage scoreStage, stage;
    private Skin skin;
    private BitmapFont font;
    private Table table;
    private TextButton buttonPlay;
    private Actor fotballPortrait;
    private Label fotballScoreLable, highscoreLable, fotballNameLable;
    private com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle labelStyle;

    private String playerName, playerImg;
    private int score;

    public ScoreScreen(final BaseGame app)
    {
        this.app = app;
        this.show();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(scoreStage);
        Gdx.app.log("SHOW", "SCORE");

        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.app.getGraphics().getHeight(), Gdx.app.getGraphics().getHeight()));
        font = new BitmapFont();

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("scorebg1.png"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        // Load skin
        this.skin = new Skin();
        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/TextUI.pack")));
        this.skin.add("default-font", font); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/TextUI.json"));

        // Variabler för att kunna skapa highscore
        playerName = "Sofie";
        score = 10;
        playerImg = "img/footbal_portrait.png";
        initHighscoreList(playerName, score, playerImg);
        initButtons();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        scoreStage.act();
        scoreStage.draw();
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
        if(scoreStage !=null){scoreStage.dispose();}
        if(stage != null){stage.dispose();}
    }

    private void initButtons(){

        buttonPlay = new TextButton("",skin, "default");
        buttonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonPlay.setSize(200,200);
        buttonPlay.setPosition(Gdx.graphics.getWidth()/4 - buttonPlay.getWidth()/2, Gdx.graphics.getHeight()/6 - buttonPlay.getHeight()/2);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "Play");

                scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(0.9f), Actions.moveTo(0, 1000, 0.5f),
                        Actions.run(new Runnable() {
                            public void run() {
                                // Gdx.app.log("done", "done");
                                app.setScreen(new GameScreen(app));
                                dispose();
                            }
                        })));
            }
        });
        scoreStage.addActor(buttonPlay);
    }

    private void initHighscoreList( String playerName, int score, String playerImg){
        table = new Table(skin);
        stage.addActor(table);
        table.setDebug(true);
        table.setFillParent(true);

        labelStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, Color.PINK);
        highscoreLable = new Label("HIGHSCORELIST:", labelStyle);
        fotballPortrait = new Image(new Sprite(new Texture(Gdx.files.internal(playerImg))));
        fotballPortrait.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, 0, .5f, Interpolation.pow5Out))));

        fotballNameLable = new Label(playerName, labelStyle);
        fotballScoreLable = new Label(Integer.toString(score), labelStyle);

        table.add(highscoreLable).padBottom(20);
        table.row();
        table.add(fotballPortrait).size(100, 100);
        table.add(fotballNameLable);
        table.add(fotballScoreLable);
        scoreStage.addActor(table);
    }
}
