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
	Integer connectcounter = 0;
	private String msg = "msg", error ="No Error", IPad = "IP", serverIPad = "";
	CreateServer create;
	JoinServer join;
	Skin skin;
	Stage stage;
	TextButton buttonCreate, buttonJoin, buttonExit, buttonDisconnect;
	public AssetManager assManager;
	Boolean hardexit = false, joinbool = false, createbool = false;
	private SendPacket sendPacket;
	
	@Override
	public void create ()
	{
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

	private void disconnectAll()
	{
		//Disconnect any active connections, or servers.
		if(joinbool && join != null)
		{
			join.disconnect();
			try
			{
				join.join();
			}catch(InterruptedException e)
			{
				e.printStackTrace();
				error = "Exception: " + e.toString();
			}
			joinbool = false;
			join = null;
		}
		if(createbool && create != null)
		{
			create.stopServer();
			try
			{
				create.join();
			}catch(InterruptedException e)
			{
				e.printStackTrace();
				error = "Exception: " + e.toString();
			}
			createbool = false;
			create = null;
		}
		msg = "Disconnected.";
		error = "No Error";
		IPad = "IP";
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
				//No creating and joining a server at the same time. Terminate application.
				if (joinbool && createbool) {
					Gdx.app.log("FATAL ERROR: ", "Cannot both create a server and join one.");
					if (create.isAlive())
						create.stopServer();
					if (join.isAlive())
						join.disconnect();
					try {
						join.join();
						create.join();
					} catch (InterruptedException e) {

					}
					create = null;
					join = null;
					createbool = false;
					joinbool = false;
					hardexit = true;
					Gdx.app.exit();
				}
				else if (create == null) {
					//Create a new server, update the text accordingly.
					create = new CreateServer();
					create.start();
					IPad = create.getIpAddress();
					msg = create.getMsg();
					error = create.getError();
				}
				else {
					//Further clicks will only update the text.
					msg = create.getMsg();
					error = create.getError();
				}
			}
		});

		buttonJoin = new TextButton("Join Server", skin, "default");
		buttonJoin.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight / 2 - 190 + buttonSizeY / 2);
		buttonJoin.setSize(buttonSizeX, buttonSizeY);
		buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonJoin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				joinbool = true;
				//No creating and joining a server at the same time. Terminate application.
				if (joinbool && createbool) {
					Gdx.app.log("FATAL ERROR: ", "Cannot both create a server and join one.");
					if (create.isAlive())
						create.stopServer();
					if (join.isAlive())
						join.disconnect();
					try {
						join.join();
						create.join();
					} catch (InterruptedException e)
					{

					}
					create = null;
					join = null;
					createbool = false;
					joinbool = false;
					hardexit = true;
					Gdx.app.exit();
				}
				else if (join == null)
				{
					//Check all available units connected to the network and see if anyone has started a server.
					sendPacket = new SendPacket();
					sendPacket.start();
					try
					{
						sendPacket.join();
						serverIPad = sendPacket.getIP();
						if(sendPacket.getErrorState())
						{
							msg = sendPacket.getMsg();
							error = sendPacket.getError();
						}
						sendPacket = null;
					}catch(InterruptedException e)
					{
						e.printStackTrace();
						error = "Exception: " + e.toString();
					}
					//If no servers are located, terminate action.
					if(serverIPad.equals("FAILED_CONNECTION"))
					{
						IPad = "No servers online.";
						joinbool = false;
					}
					else
					{
						//Connect to the server using the IP given by the server.
						IPad = "Connecting to: " + serverIPad;
						join = new JoinServer(serverIPad, 8081, "Manly Banger, the Rock God"); //All hail Manly Banger, the Rock God!
						join.start();
						msg = join.getMsg();
						error = join.getError();
					}

				} else {
					//Further clicks will only update the text.
					msg = join.getMsg();
					error = join.getError();
				}
			}
		});

		buttonDisconnect = new TextButton("Disconnect", skin, "default");
		buttonDisconnect.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight / 2 + buttonSizeY / 2 - 265);
		buttonDisconnect.setSize(buttonSizeX, buttonSizeY);
		buttonDisconnect.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonDisconnect.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				disconnectAll();
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
				//Exit the app, terminate any active servers or connections.
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

		stage.addActor(buttonCreate);
		stage.addActor(buttonJoin);
		stage.addActor(buttonExit);
		stage.addActor(buttonDisconnect);
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
		//Only retrieve active messages if the exit command hasn't been invoked. Otherwise, null values may be accessed.
		if(!hardexit)
		{
			//Update server messages
			if(createbool)
			{
				if(!create.checkIfVectorNull())
					connectcounter = create.getConnections();
				msg = create.getMsg();
				error = create.getError();
				font.draw(batch, connectcounter.toString(), screenWidth - 50, screenHeight - 25);
			}
			//Update connection messages
			else if(joinbool)
			{
				msg = join.getMsg();
				error = join.getError();
			}
			if(join != null)
			{
				//This disconnects the join function if the server disconnects. Assuming that the
				//phone receives the SERVER_SHUTDOWN message before the server shuts down completely.
				if(join.getMsg().equals("SERVER_SHUTDOWN"))
				{
					try
					{
						join.join();
						error = "No Error.";
					}catch(InterruptedException e)
					{
						e.printStackTrace();
						error = "Exception: " + e.toString();
					}
					disconnectAll();
					serverIPad = "Standing by.";
					msg = "Server disconnected.";

				}
			}
		}
		//Draw all text on screen. If you don't wish to see the debug, remove the error draw.
		font.draw(batch, msg, x - fmx, y + fmy);
		font.draw(batch, error, x - fex, y + fey - 300);
		font.draw(batch, IPad, x - fix, y + fiy + 300);
		batch.end();
	}
}
