package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Vector;

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
    public Vector<BulletEntity> coinEntitys = new Vector<BulletEntity>();
    public Vector<Coin> coins = new Vector<Coin>();


    float gameOverTimer = 0;
    public float scoreTimer;
    float contactTime = 0.2f;
    boolean collisionHappened = false;
    boolean gameOverGameScreen = false;
    boolean playerCreated = false;
    boolean getScore = true;

    private Label LabelScorePlayer1,LabelScorePlayer2,LabelScorePlayer3;
    private Label.LabelStyle labelStyle;
    private BitmapFont font;

    private Table table;

    // App reference
    private final BaseGame app;

    public static float time;
    private boolean remove = false;
    final boolean USE_CONTACT_CACHE = true;
    TestContactCache contactCache;
    BulletEntity bomb1;
    public Coin coin1;
    public Player player_1, player_2, player_3;

    // Sound
    static GameSound gameSound;
    int collisonUserId0, collisonUserId1;

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
                    }
                    if (match1) {
                        final BulletEntity e = (BulletEntity) (entities.get(userValue1));
                        e.setColor(Color.RED);
                        Gdx.app.log(Float.toString(time), "Contact started 1 " + userValue1);
                        collisonUserId1 = userValue1;
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
                    Gdx.app.log(Float.toString(time), "Contact ended " + collisonUserId1);
                }
                if (match1) {
                    final BulletEntity e = (BulletEntity) (entities.get(userValue1));
                    e.setColor(Color.BLACK);
                    Gdx.app.log(Float.toString(time), "Contact ended " + collisonUserId0);
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
        app.assets.load("3d/football2.g3dj", Model.class);
        app.assets.load("3d/apple.g3dj", Model.class);
        app.assets.load("3d/peach.g3dj", Model.class);
        app.assets.load("3d/coin.obj", Model.class);
        loading = true;

        font = new BitmapFont();
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        // Init Score lables
        labelStyle = new Label.LabelStyle(font, Color.PINK);
        LabelScorePlayer1 = new Label("", labelStyle);
        LabelScorePlayer1.setPosition(20, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 20);
        LabelScorePlayer2 = new Label("", labelStyle);
        LabelScorePlayer2.setPosition(20, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 2);
        LabelScorePlayer3 = new Label("", labelStyle);
        LabelScorePlayer3.setPosition(20, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 20) * 3);

        stage.addActor(LabelScorePlayer1);
        stage.addActor(LabelScorePlayer2);
        stage.addActor(LabelScorePlayer3);

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("img/scorebg1.png"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        scoreStage.getRoot().setPosition(0, stage.getHeight());
        Gdx.input.setInputProcessor(this);

        if (USE_CONTACT_CACHE) {
            contactCache = new TestContactCache();
            contactCache.entities = world.entities;
            // contactCache.setCacheTime(contactTime); // Change the contact time
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
            Model fotball = app.assets.get("3d/football2.g3dj", Model.class);
            String id = fotball.nodes.get(0).id;

            Model apple = app.assets.get("3d/apple.g3dj", Model.class);
            String id2 = apple.nodes.get(0).id;
            Node node = apple.getNode(id2);
            node.scale.set(0.8f, 0.8f, 0.8f);

            Model peach = app.assets.get("3d/peach.g3dj", Model.class);
            String id3 = peach.nodes.get(0).id;
            Node node2 = peach.getNode(id3);

            player_1 = new Player(fotball, "fotball");
            world.addConstructor("test1", player_1.bulletConstructor);
            player1 = world.add("test1", 0, 3.5f, 2.5f);
            player1.body.setContactCallbackFlag(1);
            //    player1.body.setContactCallbackFilter(2);
            player1.body.setContactCallbackFilter(1);

            player_2 = new Player(apple, "apple");
            world.addConstructor("test2", player_2.bulletConstructor);
            player2 = world.add("test2", 0, 3.5f, 0.5f);
            player2.body.setContactCallbackFilter(1);

            player_3 = new Player(peach, "peach");
            world.addConstructor("test3", player_3.bulletConstructor);
            player3 = world.add("test3", 0, 3.5f, -2.5f);
            player3.body.setContactCallbackFilter(1);

            Model bomb = app.assets.get("3d/coin.obj", Model.class);
            //String id4 = bomb.nodes.get(0).id;
            // Node node3 = bomb.getNode(id4);
            //Vector3 scale = new Vector3(0.8f, 0.8f, 0.8f);
            //node3.scale.set(scale);
            //bomb.meshes.get(0).scale(0.8f, 0.8f, 0.8f);

            for(int i = 0; i < 5; i++) {
                disposables.add(bomb);
                BulletConstructor bulletConstructor = (new BulletConstructor(bomb, 0.1f, createConvexHullShape(bomb, false)));
                world.addConstructor("bomb", bulletConstructor);
                bomb1 = world.add("bomb", 2 +i, 1, 2 - i*2);
                bomb1.body.setContactCallbackFilter(1);
                coinEntitys.add(bomb1);
            }

            Gdx.app.log("Loaded", "LOADED");
            loading = false;
            playerCreated = true;
        }

        // Count the score timer down.
        if(collisionHappened){
            scoreTimer -= 1f;
            if(scoreTimer < 0) { collisionHappened = false; }
            //Gdx.app.log("Score Timer = ", "" + scoreTimer);
        }
        // Points
        if(app.assets.update()) {

            if((collisonUserId1 >= 4 && collisonUserId1 <= 8) || (collisonUserId0 >= 4 && collisonUserId1 <= 8)){
                Vector3 move = new Vector3(0.2f, 1, 0.2f);
                BulletEntity temp = coinEntitys.get(collisonUserId1 - 4);
                ((btRigidBody) temp.body).applyCentralImpulse(move);
            }
              /*
                  if((collisonUserId1 >= 4 && collisonUserId1 <= 8)  || (collisonUserId0 >= 4 && collisonUserId1 <= 8)){
                      if (!(coins.get(collisonUserId1 - 4).getRemoved())) {
                          Gdx.app.log("remove", "bomb");
                          coins.get(collisonUserId1 - 4).setRemoved();
                         /// world.entities.removeIndex(collisonUserId1);

                          coinEntitys.remove(collisonUserId1-4);
                          System.out.println(collisonUserId1);
                      }
                  }
        */


            if((((btRigidBody) player2.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player2.body).getCenterOfMassPosition().y > -0.08)
                    && (collisonUserId0 == 2 || collisonUserId1 == 2) && scoreTimer > 0){
                player_1.setScore(10);
                Gdx.app.log("PLAYER2", "KRASH");

            }
            if((((btRigidBody) player3.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player3.body).getCenterOfMassPosition().y > -0.08)
                    && (collisonUserId0 == 3 ||  collisonUserId1 == 3) && scoreTimer > 0){
                player_1.setScore(10);
                Gdx.app.log("PLAYER3", "KRASH");
            }
            // Gameover
            if(((btRigidBody) player1.body).getCenterOfMassPosition().y < 0 && !gameOverGameScreen ){
                Gdx.app.log("Fall", "fall");
                player_2.setScore(20);
                player_3.setScore(20);
                gameOverGameScreen = true;
            }
            if(gameOverGameScreen)
                startGameOverTimer();
        }

        // Set the score
        if(playerCreated) {
            LabelScorePlayer1.setText("Score player 1: " + player_1.getScore());
            LabelScorePlayer2.setText("Score player 2: " + player_2.getScore());
            LabelScorePlayer3.setText("Score player 3: " + player_3.getScore());
        }
        stage.draw();
        scoreStage.draw();
    }

    public static btConvexHullShape createConvexHullShape (final Model model, boolean optimize) {
        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
        if (!optimize) return shape;
        // now optimize the shape
        final btShapeHull hull = new btShapeHull(shape);
        hull.buildHull(shape.getMargin());
        final btConvexHullShape result = new btConvexHullShape(hull);
        // delete the temporary shape
        shape.dispose();
        hull.dispose();
        return result;
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
    public void dispose () {
        super.dispose();
        //stage.dispose();
        if (rayTestCB != null) {rayTestCB.dispose(); rayTestCB = null;}
        //scoreStage.dispose(); // Borde disposas men det blir hack till nästa screen
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