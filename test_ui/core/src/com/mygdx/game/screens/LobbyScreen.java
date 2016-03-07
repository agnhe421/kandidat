package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.actors.SlideButton;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*; // Bra att importera!

/**
 * Created by sofiekhullar on 16-03-04.
 */
public class LobbyScreen implements Screen {
    // App reference
    private final MyGdxGame app;

    // Stage vars
    private Stage stage;
    private Skin skin;

    // Game grid
    private int boardSize = 4;
    private int holeX, holeY;
    private SlideButton[] [] buttonGrid;

    // Nav button
    private TextButton buttonBack;

    // Texture
    private Texture background;

    public LobbyScreen(final MyGdxGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(MyGdxGame.V_HEIGTH ,MyGdxGame.V_WIDTH, app.camera ));
    }

    @Override
    public void show() {
        System.out.println("Playscreen");
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        this.skin.add("default-font", app.font24); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

        background = app.assets.get("img/b.jpg", Texture.class);

        initNavigationButton();
        initGrid();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        app.batch.begin();
        app.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // måste göras först
        app.font24.draw(app.batch, "Screen: PLAY", 20, 20);
        app.batch.end();

        stage.draw();
    }


    private void update(float delta)
    {
        stage.act(delta);
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

    // Init the button back
    private void initNavigationButton(){
        buttonBack = new TextButton("Back", skin, "default");
        buttonBack.setPosition(20,app.camera.viewportHeight - 60);
        buttonBack.setSize(100, 50);
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.mainMenyScreen);
            }
        });
        stage.addActor(buttonBack);
    }

    // Init the gamegrid
    private void initGrid(){
        holeX = 3;
        holeY = 3;
        buttonGrid = new SlideButton[boardSize][boardSize];

        for(int i = 0; i < boardSize; i++){ // Rad
            for(int j = 0; j < boardSize; j++){ // Kolumn
               if(i != holeY || j != holeX){
                int id = j + 1 + (i * boardSize);
                buttonGrid[i][j] = new SlideButton(id +"", skin, "default", id );
                buttonGrid[i][j].setPosition(app.camera.viewportWidth / 7 * 2 + 51 * j,
                        app.camera.viewportHeight / 5 * 3 - 51 * i);
                buttonGrid[i][j].setSize(50, 50);

                buttonGrid[i][j].addAction(sequence(alpha(0), delay(id / 15f),
                        parallel(fadeIn(0.5f), moveBy(0, -10, 0.25f, Interpolation.pow5Out))));

                // Slide/Move button
                buttonGrid[i][j].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        int buttonX = 0, buttonY = 0;
                        boolean buttonFound = false;
                        SlideButton selectedButton = (SlideButton) event.getListenerActor();

                        for(int i = 0; i <boardSize && !buttonFound; i++){
                            for (int j = 0; j < boardSize && !buttonFound; j++){
                                if(buttonGrid[i][j] != null && selectedButton == buttonGrid[i][j]){
                                    buttonX = j;
                                    buttonY = i;
                                    buttonFound = true;
                                }
                            }
                        }
                        if(holeX == buttonX || holeY == buttonY){
                            moveButtons(buttonX, buttonY);

                        }
                    }
                });

                stage.addActor(buttonGrid[i][j]);
            }
        }
        }


    }
    private void moveButtons(int buttonX, int buttonY){

    }

}
