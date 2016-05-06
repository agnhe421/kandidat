package com.qualcomm.vuforia.samples.libGDX.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class SettingScreen implements Screen {
    // App reference
    private final BaseGame app;
    // Stage vars
    private Stage stage, stageBackground;
    private Rectangle viewport;
    private Skin skin;
    // Buttons
    private TextButton buttonBack;
    private Label labelVolume, labelSound;
    private Label.LabelStyle labelStyle;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    // Texture

    //music and slider
    public Music music;
    public float soundVolume = 0.5f;
    private Slider slider;
    private Slider slider2;


    public SettingScreen(final BaseGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }

    // Kallas varje gång man vill att denna screen ska visas
    @Override
    public void show() {
        System.out.println("Show");
        Gdx.input.setInputProcessor(stage); // hanterar olika input events
        Gdx.input.setCatchBackKey(true);
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/main_blurred.jpg"))));
        background.setPosition(0, 0);
        background.setSize((stageBackground.getWidth()), stageBackground.getHeight());
        stageBackground.addActor(background);

        initSlider();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stageBackground.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            System.out.println("Back key was pressed");
            app.setScreen(app.mainMenyScreen);
        }

        stage.draw();
    }


    public void update(float delta)
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
        stageBackground.dispose();
        if(skin != null){
            skin.dispose();
        }

    }

    public void initMusic(){
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/theReef.mp3")); //read in the file
        music.setLooping(true);
        music.setVolume(soundVolume); //when it start set volume = 0.5f.
        music.play();

    }
    //TODO: music ch soundeffects text, och backknappen?

    public void initSlider(){
        Table table = new Table(skin);
        stage.addActor(table);
        table.setFillParent(true);

        labelStyle = new Label.LabelStyle(app.font40, Color.WHITE);
        labelVolume = new Label("Music: ", labelStyle);
        labelSound = new Label("Sound effects: ", labelStyle);

        //public Slider(float min, float max, float stepSize, boolean vertical, Slider.SliderStyle style)
        slider = new Slider(0.0f, 1.0f, 0.1f, false, skin); //init slider
        slider.setValue(music.getVolume()); //the sliders position is equal to the musics volume
        music.setVolume(slider.getValue()); //volume is where the slider is
        slider.setAnimateDuration(0.1f);    //how fast the slider react when you move it

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (slider.isDragging()) {
                    music.setVolume(slider.getValue());  //when the slider is moving the musics volume
                }                                       //will change according to where the slider is.
            }
        });


        slider2 = new Slider(0.0f, 1.0f, 0.1f, false, skin);
        slider2.setValue(music.getVolume());
        music.setVolume(slider.getValue());
        slider2.setAnimateDuration(0.1f);
        slider2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (slider2.isDragging()) {
                    music.setVolume(slider2.getValue());  //when the slider is moving the musics volume
                }                                       //will change according to where the slider is.
            }
        });

        buttonBack = new TextButton("Back", skin, "default8");
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(new MainMenyScreen(app));
            }
        });

        table.add(labelVolume).expandX();
        table.row();
        table.add(slider).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);
        table.row();
        table.add(labelSound);
        table.row();
        table.add(slider2).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);
        table.row();
        table.add(buttonBack).size(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);

    }


}
