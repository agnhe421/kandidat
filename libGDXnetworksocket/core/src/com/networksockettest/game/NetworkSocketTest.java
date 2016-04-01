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

import java.util.Vector;

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
	private String msg = "msg", error ="No Error", IPad = "IP", serverIPad = "", playerList = "";
	private Boolean sendFail;
	private Vector<String> serverIPs;
	CreateServer create;
	JoinServer join;
	Skin skin;
	Stage stage;
	TextButton buttonCreate, buttonJoin, buttonExit, buttonDisconnect, buttonRefresh;
	private Vector<TextButton> buttonServerList;
	public AssetManager assManager;
	Boolean hardexit = false, joinbool = false, createbool = false;
	private SendPacket sendPacket;

	@Override
	public void create ()
	{
		buttonServerList = new Vector<TextButton>();
		sendFail = false;
		serverIPs = new Vector<String>();
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
		else if(joinbool)
			joinbool = false;
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
		else if(createbool)
			createbool = false;
		msg = "Disconnected.";
		error = "No Error";
		IPad = "IP";
	}

	public void addServerButton(final String ipAdress, int buttID)
	{
		GlyphLayout buttParam = new GlyphLayout();
		buttParam.setText(font, ipAdress);
		float fsx = buttParam.width/2, fsy = buttParam.height/2;
		final TextButton buttonServer = new TextButton(ipAdress, skin, "default");
		float offset = (buttParam.height + 15)*buttID;
		buttonServer.setPosition(fsx, screenHeight - 100 - fsy - offset);
		buttonServer.setSize(buttParam.width + 10, buttParam.height + 10);
		buttonServer.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonServer.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				serverIPad = buttonServer.getText().toString();
			}
		});
		buttonServerList.add(buttonServer);
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
					disconnectAll();
					IPad = "IP";
					msg = "Cannot both connect and join a server.";
					error = "No error.";
				} else if (create == null) {
					//Create a new server, update the text accordingly.
					create = new CreateServer();
					create.start();
					IPad = create.getIpAddress();
					msg = create.getMsg();
					error = create.getError();
				} else {
					//Further clicks will only update the text.
					msg = create.getMsg();
					error = create.getError();
				}
				if (!serverIPs.isEmpty()) {
					serverIPs.clear();
					serverIPs = new Vector<String>();
				}
				if(!buttonServerList.isEmpty())
				{
					for(int ids = 0; ids < buttonServerList.size(); ++ids)
					{
						buttonServerList.get(ids).remove();
					}
					buttonServerList.clear();
					buttonServerList = new Vector<TextButton>();
				}
			}
		});

		buttonRefresh = new TextButton("Refresh list", skin, "default");
		buttonRefresh.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight / 2 - 190 + buttonSizeY / 2);
		buttonRefresh.setSize(buttonSizeX, buttonSizeY);
		buttonRefresh.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonRefresh.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (createbool || joinbool) {
					error = "Cannot refresh list when connected or hosting.";
				} else {
					serverIPad = "";
					if (!serverIPs.isEmpty()) {
						serverIPs.clear();
						serverIPs = new Vector<String>();
					}
					if(!buttonServerList.isEmpty())
					{
						for(int ids = 0; ids < buttonServerList.size(); ++ids)
						{
							buttonServerList.get(ids).remove();
						}
						buttonServerList.clear();
						buttonServerList = new Vector<TextButton>();
					}
					sendPacket = new SendPacket();
					sendPacket.start();
					try {
						sendPacket.join();
						serverIPs = sendPacket.getIPs();
						if (sendPacket.getErrorState()) {
							msg = sendPacket.getMsg();
							error = sendPacket.getError();
							sendFail = sendPacket.getErrorState();
						}
						//msg = sendPacket.getMsg();
						sendPacket = null;
					} catch (InterruptedException e) {
						e.printStackTrace();
						error = "Exception: " + e.toString();
					}
					if (sendFail) {

					} else {
						for (int ids = 0; ids < serverIPs.size(); ++ids) {
							addServerButton(serverIPs.get(ids), ids);
						}
						for(int ids = 0; ids < buttonServerList.size(); ++ids)
						{
							stage.addActor(buttonServerList.get(ids));
						}
					}
				}
			}
		});

		buttonJoin = new TextButton("Join Server", skin, "default");
		buttonJoin.setPosition(screenWidth / 2 - buttonSizeX / 2, screenHeight / 2 - 340 + buttonSizeY / 2);
		buttonJoin.setSize(buttonSizeX, buttonSizeY);
		buttonJoin.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonJoin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				joinbool = true;
				//No creating and joining a server at the same time. Terminate application.
				if (joinbool && createbool) {
					disconnectAll();
					IPad = "IP";
					msg = "Cannot both connect and join a server.";
					error = "No error.";
					if (!serverIPs.isEmpty()) {
						serverIPs.clear();
						serverIPs = new Vector<String>();
					}
					if(!buttonServerList.isEmpty())
					{
						for(int ids = 0; ids < buttonServerList.size(); ++ids)
						{
							buttonServerList.get(ids).remove();
						}
						buttonServerList.clear();
						buttonServerList = new Vector<TextButton>();
					}
				} else if (join == null) {
					if (!serverIPs.isEmpty()) {
						serverIPs.clear();
						serverIPs = new Vector<String>();
					}
					if(!buttonServerList.isEmpty())
					{
						for(int ids = 0; ids < buttonServerList.size(); ++ids)
						{
							buttonServerList.get(ids).remove();
						}
						buttonServerList.clear();
						buttonServerList = new Vector<TextButton>();
					}
					if (serverIPad.equals("")) {
						error = "No server selected!";
						joinbool = false;
					} else {
						//Connect to the server using the IP given by the server.
						IPad = "Connecting to: " + serverIPad;
						join = new JoinServer(serverIPad, 8081, "player"); //All hail Manly Banger, the Rock God!
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
		buttonDisconnect.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				disconnectAll();
				if (!serverIPs.isEmpty()) {
					serverIPs.clear();
					serverIPs = new Vector<String>();
				}
				if(!buttonServerList.isEmpty())
				{
					for(int ids = 0; ids < buttonServerList.size(); ++ids)
					{
						buttonServerList.get(ids).remove();
					}
					buttonServerList.clear();
					buttonServerList = new Vector<TextButton>();
				}
			}
		});

		buttonExit = new TextButton("Exit app", skin, "default");
		buttonExit.setPosition(0, screenHeight - buttonSizeY);
		buttonExit.setSize(buttonSizeX, buttonSizeY);
		buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
		buttonExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Exit the app, terminate any active servers or connections.
				hardexit = true;
				if (create != null) {
					create.stopServer();
					try {
						create.join();
						create = null;
					} catch (InterruptedException e) {

					}
				}
				if (join != null) {
					join.disconnect();
					try {
						join.join();
						join = null;
					} catch (InterruptedException e) {

					}
				}
				Gdx.app.exit();
			}
		});

		stage.addActor(buttonCreate);
		stage.addActor(buttonJoin);
		stage.addActor(buttonExit);
		stage.addActor(buttonDisconnect);
		stage.addActor(buttonRefresh);
	}

	public void update() {stage.act();}

	@Override
	public void render () {
		//Set background color.
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Update stage.
		update();
		//Draw stage.
		stage.draw();
		batch.begin();
		//Instantiate glyphlayout for all text.
		GlyphLayout glyphLayoutmsg = new GlyphLayout(), glyphLayouterror = new GlyphLayout(),
					glyphLayoutIP = new GlyphLayout(), glyphLayoutlist = new GlyphLayout();
		//Set glyphlayout for all text.
		glyphLayoutmsg.setText(font, msg);
		glyphLayouterror.setText(font, error);
		glyphLayoutIP.setText(font, IPad);
		glyphLayoutlist.setText(font, playerList);
		//Get text sizes, in order to adjust for text when positioning.
		float fex = glyphLayouterror.width/2, fey = glyphLayouterror.height/2;
		float fmx = glyphLayoutmsg.width/2, fmy = glyphLayoutmsg.height/2;
		float fix = glyphLayoutIP.width/2, fiy = glyphLayoutIP.height/2;
		float flx = glyphLayoutlist.width/2, fly = glyphLayoutlist.height/2;
		//Set middle of screen.
		float x = screenWidth/2, y = screenHeight/2;
		//Only retrieve active messages if the exit command hasn't been invoked. Otherwise, null values may be accessed.
		playerList = "";
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
				for(int idx = 0; idx < connectcounter; ++idx)
				{
					if(idx != connectcounter - 1)
						playerList += create.getUserId(idx) + "\n";
					else
						playerList += create.getUserId(idx);
				}
				//Check if the server thread dies due to exception.
				if(!create.isAlive())
				{
					disconnectAll();
					msg = "Server died unexpectadly.";
					IPad = "Standing by.";
				}
			}
			//Update connection messages
			else if(joinbool)
			{
				msg = join.getMsg();
				error = join.getError();
			}
			if(join != null)
			{
				if(join.connected())
					IPad = "Connected to: " + serverIPad + ".";
				//This disconnects the join function if the server disconnects. Assuming that the
				//phone receives the SERVER_SHUTDOWN message before the server shuts down completely.
				if(join.getMsg().equals("Server is offline."))
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
					IPad = "Standing by.";
					msg = "Server disconnected.";

				}
				//Check if the join thread dies due to exception.
				else if(!join.isAlive())
				{
					disconnectAll();
					msg = "Heartbeat died.";
					IPad = "Standing by.";
				}
			}
		}
		//Draw all text on screen. If you don't wish to see the debug, remove the error draw.
		font.draw(batch, msg, x - fmx, y + fmy);
		font.draw(batch, playerList, 75 - flx, y + fly + 250);
		font.draw(batch, error, x - fex, y + fey - 375);
		font.draw(batch, IPad, x - fix, y + fiy + 300);
		batch.end();
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
		batch.dispose();
		font.dispose();
	}
}
