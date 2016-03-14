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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyGdxGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*; // Bra att importera!

/**
 * Created by sofiekhullar on 16-03-02.
 */
public class LobbyScreen implements Screen {

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
    private TextButton buttonCreate, buttonJoin, buttonExit, buttonBack, ButtonSkipServer;
    // Nätverk
    private String msg = "Can't touch dis.", error = "I am error.", serverip = "IP";
    boolean joinbool, createbool, hardexit = false;
    CreateServer create;
    JoinServer join;


    public LobbyScreen(final MyGdxGame app){
        this.app = app;
        this.stage = new Stage(new StretchViewport(w , h));
        this.viewport = new Rectangle();
        this.create = new CreateServer();
        this.join = new JoinServer("172.20.10.5", 8080, "Temp");
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
        this.skin.addRegions(app.assets.get("ui/menuskin.pack", TextureAtlas.class));
        this.skin.add("default-font", app.font50); // Sätter defaulf font som vår ttf font
        this.skin.load(Gdx.files.internal("ui/menuskin.json"));

        background = app.assets.get("img/background1.jpg", Texture.class);

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

        if(createbool)
        {
            msg = create.getMsg();
            error = create.getError();
        }
        else if(joinbool)
        {
            msg = join.getMsg();
            error = join.getError();
        }
        if(!join.isAlive() && joinbool)
        {
            serverip = "Join mode exited!";
            msg = "msg";
            joinbool = false;
            join = new JoinServer("172.20.10.5", 8080, "Temp");
        }

        app.batch.begin();
        app.batch.draw(background, Gdx.graphics.getHeight() / 2 - background.getHeight() / 2, Gdx.graphics.getWidth() / 2 - background.getWidth() / 2);
        app.font50.draw(app.batch, "Screen: Lobby", 30, 30);
        app.font50.draw(app.batch, msg, 500, 700);
        app.font50.draw(app.batch, error, 500, 740);
        app.font50.draw(app.batch, serverip, 500, 680);
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
        System.out.println("dispose");
        stage.dispose();
    }

    private void initButtons() {

        float buttonSizeX = 250, buttonSizeY = 50;
        buttonCreate = new TextButton("Create Server", skin, "default");
        buttonCreate.setPosition(w / 2 - buttonSizeX / 2, h / 2 - 115 + buttonSizeY / 2);
        buttonCreate.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonCreate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Set create state to true.
                createbool = true;
                //If both join state and create state is active at the same time, exit the application to prevent errors.
                if (joinbool && createbool) {
                    Gdx.app.log("FATAL ERROR: ", "Cannot create both a server and join one.");
                    //KILL EVERYTHING!!!
                    if (create.isAlive())
                        create.stopServer();
                    if (join.isAlive())
                        join.disconnect();
                    //Hold until both threads are DEAD!!!
                    try {
                        join.join();
                        create.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Gdx.app.log("EXCEPTION: ", e.toString());
                    }
                    create = null;
                    join = null;
                    createbool = false;
                    joinbool = false;
                    hardexit = true;
                    Gdx.app.exit();
                }
                //If the thread is inactive, start it.
                else if (!create.isAlive()) {
                    create.start();
                    serverip = "IP: " + create.getIpAddress() + ":" + create.SOCKETSERVERPORT;
                } else {
                    //Kill the thread and reassign it so it can be started anew.
                    Gdx.app.log("ATTENTION: ", "Stopping server.");
                    create.stopServer();
                    try {
                        Gdx.app.log("ATTENTION: ", "Joining Threads.");
                        create.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Gdx.app.log("EXCEPTION: ", e.toString());
                    }
                    if (create.isAlive()) {
                        Gdx.app.log("FATAL ERROR: ", "Thread still alive, exiting to prevent errors.");
                        Gdx.app.exit();
                    } else {
                        create = new CreateServer();
                        create.start();
                        serverip = "IP: " + create.getIpAddress() + ":" + create.SOCKETSERVERPORT;
                    }

                }
            }
        });

        buttonJoin = new TextButton("Join Server", skin, "default");
        buttonJoin.setPosition(w / 2 - buttonSizeX / 2, h / 2 - 190 + buttonSizeY / 2 - 50);
        buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonJoin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Set join state to true.
                joinbool = true;
                //If both join state and create state is active at the same time, exit the application to prevent errors.
                if (joinbool && createbool) {
                    Gdx.app.log("FATAL ERROR: ", "Cannot create both a server and join one.");
                    //KILL EVERYTHING!!!
                    if (create.isAlive())
                        create.stopServer();
                    if (join.isAlive())
                        join.disconnect();
                    //Hold until both threads are DEAD!!!
                    try {
                        join.join();
                        create.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Gdx.app.log("EXCEPTION: ", e.toString());
                    }
                    create = null;
                    join = null;
                    createbool = false;
                    joinbool = false;
                    hardexit = true;
                    Gdx.app.exit();
                }
                //If the thread is inactive, start it.
                else if (!join.isAlive()) {
                    join.start();
                    serverip = "Join Server Mode Activated!";
                } else {
                    //Kill the thread and reassign it so it can be started anew.
                    join.disconnect();
                    try {
                        join.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Gdx.app.log("EXCEPTION: ", e.toString());
                    }
                    if (join.isAlive()) {
                        Gdx.app.log("FATAL ERROR: ", "Thread still alive, exiting to prevent errors.");
                        Gdx.app.exit();
                    } else {
                        join = new JoinServer("172.20.10.5", 8080, "Temp");
                        join.start();
                        serverip = "Join Server Mode Activated!";
                    }

                }
            }
        });

        buttonExit = new TextButton("Exit app", skin, "default");
        buttonExit.setPosition(0, h - buttonSizeY);
        buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //KILL EVERYTHING!!!
                if (createbool)
                    create.stopServer();
                if (joinbool)
                    join.disconnect();
                Gdx.app.log("ATTENTION: ", "Exit command executed.");
                try {
                    join.join();
                    create.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Gdx.app.log("EXCEPTION: ", e.toString());
                }
                create = null;
                join = null;
                createbool = false;
                joinbool = false;
                hardexit = true;
                Gdx.app.exit();
            }
        });

        buttonBack = new TextButton("", skin, "default4");
        buttonBack.setPosition(20, Gdx.graphics.getHeight() - 30);
        buttonBack.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.mainMenyScreen);
            }
        });

        ButtonSkipServer = new TextButton("Skip Server", skin, "default");
        ButtonSkipServer.setPosition(Gdx.graphics.getWidth() -150, Gdx.graphics.getHeight() - 100);
        ButtonSkipServer.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        ButtonSkipServer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.pickScreen);
            }
        });

        stage.addActor(ButtonSkipServer);
        stage.addActor(buttonBack);
        stage.addActor(buttonCreate);
        stage.addActor(buttonJoin);
        //stage.addActor(buttonExit);
    }
}
