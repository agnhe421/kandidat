package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.qualcomm.QCAR.QCAR;
import com.qualcomm.vuforia.ImageTarget;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargetRenderer;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargets;

import javax.microedition.khronos.opengles.GL10;

public class CustomTextureTest extends InputAdapter implements ApplicationListener {
    public static class BgTextureUnitAttribute extends IntAttribute {
        public static final String Alias = "bgTtextureUnit";
        public final static long Type = register(Alias);
        public BgTextureUnitAttribute(long type, int textureNum) {
            super(type, textureNum);
        }
    }

    public static class BgTextureShader extends DefaultShader {
        public final static Setter customDiffuseTexture = new Setter() {
            @Override public boolean isGlobal (BaseShader shader, int inputID) { return false; }
            @Override public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                final int unit = ((IntAttribute)(combinedAttributes.get(BgTextureUnitAttribute.Type))).value;
                shader.set(inputID, unit);
            }
        };

        public BgTextureShader (Renderable renderable) {
            super(renderable);
            register(Inputs.diffuseTexture, customDiffuseTexture);
        }
    }

    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public Renderable renderable;
    public Environment environment;
    public RenderContext context;
    public Texture customTexture;
    public Shader shader;

    Renderer mRenderer;


    @Override
    public void create () {







    }

    @Override
    public void render () {

//
//        ImageTargets a = new ImageTargets();
//
//        a.initApplicationAR();


        GL20 gl = Gdx.gl20;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        Gdx.gl.glDisable(GL10.GL_CULL_FACE);
        Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
        Renderer.getInstance().begin();



        Renderer.getInstance().drawVideoBackground();

        Gdx.app.log("hej", "hej ");


        Renderer.getInstance().end();


    }

    @Override
    public void dispose () {

    }

    public boolean needsGL20 () {
        return true;
    }

    public void resume () {
    }

    public void resize (int width, int height) {
    }

    public void pause () {
    }
}