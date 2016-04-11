package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

/**
 * Created by sofiekhullar on 16-04-08.
 */
public class Player extends BaseBulletTest {
    private BulletEntity bulletEntity;
    public BulletConstructor bulletConstructor;
    private Vector3 position;
    private Model model;
    private String modelString;
    private int score;

    public Player(Model model){
        this.model = model;
        bulletConstructor = initBulletEntity(model);
        position = new Vector3(0, 0.5f, 0.5f);
        score = 0;
    }

    public BulletConstructor initBulletEntity(Model model) {
        disposables.add(model);
        BulletConstructor bulletConstructor = (new BulletConstructor(model, 1f, new btSphereShape(0.8f)));
        return bulletConstructor;
    }

    public void setPosition(){
        ((btRigidBody) bulletEntity.body).getCenterOfMassPosition().set(position);
    }

    public Vector3 getPosition(){
        return position;
    }

    public void setScore(int points){
         score += points;
    }

    public int getScore(){
        return score;
    }
}
