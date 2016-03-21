package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


/**
 * Created by sofiekhullar on 16-03-14.
 */
public class PickScreen implements Screen {

    // App reference
    private final MyGdxGame app;
    // Stage vars
    private Stage stage;
    private Rectangle viewport;
    private Skin skin;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    // Texture
    private Texture background;
    private Texture badLogic;
    private Image image;
    private Table container;

    // Buttons
    private TextButton buttonBack;

    public PickScreen(final MyGdxGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.viewport = new Rectangle();
    }

    @Override
    public void show() {
        System.out.println("Show");
        Gdx.input.setInputProcessor(stage); // hanterar olika input events
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        this.skin.add("default-font", app.font50); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

        badLogic = app.assets.get("img/badlogic.jpg", Texture.class);
        background = app.assets.get("img/greek.jpg", Texture.class);

        initButtons();
        initScrollMenu();

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
        app.batch.draw(background, Gdx.graphics.getHeight() / 2 - background.getHeight() / 2, Gdx.graphics.getWidth() / 2 - background.getWidth() / 2);
        app.font50.draw(app.batch, "Screen: PICKSCREEN", 30, 30);
        app.font120.draw(app.batch, "Pick player", w / 2 - 200, h - 180);
        app.font50.draw(app.batch, "Creating server", w / 2 - 200, h - 80);
        app.font120.draw(app.batch, app.connectcounter.toString(), 200, 100);
        app.batch.end();

        stage.draw();
    }

    public void update(float delta)
    {
        stage.act(delta);
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
    }

    private void initButtons() {
        buttonBack = new TextButton("Back", skin, "default");
        buttonBack.setPosition(20, Gdx.graphics.getHeight() - 30);
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.connectionMenuScreen);
            }
        });
        stage.addActor(buttonBack);
    }

    private void initScrollMenu(){
        // image.setDrawable(new SpriteDrawable(new Sprite(background)));
        // inizializzazione della tabella
        container = new Table();
        //container.setFillParent(true);
        container.setSize(w / 2, h / 2);
        //container.bottom();
        container.setPosition(w / 2 - container.getWidth() / 2, h / 2 - container.getHeight() / 2);
        //container.setBackground((Drawable) image);
        stage.addActor(container);
        //container.debug();

        Table table = new Table();
        //table.debug();
        table.bottom();

        final ScrollPane scroll = new ScrollPane(table, skin);
        scroll.setupFadeScrollBars(0f, 0f);
        scroll.setOverscroll(true, false);

        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };

        table.pad(0).defaults().space(10);

        for (int i = 0; i < 5; i++) {
            image = new Image(badLogic);
            image.setScaling(Scaling.fit); // Default is Scaling.stretch, as you found.
            table.add(image).height(scroll.getHeight()).width(MyGdxGame.VIRTUAL_WIDTH/4).expand().fill();
            //table.add(image).height(scroll.getHeight()).width(MyGdxGame.VIRTUAL_WIDTH/4).size(container.getWidth(), container.getHeight());
        }

        container.add(scroll).expandY().fill().colspan(1);//.height(Gdx.graphics.getHeight() / 2);//
        container.row().space(10).padBottom(10);
        //container.setColor(0,0,0,0);
        //table.setColor(0,0,0,0);*/
    }

}
