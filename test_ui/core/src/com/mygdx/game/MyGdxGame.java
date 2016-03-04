package com.mygdx.game;import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.screens.MainMenyScreen;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.screens.SplashScreen;

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
	public SplashScreen splashScreen;
	public MainMenyScreen mainMenyScreen;
	public PlayScreen playScreen;


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
		splashScreen = new SplashScreen(this);
		mainMenyScreen = new MainMenyScreen(this);
		playScreen = new PlayScreen(this);

		this.setScreen(loadingScreen);
	}


	@Override
	public void render() {

		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
		{
			Gdx.app.exit();
		}
		//batch.begin();
		//font.draw(batch, "Hello World", 200, 200);
		//batch.end();
		super.render(); // VIKTIGT ATT DET ÄR SUPER
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
		splashScreen.dispose();
		mainMenyScreen.dispose();
		playScreen.dispose();
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