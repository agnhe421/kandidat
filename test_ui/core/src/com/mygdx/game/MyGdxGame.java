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
import com.mygdx.game.screens.JoinServerScreen;
import com.mygdx.game.screens.CreateServerScreen;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.screens.MainMenyScreen;
import com.mygdx.game.screens.ConnectionMenuScreen;
import com.mygdx.game.screens.PickScreen;
import com.mygdx.game.screens.SettingScreen;

public class MyGdxGame extends Game {

	//public Viewport viewport;
	public SpriteBatch batch;
	public BitmapFont font40, font120;

	//public OrthographicCamera camera;
	public AssetManager assets;
	public LoadingScreen loadingScreen;
	public SettingScreen settingScreen;
	public MainMenyScreen mainMenyScreen;
	public ConnectionMenuScreen connectionMenuScreen;
	public PickScreen pickScreen;
	public LobbyScreen lobbyScreen;
	public JoinServerScreen joinServerScreen;
	public CreateServerScreen createServerScreen;

	public static final int VIRTUAL_WIDTH = 3840;
	public static final int VIRTUAL_HEIGHT = 2160;
	public static final float ASPECT_RATIO =  1.7f;
	public Camera camera;
	public Integer connectcounter = 0;
    public final int serverPort = 8081;


	@Override
	public void create() {

		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		assets = new AssetManager();
		batch = new SpriteBatch();

		initFonts();

		loadingScreen = new LoadingScreen(this);
		settingScreen = new SettingScreen(this);
		mainMenyScreen = new MainMenyScreen(this);
		connectionMenuScreen = new ConnectionMenuScreen(this);
		pickScreen = new PickScreen(this);
		lobbyScreen = new LobbyScreen(this);
		joinServerScreen = new JoinServerScreen(this);
		createServerScreen = new CreateServerScreen(this);

		settingScreen.initMusic(); //call the function initMusic() through the class settingScreen
		this.setScreen(loadingScreen);

	}

	@Override
	public void dispose() {
		batch.dispose();
		font40.dispose();
        font120.dispose();
		assets.dispose();
		loadingScreen.dispose();
		settingScreen.dispose();
		mainMenyScreen.dispose();
		connectionMenuScreen.dispose();
		pickScreen.dispose();
		lobbyScreen.dispose();
        joinServerScreen.dispose();
	}

	private void initFonts(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copyfonts.com_gulim.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.size = 40;
		params.color = Color.BLACK;
		font40 = generator.generateFont(params);

		params.size = 120;
		params.color = Color.BLACK;
		font120 = generator.generateFont(params);
		generator.dispose();
	}
}