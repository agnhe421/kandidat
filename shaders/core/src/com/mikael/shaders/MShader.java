package com.mikael.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MShader implements Shader{

    ShaderProgram program;
    Camera camera;
    RenderContext context;
    DirectionalLight light;
    int u_projViewTrans;
    int u_worldTrans;
    int u_light;

    @Override
    public void init () {
        program = new ShaderProgram(
                Gdx.files.internal("data/mshader.vertex.glsl"),
                Gdx.files.internal("data/mshader.fragment.glsl"));
        if(!program.isCompiled()){
            throw new GdxRuntimeException(program.getLog());
        }
        u_projViewTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_light = program.getUniformLocation("u_light");
    }

    @Override
    public void dispose () {
        program.dispose();
    }

    @Override
    public void begin (Camera cam, RenderContext rc) {
        this.camera = cam;
        this.context = rc;
        program.begin();
        program.setUniformMatrix(u_projViewTrans, camera.combined);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render (Renderable renderable) {
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        renderable.meshPart.render(program);
    }

    @Override
    public void end () {
        program.end();
    }

    @Override
    public int compareTo (Shader other) {
        return 0;
    }

    @Override
    public boolean canRender (Renderable instance) {
        return true;
    }
}
