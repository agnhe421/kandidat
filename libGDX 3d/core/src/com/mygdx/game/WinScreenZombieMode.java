package com.mygdx.game;

/**
 * Created by Martin on 2016-04-22.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.mygdx.game.GameScreenZombieMode;

import java.net.PortUnreachableException;

public class WinScreenZombieMode implements Screen {

    // App reference
    private final BaseGame app;

    private Stage scoreStage, stage;
    private Skin skin;
    private BitmapFont font;
    private TextButton buttonPlay, buttonPlayAgain, buttonMainMenu;

    private Label LabelWinner, LabelWinnerTotal;
    private Label.LabelStyle labelStyleWinner, labelStyleWinnerTotal;
    private BitmapFont font40, font60;
    public int hit_count;

    public WinScreenZombieMode(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.app.getGraphics().getHeight(), Gdx.app.getGraphics().getHeight()));
        this.skin = new Skin();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(scoreStage);
        Gdx.app.log("SHOW", "WINNER");

        font = new BitmapFont();

        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/Buttons.pack")));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("img/scorebg1.png"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        initFonts();

        //-----------------------load winner-------------------------------
        labelStyleWinner= new Label.LabelStyle(font40, Color.RED);
        LabelWinner = new Label("Winner", labelStyleWinner);
        LabelWinner.setPosition(Gdx.graphics.getHeight() / 4, Gdx.graphics.getWidth() / 3);
        stage.addActor(LabelWinner);
        //-------------------------------------------------------------------

        hit_count = PropertiesSingleton.getInstance().getHitCount();
        PropertiesSingleton.getInstance().setHitCount(hit_count);
        System.out.println("Hit: " + hit_count);

        if(hit_count >= 2){
            LabelWinner.setText(String.format("Zombie Wins"));
            PropertiesSingleton.getInstance().setHitCount(this.hit_count = 0);
        }else{
            LabelWinner.setText(String.format("Human Balls Wins"));
            PropertiesSingleton.getInstance().setHitCount(this.hit_count = 0);
        }

        //-----------------------load WinnerTotal-------------------------------
        labelStyleWinnerTotal= new Label.LabelStyle(font40,Color.BLACK);
        LabelWinnerTotal = new Label("", labelStyleWinnerTotal);
        LabelWinnerTotal.setPosition(Gdx.graphics.getHeight() / 4, Gdx.graphics.getWidth() / 6);
        stage.addActor(LabelWinnerTotal);
        //-------------------------------------------------------------------

        int current_round = PropertiesSingleton.getInstance().getRound();
        System.out.println("ScoreScreen: " + current_round);


        if(current_round == 2)
        {
            initButtonsFinalScore();
        }
        else
        {
            initButtonsTotalScore();
        }


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        scoreStage.act();
        scoreStage.draw();
        stage.draw();
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

    private void initButtonsTotalScore(){
        float buttonSizeX = 250, buttonSizeY = 50;

        buttonPlay = new TextButton("Next round",skin, "default8");
        buttonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonPlay.setSize(buttonSizeX, buttonSizeY);
        buttonPlay.setPosition(Gdx.graphics.getWidth() / 4 - buttonPlay.getWidth() / 2, Gdx.graphics.getHeight() / 6 - buttonPlay.getHeight() / 2);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "Play");
                LabelWinner.setVisible(false);

                scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(0.9f), Actions.moveTo(0, 1000, 0.5f),
                        Actions.run(new Runnable() {
                            public void run() {
                                // Gdx.app.log("done", "done");
                                app.setScreen(new GameScreenZombieMode(app));
                                dispose();
                            }
                        })));
            }
        });
        scoreStage.addActor(buttonPlay);
    }

    private void initButtonsFinalScore(){
        float buttonSizeX = 250, buttonSizeY = 50;

        buttonPlayAgain = new TextButton("Play again",skin, "default8");
        buttonPlayAgain.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonPlayAgain.setSize(buttonSizeX, buttonSizeY);
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() / 4 - buttonPlayAgain.getWidth() / 2, Gdx.graphics.getHeight() / 6);
        buttonPlayAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "Play");
                LabelWinner.setVisible(false);

                scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(0.9f), Actions.moveTo(0, 1000, 0.5f),
                        Actions.run(new Runnable() {
                            public void run() {
                                // Gdx.app.log("done", "done");
                                app.setScreen(new GameScreenZombieMode(app));
                                dispose();
                            }
                        })));
            }
        });

        buttonMainMenu = new TextButton("Main menu",skin, "default8");
        buttonMainMenu.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonMainMenu.setSize(buttonSizeX, buttonSizeY);
        buttonMainMenu.setPosition(Gdx.graphics.getWidth() / 4 - buttonMainMenu.getWidth() / 2, Gdx.graphics.getHeight() / 6 - buttonPlayAgain.getHeight());
        buttonMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "Play");
                LabelWinner.setVisible(false);

                scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(0.9f), Actions.moveTo(0, 1000, 0.5f),
                        Actions.run(new Runnable() {
                            public void run() {
                                // Gdx.app.log("done", "done");
                                app.setScreen(new GameScreenZombieMode(app));
                                dispose();
                            }
                        })));
            }
        });

        scoreStage.addActor(buttonPlayAgain);
        scoreStage.addActor(buttonMainMenu);
    }

    private void initFonts(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copyfonts.com_gulim.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 40;
        font40 = generator.generateFont(params);

        params.size = 60;
        font60 = generator.generateFont(params);

        generator.dispose();
    }


}

