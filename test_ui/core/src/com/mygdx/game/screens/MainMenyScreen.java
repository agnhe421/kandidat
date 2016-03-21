package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class MainMenyScreen implements Screen {
    // App reference
    private final MyGdxGame app;
    // Stage vars
    private Stage stage;
    private Skin skin;
    private Rectangle viewport;
    // Buttons
    private TextButton button,buttonPlay, buttonSetting, buttonScore;
    TextButton.TextButtonStyle textButtonStyle;
    TextureAtlas buttonAtlas;
    // Texture
    private Texture background;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    //private Table table = new Table(skin);


    public MainMenyScreen(final MyGdxGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h, app.camera));
        this.viewport = new Rectangle();
    }
    @Override
    public void show() {
        System.out.println("Main menu");
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/TextUI.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font50); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/TextUI.json"));

        background = app.assets.get("img/greek.jpg", Texture.class);
        initButtons();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // set viewport
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                (int) viewport.width, (int) viewport.height);

        update(delta);

        app.batch.begin();
        app.batch.draw(background, Gdx.graphics.getHeight()/ 2 - background.getHeight()/2, Gdx.graphics.getWidth() / 2 -background.getWidth()/2);
        app.font50.draw(app.batch, "Screen: MAINMENY", 30, 30);
        app.batch.end();

        // Måste göras sist!
        stage.draw();
    }

    public void update(float delta)
    {
        stage.act();
    }

    @Override
    public void resize(int width, int height) {
        //calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);

        if(aspectRatio > app.ASPECT_RATIO)
        {
            scale = (float)height/(float)app.VIRTUAL_HEIGHT;
            crop.x = (width - app.VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < app.ASPECT_RATIO)
        {
            scale = (float)width/(float)app.VIRTUAL_WIDTH;
            crop.y = (height - app.VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)app.VIRTUAL_WIDTH;
        }

        float w = (float)app.VIRTUAL_WIDTH*scale;
        float h = (float)app.VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
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
        app.batch.dispose();

    }

    private void initButtons()
    {
        int size_x = 280;
        int size_y = 60;
        int space = 70;

        Table table = new Table(skin);
        stage.addActor(table);
      // table.setDebug(true);
        table.setFillParent(true);

        buttonPlay = new TextButton("",skin, "default");
      //buttonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.lobbyScreen);
            }
        });

        buttonSetting = new TextButton("", skin, "default2");
       // buttonSetting.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonSetting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.settingScreen);
            }
        });


        buttonScore = new TextButton("", skin, "default3");
       // buttonScore.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonScore.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Score kommer!");
            }
        });

        table.add(buttonPlay).expandX().left().padLeft(150);
        table.row();
        table.add(buttonSetting).bottom().left().padLeft(150);
        table.row();
        table.add(buttonScore).bottom().left().padLeft(150);

        stage.addActor(table);

    }
}
