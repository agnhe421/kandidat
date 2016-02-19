/*==============================================================================
Copyright (c) 2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.
 
Confidential and Proprietary - Qualcomm Connected Experiences, Inc.
Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.

@file
    DigitalEyewearRenderer.java

@brief
    Sample for Digital Eyewear

==============================================================================*/


package com.qualcomm.vuforia.samples.StereoRendering.app.StereoRendering;

import java.util.Vector;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Eyewear;
import com.qualcomm.vuforia.EYEID;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;

import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Teapot;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;


//The renderer class for the Digital Eyewear ImageTargets sample. 
public class StereoRenderingRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "DigitalEyewearRenderer";
 
    private SampleApplicationSession vuforiaAppSession;
    private StereoRendering mActivity;
 
    private Vector<Texture> mTextures;
 
    private int shaderProgramID;
 
    private int vertexHandle;
 
    private int normalHandle;
 
    private int textureCoordHandle;
 
    private int mvpMatrixHandle;
 
    private int texSampler2DHandle;
 
    private Teapot mTeapot;
 
    private Renderer mRenderer;
 
    boolean mIsActive = false;
 
    private boolean mIsEyewear = true;
 
    private static final float OBJECT_SCALE_FLOAT = 3.0f;

    BackgroundMesh vbMesh = null;

    private int vbShaderProgramID;
    
    private int vbVertexPositionHandle;
    
    private int vbVertexTexCoordHandle;
    
    private int vbTexSampler2DHandle;
    
    private int vbProjectionMatrixHandle;
    
    private Matrix44F vbOrthoProjMatrix;

    private int viewportPosX;

    private int viewportPosY;

    private int viewportSizeX;

    private int viewportSizeY;

    public StereoRenderingRenderer(StereoRendering activity,
            SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
    }

    
    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content
        renderFrame();
    }
    
    
    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        
        initRendering();
        
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }
    
    
    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged width=" + width + " height=" + height);

        DisplayMetrics metrics = new DisplayMetrics();
        Eyewear eyewear = Eyewear.getInstance();

        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Vec2I backgroundSize = Renderer.getInstance().getVideoBackgroundConfig().getSize();
        Vec2I backgroundPos = Renderer.getInstance().getVideoBackgroundConfig().getPosition();

        // If this device is not supported, or it is occluded (that is, we show a video background), then we adopt the
        // standard Vuforia viewport calculation by which we adjust the viewport to match the video aspect ratio.
        if (!eyewear.isDeviceDetected() || !eyewear.isSeeThru())
        {
            viewportPosX = ((metrics.widthPixels - backgroundSize.getData()[0]) / 2) + backgroundPos.getData()[0];
            viewportPosY = ((metrics.heightPixels - backgroundSize.getData()[1]) / 2) + backgroundPos.getData()[1];
            viewportSizeX = backgroundSize.getData()[0];
            viewportSizeY = backgroundSize.getData()[1];
        }
        // This is a supported see-through device, so the viewport needs to match the OpenGL surface size. The device
        // calibration relies on this assumption.
        else
        {
            viewportPosX = 0;
            viewportPosY = 0;
            viewportSizeX= width;
            viewportSizeY = height;
        }

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }
    
    
    // Function for initializing the renderer.
    private void initRendering()
    {
        mTeapot = new Teapot();
        mRenderer = Renderer.getInstance();
        
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);
        
        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(CubeShaders.CUBE_MESH_VERTEX_SHADER,
            CubeShaders.CUBE_MESH_FRAGMENT_SHADER);
        
        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,"modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(BackgroundShader.VB_VERTEX_SHADER,
                BackgroundShader.VB_FRAGMENT_SHADER);

        if (vbShaderProgramID > 0)
        {
            // Activate shader:
            GLES20.glUseProgram(vbShaderProgramID);

            // Retrieve handler for vertex position shader attribute variable:
            vbVertexPositionHandle = GLES20.glGetAttribLocation(vbShaderProgramID, "vertexPosition");

            // Retrieve handler for texture coordinate shader attribute
            // variable:
            vbVertexTexCoordHandle = GLES20.glGetAttribLocation(vbShaderProgramID, "vertexTexCoord");

            // Retrieve handler for texture sampler shader uniform variable:
            vbTexSampler2DHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "texSampler2D");

            // Retrieve handler for projection matrix shader uniform variable:
            vbProjectionMatrixHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "projectionMatrix");

            // Set the orthographic matrix
            vbOrthoProjMatrix = Eyewear.getInstance().getOrthographicProjectionMatrix();

            // Stop using the program
            GLES20.glUseProgram(0);
        }
        
        // Hide the Loading Dialog
        mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
        
    }

    // The render function.
    private void renderFrame()
    {
        Eyewear eyewear = Eyewear.getInstance();
        checkEyewearStereo(eyewear);
        int numEyes = 1;
        if (eyewear.isStereoEnabled())
        {
            numEyes = 2;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        State state = mRenderer.begin();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
        {
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        }
        else
        {
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
        }
            
        // Don't draw video background on see-thru eyewear
        if (!eyewear.isSeeThru())
        {
            renderVideoBackground(0, numEyes);
        }
        
        // Render once for each eye
        for (int eyeIdx = 0; eyeIdx < numEyes; eyeIdx++)
        {
            Matrix44F projectionMatrix;

            int eyeViewportPosX = viewportPosX;
            int eyeViewportPosY = viewportPosY;
            int eyeViewportSizeX = viewportSizeX;
            int eyeViewportSizeY = viewportSizeY;

            if (eyewear.isDeviceDetected())
            {
                if (numEyes < 2)
                {
                    projectionMatrix = Eyewear.getInstance().getProjectionMatrix(EYEID.EYEID_MONOCULAR);
                }
                else
                {
                    // Setup the viewport filling half the screen
                    // Position viewport for left or right eye
                    if (eyeIdx == 0) // left eye
                    {
                        eyeViewportSizeX /= 2;
                        projectionMatrix = Eyewear.getInstance().getProjectionMatrix(EYEID.EYEID_LEFT);
                    }
                    else // right eye
                    {
                        eyeViewportPosX = eyeViewportSizeX / 2;
                        eyeViewportSizeX /= 2;
                        projectionMatrix = Eyewear.getInstance().getProjectionMatrix(EYEID.EYEID_RIGHT);
                    }
                }
            }
            else
            {
                // This is a standard mobile device, so use the supplied mProjectionMatrix
                projectionMatrix = vuforiaAppSession.getProjectionMatrix();
            }

            // Set the viewport
            GLES20.glViewport(eyeViewportPosX, eyeViewportPosY, eyeViewportSizeX, eyeViewportSizeY);

            // did we find any trackables this frame?
            for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
            {
                TrackableResult result = state.getTrackableResult(tIdx);
                Trackable trackable = result.getTrackable();
                printUserData(trackable);
                Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();
                
                int textureIndex = trackable.getName().equalsIgnoreCase("stones") ? 0 : 1;
                
                // deal with the modelview and projection matrices
                float[] modelViewProjection = new float[16];
                
                Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, OBJECT_SCALE_FLOAT);
                Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);

                Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix.getData(), 0, modelViewMatrix, 0);
                
                // activate the shader program and bind the vertex/normal/tex coords
                GLES20.glUseProgram(shaderProgramID);
                
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mTeapot.getVertices());
                GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, mTeapot.getNormals());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());
                
                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(normalHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);
                
                // activate texture 0, bind it, and pass to shader
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                
                // pass the model view matrix to the shader
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
                
                // finally draw the teapot
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                    mTeapot.getIndices());
                
                // disable the enabled arrays
                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);
            }

            SampleUtils.checkGLError("Render Frame");
        }
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        mRenderer.end();
    }

    private void checkEyewearStereo(Eyewear eyewear)
    {
        if (eyewear.isDeviceDetected() && eyewear.isStereoCapable())
        {
            mIsEyewear = true;

            // Change the glasses into stereo mode
            if (!eyewear.isStereoEnabled())
            {
                if (eyewear.setStereo(true))
                {
                    // Re-acquire the orthographic projection matrix which will 
                    // have changed now we are in stereo
                    vbOrthoProjMatrix = Eyewear.getInstance().getOrthographicProjectionMatrix();
                }
                else
                {
                    Log.e(LOGTAG, "Error setting device to stereo mode");
                }
            }
        }
        else
        {
            if (mIsEyewear)
            {
                mIsEyewear = false;
                // Re-acquire the orthographic projection matrix which may have changed
                vbOrthoProjMatrix = Eyewear.getInstance().getOrthographicProjectionMatrix();
            }
        }
    }
    
    private void renderVideoBackground(int vbVideoTextureUnit, int numEyes)
    {
        // Bind the video bg texture and get the Texture ID from Vuforia
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
            {
                isActivityPortrait = false;
            }
            else
            {
                isActivityPortrait = true;
            }

            // Create a 2 triangle mesh for simple video background rendering
            // If a more complex shader is to be applied you should increase the number of
            // triangles in the background mesh.
            vbMesh = new BackgroundMesh(2, 2, isActivityPortrait);

            if (!vbMesh.isValid())
            {
                vbMesh = null;
                Log.e(LOGTAG, "VB Mesh not valid!!");
                return;
            }
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // Load the shader and upload the vertex/texcoord/index data
        GLES20.glUseProgram(vbShaderProgramID);
        GLES20.glVertexAttribPointer(vbVertexPositionHandle, 3, GLES20.GL_FLOAT, false, 0,
            vbMesh.getBuffer(BackgroundMesh.BUFFER_TYPE_VERTEX));

        GLES20.glVertexAttribPointer(vbVertexTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
            vbMesh.getBuffer(BackgroundMesh.BUFFER_TYPE_TEXTURE_COORD));

        GLES20.glUniform1i(vbTexSampler2DHandle, vbVideoTextureUnit);
        GLES20.glUniformMatrix4fv(vbProjectionMatrixHandle, 1, false, vbOrthoProjMatrix.getData(), 0);

        // Render the video background with the custom shader
        // First, we enable the vertex arrays
        GLES20.glEnableVertexAttribArray(vbVertexPositionHandle);
        GLES20.glEnableVertexAttribArray(vbVertexTexCoordHandle);

        // Then, we issue the render call
        for (int eyeIdx=0; eyeIdx<numEyes; eyeIdx++)
        {
            if (Eyewear.getInstance().isDeviceDetected())
            {
                int eyeViewportPosX = viewportPosX;
                int eyeViewportPosY = viewportPosY;
                int eyeViewportSizeX = viewportSizeX;
                int eyeViewportSizeY = viewportSizeY;

                // Setup the viewport filling half the screen
                // Position viewport for left or right eye
                if (eyeIdx == 0) // left eye
                {
                    eyeViewportSizeX = viewportSizeX / 2;
                }
                else // right eye
                {
                    eyeViewportPosX = viewportSizeX / 2;
                    eyeViewportSizeX = viewportSizeX / 2;
                }
                GLES20.glViewport(eyeViewportPosX, eyeViewportPosY, eyeViewportSizeX, eyeViewportSizeY);
            }
            else
            {
                GLES20.glViewport(viewportPosX, viewportPosY, viewportSizeX, viewportSizeY);
            }

            // Then, we issue the render call
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, vbMesh.getNumObjectIndex(), GLES20.GL_UNSIGNED_BYTE, 
                vbMesh.getBuffer(BackgroundMesh.BUFFER_TYPE_INDICES));
        }

        // Finally, we disable the vertex arrays
        GLES20.glDisableVertexAttribArray(vbVertexPositionHandle);
        GLES20.glDisableVertexAttribArray(vbVertexTexCoordHandle);
        
        // Wrap up this rendering
        GLES20.glUseProgram(0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        SampleUtils.checkGLError("Rendering of the video background failed");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }


    private void printUserData(Trackable trackable)
    {
        String userData = (String) trackable.getUserData();
        Log.d(LOGTAG, "UserData:Retreived User Data \"" + userData + "\"");
    }
    
    
    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
    }

}
