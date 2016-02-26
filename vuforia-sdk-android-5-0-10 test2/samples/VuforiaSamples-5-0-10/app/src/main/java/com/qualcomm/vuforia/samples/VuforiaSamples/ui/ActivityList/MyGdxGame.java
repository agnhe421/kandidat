package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.qualcomm.vuforia.Renderer;

import javax.microedition.khronos.opengles.GL10;

public class MyGdxGame extends Game {

    public PerspectiveCamera cam;
        SpriteBatch batch;
        Texture img;

    Renderer mRenderer;

        @Override
        public void create () {

            mRenderer = Renderer.getInstance();





//            //mVideoMesh is a simple rect, with text coords
//            mVideoTextureShader.begin();
//            mVideoTextureShader.setUniformMatrix("u_projTrans", camera.combined);
//            mVideoTextureShader.setUniformi("s_texture", 0);
//            mVideoMesh.render(mVideoTextureShader, GL10.GL_TRIANGLE_FAN);
//            mVideoTextureShader.end();
//
//            gl.glActiveTexture(GL20.GL_TEXTURE0);
//            gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);


//            batch = new SpriteBatch();
//            img = new Texture("badlogic.jpg");
        }

        @Override
        public void render () {


            mRenderer = Renderer.getInstance();

            mRenderer.bindVideoBackground(0);

            Gdx.app.log("hej", "hej ");



//            Gdx.gl.glDisable(GL10.GL_CULL_FACE);
//            Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);

//            mRenderer = Renderer.getInstance();
//
//            mRenderer.bindVideoBackground(0);

//            Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
//            Gdx.gl.glEnable(GL10.GL_CULL_FACE);


        }


}
