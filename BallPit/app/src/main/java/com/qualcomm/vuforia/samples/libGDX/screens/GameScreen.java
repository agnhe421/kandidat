package com.qualcomm.vuforia.samples.libGDX.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ContactCache;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;
import com.qualcomm.vuforia.samples.libGDX.bullet.BaseBulletTest;
import com.qualcomm.vuforia.samples.libGDX.bullet.BaseWorld;
import com.qualcomm.vuforia.samples.libGDX.bullet.BulletConstructor;
import com.qualcomm.vuforia.samples.libGDX.bullet.BulletEntity;
import com.qualcomm.vuforia.samples.libGDX.classes.Coin;
import com.qualcomm.vuforia.samples.libGDX.classes.GameSound;
import com.qualcomm.vuforia.samples.libGDX.classes.Player;
import com.qualcomm.vuforia.samples.singletons.DataHolder;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;

import java.util.Collections;
import java.util.Vector;

public class GameScreen extends BaseBulletTest implements Screen {

    //public AssetManager assets;
    Vector<BulletEntity> playerEntityList = new Vector<BulletEntity>();
    Vector<Player> playerList = new Vector<Player>();
    private Stage stage;
    private Stage scoreStage;

    // App reference
    BaseGame app;

    // UI
    private Vector<Label> labelScorePlayers = new Vector<Label>();
    private Label.LabelStyle labelStyle;
    Vector<Integer> playerScorePosList = new Vector();

    // Stages

    int thisUnitId;

    // Game related variables
    float gameOverTimer = 0;
    public float scoreTimer;

    boolean collisionHappened = false;
    boolean gameOverGameScreen = false;
    boolean playerCreated = false;
    boolean loading = false;


    // Control
    private ClosestRayResultCallback rayTestCB;
    private Vector3 rayFrom = new Vector3();
    private Vector3 rayTo = new Vector3();


    private ModelInstance instance;

    // Score lables

    public static float time;

    private boolean remove = false;
    final boolean USE_CONTACT_CACHE = true;
    TestContactCache contactCache;
    BulletEntity bomb1;
    public Coin coin1;
    //public Player player_1, player_2, player_3;

    // Sound
    static GameSound gameSound;
    int collisionUserId0, collisionUserId1;

    //countdown
    private Label LabelCountdown;
    private Label.LabelStyle labelStyleCountdown;
    private float totalTime = 3;
    boolean countdownFinished = false;

    AssetManager assets;
    String chosenIsland;
    BulletEntity player;
    Model arrowInstance;
    Label labelTitle;
    private AnimationController controller;

    private volatile float[][] scoreTimers;


    public GameScreen(final BaseGame app)
    {

        this.app = app;
        this.assets = PropertiesSingleton.getInstance().getAssets();
        if(app.createServerScreen.create != null)
            thisUnitId = 0;
        else if(app.joinServerScreen.join != null)
            thisUnitId = (Character.getNumericValue(app.joinServerScreen.join.getUnitUserId().charAt(
                    app.joinServerScreen.join.getUnitUserId().length() - 1)) - 1);
        this.chosenIsland = PropertiesSingleton.getInstance().getChosenIsland();
        this.create();

    }

    public class TestContactCache extends ContactCache {
        public Array<BulletEntity> entities;
        @Override
        public void onContactStarted (btPersistentManifold manifold, boolean match0, boolean match1) {
            final int userValue0 = manifold.getBody0().getUserValue();
            final int userValue1 = manifold.getBody1().getUserValue();

            collisionHappened = true;

            if((entities.get(userValue0) != entities.get(0) && entities.get(userValue1) != entities.get(0))) {

                for (int i = 0; i < PropertiesSingleton.getInstance().getNrPlayers(); i++)
                {
                    scoreTimers[userValue0-1][i] = 0;
                    scoreTimers[userValue1-1][i] = 0;
                }

                scoreTimers[userValue0 - 1][userValue1 - 1] = 210f; // 210/30 = 7 seconds
                scoreTimers[userValue1 - 1][userValue0 - 1] = 210f;


                Gdx.app.log("HALLÅ STARTEEEED", "Contact started 0 " + userValue0);

                GameSound.getInstance().playCollisionSound(((btRigidBody) world.entities.get(userValue0).body).getCenterOfMassPosition(), "football", "football", camera.position);

                if (entities.get(userValue0) != entities.get(0) && entities.get(userValue1) != entities.get(0)) {

                    Vector3 p1 = new Vector3();
                    ((btRigidBody) world.entities.get(userValue0).body).getWorldTransform().getTranslation(p1);

                    Vector3 p2 = new Vector3();
                    ((btRigidBody) world.entities.get(userValue1).body).getWorldTransform().getTranslation(p2);

                    Vector3 vec;

                    if (((btRigidBody) world.entities.get(userValue0).body).getLinearVelocity().len() < ((btRigidBody) world.entities.get(userValue1).body).getLinearVelocity().len()) {

                        vec = new Vector3((p1.x - p2.x), 0, (p1.z - p2.z));

                        float normFactor = 20000 * 6 / vec.len();
                        Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
                        ((btRigidBody) entities.get(userValue0).body).applyCentralImpulse(normVec);
                    } else {
                        vec = new Vector3((p2.x - p1.x), 0, (p2.z - p1.z));

                        float normFactor = 20000 * 6 / vec.len();
                        Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
                        ((btRigidBody) entities.get(userValue1).body).applyCentralImpulse(normVec);
                    }

//
//                if ((entities.get(userValue0) != entities.get(0))) {
//                    if (entities.get(userValue0) == entities.get(1) || entities.get(userValue1) == entities.get(1)) {
//                        if (match0) {
//                            final BulletEntity e = (BulletEntity) (entities.get(userValue0));
//                            e.setColor(Color.BLUE);
//                            //Gdx.app.log(Float.toString(time), "Contact started 0 " + userValue0);
//                            collisionUserId0 = userValue0;
//                            move = false;
//                        }
//                        if (match1) {
//                            final BulletEntity e = (BulletEntity) (entities.get(userValue1));
//                            e.setColor(Color.RED);
//                            //Gdx.app.log(Float.toString(time), "Contact started 1 " + userValue1);
//                            collisionUserId1 = userValue1;
//                            move = false;
//                        }
//
//                        // Play the collision sound if colliding with a ball.
//                        if (userValue0 <= playerList.size() && userValue1 <= playerList.size()) {
//                            gameSound.playCollisionSound(p1, playerList.get(userValue0 - 1).getModelName(), playerList.get(userValue1 - 1).getModelName());
//                            Gdx.app.log("userValue0 = ", "" + playerList.get(userValue0 - 1).getModelName());
//                            Gdx.app.log("userValue1 = ", "" + playerList.get(userValue1 - 1).getModelName());
//                        }
//                    }
//                }
                }
            }
        }

        @Override
        public void onContactEnded (btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
            final int userValue0 = colObj0.getUserValue();
            final int userValue1 = colObj1.getUserValue();

            if (entities.get(userValue0) == entities.get(1)|| entities.get(userValue1) == entities.get(1)) {
                if (match0) {
                    final BulletEntity e = (BulletEntity) (entities.get(userValue0));
                    e.setColor(Color.BLACK);
                    Gdx.app.log(Float.toString(time), "Contact ended " + collisionUserId1);
                }
                if (match1) {
                    final BulletEntity e = (BulletEntity) (entities.get(userValue1));
                    e.setColor(Color.BLACK);
                    Gdx.app.log(Float.toString(time), "Contact ended " + collisionUserId0);
                }
            }
        }
    }

    @Override
    public void create () {
        super.create();

        final Model island = assets.get("3d/islands/"+chosenIsland+".g3db", Model.class);
        disposables.add(island);
        final BulletConstructor sceneConstructor = new BulletConstructor(island, 0f, new btBvhTriangleMeshShape(
                island.meshParts));
        sceneConstructor.bodyInfo.setRestitution(0.25f);
        world.addConstructor("island", sceneConstructor);
        world.add("island", 0, 0, 0);


        arrowInstance = assets.get("3d/misc/"+"arrow"+".g3db", Model.class);
//        arrowInstance.meshes.get(0).scale(0.1f, 0.1f, 0.1f);


        // Setup the stages
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));

        // Create the entitie

//        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);

        // Load models
//        app.assets.load("3d/balls/football.g3db", Model.class);
//        app.assets.load("3d/balls/apple.g3db", Model.class);
//        app.assets.load("3d/balls/peach.g3db", Model.class);
//        loading = true;

//        while(loading)
//        {
//            app.assets.update();
//            if(app.assets.isLoaded("3d/balls/football.g3db"))
//                loading = false;
//        }
        Gdx.app.log("SHOOT", "Begin");

        // Create font

        // Init Score lables
        labelStyle = new Label.LabelStyle(app.font40, Color.PINK);


        Label.LabelStyle labelStyle = new Label.LabelStyle(app.font120, Color.WHITE);
        labelTitle = new Label("NOT TRACKING ", labelStyle);
        labelTitle.setPosition(Gdx.graphics.getHeight() / 2 - labelTitle.getWidth() / 2, Gdx.graphics.getHeight() - labelTitle.getHeight() * 2);

        stage.addActor(labelTitle);

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("img/" + PropertiesSingleton.getInstance().getChosenIsland() +"1.jpg"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        scoreStage.getRoot().setPosition(0, stage.getHeight());
        Gdx.input.setInputProcessor(this);

//        Model football = app.assets.get("3d/balls/football.g3db", Model.class);

//        disposables.add(choosenBall);
//        world.addConstructor("ball", new BulletConstructor(ship, 1000, new btSphereShape(9f)));
//        player = world.add("ball", 0, 300f, 0f);

//        player.body.setRollingFriction(4);


        float playerPosOffset = 100f;
        Gdx.app.log("HEJ!", "Nr of players: " + PropertiesSingleton.getInstance().getNrPlayers());
        int joinOffset = 0;
        for(int idu = 0; idu < PropertiesSingleton.getInstance().getNrPlayers(); ++idu)
        {
            Model chosenBallModel = assets.get("3d/balls/"+PropertiesSingleton.getInstance().getChosenBall(idu)+".g3db", Model.class);

            playerScorePosList.add(Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * (2*idu));

            labelScorePlayers.add(new Label("", labelStyle));
            labelScorePlayers.get(idu).setPosition(20, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * ((idu+1)*2));
            stage.addActor(labelScorePlayers.get(idu));

            if(app.joinServerScreen.join != null)
            {
                if(idu != Character.getNumericValue(app.joinServerScreen.join.getUnitUserId().charAt(app.joinServerScreen.join.getUnitUserId().length() - 1)) - 1)
                {
                    Gdx.app.log("HEJ!", "Adding other.");
                    playerList.add(new Player(chosenBallModel, app.joinServerScreen.join.getPlayerId(idu - joinOffset)));
                    world.addConstructor("Test " + idu, playerList.get(idu).bulletConstructor);
                    playerEntityList.add(world.add("Test " + idu, 0, 300f, 1.0f + playerPosOffset));
                    playerEntityList.get(idu).body.setContactCallbackFilter(1);
                }
                else
                {
                    Gdx.app.log("HEJ!", "Adding me.");
                    ++joinOffset;
                    Gdx.app.log("HEJ!", "New offset: " + joinOffset);
                    thisUnitId = idu;
                    playerList.add(new Player(chosenBallModel, app.joinServerScreen.join.getUnitUserId()));
                    world.addConstructor("Test " + idu, playerList.get(idu).bulletConstructor);
                    playerEntityList.add(world.add("Test " + idu, 0, 300f, 1.0f + playerPosOffset));
                    playerEntityList.get(idu).body.setContactCallbackFlag(1);
                    playerEntityList.get(idu).body.setContactCallbackFilter(1);
                }
            }
            else if(app.createServerScreen.create != null)
            {
                Gdx.app.log("HEJ!", "Create is not null.");
                thisUnitId = 0;
                if(idu == 0)
                {
                    playerList.add(new Player(chosenBallModel, app.createServerScreen.create.getServerName()));
                    world.addConstructor("Test " + idu, playerList.get(idu).bulletConstructor);
                    playerEntityList.add(world.add("Test " + idu, 0, 300f, 1.0f));
                    playerEntityList.get(idu).body.setContactCallbackFilter(1);
                    playerEntityList.get(idu).body.setContactCallbackFlag(1);
                }
                else
                {
                    playerList.add(new Player(chosenBallModel, app.createServerScreen.create.getUserId(idu - 1)));
                    world.addConstructor("Test " + idu, playerList.get(idu).bulletConstructor);
                    playerEntityList.add(world.add("Test " + idu, 0, 300f, 1.0f + playerPosOffset));
                    playerEntityList.get(idu).body.setContactCallbackFilter(1);
                }
            }
            playerPosOffset += 100;
            Gdx.app.log("HEJ!", "End of loop.");
        }
        playerCreated = true;
        if (USE_CONTACT_CACHE) {
            contactCache = new TestContactCache();
            contactCache.entities = world.entities;
            // contactCache.setCacheTime(contactTime); // Change the contact time
        }
        Gdx.app.log("SHOOT", "END");
        Gdx.app.log("SHOOT", "Singleton: " + PropertiesSingleton.getInstance().getNrPlayers());
        gameSound = new GameSound();
        // Play background music.
        // gameSound.playBackgroundMusic(0.45f);

        //-------------------------load countdown--------------------------
        labelStyleCountdown = new Label.LabelStyle(app.font40, Color.GREEN);
        LabelCountdown = new Label("", labelStyleCountdown);
        LabelCountdown.setPosition(Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() / 2);
        stage.addActor(LabelCountdown);
        Gdx.app.log("SHOOT", "Nr of characters: " + playerList.size());
        // Sound
        //-------------------------------------------------------------------

        scoreTimers = new float[PropertiesSingleton.getInstance().getNrPlayers()][PropertiesSingleton.getInstance().getNrPlayers()];
    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        shoot(x, y);
        Gdx.app.log("TAP", "Tap");
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // shoot(screenX, screenY);
        Gdx.app.log("SHOOT", "SHOOT");

        //TO REMOVE AN ENTITY FROM THE WORLD
//        world.remove(playerEntityList.indexOf(player1.body) + 2);

        //LOG THE POSITION OF A BALL
        /*Vector3 tmpVec = new Vector3(0,2,0);
        world.entities.get(1).body.getWorldTransform().setToTranslation(tmpVec);


        Matrix4 m = new Matrix4();
        world.entities.get(1).body.setWorldTransform(m.setToTranslation(tmpVec));*/


        //FÅ ROTATIONEN
//        (((btRigidBody) player1.body).getAngularVelocity();


        //Gdx.app.log("LOG",tmpVec+ "");


        //if(countdownFinished){
        Ray ray = camera.getPickRay(screenX, screenY);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(1000f).add(rayFrom); // 50 meters max from the origin

        // Because we reuse the ClosestRayResultCallback, we need reset it's values
        ClosestRayResultCallback rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);

        world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);
        if(rayTestCB.hasHit())
        {
            rayTestCB.getHitPointWorld(tmpV1);

            Gdx.app.log("BANG", "BANG");
//            Model model;
//            ModelBuilder modelBuilder = new ModelBuilder();
//            modelBuilder.begin();
//            modelBuilder.part("ball", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
//                    new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED))).sphere(1f, 1f, 1f, 10, 10);
//            model = modelBuilder.end();
//
//            instance = new ModelInstance(model,tmpV1);

            Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) playerEntityList.get(thisUnitId).body).getCenterOfMassPosition().x),
                    0, (tmpV1.z - ((btRigidBody) playerEntityList.get(thisUnitId).body).getCenterOfMassPosition().z));


            instance = new ModelInstance(arrowInstance, tmpV1);
            // You use an AnimationController to um, control animations.  Each control is tied to the model instance
            controller = new AnimationController(instance);

            // Pick the current animation by name
            controller.setAnimation("ArrowAction", -1);

            float normFactor = playerList.get(thisUnitId).impulseFactor / vec.len();
            Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
            if(app.createServerScreen.create != null)
            {
                playerEntityList.get(thisUnitId).body.activate();
                ((btRigidBody) playerEntityList.get(thisUnitId).body).applyCentralImpulse(normVec);
            }
            Gdx.app.log("HEJ!", "Player " + (thisUnitId+1));
            Gdx.app.log("HEJ!", "Normvec: " + normVec.toString());
            if(app.joinServerScreen.join != null)
            {
                app.joinServerScreen.join.sendClickPosVector(normVec);
            }
        }
        // Är det normVec som ska skickas till servern som ´sen skickar till varje client och varje client lägger impulsen på rätt spelare.
        // sendImpulse(normVec);
        // Skriva en ny funktion i GameScreen som faktiskt sätter denna impuls, vart ska den sättas? Vill inte att den ska köras varje frame.
        // Ifall klick har hänt,
        //}
        return true;
    }

    boolean up, down, left, right;
    @Override
    public boolean keyDown (int keycode) {
        /*player2.body.activate();
        Vector3 moveDown = new Vector3(1f, 0f, 0f);
        Vector3 moveUp = new Vector3(-1f, 0f, 0f);
        Vector3 moveLeft = new Vector3(0f, 0f, 1f);
        Vector3 moveRight = new Vector3(0f, 0f, -1f);

        switch(keycode) {
            case Input.Keys.UP: up = true;
                ((btRigidBody) player2.body).applyCentralImpulse(moveUp);
                break;
            case Input.Keys.DOWN: down = true;
                ((btRigidBody) player2.body).applyCentralImpulse(moveDown);
                break;
            case Input.Keys.LEFT: left = true;
                ((btRigidBody) player2.body).applyCentralImpulse(moveLeft);
                break;
            case Input.Keys.RIGHT: right = true;
                ((btRigidBody) player2.body).applyCentralImpulse(moveRight);
                break;
            default: return false;
        }*/
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        super.keyUp(keycode);
        switch(keycode) {
            case Input.Keys.UP: up = false; break;
            case Input.Keys.DOWN: down = false; break;
            case Input.Keys.LEFT: left = false; break;
            case Input.Keys.RIGHT: right = false; break;
            default: return false;
        }
        Gdx.app.log("RAY pick", "RAY pick");
        return true;
    }

    public void updateImpulse(Vector3 newImpulseVector, int playerID)
    {
        Gdx.app.log("HEJ!", "Updating impulse for player: " + (playerID + 1));
        //playerList.get(playerID).setPosition
        playerList.get(playerID).setImpulseVector(newImpulseVector);
        playerEntityList.get(playerID).body.activate();
        ((btRigidBody)playerEntityList.get(playerID).body).applyCentralImpulse(newImpulseVector);
    }

    public void setToScoreScreen()
    {

    }

    public void updatePositions(Vector<Vector3> checkCharPos, Vector<Vector3> checkCharRot)
    {
//        Gdx.app.log("UPDATING POS", "HALLÅ ELLER");

        if(playerCreated)
        {
            Matrix4 tmp;
            Quaternion tmpq;
            for (int ide = 1; ide <= playerEntityList.size(); ++ide)
            {
                tmpq = new Quaternion().setEulerAngles(checkCharRot.get(ide - 1).x, checkCharRot.get(ide - 1).y, checkCharRot.get(ide - 1).z);
                tmp = new Matrix4().set(tmpq);
                playerEntityList.get(ide - 1).body.activate();
                world.entities.get(ide).body.setWorldTransform(tmp.setTranslation(checkCharPos.get(ide - 1)));
                //((btRigidBody)world.entities.get(ide).body).setAngularVelocity(checkCharRot.get(ide - 1));
            }
        }
    }

    /*public void updatePosition(Vector3 checkCharPos, int PlayerID)
    {
        if(playerCreated)
        {
            Matrix4 tmp = new Matrix4();
            world.entities.get(PlayerID).body.setWorldTransform(tmp.setToTranslation(checkCharPos));
        }
    }*/

    @Override
    public void render () {

        if(app.joinServerScreen.join != null)
            if(!app.joinServerScreen.join.isAlive())
            {
                app.joinServerScreen.join = null;
                app.mainMenyScreen = new MainMenyScreen(app);
                app.setScreen(app.mainMenyScreen);
            }
        if(app.createServerScreen.create != null)
        {
            Vector<Vector3> tempPosList = new Vector<Vector3>();
            Vector<Vector3> tempRotList = new Vector<Vector3>();
            for(int ide = 1; ide <= playerEntityList.size(); ++ide)
            {
                Vector3 tmpv = new Vector3(), tmpr;
                Quaternion tmpq = new Quaternion();
                world.entities.get(ide).body.getWorldTransform().getTranslation(tmpv);
                world.entities.get(ide).body.getWorldTransform().getRotation(tmpq);
                tmpr = new Vector3(tmpq.getYaw(), tmpq.getPitch(), tmpq.getRoll());
                tempPosList.add(tmpv);
                tempRotList.add(tmpr);
            }
            for(int idu = 0; idu < playerEntityList.size(); ++idu)
            {
                app.createServerScreen.create.sendCharData(tempPosList, tempRotList);
            }
        }
        /*else if(app.joinServerScreen.join != null)
        {
            Vector3 tmp = new Vector3();
            world.entities.get(thisUnitId + 1).body.getWorldTransform().getTranslation(tmp);
            app.joinServerScreen.join.sendCharPosition(tmp);
        }*/

        super.render();

        if(instance != null) {
            modelBatch.begin(camera);
            modelBatch.render(instance);
            modelBatch.end();

            controller.update(Gdx.graphics.getDeltaTime());
        }

        /*if (app.assets.update() && loading) {

            Model fotball = app.assets.get("3d/football2.g3dj", Model.class);
            String id = fotball.nodes.get(0).id;
            Model football = app.assets.get("3d/football2.g3dj", Model.class);
            String id = football.nodes.get(0).id;

        if (app.assets.update() && loading) {
            Model football = app.assets.get("3d/balls/football2.g3dj", Model.class);
            String id = football.nodes.get(0).id;

            Model apple = app.assets.get("3d/balls/apple.g3dj", Model.class);
            String id2 = apple.nodes.get(0).id;
            Node node = apple.getNode(id2);
            node.scale.set(0.8f, 0.8f, 0.8f);

            Model peach = app.assets.get("3d/balls/peach.g3dj", Model.class);
            String id3 = peach.nodes.get(0).id;
            Node node2 = peach.getNode(id3);

            player_1 = new Player(football, "football");
            world.addConstructor("test1", player_1.bulletConstructor);
            /*if(app.joinServerScreen.join != null)
            {
                world.addConstructor("test1", app.joinServerScreen.join.constructor);
                app.joinServerScreen.join.playerChar = world.add("test1", 0, 3.5f, 2.5f);
            }*/
           /* player1 = world.add("test1", 0, 3.5f, 2.5f);
            player1.body.setContactCallbackFlag(1);
            player1.body.setContactCallbackFilter(1);
            playerEntityList.add(player1);

            player_2 = new Player(apple, "apple");
            world.addConstructor("test2", player_2.bulletConstructor);
            player2 = world.add("test2", 0, 3.5f, 0.5f);
            player2.body.setContactCallbackFilter(1);

            player_3 = new Player(peach, "peach");
            world.addConstructor("test3", player_3.bulletConstructor);
            player3 = world.add("test3", 0, 3.5f, -2.5f);
            player3.body.setContactCallbackFilter(1);
            playerEntityList.add(player1);

            player_4 = new Player(peach, "peach");
            world.addConstructor("test4", player_4.bulletConstructor);
            player4 = world.add("test4", 0, 3.5f, -2.5f);
            player4.body.setContactCallbackFilter(1);

            Gdx.app.log("Loaded", "LOADED");
            loading = false;
            playerCreated = true;
*/
        // Count the score timer down.
        /*if(collisionHappened){
            scoreTimer -= 1f;
            if(scoreTimer < 0) { collisionHappened = false; }
            //Gdx.app.log("Score Timer = ", "" + scoreTimer);
        }*/

        // Points
          /*if(app.assets.update() && playerCreated) {
                  if ((((btRigidBody) playerEntityList.get(1).body).getCenterOfMassPosition().y < 0) && (((btRigidBody) playerEntityList.get(1).body).getCenterOfMassPosition().y > -0.08)
          if(app.assets.update() && playerCreated) {
              if ((((btRigidBody) player2.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player2.body).getCenterOfMassPosition().y > -0.08)
                          && (collisionUserId0 == 2 || collisionUserId1 == 2) && scoreTimer > 0) {
                  player_1.setScore(10);
                  Gdx.app.log("PLAYER2", "KRASH");

              }
              if((((btRigidBody) player3.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player3.body).getCenterOfMassPosition().y > -0.08)
                      && (collisionUserId0 == 3 ||  collisionUserId1 == 3) && scoreTimer > 0){
                  player_2.setScore(10);
                  Gdx.app.log("PLAYER3", "KRASH");
              }
            // Gameover
            if(((btRigidBody) playerEntityList.get(thisUnitId).body).getCenterOfMassPosition().y < 0 && !gameOverGameScreen ){
              if(((btRigidBody) player1.body).getCenterOfMassPosition().y < 0 && !gameOverGameScreen ){
                Gdx.app.log("Fall", "fall");
                player_2.setScore(20);
                player_3.setScore(20);

                // Add 1 to the current round
                int current_round = PropertiesSingleton.getInstance().getRound();
                PropertiesSingleton.getInstance().setRound(current_round);
                System.out.println("Round: " + current_round);
                gameOverGameScreen = true;
            }
            if(gameOverGameScreen)
                startGameOverTimer();
        }*/

        // Set the score
        /*for(int idu = 0; idu < PropertiesSingleton.getInstance().getNrPlayers(); ++idu)
        {
            //LabelScoreList..setText("Score " + playerList.get(idu).getId() + ": " + playerList.get(idu).getScore());
        }
        if(playerCreated) {
            LabelScorePlayer1.setText("Score player 1: " + player_1.getScore());
            LabelScorePlayer2.setText("Score player 2: " + player_2.getScore());
            LabelScorePlayer3.setText("Score player 3: " + player_3.getScore());
        }
              }
              if(gameOverGameScreen)
                  startGameOverTimer();
        }


        // Draw the sorted scores.
        drawScores();
        //draw countdown timer
        if(loading == false){
            countDown();
        }
        */



        if(playerCreated == true) {
            // Count the score timer down
            for (int i = 0; i < PropertiesSingleton.getInstance().getNrPlayers(); i++) {

                labelScorePlayers.get(i).setText("Score player " + (i+1) + ": " + PropertiesSingleton.getInstance().getScore(i));

                if ((((btRigidBody) playerEntityList.get(i).body).getCenterOfMassPosition().y < 0)
                        && playerList.get(i).getHasFallen() == false
                        ) {
                    playerList.get(i).setHasFallen(true);

                    int n_playersLeft = 0;
                    // Give the score.
                    for (int k = 0; k < PropertiesSingleton.getInstance().getNrPlayers(); k++) { // TODO: nrPlayers
                        if (scoreTimers[i][k] > 0) {
                            PropertiesSingleton.getInstance().setScore(k,44);
                            updateScorePos();
                        }

                        if(playerList.get(k).getHasFallen() == false)
                            n_playersLeft++;

                    }

                    if(n_playersLeft < 2)
                        startGameOverTimer();
                }

                for (int k = 0; k < PropertiesSingleton.getInstance().getNrPlayers(); k++) {
                    if (scoreTimers[i][k] > 0) {
                        scoreTimers[i][k] -= 1f;
                    }
                }
            }
        }

        if(DataHolder.getInstance().getIsTracking() == false)
        {
            labelTitle.setPosition(Gdx.graphics.getHeight() / 2 - labelTitle.getWidth() / 2, labelTitle.getHeight() * 2);
        }
        else
        {
            labelTitle.setPosition(Gdx.graphics.getHeight() / 2 - labelTitle.getWidth() / 2, -labelTitle.getHeight() * 2);
        }

        stage.draw();
        scoreStage.draw();
        scoreStage.act();

        for(int i = 0; i<labelScorePlayers.size();i++)
            labelScorePlayers.get(i).act(Gdx.graphics.getDeltaTime());

    }

    private void updateScorePos(){
        if(playerCreated) {

            Array<Integer> currentScores = new Array<Integer>();
            boolean[] found = new boolean[playerList.size()];

            for(int i = 0; i < playerList.size(); i++)
            {
                currentScores.add(PropertiesSingleton.getInstance().getScore(i));
                found[i] = false;
            }
            currentScores.sort();
            currentScores.reverse();

            int currentPosIdx = 0;
            for(int i = 0; i < currentScores.size; i++)
            {
                for(int k = 0; k < playerList.size(); k++)
                {
                    if(found[k] == false && currentScores.get(i) == PropertiesSingleton.getInstance().getScore(k)) {
                        labelScorePlayers.get(k).addAction(Actions.moveTo(20, playerScorePosList.get(currentPosIdx), 0.5f));
                        currentPosIdx++;
                        found[k] = true;
                    }
                }
            }
        }
    }

    @Override
    public void update () {
        float delta = Gdx.graphics.getRawDeltaTime();
        time += delta;
        super.update();
        if (contactCache != null) contactCache.update(delta);


    }

    @Override
    public void show() {


    }

    @Override
    public void render(float delta) {
        render();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
        //stage.dispose();
        if (rayTestCB != null) {rayTestCB.dispose(); rayTestCB = null;}
        //scoreStage.dispose(); // Borde disposas men det blir hack till nästa screen
    }


    private void startGameOverTimer() {

        Gdx.app.log("GAMEOVER", "GAMEOVER");

        while(gameOverTimer < 0.5f)
        {
            gameOverTimer += Gdx.graphics.getDeltaTime();
        }

//        super.setGameOver();
        scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(1.2f), Actions.moveTo(0, 0, 0.5f), Actions.delay(1),
                Actions.run(new Runnable() {
                    public void run() {
                        DataHolder.getInstance().setActivateCamera(false);
                        app.setScreen(new ScoreScreen(app));
                    }
                })));


//        scoreStage.act();
//
//        gameOverTimer += Gdx.graphics.getDeltaTime();
//
//        if (gameOverTimer > 0.5) {
//            super.setGameOver();
//            scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(1.2f), Actions.moveTo(0, 0, 0.5f), Actions.delay(1),
//                    Actions.run(new Runnable() {
//                        public void run() {
//
//                            PropertiesSingleton.getInstance().setNrPlayers(n_players);
//
//                            // Prepare necessary data for the highscore screen.
//
//                            // Set the scores.
//                            PropertiesSingleton.getInstance().setPlayer1Score(player_1.getScore());
//                            PropertiesSingleton.getInstance().setPlayer2Score(player_2.getScore());
//                            PropertiesSingleton.getInstance().setPlayer3Score(player_3.getScore());
//                            PropertiesSingleton.getInstance().setPlayer4Score(player_4.getScore());
//
//                            // Get the model names.
//                            PropertiesSingleton.getInstance().setPlayer1Ball(player_1.getModelName());
//                            PropertiesSingleton.getInstance().setPlayer2Ball(player_2.getModelName());
//                            PropertiesSingleton.getInstance().setPlayer3Ball(player_3.getModelName());
//                            PropertiesSingleton.getInstance().setPlayer4Ball(player_4.getModelName());
//
//                            // Prepare the "Ball String" so that it can later be sent over the network as a string.
//                            PropertiesSingleton.getInstance().createBallString();
//
//                            app.setScreen(new ScoreScreen(app));
//                            dispose();
//                        }
//                    })));
//            }
        }

    public void playCollisionSound(Vector3 pos, String m1, String m2)
    {
        gameSound.playCollisionSound(pos, m1, m2, camera.position);
    }

    //--------------Countdown-------------------------------------
    private void countDown() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        totalTime -= deltaTime;
        float seconds = totalTime - deltaTime;

        if(totalTime < 1) {
            LabelCountdown.setText("GO");
            if(totalTime <0){
                LabelCountdown.setVisible(false);
                countdownFinished = true;
            }
        }else{
            LabelCountdown.setText(String.format("%.0f", seconds));
        }
    }
}