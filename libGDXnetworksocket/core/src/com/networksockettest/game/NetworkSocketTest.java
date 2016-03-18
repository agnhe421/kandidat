package com.networksockettest.game;

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

public class NetworkSocketTest extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	int screenWidth, screenHeight;
	private String msg = "msg", error ="error", IPad = "IP";
	CreateServer create;
	JoinServer join;
	Skin skin;
	Stage stage;
	TextButton buttonCreate, buttonJoin, buttonCheck, buttonExit;
	public AssetManager assManager;
	Boolean hardexit = false, joinbool = false, createbool = false;
	
	@Override
	public void create () {
		stage = new Stage();
		assManager = new AssetManager();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		create = null;
		join = null;
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/print_clearly_tt.ttf"));
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
		Gdx.input.setInputProcessor(stage);
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
			public void clicked(InputEvent event, float x, float y)
			{
				createbool = true;
				if(joinbool && createbool)
				{
					Gdx.app.log("FATAL ERROR: ", "Cannot both create a server and join one.");
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

					}
					create = null;
					join = null;
					createbool = false;
					joinbool = false;
					hardexit = true;
					Gdx.app.exit();
				}
				else if(create == null)
				{
					create = new CreateServer();
					create.start();
					IPad = create.getIpAddress();
					msg = create.getMsg();
					error = create.getError();
				}
				else
				{
					msg = create.getMsg();
					error = create.getError();
				}
			}
		});

		buttonJoin = new TextButton("Join Server", skin, "default");
		buttonJoin.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight/2 - 190 + buttonSizeY / 2);
		buttonJoin.setSize(buttonSizeX, buttonSizeY);
		buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonJoin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				joinbool = true;
				if(joinbool && createbool)
				{
					Gdx.app.log("FATAL ERROR: ", "Cannot both create a server and join one.");
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

					}
					create = null;
					join = null;
					createbool = false;
					joinbool = false;
					hardexit = true;
					Gdx.app.exit();
				}
				else if(join == null)
				{
					join = new JoinServer("172.20.10.4", 8080, "temp");
					join.start();
					msg = join.getMsg();
					error = join.getError();
					IPad = "JoinServerMode.";
				}
				else
				{
					msg = join.getMsg();
					error = join.getError();
				}
			}
		});

		buttonExit = new TextButton("Exit app", skin, "default");
		buttonExit.setPosition(0, screenHeight - buttonSizeY);
		buttonExit.setSize(buttonSizeX, buttonSizeY);
		buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				hardexit = true;
				if(create != null)
				{
					create.stopServer();
					try
					{
						create.join();
						create = null;
					}catch(InterruptedException e)
					{

					}
				}
				if(join != null)
				{
					join.disconnect();
					try
					{
						join.join();
						join = null;
					}catch(InterruptedException e)
					{

					}
				}
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
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update();
		stage.draw();
		batch.begin();
		GlyphLayout glyphLayoutmsg = new GlyphLayout(), glyphLayouterror = new GlyphLayout(), glyphLayoutIP = new GlyphLayout();
		glyphLayoutmsg.setText(font, msg);
		glyphLayouterror.setText(font, error);
		glyphLayoutIP.setText(font, IPad);
		float fex = glyphLayouterror.width/2, fey = glyphLayouterror.height/2;
		float fmx = glyphLayoutmsg.width/2, fmy = glyphLayoutmsg.height/2;
		float fix = glyphLayoutIP.width/2, fiy = glyphLayoutIP.height/2;
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
		}
		font.draw(batch, msg, x - fmx, y + fmy);
		font.draw(batch, error, x - fex, y + fey - 300);
		font.draw(batch, IPad, x - fix, y + fiy + 300);
		batch.end();
	}
}
