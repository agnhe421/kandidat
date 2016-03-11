package com.mygdx.game;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MyGdxGame extends ApplicationAdapter //implements InputProcessor
{
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	int screenWidth, screenHeight;
	private String msg = "Can't touch dis.", error = "I am error.", serverip = "IP";
	boolean joinbool, createbool, hardexit = false;
	CreateServer create;
	JoinServer join;
	Skin skin;
	Stage stage;
	TextButton buttonCreate, buttonJoin, buttonCheck, buttonExit;
	TextButton.TextButtonStyle buttonCreateStyle, buttonJoinStyle;
	public AssetManager assManager;
	public ServerInterface sInterface;

	/*class TouchInfo
	{
		public float touchX, touchY;
		public boolean touched = false;
	}*/

	//private Map<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();

	@Override
	public void create () {
		stage = new Stage();
		assManager = new AssetManager();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		create = new CreateServer();
		join = new JoinServer("172.20.10.5", 8080, "Temp");
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Old_London.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 36;
		parameter.color = Color.BLACK;
		font = generator.generateFont(parameter);
		generator.dispose();
		assManager.load("Knappar/uiskin.atlas", TextureAtlas.class);
		while(!assManager.isLoaded("Knappar/uiskin.atlas", TextureAtlas.class))
		{
			assManager.update();
		}
		skin = new Skin();
		skin.addRegions(this.assManager.get("Knappar/uiskin.atlas", TextureAtlas.class));
		skin.add("default-font", font);
		skin.load(Gdx.files.internal("Knappar/uiskin.json"));
		//font = new BitmapFont();
		//Gdx.app.log("HEJ!!!", "DÄR!!!");
		Gdx.input.setInputProcessor(stage);
		/*for(int idf = 0; idf < 5; ++idf)
		{
			touches.put(idf, new TouchInfo());
		}*/
		initButtons();
	}

	public void initButtons()
	{
		float buttonSizeX = 250, buttonSizeY = 50;
		buttonCreate = new TextButton("Create Server", skin, "default");
		buttonCreate.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight/2 - 115 + buttonSizeY / 2);
		buttonCreate.setSize(buttonSizeX, buttonSizeY);
		buttonCreate.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonCreate.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				createbool = true;
				if(joinbool && createbool)
				{
					Gdx.app.log("FATAL ERROR: ", "Cannot create both a server and join one.");
					create.stopServer();
					join.disconnect();
					try
					{
						join.join();
						create.join();
					}catch(InterruptedException e)
					{
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
				else if(!create.isAlive())
				{
					create.start();
					serverip = "IP: " + create.getIpAddress() + ":" + create.SOCKETSERVERPORT;
				}
				else
				{
					Gdx.app.log("ATTENTION: ", "Stopping server.");
					create.stopServer();
					try
					{
						Gdx.app.log("ATTENTION: ", "Joining Threads.");
						create.join();
					}catch(InterruptedException e)
					{
						e.printStackTrace();
						Gdx.app.log("EXCEPTION: ", e.toString());
					}
					if(create.isAlive())
					{
						Gdx.app.log("FATAL ERROR: ", "Thread still alive, exiting to prevent errors.");
						Gdx.app.exit();
					}
					else
					{
						create = new CreateServer();
						create.start();
						serverip = "IP: " + create.getIpAddress() + ":" + create.SOCKETSERVERPORT;
					}

				}
			}
		});

		buttonJoin = new TextButton("Join Server", skin, "default");
		buttonJoin.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight/2 - 190 + buttonSizeY / 2);
		buttonJoin.setSize(buttonSizeX, buttonSizeY);
		buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonJoin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				joinbool = true;
				if(joinbool && createbool) {
					Gdx.app.log("FATAL ERROR: ", "Cannot create both a server and join one.");
					if(create.isAlive())
						create.stopServer();
					if(join.isAlive())
						join.disconnect();
					try
					{
						join.join();
						create.join();
					}catch(InterruptedException e)
					{
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
				else if(!join.isAlive())
				{
					join.start();
					serverip = "Join Server Mode Activated!";
				}
				else
				{
					join.disconnect();
					try
					{
						join.join();
					}catch(InterruptedException e)
					{
						e.printStackTrace();
						Gdx.app.log("EXCEPTION: ", e.toString());
					}
					if(join.isAlive())
					{
						Gdx.app.log("FATAL ERROR: ", "Thread still alive, exiting to prevent errors.");
						Gdx.app.exit();
					}
					else
					{
						join = new JoinServer("172.20.10.5", 8080, "Temp");
						join.start();
						serverip = "Join Server Mode Activated!";
					}

				}
				/*if(join.isConnected())
				{
					msg = "Connected! Well done! :D";
				}
				else
				{
					msg = join.getError();
				}*/
			}
		});

		buttonExit = new TextButton("Exit app", skin, "default");
		buttonExit.setPosition(0, screenHeight - buttonSizeY);
		buttonExit.setSize(buttonSizeX, buttonSizeY);
		buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonExit.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				if(createbool)
					create.stopServer();
				Gdx.app.log("ATTENTION: ", "Exit command executed.");
				Gdx.app.exit();
			}
		});

		/*buttonCheck = new TextButton("Check Error", skin, "default");
		buttonCheck.setPosition(screenWidth/2 - buttonSizeX/2, screenHeight/2 - 265 + buttonSizeY/2);
		buttonCheck.setSize(buttonSizeX, buttonSizeY);
		buttonCheck.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonCheck.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(join.isAlive())
					msg = join.getError();
			}
		});*/

		stage.addActor(buttonCreate);
		stage.addActor(buttonJoin);
		stage.addActor(buttonExit);
		//stage.addActor(buttonCheck);
	}

	public void update() {stage.act();}

	@Override
	public void dispose()
	{
		batch.dispose();
		img.dispose();
		assManager.dispose();

	}

	@Override
	public void render () {
		boolean clearText = false;
		boolean fingersExist = false;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//BitmapFont.TextBounds textSize = font.getBounds(msg);
		update();
		stage.draw();
		batch.begin();
		//batch.draw(img, 0, 0);

		/*for(int idf = 0; idf < 5; ++idf) {
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
		}*/
		GlyphLayout glyphLayoutmsg = new GlyphLayout();
		glyphLayoutmsg.setText(font, msg);
		float fontx = glyphLayoutmsg.width/2, fonty = glyphLayoutmsg.height/2;
		GlyphLayout glyphLayouterror = new GlyphLayout();
		glyphLayouterror.setText(font, error);
		float fontex = glyphLayouterror.width/2, fontey = glyphLayouterror.height/2;
		GlyphLayout glyphLayoutIP = new GlyphLayout();
		glyphLayoutIP.setText(font, serverip);
		float fontix = glyphLayoutIP.width/2, fontiy = glyphLayoutIP.height/2;
		float x = screenWidth/2, y = screenHeight/2;
		if(!hardexit)
		{
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
		}
		font.draw(batch, msg, x - fontx, y + fonty);
		font.draw(batch, error, x - fontex, y + fontey - 300);
		font.draw(batch, serverip, x - fontix, y + fontiy + 300);
		batch.end();
	}
/*
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
	*/
}
