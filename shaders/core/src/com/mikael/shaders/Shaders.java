package com.mikael.shaders;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.utils.JsonReader;


public class Shaders extends ApplicationAdapter implements ApplicationListener {
	private PerspectiveCamera cam;
	private Environment environment;
	private CameraInputController camController;
	private Shader shader;
	private Model apple;
	private Renderable renderable;
	private RenderContext renderContext;
	private Model model;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private ModelBatch modelBatch;
	private AssetManager assets;
	private Texture tex;
	private BitmapFont font;
	private SpriteBatch batch;
	private DirectionalLight light;
	
	@Override
	public void create () {
		cam = new PerspectiveCamera(67,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(2f, 2f, 2f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -5f, -5f, 0f);

		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.YELLOW);

		assets = new AssetManager();
		assets.load("apple.g3dj", Model.class);
		assets.load("apple.jpg", Texture.class);
		assets.finishLoading();

		apple = assets.get("apple.g3dj", Model.class);
		tex = assets.get("apple.jpg", Texture.class);

		NodePart blockPart = apple.nodes.get(0).parts.get(0);

		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = environment;
		renderable.worldTransform.idt();

		renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

		shader = new MShader();
		shader.init();
	}

	@Override
	public void render () {
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		tex.bind(0);

		renderContext.begin();
		shader.begin(cam, renderContext);
		shader.render(renderable);
		shader.end();
		renderContext.end();

		//batch.begin();
		//font.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 200, 200);
		//batch.end();

	}

	@Override
	public void dispose(){
		assets.dispose();
		shader.dispose();
		batch.dispose();
		font.dispose();
	}

	@Override
	public void resize(int width, int height){

	}

	@Override
	 public void pause(){

	}

	@Override
	public void resume(){

	}
}