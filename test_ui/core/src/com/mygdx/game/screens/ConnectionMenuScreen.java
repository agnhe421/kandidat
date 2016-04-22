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
 * Created by sofiekhullar on 16-03-02.
 */
public class ConnectionMenuScreen implements Screen {

    // App reference
    private final MyGdxGame app;
    // Stage vars
    private Stage stage, stageBackground;
    private Skin skin;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    // Buttons
    private TextButton buttonCreate, buttonJoin, buttonExit,buttonDisconnect, buttonBack, ButtonSkipServer;
    // Nätverk
    //public Integer connectcounter = 0;
    private String msg = "msg", error ="No Error", IPad = "IP", serverIPad = "";
    public CreateServer create;
    JoinServer join;
    Boolean hardexit = false;
    private SendPacket sendPacket;


    public ConnectionMenuScreen(final MyGdxGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));

        create = null;
        join = null;
    }

    @Override
    public void show() {
        System.out.println("Show");
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40);
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/greek.jpg"))));
        background.setPosition(0, 0);
        background.setSize((stage.getWidth()), stage.getHeight());
        stage.addActor(background);

        initButtons();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stageBackground.draw();

        app.batch.begin();
        GlyphLayout glyphLayoutmsg = new GlyphLayout(), glyphLayouterror = new GlyphLayout(), glyphLayoutIP = new GlyphLayout();
        glyphLayoutmsg.setText(app.font40, msg);
        glyphLayouterror.setText(app.font40, error);
        glyphLayoutIP.setText(app.font40, IPad);
        float fex = glyphLayouterror.width/2, fey = glyphLayouterror.height/2;
        float fmx = glyphLayoutmsg.width/2, fmy = glyphLayoutmsg.height/2;
        float fix = glyphLayoutIP.width/2, fiy = glyphLayoutIP.height/2;
        float x = w/2, y = h/2;

        //Only retrieve active messages if the exit command hasn't been invoked. Otherwise, null values may be accessed.
        /*if(!hardexit)
        {
            //Update server messages
            /*if(createbool)
            {
                if(!create.checkIfVectorNull())
                    app.connectcounter = create.getConnections();
                msg = create.getMsg();
                error = create.getError();
                app.font40.draw(app.batch, app.connectcounter.toString(), w - 50, h - 25);
            }
            //Update connection messages
            else if(joinbool)
            {
                msg = join.getMsg();
                error = join.getError();
            }
            if(join != null)
            {
                //This disconnects the join function if the server disconnects. Assuming that the
                //phone receives the SERVER_SHUTDOWN message before the server shuts down completely.
                if(join.getMsg().equals("SERVER_SHUTDOWN"))
                {
                    try
                    {
                        join.join();
                        error = "No Error.";
                    }catch(InterruptedException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                    disconnectAll();
                    serverIPad = "Standing by.";
                    msg = "Server disconnected.";

                }
            }
        }*/
        //Draw all text on screen. If you don't wish to see the debug, remove the error draw.
        app.font40.draw(app.batch, msg, x - fmx, y + fmy);
        app.font40.draw(app.batch, error, x - fex, y + fey - 300);
        app.font40.draw(app.batch, IPad, x - fix, y + fiy + 300);
        app.batch.end();

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
    }

    public void disconnectAll()
    {
        //Disconnect any active connections, or servers.
        if(join != null)
        {
            join.disconnect();
            try
            {
                join.join();
            }catch(InterruptedException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            join = null;
        }
        if(create != null)
        {
            create.stopServer();
            try
            {
                create.join();
            }catch(InterruptedException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            create = null;
        }
        Gdx.app.log("Errorlog", error);
        msg = "Disconnected.";
        error = "No Error";
        IPad = "IP";
    }


    private void initButtons() {

        Table table = new Table(skin);
        stage.addActor(table);
        //table.setDebug(true);
        table.setFillParent(true);

        buttonCreate = new TextButton("", skin, "default4");
        buttonCreate.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonCreate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.createServerScreen);
            }
        });


        buttonJoin = new TextButton("", skin, "default6");
        buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonJoin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.joinServerScreen);
            }
        });

        buttonDisconnect = new TextButton("", skin, "default5");
        buttonDisconnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonDisconnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                disconnectAll();
            }
        });

        buttonExit = new TextButton("", skin, "default7");
        buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Exit the app, terminate any active servers or connections.
                hardexit = true;
                if (create != null) {
                    create.stopServer();
                    try {
                        create.join();
                        create = null;
                    } catch (InterruptedException e) {

                    }
                }
                if (join != null) {
                    join.disconnect();
                    try {
                        join.join();
                        join = null;
                    } catch (InterruptedException e) {

                    }
                }
                Gdx.app.exit();
            }
        });

        table.add(buttonCreate).bottom().left().padLeft(150).expandX();
        table.row();
        table.add(buttonJoin).bottom().left().padLeft(150);
        table.row();
        table.add(buttonDisconnect).bottom().left().padLeft(150);
        table.row();
        table.add(buttonExit).top().left().padLeft(150);
        stage.addActor(table);
    }
}
