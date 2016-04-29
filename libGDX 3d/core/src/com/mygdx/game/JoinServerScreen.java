package com.mygdx.game;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Vector;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by sofiekhullar on 16-04-13.
 */
public class JoinServerScreen implements Screen{

    // App reference
    private final BaseGame app;
    private Stage stage, stageBackground;

    private TextButton buttonBack, buttonConnect, buttonRefresh, buttonDisconnect;
    private Skin skin;

    private String playerName = "Player 1", chooseServer = "Choose server";
    private String serverIPad = "IP", msg = "msg", error = "error", msglog = "log";
    private Vector<String> serverIPs;
    private Vector<TextButton> buttonServerList;
    private Boolean sendFail;
    private SendPacket sendPacket;
    public JoinServer join;
    private float buttonSizeX = 250, buttonSizeY = 50;

    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();

    public JoinServerScreen(final BaseGame app)
    {
        serverIPs = new Vector<String>();
        buttonServerList = new Vector<TextButton>();
        sendFail = false;
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.stageBackground = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {
        System.out.println("Join server");
        Gdx.input.setInputProcessor(stage);
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

        initButtons();
    }

    @Override
    public void render(float delta)
    {

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stageBackground.draw();

        GlyphLayout glyphPlayerName = new GlyphLayout();
        glyphPlayerName.setText(app.font40, playerName);

        GlyphLayout glyphChooseServer = new GlyphLayout();
        glyphChooseServer.setText(app.font40, chooseServer);

        GlyphLayout glyphLayoutmsg = new GlyphLayout(), glyphLayouterror = new GlyphLayout(), glyphLayoutlog = new GlyphLayout();

        float fpx = glyphPlayerName.width/2, fpy = glyphPlayerName.height/2;
        float fcx = glyphChooseServer.width/2, fcy = glyphChooseServer.height/2;
        float fmsgx = glyphLayoutmsg.width/2, fmsgy = glyphLayoutmsg.height/2;
        float fmlx = glyphLayoutlog.width/2, fmly = glyphLayoutlog.height/2;
        float ferx = glyphLayouterror.width/2, fery = glyphLayouterror.height/2;
        float x = w/2, y = h/2;

        if(join != null)
        {
            if(join.getAllReadyState())
            {
                PropertiesSingleton.getInstance().setNrPlayers(join.getPlayerAmount());
                app.gameScreen = new GameScreen(app);
                if(join == null)
                    Gdx.app.log("HEJ!", "JoinServerScreen: Join is null.");
                app.setScreen(app.gameScreen);
            }

            msg = join.getMsg();
            error = join.getError();
            msglog = join.getLog();
            if(join.connected())
            //IPad = "Connected to: " + serverIPad + ".";
            //This disconnects the join function if the server disconnects. Assuming that the
            //phone receives the SERVER_SHUTDOWN message before the server shuts down completely.
            if(join.getMsg().equals("Server is offline."))
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
                //IPad = "Standing by.";
                msg = "Server disconnected.";
            }
            //Check if the join thread dies due to exception.
            else if(!join.isAlive())
            {
                disconnectAll();
                msg = "Heartbeat died.";
                //IPad = "Standing by.";
                msglog = join.getLog();
            }

        }

        app.batch.begin();
        app.font40.draw(app.batch, chooseServer, x - fcx, h - fcy);
        app.font40.draw(app.batch, playerName, x - fpx, h - fpy * 5);
        app.font40.draw(app.batch, msg, w/2 - fmsgx, h/2 + fmsgy);
        app.font40.draw(app.batch, msglog, w/2 - fmlx, h/2 - 65 + fmly);
        app.font40.draw(app.batch, error, w/2 - ferx, h/2 + 65 + fery);
        app.batch.end();

        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            System.out.println("Back key was pressed");
            app.setScreen(app.connectionMenuScreen);
        }
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

        Gdx.app.log("Errorlog", error);
        msg = "Disconnected.";
        error = "No Error";
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

    private void initButtons()
    {
        //TODO Lägga in buttonConnect i table, oklart varför det inte fungerar nu

        /*int space = 0;
        Table table = new Table(skin);
        stage.addActor(table);
        table.setFillParent(true);

            buttonConnect = new TextButton("Server 1", skin, "default");
            buttonConnect.setWidth(w - w/3);
            buttonConnect.getLabel().setAlignment(Align.left);
            buttonConnect.setPosition(w / 2 - buttonConnect.getWidth() / 2, h / 2 - buttonConnect.getHeight() / 2 - space);
            buttonConnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
            space += 20;
            System.out.print(space);
            buttonConnect.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    buttonConnect.setText("Server 1        Connected: " + nr_connected_players);
                }
            });
        */
        buttonRefresh = new TextButton("Refresh list", skin, "default8");
        buttonRefresh.setSize(buttonSizeX, buttonSizeY);
        buttonRefresh.setPosition(w / 2 - buttonSizeX / 2, h / 6 + 65 * 2);
        buttonRefresh.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonRefresh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                serverIPad = "";
                if (!serverIPs.isEmpty()) {
                    serverIPs.clear();
                    serverIPs = new Vector<String>();
                }
                if (!buttonServerList.isEmpty()) {
                    for (int ids = 0; ids < buttonServerList.size(); ++ids) {
                        buttonServerList.get(ids).remove();
                    }
                    buttonServerList.clear();
                    buttonServerList = new Vector<TextButton>();
                }
                sendPacket = new SendPacket();
                sendPacket.start();
                try {
                    sendPacket.join();
                    serverIPs = sendPacket.getIPs();
                    if (sendPacket.getErrorState()) {
                        msg = sendPacket.getMsg();
                        error = sendPacket.getError();
                        sendFail = sendPacket.getErrorState();
                    }
                    sendPacket = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
                if (sendFail) {
                } else {
                    for (int ids = 0; ids < serverIPs.size(); ++ids) {
                        addServerButton(serverIPs.get(ids), ids);
                    }
                    for (int ids = 0; ids < buttonServerList.size(); ++ids) {
                        stage.addActor(buttonServerList.get(ids));
                    }
                    chooseServer = "Servers found: " + serverIPs.size();
                }
            }

        });

        buttonDisconnect = new TextButton("Disconnect", skin, "default8");
        buttonDisconnect.setSize(buttonSizeX, buttonSizeY);
        buttonDisconnect.setPosition(w / 2 - buttonSizeX / 2, h / 6 - 65);
        buttonDisconnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonDisconnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                disconnectAll();
                msg = "Disconnected.";
                msglog = "Log.";
                error = "No error";
            }
        });

        buttonConnect = new TextButton("Connect", skin, "default8");
        buttonConnect.setSize(buttonSizeX, buttonSizeY);
        buttonConnect.setPosition(w / 2 - buttonSizeX / 2, h / 6 + 65);
        buttonConnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonConnect.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (join == null) {
                    if (!serverIPs.isEmpty()) {
                        serverIPs.clear();
                        serverIPs = new Vector<String>();
                    }
                    if (!buttonServerList.isEmpty()) {
                        for (int ids = 0; ids < buttonServerList.size(); ++ids) {
                            buttonServerList.get(ids).remove();
                        }
                        buttonServerList.clear();
                        buttonServerList = new Vector<TextButton>();
                    }
                    if (serverIPad.equals("")) {
                        error = "No server selected!";
                    } else {
                        //IPad = "Connecting to: " + serverIPad;
                        join = new JoinServer(serverIPad, 8081, "player", app); //All hail Manly Banger, the Rock God!
                        join.start();
                        join.getMsg();
                        join.getError();
                    }
                } else {
                    join.getMsg();
                    join.getError();
                }
            }

        });




        buttonBack = new TextButton("Back", skin, "default8");
        buttonBack.setSize(buttonSizeX, buttonSizeY);
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(20, -20, .5f, Interpolation.pow5Out))));
        buttonBack.setPosition(w / 2 - buttonBack.getWidth() / 2, h / 6);
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(new ConnectionMenuScreen(app));
            }
        });


        stage.addActor(buttonConnect);
        stage.addActor(buttonBack);
        stage.addActor(buttonRefresh);
        stage.addActor(buttonDisconnect);
        //table.add(buttonConnect).  //.left().width(w - w/3).height(40);
        //table.add(buttonBack);

        //stage.addActor(table);
    }

    public void addServerButton(final String ipAdress, int buttID)
    {
        final TextButton buttonServer = new TextButton(ipAdress, skin, "default8");
        float offset = (buttonSizeY + 15)*buttID;
        buttonServer.setPosition(w/2 - (w*(2.0f/3.0f)/2), h/2 + 150 - offset);
        buttonServer.setSize(w*(2.0f/3.0f), 50);
        buttonServer.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonServer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                serverIPad = buttonServer.getText().toString();
            }
        });
        buttonServerList.add(buttonServer);
    }

    @Override
    public void dispose() {
        stage.dispose();
        stageBackground.dispose();

    }
}
