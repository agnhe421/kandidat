package com.mygdx.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	int screenWidth, screenHeight;
	private String msg = "Can't touch dis.";
	CreateServer create;
	JoinServer join;
	Stage stage;
	TextButton buttonCreate, buttonJoin;
	TextButton.TextButtonStyle buttonCreateStyle, buttonJoinStyle;


	class TouchInfo
	{
		public float touchX, touchY;
		public boolean touched = false;
	}

	private Map<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		create = new CreateServer();
		create.start();
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Old_London.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 36;
		parameter.color = Color.BLACK;
		font = generator.generateFont(parameter);
		generator.dispose();
		//font = new BitmapFont();
		Gdx.app.log("HEJ!!!", "DÄR!!!");
		Gdx.input.setInputProcessor(this);
		for(int idf = 0; idf < 5; ++idf)
		{
			touches.put(idf, new TouchInfo());
		}
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		font.dispose();
		img.dispose();
	}

	@Override
	public void render () {
		boolean clearText = false;
		boolean fingersExist = false;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//BitmapFont.TextBounds textSize = font.getBounds(msg);
		batch.begin();
		//batch.draw(img, 0, 0);
		for(int idf = 0; idf < 5; ++idf) {
			if (touches.get(idf).touched) {
				msg = "Finger: " + Integer.toString(idf) + " touched me at: " +
						Float.toString(touches.get(idf).touchX) + ", " +
						Float.toString(touches.get(idf).touchY) + "\n";
				clearText = false;
				fingersExist = true;
			}
			else if(!touches.get(idf).touched && fingersExist == false)
			{
				clearText = true;
			}
			else
			{
				clearText = false;
			}
		}
		if(clearText)
		{
			msg = "Can't touch dis!";
			clearText = false;
			fingersExist = false;
		}
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(font, msg);
		float fontx = glyphLayout.width/2, fonty = glyphLayout.height/2;
		float x = screenWidth/2 - fontx, y = screenHeight/2 + fonty;
		font.draw(batch, msg, x, y);
		batch.end();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer < 5)
		{
			touches.get(pointer).touchX = screenX;
			touches.get(pointer).touchY = screenY;
			touches.get(pointer).touched = true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer < 5)
		{
			touches.get(pointer).touchX = 0;
			touches.get(pointer).touchY = 0;
			touches.get(pointer).touched = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//msg = "Gech!";
		if(pointer < 5)
		{
			touches.get(pointer).touchX = screenX;
			touches.get(pointer).touchY = screenY;
			touches.get(pointer).touched = true;
		}
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
