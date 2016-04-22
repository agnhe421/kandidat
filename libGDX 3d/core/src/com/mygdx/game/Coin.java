package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

/**
 * Created by sofiekhullar on 16-04-18.
 */
public class Coin extends BaseBulletTest{

    public float posx, posy, posz;
    public BulletConstructor bulletConstructor;
    private Model model;
    public boolean removed;

    public Coin(Model model){
       /* this.model = model;
        this.posx = posx;
        this.posy = posy;
        this.posz = posz; */
        removed = false;
        bulletConstructor = initBulletConstructor(model);
    }

    public BulletConstructor initBulletConstructor(Model model) {
        disposables.add(model);
        BulletConstructor bulletConstructor = (new BulletConstructor(model, 0.8f, new btBvhTriangleMeshShape(model.meshParts)));
        return bulletConstructor;
    }

    public void setRemoved(){
        removed = true;
    }
    public boolean getRemoved(){
        return removed;
    }
}
