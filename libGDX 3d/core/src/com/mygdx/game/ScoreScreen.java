package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class ScoreScreen implements Screen{

    // App reference
    private final BaseGame app;

    private Stage scoreStage, stage;
    private Skin skin;
    private BitmapFont font;
    private Table table;
    private TextButton buttonPlay;
    private Actor footballPortrait;
    private Label footballScoreLable, highscoreLable, footballNameLable;
    private com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle labelStyle;

    private boolean createTable = true, createTableOnce = false;
    List<PlayerInfo> playerInfoList;
    private int n_players;
    String [] ballNamesArray;
    int [] playerScores;

    public ScoreScreen(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.app.getGraphics().getHeight(), Gdx.app.getGraphics().getHeight()));
        this.skin = new Skin();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(scoreStage);
        Gdx.app.log("SHOW", "SCORE");

        font = new BitmapFont();

        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/TextUI.pack")));
        this.skin.add("default-font", font); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/TextUI.json"));

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("img/scorebg1.png"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        initButtons();

        n_players = PropertiesSingleton.getInstance().getNrPlayers();
        ballNamesArray = PropertiesSingleton.getInstance().getBallsArray(); // TODO: Nu används ballsArray från Singleton. Ska förmodligen vara en array med spelarnas namn istället.
        playerScores = PropertiesSingleton.getInstance().getPlayerScores();

        initHighscoreList();
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

    private void initHighscoreList(){
        if(createTable) {
            table = new Table(skin);
            stage.addActor(table);
            table.setDebug(true);
            table.setFillParent(true);

            labelStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, Color.BLACK);
            highscoreLable = new Label("HIGHSCORELIST:", labelStyle);

            table.add(highscoreLable).align(Align.top);
            table.row();

            createTable = false;
        }

        sortTableByScore();

        for(int i = 0; i < n_players; i++) {
            footballPortrait = playerInfoList.get(i).getPortrait();
            footballPortrait.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, 0, .5f, Interpolation.pow5Out))));
            footballNameLable = new Label(playerInfoList.get(i).getModelName(), labelStyle);
            footballScoreLable = new Label(Integer.toString(playerInfoList.get(i).getScore()), labelStyle);

            table.row();
            table.add(footballPortrait).size(100, 100);
            table.add(footballNameLable).uniform();
            table.add(footballScoreLable).uniform();
            scoreStage.addActor(table);
        }
    }

    public void sortTableByScore(){
        playerInfoList = new Vector<PlayerInfo>(n_players);

        // Add all of the player info into a vector.
        for(int i = 0; i < n_players ; i++) {
            PlayerInfo playerInfo = new PlayerInfo(playerScores[i], new Image(new Sprite(new Texture(
                    Gdx.files.internal("img/" + ballNamesArray[i] + ".png")))), ballNamesArray[i]);

            playerInfoList.add(playerInfo);
        }
        // Sort the vector.
        Collections.sort(playerInfoList);
    }

    // Class that contains all of the info needed for the highscore table.
    private class PlayerInfo implements Comparable<PlayerInfo>{
        int score;
        Image portrait;
        String modelName; // TODO: När nätverk funkar, byt denna till player name istället.

        private PlayerInfo(int theScore, Image thePortrait, String theModelName){
            this.score = theScore;
            this.portrait = thePortrait;
            this.modelName = theModelName;
        }

        @Override
        public int compareTo(PlayerInfo comparesTo){
            int compareScore = ((PlayerInfo)comparesTo).getScore();
            return compareScore - this.score;
        }

        private int getScore(){return this.score;}

        private Image getPortrait(){return this.portrait;}

        public String getModelName(){return this.modelName;}

    }
}


