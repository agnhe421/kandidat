package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import javax.swing.plaf.synth.SynthEditorPaneUI;

/**
 * Created by sofiekhullar on 16-04-08.
 */
public class Player extends BaseBulletTest {
    private BulletEntity bulletEntity;
    public BulletConstructor bulletConstructor;
    private Vector3 position;
    private Model model;
    private int score = 0;
    public int impulseFactor = 0;
    public int weigth = 0;
    private String name;

    // Variables
    private int weigthFotball = 3;
    private int weigthApple = 1;
    private int weigthPeach = 20;

    private int impulseFotball = 3;
    private int impulseApple = 2;
    private int impulsePeach = 1;

    public Player(Model model, String name){
        this.model = model;
        this.name = name;

        weigth = setWeigth(name);
        impulseFactor = setImpulseFactor(name);

        bulletConstructor = initBulletConstructor(model, weigth);
        position = new Vector3(0, 0.5f, 0.5f);
        score = 0;
    }

    public BulletConstructor initBulletConstructor(Model model, float weigth) {
        disposables.add(model);
        BulletConstructor bulletConstructor = (new BulletConstructor(model, weigth, new btSphereShape(0.8f)));
        return bulletConstructor;
    }


    // Set the weith depending on the model
    public int setWeigth(String name){
        if(name == "fotball"){
            return weigthFotball;
        }
        if(name == "apple"){
            return weigthApple;
        }
        if(name == "peach"){
            return weigthPeach;
        }
        else return 0;
    }

    // Set the impulse strength depending on the model
    public int setImpulseFactor(String name){
        if(name == "fotball"){
            return impulseFotball;
        }
        if(name == "apple"){
            return impulseApple;
        }
        if(name == "peach"){
            return impulsePeach;
        }
        else return 0;
    }

    public void setScore(int points){
         score += points;
    }

    public int getScore(){
        return score;
    }
}
