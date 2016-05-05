package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ContactCache;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Random;

public class GameCoinScreen extends BaseBulletTest implements Screen {

    //public AssetManager assets;
    boolean loading;
    BulletEntity player1, player2, player3;
    private Stage stage;
    private Stage scoreStage;

    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

    ModelInstance instance;

    private Coin coin_1;
    private int n_coins = 3;

    public ArrayList<BulletEntity> coinEntityList;
    public ArrayList<BulletEntity> playerEntityList;
    public ArrayList<BulletEntity> powerUpEntityList;

    float gameOverTimer = 0;
    float coinTimer = 0;
    public float scoreTimer;
    boolean collisionHappened = false;
    boolean gameOverGameScreen = false;
    boolean playerCreated = false;
    boolean isCollisionHappened = false;
    boolean removed = false;

    private AnimationController controller;
    private AnimationController[] controllers = new AnimationController[n_coins];


    private Label labelScorePlayer1, labelScorePlayer2, labelScorePlayer3;
    private Label.LabelStyle labelStyle;
    private BitmapFont font;

    // App reference
    private final BaseGame app;

    public static float time;
    private boolean move = false;
    final boolean USE_CONTACT_CACHE = true;
    TestContactCache contactCache;
    private BulletEntity coin, powerupSpeed;
    public Player player_1, player_2, player_3;

    // Sound
    static GameSound gameSound;
    int collisonUserId0, collisonUserId1;

    Random rand = new Random();

    public GameCoinScreen(final BaseGame app)
    {
        this.app = app;
        this.create();
    }

    public class TestContactCache extends ContactCache {
        public Array<BulletEntity> entities;
        @Override
        public void onContactStarted (btPersistentManifold manifold, boolean match0, boolean match1) {
            final int userValue0 = manifold.getBody0().getUserValue();
            final int userValue1 = manifold.getBody1().getUserValue();

            // Take the positions of the colliding balls. Used in the handling of sounds.
            Vector3 p1 = ((btRigidBody) manifold.getBody0()).getCenterOfMassPosition();
            Vector3 p2 = ((btRigidBody) manifold.getBody1()).getCenterOfMassPosition();

            // Set the time which the player1 can receive a points after a collision has happened.
            // 1 second = 30f
            scoreTimer = 210f;  // 210/30 = 7 seconds
            collisionHappened = true;

            if((entities.get(userValue0) != entities.get(0))){
                if (entities.get(userValue0) == entities.get(1) || entities.get(userValue1) == entities.get(1)) {
                    if (match0) {
                        final BulletEntity e = (BulletEntity) (entities.get(userValue0));
                        e.setColor(Color.BLUE);
                        Gdx.app.log(Float.toString(time), "Contact started 0 " + userValue0);
                        collisonUserId0 = userValue0;
                        move = false;
                    }
                    if (match1) {
                        final BulletEntity e = (BulletEntity) (entities.get(userValue1));
                        e.setColor(Color.RED);
                        Gdx.app.log(Float.toString(time), "Contact started 1 " + userValue1);
                        collisonUserId1 = userValue1;
                        move = false;
                    }
                    // Play the collision sound.
                    gameSound.playCollisionSound(p1, p2);
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
                    //Gdx.app.log(Float.toString(time), "Contact ended " + collisonUserId1);
                }
                if (match1) {
                    final BulletEntity e = (BulletEntity) (entities.get(userValue1));
                    e.setColor(Color.BLACK);
                    //Gdx.app.log(Float.toString(time), "Contact ended " + collisonUserId0);
                }
            }
        }
    }

    @Override
    public void create () {
        super.create();
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));

        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);

        // Load models
        app.assets.load("3d/balls/football2.g3dj", Model.class);
        app.assets.load("3d/balls/apple.g3dj", Model.class);
        app.assets.load("3d/balls/peach.g3dj", Model.class);
        app.assets.load("3d/gem.g3dj", Model.class);
        app.assets.load("3d/powerup/powerup_speed.g3db", Model.class);
        loading = true;

        font = new BitmapFont();
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        playerEntityList = new ArrayList<BulletEntity>(10);
        coinEntityList = new ArrayList<BulletEntity>(10);
        powerUpEntityList =  new ArrayList<BulletEntity>(10);

        // Init Score lables
        labelStyle = new Label.LabelStyle(font, Color.PINK);
        labelScorePlayer1 = new Label("", labelStyle);
        labelScorePlayer1.setPosition(20, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 20);
        labelScorePlayer2 = new Label("", labelStyle);
        labelScorePlayer2.setPosition(20, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 2);
        labelScorePlayer3 = new Label("", labelStyle);
        labelScorePlayer3.setPosition(20, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 3);

        stage.addActor(labelScorePlayer1);
        stage.addActor(labelScorePlayer2);
        stage.addActor(labelScorePlayer3);

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("img/scorebg1.png"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        scoreStage.getRoot().setPosition(0, stage.getHeight());
        Gdx.input.setInputProcessor(this);

        if (USE_CONTACT_CACHE) {
            contactCache = new TestContactCache();
            contactCache.entities = world.entities;
            contactCache.setCacheTime(0.001f); // Change the contact time
        }

        // Sound
        gameSound = new GameSound();
        // Play background music.
        // gameSound.playBackgroundMusic(0.45f);


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
        // Gdx.app.log("SHOOT", "SHOOT");
        Ray ray = camera.getPickRay(screenX, screenY);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom); // 50 meters max from the origin

        // Because we reuse the ClosestRayResultCallback, we need reset it's values
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);

        world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

        if (rayTestCB.hasHit() && (((btRigidBody) player1.body).getCenterOfMassPosition() != null)) {
            rayTestCB.getHitPointWorld(tmpV1);

            //Gdx.app.log("BANG", "BANG");
            Model model;
            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part("ball", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                    new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED))).sphere(1f, 1f, 1f, 10, 10);
            model = modelBuilder.end();

            instance = new ModelInstance(model,tmpV1);

            Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) player1.body).getCenterOfMassPosition().x), 0, (tmpV1.z - ((btRigidBody) player1.body).getCenterOfMassPosition().z));

            float normFactor = player_1.impulseFactor / vec.len();
            Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);

            player1.body.activate();
            ((btRigidBody) player1.body).applyCentralImpulse(normVec);
        }
        return true;
    }

    boolean up, down, left, right;
    @Override
    public boolean keyDown (int keycode) {
        player2.body.activate();
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
        }
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

    @Override
    public void render () {
        super.render();

        if(instance != null) {
            modelBatch.begin(camera);
            modelBatch.render(instance);
            modelBatch.end();
        }

        if (app.assets.update() && loading) {
            Model fotball = app.assets.get("3d/balls/football2.g3dj", Model.class);
            String id = fotball.nodes.get(0).id;

            Model apple = app.assets.get("3d/balls/apple.g3dj", Model.class);
            String id2 = apple.nodes.get(0).id;
            Node node = apple.getNode(id2);
            node.scale.set(0.8f, 0.8f, 0.8f);

            Model peach = app.assets.get("3d/balls/peach.g3dj", Model.class);
            String id3 = peach.nodes.get(0).id;
            Node node2 = peach.getNode(id3);

            player_1 = new Player(fotball, "fotball");
            world.addConstructor("test1", player_1.bulletConstructor);
            player1 = world.add("test1", 0, 3.5f, 2.5f);
            player1.body.setContactCallbackFlag(1);
            player1.body.setContactCallbackFilter(1);
            playerEntityList.add(player1);

            player_2 = new Player(apple, "apple");
            world.addConstructor("test2", player_2.bulletConstructor);
            player2 = world.add("test2", 0, 3.5f, 0.5f);
            player2.body.setContactCallbackFilter(1);
            playerEntityList.add(player2);

            player_3 = new Player(peach, "peach");
            world.addConstructor("test3", player_3.bulletConstructor);
            player3 = world.add("test3", 0, 3.5f, -2.5f);
            player3.body.setContactCallbackFilter(1);
            playerEntityList.add(player3);

            // Create powerup
            Model speedModel = app.assets.get("3d/powerup/powerup_speed.g3db", Model.class);
            disposables.add(speedModel);
            speedModel.meshes.get(0).scale(0.02f, 0.02f, 0.02f);
            world.addConstructor("speed", new BulletConstructor(speedModel, 0.1f, new btSphereShape(0.8f)));
            powerupSpeed = world.add("speed", 2, 1, -4);
            powerupSpeed.body.setContactCallbackFilter(1);
            ((btRigidBody) powerupSpeed.body).setGravity(new Vector3(0, 0, 0));
            powerUpEntityList.add(powerupSpeed);

            Model coinModel = app.assets.get("3d/gem.g3dj", Model.class);
            coinModel.meshes.get(0).scale(0.02f, 0.02f, 0.02f);

            for(int i = 0; i < n_coins; i++) {

                // Random placement
                int  x = rand.nextInt(15) + 1;
                int  z = rand.nextInt(15) + 1;

                coin_1 = new Coin(coinModel);
                world.addConstructor("coin", coin_1.bulletConstructor);
                coin = world.add("coin", x, 1, -z);
                coin.body.setContactCallbackFilter(1);
                ((btRigidBody) coin.body).setGravity(new Vector3(0, 0, 0));
                coinEntityList.add(coin);
                //initAnimationController(coin.modelInstance, i); Kommentera fram för animation
            }


            loading = false;
            playerCreated = true;
        }

        if(playerCreated){
            // You need to call update on the animation controller so it will advance the animation.  Pass in frame delta
            for(int i = 0; i < coinEntityList.size(); i++) {
                // controllers[i].update(Gdx.graphics.getDeltaTime());
            }
        }

        // Count the score timer down
        if(collisionHappened){
            scoreTimer -= 1f;
            if(scoreTimer < 0) { collisionHappened = false; }
        }

        if(app.assets.update() && playerCreated) {

            // Check collision between coins and player 1
            if(!move && collisonUserId1 >= playerEntityList.size() + powerUpEntityList.size() +1 && collisonUserId1 <= (powerUpEntityList.size() + playerEntityList.size() +1 + coinEntityList.size()) ||
                    (collisonUserId0 >= (playerEntityList.size() + powerUpEntityList.size() +1) && collisonUserId1 <= (playerEntityList.size()+ powerUpEntityList.size() +1 + coinEntityList.size()))) {

                int  x = rand.nextInt(15) + 1;
                int  z = rand.nextInt(15) + 1;

                BulletEntity temp = coinEntityList.get(collisonUserId1 -  (playerEntityList.size() + powerUpEntityList.size() + 1));
                player_1.setScore(5);

                Matrix4 m = new Matrix4();
                Vector3 tmpVec = new Vector3(x, 1, -z);

                world.entities.get(collisonUserId1).body.setWorldTransform(m.setToTranslation(tmpVec));
                ((btRigidBody) temp.body).setGravity(new Vector3(0, 0, 0));
                ((btRigidBody) temp.body).setLinearVelocity(new Vector3(0, 0, 0));
                ((btRigidBody) temp.body).setAngularVelocity(new Vector3(0, 0, 0));

                move = true;
            }

            //System.out.println("Player " + playerEntityList.size() + "Powerups " + powerUpEntityList.size() + "Collid " + collisonUserId1);

            // Check collision with powerups and player 1
            //if(collisonUserId1 >= (playerEntityList.size() +1) && collisonUserId1 <= (playerEntityList.size() +1 + powerUpEntityList.size()) ||
              //      (collisonUserId0 >= (playerEntityList.size() +1) && collisonUserId1 <= (playerEntityList.size() +1 + powerUpEntityList.size()))) {

                //Matrix4 m = new Matrix4();
                //Vector3 tmpVec = new Vector3(20, 1, -30);
                //world.entities.get(collisonUserId1).body.setWorldTransform(m.setToTranslation(tmpVec));
                /*if(!removed) {
                    world.remove(collisonUserId1);
                    powerUpEntityList.remove(collisonUserId1 - (playerEntityList.size() + 1));
                }*/
                //System.out.println("POWERUP");
           // }

                // Check fall and collision with player 1 for player 2
            if((((btRigidBody) player2.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player2.body).getCenterOfMassPosition().y > -0.08)
                    && (collisonUserId0 == 2 || collisonUserId1 == 2) && scoreTimer > 0){
                player_1.setScore(10);

            }
            // Check fall and collision with player 1 for player 3
            if((((btRigidBody) player3.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player3.body).getCenterOfMassPosition().y > -0.08)
                    && (collisonUserId0 == 3 ||  collisonUserId1 == 3) && scoreTimer > 0){
                player_1.setScore(10);
            }

            // Gameover if player 1 falls
            if(((btRigidBody) player1.body).getCenterOfMassPosition().y < 0 && !gameOverGameScreen ){
                // setScore to the other players
                player_2.setScore(20);
                player_3.setScore(20);

                // Add 1 to the current round
                int current_round = PropertiesSingleton.getInstance().getRound();
                PropertiesSingleton.getInstance().setRound(current_round);

                gameOverGameScreen = true;
            }

            if(gameOverGameScreen)
                startGameOverTimer();
        }

        // Set the score
        if(playerCreated) {
            labelScorePlayer1.setText("Score player 1: " + player_1.getScore());
            labelScorePlayer2.setText("Score player 2: " + player_2.getScore());
            labelScorePlayer3.setText("Score player 3: " + player_3.getScore());
        }

        stage.draw();
        scoreStage.draw();
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        render();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        super.dispose();
        //stage.dispose();
        if (rayTestCB != null) {rayTestCB.dispose(); rayTestCB = null;}
        //scoreStage.dispose(); // Borde disposas men det blir hack till nästa screen
        System.gc();
    }

    private void initAnimationController(ModelInstance modelInstance, int i){
        // You use an AnimationController to um, control animations.  Each control is tied to the model instance
        controllers[i] = new AnimationController(modelInstance);
        // Pick the current animation by name
        controllers[i].setAnimation("GemAction.idle", -1);
        //controllers[i].setAnimation("GemAction.onTouch");
    }

    private void startGameOverTimer(){

        scoreStage.act();

        gameOverTimer += Gdx.graphics.getDeltaTime();

        if(gameOverTimer > 0.5)
        {
            super.setGameOver();
            scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(1.2f), Actions.moveTo(0, 0, 0.5f), Actions.delay(1),
                    Actions.run(new Runnable() {
                        public void run() {
                            app.setScreen(new ScoreScreen(app));
                            dispose();
                        }
                    })));
        }
    }
}