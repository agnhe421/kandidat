package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by DJ on 2016-02-26.
 */
public class libgdxRenderer extends ApplicationAdapter {

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}

