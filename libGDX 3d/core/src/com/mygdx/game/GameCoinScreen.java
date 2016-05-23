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
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class GameCoinScreen extends BaseBulletTest implements Screen {

    // Public AssetManager assets;
    boolean loading;
    BulletEntity player1, player2, player3, player4;
    private Stage stage;
    private Stage scoreStage;

    // Players
    //public Player player_1, player_2, player_3, player_4;
    Vector<Player> playerList = new Vector<Player>();
    Vector<Integer> playerScorePosList = new Vector();

    // Controls
    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

    // Coins
    ModelInstance instance;

    private Coin coin_1;
    private int n_coins = 0;

    public ArrayList<BulletEntity> coinEntityList;

    //private BulletEntity coin;
    public ArrayList<BulletEntity> playerEntityList;
    public ArrayList<BulletEntity> powerUpEntityList;

    // Gameplay variables
    float gameOverTimer = 0;
    float coinTimer = 0;
    private float[][] scoreTimers;
    boolean collisionHappened = false;
    boolean gameOverGameScreen = false;
    boolean playerCreated = false;
    boolean isCollisionHappened = false;

    boolean removed = false;
    Random rand = new Random();


    // Animation
    private AnimationController controller;
    private AnimationController[] controllers = new AnimationController[n_coins];

    // UI
    private Vector<Label> labelScorePlayers = new Vector<Label>();
    private Label.LabelStyle labelStyle;
    private BitmapFont font;

    // App reference
    private final BaseGame app;

    // Misc
    public static float time;
    private boolean move = false;
    final boolean USE_CONTACT_CACHE = true;
    TestContactCache contactCache;
    private BulletEntity coin, powerupSpeed;
    public Player player_1, player_2, player_3, player_4;


    // Sound
    static GameSound gameSound;
    int collisionUserId0, collisionUserId1;

    public GameCoinScreen(final BaseGame app) {
        this.app = app;
        this.create();
    }

    // Collision listener
    public class TestContactCache extends ContactCache {
        public Array<BulletEntity> entities;
        @Override
        public void onContactStarted (btPersistentManifold manifold, boolean match0, boolean match1) {
            final int userValue0 = manifold.getBody0().getUserValue();
            final int userValue1 = manifold.getBody1().getUserValue();


//            ((btRigidBody) manifold.getBody0()).setGravity(new Vector3(0,5,0));

            collisionHappened = true;

            if((entities.get(userValue0) != entities.get(0) && entities.get(userValue1) != entities.get(0))) {





                // Give the score timers a value to the ones that have collided.
//
//                for (int i = 0; i < 4; i++)
//                {
//                    scoreTimers[userValue0-1][i] = 0; // 210/30 = 7 seconds
//                    scoreTimers[userValue1-1][i] = 0;
//                }

                scoreTimers[userValue0-1][userValue1-1] = 210f; // 210/30 = 7 seconds
                scoreTimers[userValue1-1][userValue0-1] = 210f;
//
//                Vector3 p1 = ((btRigidBody) world.entities.get(userValue0).body).getCenterOfMassPosition();
//                Vector3 p2 = new Vector3(p1);
//
//                Vector3 linearVelocity =  new Vector3(((btRigidBody)world.entities.get(userValue0).body).getLinearVelocity());
//                linearVelocity = linearVelocity.nor();
//
//
//                p1.x = p1.x - linearVelocity.x;
//                p1.z = p1.z - linearVelocity.z;
//
                Vector3 vec;

            Vector3 p1 = new Vector3();
            ((btRigidBody)world.entities.get(userValue0).body).getWorldTransform().getTranslation(p1);
//            Gdx.app.log("PosUser0", "" + pos1);


            Vector3 p2 = new Vector3();
            ((btRigidBody)world.entities.get(userValue1).body).getWorldTransform().getTranslation(p2);
//            Gdx.app.log("PosUser0", "" + pos2);
//
//
//
//
//                Gdx.app.log("User0", "" + ((btRigidBody) world.entities.get(userValue0).body).getLinearVelocity().len());
//                Gdx.app.log("User1", "" + ((btRigidBody) world.entities.get(userValue1).body).getLinearVelocity().len());
//
//
                if (((btRigidBody) world.entities.get(userValue0).body).getLinearVelocity().len() < ((btRigidBody) world.entities.get(userValue1).body).getLinearVelocity().len()) {

                    vec = new Vector3((p1.x - p2.x), 0, (p1.z - p2.z));

                    float normFactor = 200 / vec.len();
                    Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);
                    ((btRigidBody) entities.get(userValue0).body).applyCentralImpulse(normVec);
                } else {
                    vec = new Vector3((p2.x - p1.x), 0, (p2.z - p1.z));

                    float normFactor = 200 / vec.len();
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

        @Override
        public void onContactEnded (btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
            final int userValue0 = colObj0.getUserValue();
            final int userValue1 = colObj1.getUserValue();

//            Gdx.app.log("ENDED","");

//            ((btRigidBody)world.entities.get(userValue0).body).setGravity(new Vector3(0, -100, 0));
//
//            if (entities.get(userValue0) == entities.get(1)|| entities.get(userValue1) == entities.get(1)) {
//                if (match0) {
//                    final BulletEntity e = (BulletEntity) (entities.get(userValue0));
//                    e.setColor(Color.BLACK);
//                    //Gdx.app.log(Float.toString(time), "Contact ended " + collisionUserId1);
//                }
//                if (match1) {
//                    final BulletEntity e = (BulletEntity) (entities.get(userValue1));
//                    e.setColor(Color.BLACK);
//                    //Gdx.app.log(Float.toString(time), "Contact ended " + collisionUserId0);
//                }
//            }
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

        scoreTimers = new float[4][4];
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
        Vector3 moveDown = new Vector3(6f, 0f, 0f);
        Vector3 moveUp = new Vector3(-6f, 0f, 0f);
        Vector3 moveLeft = new Vector3(0f, 0f, 6f);
        Vector3 moveRight = new Vector3(0f, 0f, -1000f);

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
            player1 = world.add("test1", 10, 3.5f, 0f);
            player1.body.setContactCallbackFlag(1);
            player1.body.setContactCallbackFilter(1);
            playerEntityList.add(player1);

            player_2 = new Player(apple, "apple");
            world.addConstructor("test2", player_2.bulletConstructor);
            player2 = world.add("test2", 0, 3.5f, 10f);
            player2.body.setContactCallbackFilter(1);
            playerEntityList.add(player2);

            player_3 = new Player(peach, "peach");
            world.addConstructor("test3", player_3.bulletConstructor);
            player3 = world.add("test3", 0, 3.5f, -10f);
            player3.body.setContactCallbackFilter(1);
            playerEntityList.add(player3);


            player_4 = new Player(football, "football");
            world.addConstructor("test4", player_3.bulletConstructor);
            player4 = world.add("test4", -10, 3.5f, 0f);
            player4.body.setContactCallbackFilter(1);
            playerEntityList.add(player4);

            playerList.add(player_1);
            playerList.add(player_2);
            playerList.add(player_3);
            playerList.add(player_4);

            playerScorePosList.add(Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 2);
            playerScorePosList.add(Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 4);
            playerScorePosList.add(Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 6);
            playerScorePosList.add(Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 8);

            for(int i = 0; i < playerList.size();i++) {
                labelScorePlayers.add(new Label("", labelStyle));
                labelScorePlayers.get(i).setPosition(20, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * ((i+1)*2));
                stage.addActor(labelScorePlayers.get(i));
            }


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
        for(int i = 0; i < 4; i++){ //TODO: hårdkodat

            for(int k = 0; k < 4; k++){
                if(scoreTimers[i][k] > 0){
                    scoreTimers[i][k] -= 1f;
                }
            }
        }


        if(playerCreated == true) {
            // Give score to the correct player.
            for (int i = 0; i < 4; i++) {
                if ((((btRigidBody) playerEntityList.get(i).body).getCenterOfMassPosition().y < 0)
                        && playerList.get(i).getHasFallen() == false
                        ) {

                    playerList.get(i).setHasFallen(true);
                    // Give the score.
                    for (int k = 0; k < 4; k++) { // TODO: nrPlayers
                        if (scoreTimers[i][k] > 0) {
                            playerList.get(k).setScore(44);

                            updateScorePos();
                        }
                    }
                }
            }
        }
        if(app.assets.update() && playerCreated) {

            // Check collision between coins and player 1

            if(!move && collisionUserId1 >= (playerEntityList.size() + powerUpEntityList.size() +1 ) && collisionUserId1 <= (playerEntityList.size() +1 + powerUpEntityList.size() + coinEntityList.size()) ||
                    (collisionUserId0 >= (playerEntityList.size() + powerUpEntityList.size()+ 1) && collisionUserId1 <= (playerEntityList.size() +1 + powerUpEntityList.size() + coinEntityList.size()))) {

                int  x = rand.nextInt(15) + 1;
                int  z = rand.nextInt(15) + 1;

                BulletEntity temp = coinEntityList.get(collisionUserId1 -  (playerEntityList.size() + powerUpEntityList.size() + 1));

//                player_1.setScore(5);

                Matrix4 m = new Matrix4();
                Vector3 tmpVec = new Vector3(x, 1, -z);

                world.entities.get(collisionUserId1).body.setWorldTransform(m.setToTranslation(tmpVec));
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

//            // Check fall and collision with player 1 for player 3
//            if((((btRigidBody) player3.body).getCenterOfMassPosition().y < 0)){
//                player_3.setScore(10);
//                updateScorePos();
//            }

            // Gameover if player 1 falls
            if(((btRigidBody) player1.body).getCenterOfMassPosition().y < 0 && !gameOverGameScreen ){
                // setScore to the other players
//                player_2.setScore(20);
//                player_3.setScore(20);
//                updateScorePos();

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
            labelScorePlayers.get(0).setText("Score player 1: " + player_1.getScore());
            labelScorePlayers.get(1).setText("Score player 2: " + player_2.getScore());
            labelScorePlayers.get(2).setText("Score player 3: " + player_3.getScore());
            labelScorePlayers.get(3).setText("Score player 4: " + player_4.getScore());
        }

        stage.draw();
        scoreStage.draw();
    }



    //LÄGG ÖVER TILL BALLPIT////////////////////////////////////////
    private void updateScorePos(){
        if(playerCreated) {

            Array<Integer> currentScores = new Array<Integer>();
            boolean[] found = new boolean[playerList.size()];

            for(int i = 0; i < playerList.size(); i++)
            {
                currentScores.add(playerList.get(i).getScore());
                found[i] = false;
            }
            currentScores.sort();
            currentScores.reverse();

            int currentPosIdx = 0;
            for(int i = 0; i < currentScores.size; i++)
            {
                for(int k = 0; k < playerList.size(); k++)
                {
                    if(found[k] == false && currentScores.get(i) == playerList.get(k).getScore()) {
                        labelScorePlayers.get(k).addAction(Actions.moveTo(20, playerScorePosList.get(currentPosIdx), 0.5f));
                        currentPosIdx++;
                        found[k] = true;
                    }
                }
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////


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

        for(int i = 0; i<labelScorePlayers.size();i++)
            labelScorePlayers.get(i).act(Gdx.graphics.getDeltaTime());

        gameOverTimer += Gdx.graphics.getDeltaTime();

        if(gameOverTimer > 0.5)
        {
            super.setGameOver();
            scoreStage.getRoot().addAction(Actions.sequence(Actions.delay(1.2f), Actions.moveTo(0, 0, 0.5f), Actions.delay(1),
                    Actions.run(new Runnable() {
                        public void run() {
                            app.setScreen(new ScoreScreen(app));
//                            dispose();
                        }
                    })));
        }
    }
}