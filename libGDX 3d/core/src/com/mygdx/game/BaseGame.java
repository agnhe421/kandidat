package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class BaseGame extends Game {

    public SpriteBatch batch;
    public BitmapFont font40, font120;
    //public OrthographicCamera camera;
    public AssetManager assets;
    public LoadingScreen loadingScreen;
    public SettingScreen settingScreen;
    public MainMenyScreen mainMenyScreen;
    public ConnectionMenuScreen connectionMenuScreen;
    public LobbyScreen lobbyScreen;
    public JoinServerScreen joinServerScreen;
    public CreateServerScreen createServerScreen;
    public GameScreen gameScreen;

    public static final int VIRTUAL_WIDTH = 3840;
    public static final int VIRTUAL_HEIGHT = 2160;
    public static final float ASPECT_RATIO =  1.7f;
    public Camera camera;
    public Integer connectcounter = 0;
    public final int serverPort = 8081;

    @Override
    public void create() {
        assets = new AssetManager();
        camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch = new SpriteBatch();

        loadingScreen = new LoadingScreen(this);
        settingScreen = new SettingScreen(this);
        mainMenyScreen = new MainMenyScreen(this);
        connectionMenuScreen = new ConnectionMenuScreen(this);
        lobbyScreen = new LobbyScreen(this);
        joinServerScreen = new JoinServerScreen(this);
        createServerScreen = new CreateServerScreen(this);
        //gameScreen = new GameScreen(this);


//        settingScreen.initMusic(); //call the function initMusic() through the class settingScreen
        initFonts();
        setScreen(new LoadingScreen(this));
    }

    private void initFonts(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copyfonts.com_gulim.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 40;
        params.color = Color.WHITE;
        font40 = generator.generateFont(params);

        params.size = 120;
        params.color = Color.WHITE;
        font120 = generator.generateFont(params);
        generator.dispose();
    }

}