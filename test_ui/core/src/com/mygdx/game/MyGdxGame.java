package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.screens.MainMenyScreen;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.screens.SettingScreen;

public class MyGdxGame extends Game {

	public static final String TITLE = "UI_TEST";
	public static final float VERSION = 1.0f;
	public static final int V_WIDTH = 480;
	public static final int V_HEIGTH = 420;
	public SpriteBatch batch;
	public BitmapFont font24;
	public OrthographicCamera camera;
	public AssetManager assets;
	public LoadingScreen loadingScreen;
	public SettingScreen settingScreen;
	public MainMenyScreen mainMenyScreen;
	public LobbyScreen lobbyScreen;


	@Override
	public void create() {
		assets = new AssetManager();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGTH);

		initFonts();

		font24 = new BitmapFont();
		font24.setColor(Color.BLACK) ;

		loadingScreen = new LoadingScreen(this);
		settingScreen = new SettingScreen(this);
		mainMenyScreen = new MainMenyScreen(this);
		lobbyScreen = new LobbyScreen(this);

		this.setScreen(loadingScreen);
	}


	@Override
	public void render() {

		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
		{
			Gdx.app.exit();
		}

		super.render(); // måste vara super.render
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
	public void dispose() {
		batch.dispose();
		font24.dispose();
		assets.dispose();
		loadingScreen.dispose();
		settingScreen.dispose();
		mainMenyScreen.dispose();
		lobbyScreen.dispose();
	}

	// Hur man lägger till egna ttf fonts i Libgdx
	private void initFonts(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Arcon.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.size = 50;
		params.color = Color.BLACK;
		font24 = generator.generateFont(params);
	}
}