package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.screens.MainMenyScreen;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.screens.SettingScreen;

public class MyGdxGame extends Game {

	//public Viewport viewport;
	public SpriteBatch batch;
	public BitmapFont font24;

	//public OrthographicCamera camera;
	public AssetManager assets;
	public LoadingScreen loadingScreen;
	public SettingScreen settingScreen;
	public MainMenyScreen mainMenyScreen;
	public LobbyScreen lobbyScreen;


	public static final int VIRTUAL_WIDTH = 3840;
	public static final int VIRTUAL_HEIGHT = 2160;
	//public static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	public static final float ASPECT_RATIO =  1.7f;
	public Camera camera;
	private Rectangle viewport;
	private SpriteBatch sb;
	private Texture backgroundImage;

	@Override
	public void create() {

		sb = new SpriteBatch();
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

		assets = new AssetManager();
		batch = new SpriteBatch();

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

		// update camera
		camera.update();

		// set viewport
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
				(int) viewport.width, (int) viewport.height);

		// clear previous frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		super.render(); // måste vara super.render
	}

	@Override
	public void resize(int width, int height) {

		//calculate new viewport
		float aspectRatio = (float)width/(float)height;
		float scale = 1f;
		Vector2 crop = new Vector2(0f, 0f);

		if(aspectRatio > ASPECT_RATIO)
		{
			scale = (float)height/(float)VIRTUAL_HEIGHT;
			crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
		}
		else if(aspectRatio < ASPECT_RATIO)
		{
			scale = (float)width/(float)VIRTUAL_WIDTH;
			crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
		}
		else
		{
			scale = (float)width/(float)VIRTUAL_WIDTH;
		}

		float w = (float)VIRTUAL_WIDTH*scale;
		float h = (float)VIRTUAL_HEIGHT*scale;
		viewport = new Rectangle(crop.x, crop.y, w, h);
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