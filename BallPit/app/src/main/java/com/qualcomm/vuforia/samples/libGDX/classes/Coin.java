package com.qualcomm.vuforia.samples.libGDX.classes;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.qualcomm.vuforia.samples.libGDX.bullet.BaseBulletTest;
import com.qualcomm.vuforia.samples.libGDX.bullet.BulletConstructor;

/**
 * Created by sofiekhullar on 16-04-18.
 */
public class Coin extends BaseBulletTest {

    public BulletConstructor bulletConstructor;
    private Model model;
    public boolean removed;

    public Coin(Model model){
        this.model = model;
        removed = true;
        bulletConstructor = initBulletConstructor(model);
    }

    public BulletConstructor initBulletConstructor(Model model) {
        disposables.add(model);
        BulletConstructor bulletConstructor = (new BulletConstructor(model, 0.2f, new btSphereShape(0.8f)));
        return bulletConstructor;
    }

    public void setRemoved(){
        removed = false;
    }
    public boolean getRemoved(){
        return removed;
    }
}
