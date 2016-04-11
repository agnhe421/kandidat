package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
import com.mygdx.game.Player;
import java.util.Vector;

public class GameScreen extends BaseBulletTest implements Screen {

    //public AssetManager assets;
    boolean loading;
    BulletEntity player, player2, player3, player4;
    private Stage stage;
    private Stage scoreStage;

    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

    ModelInstance instance;

    float gameOverTimer = 0;

    boolean gameOverGameScreen = false;

    private Label LabelScore;
    private Label.LabelStyle labelStyle;
    private BitmapFont font;
    private int score;

    public Vector <BulletEntity> playerVec = new Vector<BulletEntity>();
    // App reference
    private final BaseGame app;

    public static float time;
    final boolean USE_CONTACT_CACHE = true;
    TestContactCache contactCache;

    // Sound
    static GameSound gameSound;
    int collisonUserId0, collisonUserId1;

    public GameScreen(final BaseGame app)
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

            if (entities.get(userValue0) != entities.get(0)) {
                if (entities.get(userValue1) != entities.get(0)) {

                    if (match0) {
                        final BulletEntity e = (BulletEntity) (entities.get(userValue0));
                        e.setColor(Color.BLUE);
                        Gdx.app.log(Float.toString(time), "Contact started " + userValue0);
                        collisonUserId0 = userValue0;
                    }
                    if (match1) {
                        final BulletEntity e = (BulletEntity) (entities.get(userValue1));
                        e.setColor(Color.RED);
                        Gdx.app.log(Float.toString(time), "Contact started " + userValue1);
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
            collisonUserId1 = -1;
            collisonUserId0 = -1;

            if (match0) {
                final BulletEntity e = (BulletEntity)(entities.get(userValue0));
                e.setColor(Color.BLACK);
                Gdx.app.log(Float.toString(time), "Contact ended " + collisonUserId1);
                //collisonUserId0 = userValue0;
            }
            if (match1) {
                final BulletEntity e = (BulletEntity)(entities.get(userValue1));
                e.setColor(Color.BLACK);
                Gdx.app.log(Float.toString(time), "Contact ended " + collisonUserId0);
                //collisonUserId1 = userValue1;
            }
        }
    }

    @Override
    public void create () {
        super.create();

        this.stage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));
        this.scoreStage = new Stage(new StretchViewport(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()));

        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),0.25f + 0.5f * (float) Math.random(), 1f);

        // Load texture
        //assets = new AssetManager();
        app.assets.load("football2.g3dj", Model.class);
        app.assets.load("apple.g3dj", Model.class);
        app.assets.load("peach.g3dj", Model.class);
        loading = true;
        font = new BitmapFont();

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
        labelStyle = new Label.LabelStyle(font, Color.PINK);
        LabelScore = new Label("Score: " + score, labelStyle);
        LabelScore.setPosition(20, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 30);
        stage.addActor(LabelScore);

        Actor scoreActor = new Image(new Sprite(new Texture(Gdx.files.internal("scorebg1.png"))));
        scoreActor.setPosition(0, 0);
        scoreActor.setSize((stage.getWidth()), stage.getHeight());
        scoreStage.addActor(scoreActor);

        scoreStage.getRoot().setPosition(0, stage.getHeight());

        Gdx.input.setInputProcessor(this);

        if (USE_CONTACT_CACHE) {
            contactCache = new TestContactCache();
            contactCache.entities = world.entities;
            contactCache.setCacheTime(2f); // Change the contact time
        }

        // Sound
        gameSound = new GameSound();

        // Play background music.
//        gameSound.playBackgroundMusic(0.45f);
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

        if (rayTestCB.hasHit() && (((btRigidBody) player.body).getCenterOfMassPosition() != null)) {
            rayTestCB.getHitPointWorld(tmpV1);

            //Gdx.app.log("BANG", "BANG");
            Model model;
            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part("ball", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                    new Material("diffuseGreen", ColorAttribute.createDiffuse(Color.RED)))
                    .sphere(1f, 1f, 1f, 10, 10);
            model = modelBuilder.end();

            instance = new ModelInstance(model,tmpV1);

            Vector3 vec = new Vector3((tmpV1.x - ((btRigidBody) player.body).getCenterOfMassPosition().x), 0, (tmpV1.z - ((btRigidBody) player.body).getCenterOfMassPosition().z));

            float normFactor = 3 / vec.len();
            Vector3 normVec = new Vector3(normFactor * vec.x, normFactor * vec.y, normFactor * vec.z);

            player.body.activate();
            ((btRigidBody) player.body).applyCentralImpulse(normVec);
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
        //Gdx.app.log("RAY pick", "RAY pick");
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
            Model fotball = app.assets.get("football2.g3dj", Model.class);
            String id = fotball.nodes.get(0).id;

            Model apple = app.assets.get("apple.g3dj", Model.class);
            String id2 = apple.nodes.get(0).id;
            Node node = apple.getNode(id2);
            node.scale.set(0.8f, 0.8f, 0.8f);

            Model peach = app.assets.get("peach.g3dj", Model.class);
            String id3 = peach.nodes.get(0).id;
            Node node2 = peach.getNode(id3);

           /* disposables.add(fotball);
            world.addConstructor("fotball", new BulletConstructor(fotball, 1f, new btSphereShape(0.8f)));
            player = world.add("fotball", 0, 0.5f, 0.5f);
            //playerVec.add(player);
            player.body.setContactCallbackFlag(1);
            player.body.setContactCallbackFilter(1);

            disposables.add(apple);
            world.addConstructor("apple", new BulletConstructor(apple, 1f, new btSphereShape(0.8f)));
            player2 = world.add("apple", 0, 0.5f, 0.5f);
            //playerVec.add(player2);
            player2.body.setContactCallbackFilter(1);
            //player2.body.setContactCallbackFlag(1);

            disposables.add(peach);
            world.addConstructor("peach", new BulletConstructor(peach, 1f, new btSphereShape(0.8f)));
            player3 = world.add("peach", 0, 0.5f, 0.5f);
            //playerVec.add(player3);
            player3.body.setContactCallbackFilter(1);
           // player3.body.setContactCallbackFlag(1);
*/
            Player player_1 = new Player(fotball);
            world.addConstructor("test1", player_1.bulletConstructor);
            player = world.add("test1", 0, 2.5f, 2.5f);
            player.body.setContactCallbackFlag(1);
            player.body.setContactCallbackFilter(1);

            Player player_2 = new Player(apple);
            world.addConstructor("test2", player_2.bulletConstructor);
            player2 = world.add("test2", 0, 2.5f, 0.5f);
            player2.body.setContactCallbackFilter(1);

            Player player_3 = new Player(peach);
            world.addConstructor("test3", player_3.bulletConstructor);
            player3 = world.add("test3", 0, 2.5f, -2.5f);
            player3.body.setContactCallbackFilter(1);

            Gdx.app.log("Loaded", "LOADED");
            loading = false;
        }

        // Start till poängsättning
          if(app.assets.update()){
              if((((btRigidBody) player2.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player2.body).getCenterOfMassPosition().y > -10)
                      && (collisonUserId0 == 2 || collisonUserId1 == 2) && collisonUserId1 != -1 && collisonUserId0 != -1 ){
                  Gdx.app.log("PLAYER2", "KRASH");
                  score += 10;
              }
          if((((btRigidBody) player3.body).getCenterOfMassPosition().y < 0) && (((btRigidBody) player3.body).getCenterOfMassPosition().y > -10)
                    && (collisonUserId0 == 3 ||  collisonUserId1 == 3  && collisonUserId1 != -1 && collisonUserId0 != -1)){
                Gdx.app.log("PLAYER3", "KRASH");
                score += 10;
            }
            // Gameover
            if(((btRigidBody) player.body).getCenterOfMassPosition().y < 0 && !gameOverGameScreen ){
                Gdx.app.log("Fall", "fall");
                score += 10;
                gameOverGameScreen = true;
            }
            if(gameOverGameScreen)
                startGameOverTimer();
        }

        LabelScore.setText("Score: " + score);
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