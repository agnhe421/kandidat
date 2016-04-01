package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.qualcomm.QCAR.QCAR;
import com.qualcomm.vuforia.ImageTarget;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargetRenderer;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargets;

import javax.microedition.khronos.opengles.GL10;

    public class CustomTextureTest extends InputAdapter implements ApplicationListener  {
        public static class BgTextureUnitAttribute extends IntAttribute{
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
        public Matrix4 temp;

        @Override
        public void create () {
            Gdx.app.log("create","a");
            context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 2));
            modelBatch = new ModelBatch(context);
            environment = new Environment();
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
            environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

            cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            cam.position.set(0f, 0f, 2f);
            cam.lookAt(0, 0, 0);
            cam.near = 1f;
            cam.far = 300f;
            cam.update();




            Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));
            ModelBuilder modelBuilder = new ModelBuilder();
            model = modelBuilder.createBox(1, 1, 1,
                    new Material(TextureAttribute.createDiffuse(texture), new IntAttribute(BgTextureUnitAttribute.Type, 1)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
            model.manageDisposable(texture);



            renderable = new Renderable();
            model.nodes.get(0).parts.get(0).setRenderable(renderable);
            renderable.environment = environment;
            shader = new BgTextureShader(renderable);
            shader.init();
            renderable.shader = shader; // comment this line to see the difference

            customTexture = new Texture(Gdx.files.internal("egg.png"));
            customTexture.bind(1);
            Gdx.gl.glActiveTexture(0);

            Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController = new CameraInputController(cam)));
        }

        @Override
        public void render () {
            inputController.update();



//            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            Gdx.gl.glClearColor( 0, 0, 0, 0f );
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            float[] modelViewMatrix = DataHolder.getInstance().getData();

            if(modelViewMatrix != null)
            {

                temp = new Matrix4(modelViewMatrix);

                cam.combined.set(temp);

//
//                Gdx.app.log("---------------------------------------------","------------------------------------------------------------------");
//                Gdx.app.log("aa", "" + modelViewMatrix[0] + "  " + modelViewMatrix[1] + "  " + modelViewMatrix[2] + "  " + modelViewMatrix[3]);
//                Gdx.app.log("aa", "" + modelViewMatrix[4] + "  " + modelViewMatrix[5] + "  " + modelViewMatrix[6] + "  " + modelViewMatrix[7]);
//                Gdx.app.log("a", "" + modelViewMatrix[8] + "  " + modelViewMatrix[9] + "  " + modelViewMatrix[10] + "  " + modelViewMatrix[11]);
//                Gdx.app.log("aa", "" + modelViewMatrix[12] + "  " + modelViewMatrix[13] + "  " + modelViewMatrix[14] + "  " + modelViewMatrix[15]);
//                Gdx.app.log("---------------------------------------------", "------------------------------------------------------------------");

//                cam.view.mul(temp);

            }



//            cam.update();

//            Gdx.app.log("mm", "" + cam.view);
            context.begin();
            modelBatch.begin(cam);
            modelBatch.render(renderable);
            modelBatch.end();
            context.end();

        }

        @Override
        public void dispose () {
            modelBatch.dispose();
            model.dispose();
            customTexture.dispose();
            shader.dispose();
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