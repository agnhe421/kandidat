/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.qualcomm.vuforia.samples.BackgroundTextureAccess.app.BackgroundTextureAccess;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Teapot;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;


// The renderer class for the BackgroundTextureAccess sample.
public class BackgroundTextureAccessRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "BackgroundTextureAccessRenderer";
    
    SampleApplicationSession vuforiaAppSession;
    
    private static final float OBJECT_SCALE_FLOAT = 3.0f;
    
    public boolean mIsActive = false;
    
    public BackgroundTextureAccess mActivity;
    
    private Vector<Texture> mTextures;
    
    private int shaderProgramID;
    
    private int vertexHandle;
    
    private int normalHandle;
    
    private int textureCoordHandle;
    
    private int mvpMatrixHandle;
    
    private int texSampler2DHandle;
    
    private Teapot mTeapot;
    
    private int vbShaderProgramID;
    
    private int vbVertexPositionHandle;
    
    private int vbVertexTexCoordHandle;
    
    private int vbTexSampler2DHandle;
    
    private int vbProjectionMatrixHandle;
    
    private int vbTouchLocationXHandle;
    
    private int vbTouchLocationYHandle;
    
    private float[] vbOrthoProjMatrix;
    
    private int viewportPosition_x;
    
    private int viewportPosition_y;
    
    private int viewportSize_x;
    
    private int viewportSize_y;
    
    private float touchLocation_x;
    
    private float touchLocation_y;
    
    BackgroundMesh vbMesh = null;
    
    
    public BackgroundTextureAccessRenderer(SampleApplicationSession appSession)
    {
        vuforiaAppSession = appSession;
    }
    
    
    public void configureViewport(int viewportPosition_x,
        int viewportPosition_y, int viewportSize_x, int viewportSize_y)
    {
        this.viewportPosition_x = viewportPosition_x;
        this.viewportPosition_y = viewportPosition_y;
        this.viewportSize_x = viewportSize_x;
        this.viewportSize_y = viewportSize_y;
    }
    
    
    // Renderer initializing function.
    public void initRendering()
    {
        mTeapot = new Teapot();
        
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
            : 1.0f);
        
        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
            Shaders.CUBE_MESH_VERTEX_SHADER, Shaders.CUBE_MESH_FRAGMENT_SHADER);
        
        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "texSampler2D");
        
        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(
            Shaders.BTA_VERTEX_SHADER, Shaders.BTA_FRAGMENT_SHADER);
        
        if (vbShaderProgramID > 0)
        {
            // Activate shader:
            GLES20.glUseProgram(vbShaderProgramID);
            
            // Retrieve handler for vertex position shader attribute variable:
            vbVertexPositionHandle = GLES20.glGetAttribLocation(
                vbShaderProgramID, "vertexPosition");
            
            // Retrieve handler for texture coordinate shader attribute
            // variable:
            vbVertexTexCoordHandle = GLES20.glGetAttribLocation(
                vbShaderProgramID, "vertexTexCoord");
            
            // Retrieve handler for texture sampler shader uniform variable:
            vbTexSampler2DHandle = GLES20.glGetUniformLocation(
                vbShaderProgramID, "texSampler2D");
            
            // Retrieve handler for projection matrix shader uniform variable:
            vbProjectionMatrixHandle = GLES20.glGetUniformLocation(
                vbShaderProgramID, "projectionMatrix");
            
            // Retrieve handler for projection matrix shader uniform variable:
            vbTouchLocationXHandle = GLES20.glGetUniformLocation(
                vbShaderProgramID, "touchLocation_x");
            
            // Retrieve handler for projection matrix shader uniform variable:
            vbTouchLocationYHandle = GLES20.glGetUniformLocation(
                vbShaderProgramID, "touchLocation_y");
            
            // Set the orthographic matrix
            vbOrthoProjMatrix = SampleUtils.getOrthoMatrix(-1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, 1.0f);
            
            // Stop using the program
            GLES20.glUseProgram(0);
        }
        
    }
    
    
    // Called when the surface is created or recreated.
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        
        // Call function to initialize rendering:
        initRendering();
        
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        Vuforia.onSurfaceCreated();
    }
    
    
    // Called when the surface changed size.
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
        
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
    }
    
    
    // The render function.
    public void renderFrame()
    {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        
        // ///////////////////////////////////////////////////////////////
        // This section renders the video background with a
        // custom shader defined in Shaders.java
        
        // Bind the video bg texture and get the Texture ID from Vuforia
        int vbVideoTextureUnit = 0;
        if (!Renderer.getInstance().bindVideoBackground(vbVideoTextureUnit))
        {
            Log.e(LOGTAG, "Unable to bind video background texture!!");
            return;
        }
        
        // We need a finer mesh for this background
        // We have to create it here because it will request the texture info of
        // the video background
        if (vbMesh == null)
        {
            boolean isActivityPortrait;
            Configuration config = mActivity.getResources().getConfiguration();
            
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                isActivityPortrait = false;
            else
                isActivityPortrait = true;
            
            vbMesh = new BackgroundMesh(10, 10, isActivityPortrait);
            if (!vbMesh.isValid())
            {
                vbMesh = null;
                Log.e(LOGTAG, "VB Mesh not valid!!");
                return;
            }
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        
        // Set the viewport
        GLES20.glViewport(viewportPosition_x, viewportPosition_y,
            viewportSize_x, viewportSize_y);
        
        // Load the shader and upload the vertex/texcoord/index data
        GLES20.glUseProgram(vbShaderProgramID);
        GLES20.glVertexAttribPointer(vbVertexPositionHandle, 3,
            GLES20.GL_FLOAT, false, 0,
            vbMesh.getBuffer(BackgroundMesh.BUFFER_TYPE_VERTEX));
        GLES20.glVertexAttribPointer(vbVertexTexCoordHandle, 2,
            GLES20.GL_FLOAT, false, 0,
            vbMesh.getBuffer(BackgroundMesh.BUFFER_TYPE_TEXTURE_COORD));
        GLES20.glUniform1i(vbTexSampler2DHandle, vbVideoTextureUnit);
        GLES20.glUniformMatrix4fv(vbProjectionMatrixHandle, 1, false,
            vbOrthoProjMatrix, 0);
        GLES20.glUniform1f(vbTouchLocationXHandle,
            (touchLocation_x * 2.0f) - 1.0f);
        GLES20.glUniform1f(vbTouchLocationYHandle,
            (2.0f - (touchLocation_y * 2.0f)) - 1.0f);
        
        // Render the video background with the custom shader
        // First, we enable the vertex arrays
        GLES20.glEnableVertexAttribArray(vbVertexPositionHandle);
        GLES20.glEnableVertexAttribArray(vbVertexTexCoordHandle);
        
        // Then, we issue the render call
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vbMesh.getNumObjectIndex(),
            GLES20.GL_UNSIGNED_BYTE,
            vbMesh.getBuffer(BackgroundMesh.BUFFER_TYPE_INDICES));
        
        // Finally, we disable the vertex arrays
        GLES20.glDisableVertexAttribArray(vbVertexPositionHandle);
        GLES20.glDisableVertexAttribArray(vbVertexTexCoordHandle);
        
        // Wrap up this rendering
        GLES20.glUseProgram(0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        SampleUtils.checkGLError("Rendering of the background failed");
        
        // ///////////////////////////////////////////////////////////////
        // The following section is similar to image targets
        // we still render the teapot on top of the targets
        
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
        // We must detect if background reflection is active and adjust the
        // culling direction.
        // If the reflection is active, this means the post matrix has been
        // reflected as well,
        // therefore standard counter clockwise face culling will result in
        // "inside out" models.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
            
        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
        {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(tIdx);
            Matrix44F modelViewMatrixMat44f = Tool
                .convertPose2GLMatrix(trackableResult.getPose());
            
            // Choose the texture based on the target name:
            int textureIndex = 0;
            
            Texture thisTexture = mTextures.get(textureIndex);
            
            // deal with the modelview and projection matrices
            float[] modelViewProjection = new float[16];
            float[] modelViewMatrix = modelViewMatrixMat44f.getData();
            Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f,
                OBJECT_SCALE_FLOAT);
            Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT,
                OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
            Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            
            GLES20.glUseProgram(shaderProgramID);
            
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                false, 0, mTeapot.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 0, mTeapot.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());
            
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                thisTexture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0 /* GL_TEXTURE0 */);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                mTeapot.getIndices());
            
            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
            
            SampleUtils.checkGLError("BackgroundTextureAccess renderFrame");
            
        }
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        // ///////////////////////////////////////////////////////////////
        
        // It is always important to tell the Vuforia Renderer
        // that we are finished
        Renderer.getInstance().end();
        
    }
    
    
    @SuppressWarnings("unused")
    private void printUserData(Trackable trackable)
    {
        String userData = (String) trackable.getUserData();
        Log.d(LOGTAG, "UserData.Retreived User Data	\"" + userData + "\"");
    }
    
    
    /** Called to draw the current frame. */
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content
        renderFrame();
    }
    
    
    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
        
    }
    
    
    public void onTouchEvent(float x, float y)
    {
        if ((x >= -1.0) && (x <= 1.0))
            touchLocation_x = x;
        else
            touchLocation_x = -100.0f;
        if ((y >= -1.0) && (y <= 1.0))
            touchLocation_y = y;
        else
            touchLocation_y = -100.0f;

    }
}
