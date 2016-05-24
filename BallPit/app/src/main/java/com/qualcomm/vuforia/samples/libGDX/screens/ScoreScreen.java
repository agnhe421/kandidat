package com.qualcomm.vuforia.samples.libGDX.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;
import com.qualcomm.vuforia.samples.singletons.DataHolder;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
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

    private Actor footballPortrait;
    private Label footballScoreLable, highscoreLable, footballNameLable, roundLable;
    private com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle labelStyle;

    List<PlayerInfo> playerInfoList;
    private int n_players, nextRoundNumber;
    String [] ballNamesArray;
    int [] playerScores;

    private TextButton buttonPlay, buttonPlayAgain, buttonMainMenu;

    private String playerName, playerImg;
    private int score, nrPlayers =4;
    private boolean createTable = true;

    private String stringHeadlinte = "Total score: ";


    public ScoreScreen(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.app.getGraphics().getHeight(), Gdx.app.getGraphics().getHeight()));
        this.skin = new Skin();
    }

    @Override
    public void show() {
        Gdx.app.log("HEJ!", "Scorescreen.");
        if(app.createServerScreen.create != null)
            app.createServerScreen.create.startNextRoundCall();


        Gdx.input.setInputProcessor(scoreStage);
        Gdx.app.log("SHOW", "SCORE");

        font = new BitmapFont();

        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/Buttons.pack")));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("img/" + PropertiesSingleton.getInstance().getChosenIsland() +"1.jpg"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        n_players = PropertiesSingleton.getInstance().getNrPlayers();
        ballNamesArray = PropertiesSingleton.getInstance().getBallNames(); // TODO: Nu används ballsArray från Singleton. Ska förmodligen vara en array med spelarnas namn istället.
        playerScores = PropertiesSingleton.getInstance().getScores();

        initHighscoreList();

        int current_round = PropertiesSingleton.getInstance().getRound();
        System.out.println("ScoreScreen: " + current_round);
        String round = Integer.toString(current_round);

        labelStyle = new Label.LabelStyle(app.font40, Color.PINK);
        roundLable = new Label("Round: " + round + "/3", labelStyle);
        roundLable.setPosition(Gdx.graphics.getHeight()/2 - roundLable.getWidth()/2, Gdx.graphics.getHeight() - roundLable.getHeight());
        scoreStage.addActor(roundLable);

        if(current_round == 3)
        {
            stringHeadlinte = "Final score ";
            initButtonsFinalScore();
        }
        else
        {
            initButtonsTotalScore();
        }
    }

    @Override
    public void render(float delta) {
        if(app.createServerScreen.create != null)
            if(app.createServerScreen.create.checkAllReadyNextRound())
                startNewRound();
        if(app.joinServerScreen.join != null)
            if(!app.joinServerScreen.join.isAlive())
            {
                app.joinServerScreen.join = null;
                app.mainMenyScreen = new MainMenyScreen(app);
                app.setScreen(app.mainMenyScreen);
            }
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
        scoreStage.clear();
        if(scoreStage !=null)
        {
            scoreStage.dispose();
            System.out.println("scoreStage dispose");
        }
        if(stage != null){stage.dispose();}
    }

    private void initButtonsTotalScore(){

        buttonPlay = new TextButton("Next round",skin, "default8");
        buttonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonPlay.setSize(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 9);
        buttonPlay.setPosition(Gdx.graphics.getWidth() / 3 , Gdx.graphics.getHeight() / 6 - buttonPlay.getHeight() / 2);
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

    public void startNewRound()
    {
        if(nextRoundNumber == 1)
            for(int idu = 0; idu < PropertiesSingleton.getInstance().getNrPlayers(); ++idu)
                PropertiesSingleton.getInstance().setScore(idu, 0);
        PropertiesSingleton.getInstance().setRound(nextRoundNumber);
        if(app.createServerScreen.create != null)
            app.createServerScreen.create.resetNextRoundState();
        scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(1.2f), Actions.moveTo(0, 1000, 0.5f), Actions.delay(1),
                Actions.run(new Runnable() {
                    public void run() {
                        // Gdx.app.log("done", "done");
                        DataHolder.getInstance().setActivateCamera(true);
                        app.gameScreen = new GameScreen(app);
                        app.setScreen(app.gameScreen);
//                                dispose();
                    }
                })));
    }

    private void initButtonsFinalScore(){

        buttonPlayAgain = new TextButton("Play again",skin, "default8");
        buttonPlayAgain.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonPlayAgain.setSize(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 9);
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() - buttonPlayAgain.getWidth(), Gdx.graphics.getHeight() / 6);
        buttonPlayAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "Play again");

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

        buttonMainMenu = new TextButton("Main menu",skin, "default8");
        buttonMainMenu.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonMainMenu.setSize(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 9);
        buttonMainMenu.setPosition(Gdx.graphics.getWidth() - buttonMainMenu.getWidth(), Gdx.graphics.getHeight() / 6);
        buttonMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "Main");

                scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(0.9f), Actions.moveTo(0, 1000, 0.5f),
                        Actions.run(new Runnable() {
                            public void run() {
                                // Gdx.app.log("done", "done");
                                app.setScreen(app.mainMenyScreen);
                                dispose();
                            }
                        })));
            }
        });

        scoreStage.addActor(buttonPlayAgain);
        scoreStage.addActor(buttonMainMenu);
    }
    private void initHighscoreList(){
        if(createTable) {
            table = new Table(skin);
            stage.addActor(table);
            //  table.setDebug(true);
            table.setFillParent(true);
            table.left();
            table.padTop(Gdx.graphics.getHeight() / 10);
            table.padLeft(Gdx.graphics.getWidth() / 25);

            labelStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(app.font40, Color.BLACK);
            highscoreLable = new Label(stringHeadlinte, labelStyle);
            highscoreLable.setPosition(Gdx.graphics.getWidth()/25, Gdx.graphics.getHeight()-(highscoreLable.getHeight()*3));
            scoreStage.addActor(highscoreLable);
            //  table.add(highscoreLable).align(Align.top);
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
            table.add(footballPortrait).padTop(10).size(Gdx.graphics.getWidth() / 8, Gdx.graphics.getHeight() / 5);
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
                    Gdx.files.internal("portraits/" + ballNamesArray[i] + ".png")))), ballNamesArray[i]);

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


