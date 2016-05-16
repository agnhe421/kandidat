package com.qualcomm.vuforia.samples.libGDX;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.qualcomm.vuforia.samples.libGDX.screens.ChooseBallScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.ChooseIslandScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.ConnectionMenuScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.CreateServerScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.GameScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.JoinServerScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.LoadingScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.MainMenyScreen;
import com.qualcomm.vuforia.samples.libGDX.screens.SettingScreen;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;

public class BaseGame extends Game {


    // Define an interface for your various callbacks to the android launcher
    public interface MyGameCallback {
        public void onStartActivityA();
    }

    // Local variable to hold the callback implementation
    private MyGameCallback myGameCallback;

    // ** Additional **
    // Setter for the callback
    public void setMyGameCallback(MyGameCallback callback) {
        myGameCallback = callback;
    }

    public SpriteBatch batch;
    public BitmapFont font40, font120;
    //public OrthographicCamera camera;
    public AssetManager assets;
    public LoadingScreen loadingScreen;
    public SettingScreen settingScreen;
    public MainMenyScreen mainMenyScreen;
    public ConnectionMenuScreen connectionMenuScreen;
    public JoinServerScreen joinServerScreen;
    public CreateServerScreen createServerScreen;
    public GameScreen gameScreen;

    public AssetManager GameAssets;
    public Array<String> islandNames;
    public Array<String> ballNames;

    public static final int VIRTUAL_WIDTH = 3840;
    public static final int VIRTUAL_HEIGHT = 2160;
    public static final float ASPECT_RATIO = 1.7f;
    public Camera camera;
    public Integer connectcounter = 0;
    public final int serverPort = 8081;

    @Override
    public void create() {
        assets = new AssetManager();
        camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch = new SpriteBatch();

        GameAssets = new AssetManager();
        islandNames = new Array<String>();
        ballNames = new Array<String>();

        loadingScreen = new LoadingScreen(this);
        settingScreen = new SettingScreen(this);
        mainMenyScreen = new MainMenyScreen(this);
        connectionMenuScreen = new ConnectionMenuScreen(this);
        joinServerScreen = new JoinServerScreen(this);
        createServerScreen = new CreateServerScreen(this);


        //gameScreen = new GameScreen(this);


//        settingScreen.initMusic(); //call the function initMusic() through the class settingScreen
        initFonts();
        setScreen(new LoadingScreen(this));
    }

    private void initFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copyfonts.com_gulim.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 60;
        params.color = Color.BLACK;
        font40 = generator.generateFont(params);

        params.size = 160;
        params.color = Color.BLACK;
        font120 = generator.generateFont(params);
        generator.dispose();
    }

    public void launchVuforia() {
        // check the calling class has actually implemented MyGameCallback
        if (myGameCallback != null) {

            // initiate which ever callback method you need.
                myGameCallback.onStartActivityA();
        }

    }
}