package com.qualcomm.vuforia.samples.libGDX.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.qualcomm.vuforia.samples.Network.CreateServer;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;
import com.qualcomm.vuforia.samples.singletons.DataHolder;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Agnes on 2016-04-13.
 */
public class CreateServerScreen implements Screen{

    private final BaseGame app;
    public CreateServer create;

    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    float buttonSizeX = 250, buttonSizeY = 50;

    private Stage stage, stageBackground;
    private Skin skin;
    private Table container;

    private TextButton buttonReady, buttonDisconnect, buttonGameMode,buttonStandard, buttonZombie, buttonTeam;;
    private String player1 = "Player 1", serverName = "Server name", playerList = "";
    public String msg = "msg", error = "error", msglog = "log", IPad = "IP";

    private Label labelList, labelPlayer, labelServer;
    private Label.LabelStyle labelStyle;

    public CreateServerScreen(final BaseGame app)
    {
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {

        System.out.println("Create server screen");
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        stage.clear();

        if (create == null) {
            //Create a new server, update the text accordingly.
            create = new CreateServer(app);
            create.start();
            IPad = create.getIpAddress();
            msg = create.getMsg();
            error = create.getError();
            //app.setScreen(app.pickScreen);
        } else {
            //Further clicks will only update the text.
            msg = create.getMsg();
            error = create.getError();
            //app.setScreen(app.pickScreen);
        }

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font40); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

        Actor background = new Image(new Sprite(new Texture(Gdx.files.internal("img/main_blurred.jpg"))));
        background.setPosition(0, 0);
        background.setSize((stageBackground.getWidth()), stageBackground.getHeight());
        stageBackground.addActor(background);


        initButtons();
        initScrollMenu();

        PropertiesSingleton.getInstance().initRandomCoinPosition();
        PropertiesSingleton.getInstance().setRandomCoinPosition();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stageBackground.draw();

        GlyphLayout player = new GlyphLayout();
        player.setText(app.font40, player1);

        GlyphLayout Server = new GlyphLayout();
        Server.setText(app.font40, player1);

        GlyphLayout glyphLayoutlist = new GlyphLayout();
        glyphLayoutlist.setText(app.font40, playerList);

        GlyphLayout glyphLayoutmsg = new GlyphLayout(), glyphLayouterror = new GlyphLayout(), glyphLayoutlog = new GlyphLayout();

        //float fmx = player.width/2, fmy = player.height/2;
        //float fex = Server.width/2, fey = Server.height/2;
        float fmsgx = glyphLayoutmsg.width/2, fmsgy = glyphLayoutmsg.height/2;
        //float fmlx = glyphLayoutlog.width/2, fmly = glyphLayoutlog.height/2;
        //float ferx = glyphLayouterror.width/2, fery = glyphLayouterror.height/2;

        float x = w/2, y = h/2;

        app.batch.begin();

        if(create != null)
        {
            playerList = create.getServerName() + "\n";
            app.connectcounter = 0;
            //Detta blir långsamt ibland av nån anledning.
            if(!create.checkIfVectorNull())
                app.connectcounter = create.getConnections();

            msg = create.getMsg();
            error = create.getError();
            msglog = create.getlog();
            app.font40.draw(app.batch, app.connectcounter.toString(), w - 50, h - 25);
            for(int idx = 0; idx < app.connectcounter; ++idx)
            {
                if(create.getConnections() == 0)
                    break;
                if(idx != app.connectcounter - 1)
                    playerList += create.getUserId(idx) + "\n";
                else
                    playerList += create.getUserId(idx);
            }
            //Check if the server thread dies due to exception.
            if(!create.isAlive())
            {
                disconnectAll();
                msg = "Server died unexpectedly.";
                IPad = "Standing by.";
                msglog = "";
            }
        }

        // app.font40.draw(app.batch, serverName, x - fex, y + fey + 120);
        // app.font40.draw(app.batch, player1, x - fmx, y + fmy + 50);
        //  app.font40.draw(app.batch, playerList, 75, y + fmy + 250);
        app.font40.draw(app.batch, msg, w/2 - fmsgx, h/2 + fmsgy + 265);
        //app.font40.draw(app.batch, msglog, w/2 - fmlx, h/2 - 65 + fmly + 265);
        //app.font40.draw(app.batch, error, w / 2 - ferx, h / 2 + 65 + fery + 265);

        app.batch.end();
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            System.out.println("Back key was pressed");
            Gdx.app.exit();
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

    @Override
    public void dispose() {
        stage.dispose();
        stageBackground.dispose();
    }

    private void initButtons()
    {
        Table table = new Table(skin);
        stage.addActor(table);
        // table.setDebug(true);
        table.setFillParent(true);

        labelStyle = new Label.LabelStyle(app.font40, Color.BLACK);
        labelList = new Label( playerList, labelStyle);
        labelPlayer = new Label(player1, labelStyle);
        labelServer = new Label(serverName, labelStyle);

        buttonDisconnect = new TextButton("Disconnect", skin, "default8");
        stage.addActor(buttonDisconnect);
        buttonDisconnect.setSize(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/6);
        buttonDisconnect.setPosition(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() / 3));
        buttonDisconnect.addAction(sequence(alpha(0), parallel(fadeIn(1.0f), moveBy(0, -1, 0.5f, Interpolation.pow5Out))));
        buttonDisconnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                msg = "Disconnecting...";
                disconnectAll();
                msg = "Disconnected.";
                msglog = "Log.";
                error = "No error";
                playerList = "";
                app.setScreen(new ConnectionMenuScreen(app));
            }
        });


        buttonReady = new TextButton("Ready", skin, "default8");
        stage.addActor(buttonReady);
        buttonReady.setSize(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/6);
        buttonReady.setPosition((Gdx.graphics.getWidth() / 6) * 3, (Gdx.graphics.getHeight() / 3));
        buttonReady.addAction(sequence(alpha(0), parallel(fadeIn(1.0f), moveBy(0, -1, 0.5f, Interpolation.pow5Out))));
        buttonReady.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (create.getConnections() != 0) {
                    Boolean rdy = create.checkReadyState();
                    if (rdy) {
                        create.sendReadyMsg();
                        PropertiesSingleton.getInstance().setNrPlayers(create.getConnections() + 1);
                        //app.gameScreen = new GameScreen(app);

                        DataHolder.getInstance().setActivateCamera(true);
                        app.setScreen(new ChooseIslandScreen(app));
                    }
                    msg = "Not all players are ready.";
                }
                msg = "No connections present.";
            }
        });

        buttonStandard = new TextButton("Standard", skin, "default8");
        stage.addActor(buttonStandard);
        buttonStandard.setSize((Gdx.graphics.getWidth()/24)*5, Gdx.graphics.getHeight()/7);
        buttonStandard.setPosition((Gdx.graphics.getWidth() / 24) * 4, (Gdx.graphics.getHeight() / 6));
        buttonStandard.addAction(sequence(alpha(0), parallel(fadeIn(1.0f), moveBy(0, -1, 1.5f, Interpolation.pow5Out))));
        buttonStandard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().addAction(Actions.sequence(Actions.delay(0.0f), Actions.parallel(fadeOut(0.1f), moveBy(-150, 0, 0.5f, Interpolation.pow5Out)),
                        Actions.run(new Runnable() {
                            public void run() {
                                app.setScreen(new MainMenyScreen(app));

                            }
                        })));
            }
        });


        buttonZombie = new TextButton("Zombie", skin, "default8");
        stage.addActor(buttonZombie);
        buttonZombie.setSize((Gdx.graphics.getWidth()/24)*5, Gdx.graphics.getHeight()/7);
        buttonZombie.setPosition((Gdx.graphics.getWidth() / 48) * 19, (Gdx.graphics.getHeight() / 6));
        buttonZombie.addAction(sequence(alpha(0), parallel(fadeIn(1.0f), moveBy(0, -1, 1.5f, Interpolation.pow5Out))));

        buttonTeam = new TextButton("Team", skin, "default8");
        stage.addActor(buttonTeam);
        buttonTeam.setSize((Gdx.graphics.getWidth()/24)*5, Gdx.graphics.getHeight()/7);
        buttonTeam.setPosition((Gdx.graphics.getWidth() / 24) * 15, (Gdx.graphics.getHeight() / 6));
        buttonTeam.addAction(sequence(alpha(0), parallel(fadeIn(1.0f), moveBy(0, -1, 1.5f, Interpolation.pow5Out))));


        table.add(labelServer).top();
        table.row();
        table.add(labelPlayer).top();
        table.row();
        table.add(labelList).top().padBottom(Gdx.graphics.getHeight()/2);
        table.row();

        stage.addActor(table);

    }


    private void initScrollMenu(){



    }

    public void disconnectAll()
    {
        //Disconnect any active connections, or servers.
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
}