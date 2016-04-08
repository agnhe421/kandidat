package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
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
    private Stage stage;
    private Rectangle viewport;
    private Skin skin;
    // width och heigth
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    // Texture
    private Texture background;
    // Buttons
    private TextButton buttonCreate, buttonJoin, buttonExit,buttonDisconnect, buttonBack, ButtonSkipServer;
    // Nätverk
    //public Integer connectcounter = 0;
    private String msg = "msg", error ="No Error", IPad = "IP", serverIPad = "";
    public CreateServer create;
    JoinServer join;
    Boolean hardexit = false, joinbool = false, createbool = false;
    private SendPacket sendPacket;


    public ConnectionMenuScreen(final MyGdxGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.viewport = new Rectangle();

        create = null;
        join = null;
    }

    // Kallas varje gång man vill att denna screen ska visas
    @Override
    public void show() {
        System.out.println("Show");
        Gdx.input.setInputProcessor(stage); // hanterar olika input events
        stage.clear();

        /*this.skin = new Skin();
        //this.skin.load(Gdx.files.internal("ui/uiskin.json"));
        this.skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        this.skin.add("default-font", app.font50); // Sätter defaulf font som vår ttf font*/

        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("ui/Buttons.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font50); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/Buttons.json"));

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
        app.batch.draw(background, Gdx.graphics.getHeight() / 2 - background.getHeight() / 2, Gdx.graphics.getWidth() / 2 - background.getWidth() / 2);
        GlyphLayout glyphLayoutmsg = new GlyphLayout(), glyphLayouterror = new GlyphLayout(), glyphLayoutIP = new GlyphLayout();
        glyphLayoutmsg.setText(app.font50, msg);
        glyphLayouterror.setText(app.font50, error);
        glyphLayoutIP.setText(app.font50, IPad);
        float fex = glyphLayouterror.width/2, fey = glyphLayouterror.height/2;
        float fmx = glyphLayoutmsg.width/2, fmy = glyphLayoutmsg.height/2;
        float fix = glyphLayoutIP.width/2, fiy = glyphLayoutIP.height/2;
        float x = w/2, y = h/2;

        //Only retrieve active messages if the exit command hasn't been invoked. Otherwise, null values may be accessed.
        if(!hardexit)
        {
            //Update server messages
            if(createbool)
            {
                if(!create.checkIfVectorNull())
                    app.connectcounter = create.getConnections();
                msg = create.getMsg();
                error = create.getError();
                app.font50.draw(app.batch, app.connectcounter.toString(), w - 50, h - 25);
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
        }
        //Draw all text on screen. If you don't wish to see the debug, remove the error draw.
        app.font50.draw(app.batch, msg, x - fmx, y + fmy);
        app.font50.draw(app.batch, error, x - fex, y + fey - 300);
        app.font50.draw(app.batch, IPad, x - fix, y + fiy + 300);
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
        background.dispose();
    }

    private void disconnectAll()
    {
        //Disconnect any active connections, or servers.
        if(joinbool && join != null)
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
            joinbool = false;
            join = null;
        }
        else if(joinbool)
            joinbool = false;
        if(createbool && create != null)
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
            createbool = false;
            create = null;
        }
        else if(createbool)
            createbool = false;
        msg = "Disconnected.";
        error = "No Error";
        IPad = "IP";
    }


    private void initButtons() {

        Table table = new Table(skin);
        stage.addActor(table);
        //table.setDebug(true);
        table.setFillParent(true);

        float buttonSizeX = 250, buttonSizeY = 50;
        buttonCreate = new TextButton("", skin, "default4");
        buttonCreate.setPosition(w / 2 - buttonSizeX / 2, h / 2 - 115 + buttonSizeY / 2);
       // buttonCreate.setSize(buttonSizeX, buttonSizeY);
        buttonCreate.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonCreate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createbool = true;
                //No creating and joining a server at the same time. Terminate application.
                if (joinbool && createbool) {
                    disconnectAll();
                    IPad = "IP";
                    msg = "Cannot both connect and join a server.";
                    error = "No error.";
                } else if (create == null) {
                    //Create a new server, update the text accordingly.
                    create = new CreateServer();
                    create.start();
                    IPad = create.getIpAddress();
                    msg = create.getMsg();
                    error = create.getError();
                    app.setScreen(app.pickScreen);
                } else {
                    //Further clicks will only update the text.
                    msg = create.getMsg();
                    error = create.getError();
                    app.setScreen(app.pickScreen);
                }
            }
        });

        buttonJoin = new TextButton("", skin, "default6");
        buttonJoin.setPosition(w / 2 - buttonSizeX / 2, h / 2 - 190 + buttonSizeY / 2);
      //  buttonJoin.setSize(buttonSizeX, buttonSizeY);
        buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonJoin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinbool = true;
                //No creating and joining a server at the same time. Terminate application.
                if (joinbool && createbool) {
                    disconnectAll();
                    IPad = "IP";
                    msg = "Cannot both connect and join a server.";
                    error = "No error.";
                } else if (join == null) {
                    //Check all available units connected to the network and see if anyone has started a server.
                    sendPacket = new SendPacket();
                    sendPacket.start();
                    try {
                        sendPacket.join();
                        serverIPad = sendPacket.getIP();
                        if (sendPacket.getErrorState()) {
                            msg = sendPacket.getMsg();
                            error = sendPacket.getError();
                        }
                        sendPacket = null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                    //If no servers are located, terminate action.
                    if (serverIPad.equals("FAILED_CONNECTION")) {
                        IPad = "No servers online.";
                        joinbool = false;
                    } else {
                        //Connect to the server using the IP given by the server.
                        IPad = "Connecting to: " + serverIPad;
                        join = new JoinServer(serverIPad, app.serverPort, "player");
                        join.start();
                        msg = join.getMsg();
                        error = join.getError();
                    }

                } else {
                    //Further clicks will only update the text.
                    msg = join.getMsg();
                    error = join.getError();
                }
            }
        });

        buttonDisconnect = new TextButton("", skin, "default5");
        buttonDisconnect.setPosition(w / 2 - buttonSizeX / 2, h / 2 + buttonSizeY / 2 - 265);
        //buttonDisconnect.setSize(buttonSizeX, buttonSizeY);
        buttonDisconnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonDisconnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                disconnectAll();
            }
        });

        buttonExit = new TextButton("", skin, "default7");
        buttonExit.setPosition(0, h - buttonSizeY);
        buttonExit.setSize(buttonSizeX, buttonSizeY);
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
