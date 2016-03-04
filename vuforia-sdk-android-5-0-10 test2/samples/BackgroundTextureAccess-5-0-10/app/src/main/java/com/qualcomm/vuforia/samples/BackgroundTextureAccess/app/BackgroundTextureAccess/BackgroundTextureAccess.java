/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.qualcomm.vuforia.samples.BackgroundTextureAccess.app.BackgroundTextureAccess;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ObjectTracker;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.BackgroundTextureAccess.R;
import com.qualcomm.vuforia.samples.BackgroundTextureAccess.ui.SampleAppMenu.SampleAppMenu;
import com.qualcomm.vuforia.samples.BackgroundTextureAccess.ui.SampleAppMenu.SampleAppMenuGroup;
import com.qualcomm.vuforia.samples.BackgroundTextureAccess.ui.SampleAppMenu.SampleAppMenuInterface;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationControl;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationException;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleApplicationGLView;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;


public class BackgroundTextureAccess extends Activity implements
    SampleApplicationControl, SampleAppMenuInterface
{
    private static final String LOGTAG = "BackgroundTextureAccess";
    
    SampleApplicationSession vuforiaAppSession;
    
    // Constants for Hiding/Showing Loading dialog
    static final int HIDE_LOADING_DIALOG = 0;
    static final int SHOW_LOADING_DIALOG = 1;
    
    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    
    // Our renderer:
    private BackgroundTextureAccessRenderer mRenderer;
    
    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    
    private RelativeLayout mUILayout;
    
    private SampleAppMenu mSampleAppMenu;
    
    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
        this);
    private DataSet mDataSetTarmac;
    
    private boolean isARInitialized = false;
    
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    
    boolean mIsDroidDevice = false;
    
    // Called when the activity first starts or the user navigates back
    // to an activity.
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();
        
        vuforiaAppSession = new SampleApplicationSession(this);
        
        startLoadingAnimation();
        
        vuforiaAppSession
            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");
    }
    
    
    // We want to load specific textures from the APK, which we will later
    // use for rendering.
    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk(
            "BgdTexture/TextureTeapotRed.png", getAssets()));
    }
    
    
    // Tracker initialization and deinitialization.
    @Override
    public boolean doInitTrackers()
    {
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                LOGTAG,
                "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        
        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        
        return result;
    }
    
    
    // Functions to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData()
    {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        
        mDataSetTarmac = objectTracker.createDataSet();
        if (mDataSetTarmac == null)
            return false;
        
        if (!mDataSetTarmac.load("Tarmac.xml",
            STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;
        
        if (!objectTracker.activateDataSet(mDataSetTarmac))
            return false;
        
        int numTrackables = mDataSetTarmac.getNumTrackables();
        for (int count = 0; count < numTrackables; count++)
        {
            String name = "Tarmac : "
                + mDataSetTarmac.getTrackable(count).getName();
            mDataSetTarmac.getTrackable(count).setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                + (String) mDataSetTarmac.getTrackable(count).getUserData());
        }
        
        return true;
    }
    
    
    @Override
    public boolean doUnloadTrackersData()
    {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        
        if (mDataSetTarmac != null && mDataSetTarmac.isActive())
        {
            if (objectTracker.getActiveDataSet().equals(mDataSetTarmac)
                && !objectTracker.deactivateDataSet(mDataSetTarmac))
            {
                result = false;
            } else if (!objectTracker.destroyDataSet(mDataSetTarmac))
            {
                result = false;
            }
            
            mDataSetTarmac = null;
        }
        
        return result;
    }
    
    
    // Called when the activity will start interacting with the user.
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();
        
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        try
        {
            vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        if (isARInitialized)
        {
            VideoBackgroundConfig config = Renderer.getInstance()
                .getVideoBackgroundConfig();
            
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mRenderer
                .configureViewport(
                    (((int) (metrics.widthPixels - config.getSize().getData()[0])) / (int) 2)
                        + config.getPosition().getData()[0],
                    (((int) (metrics.heightPixels - config.getSize().getData()[1])) / (int) 2)
                        + config.getPosition().getData()[1], config.getSize()
                        .getData()[0], config.getSize().getData()[1]);
        }
        
        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
        
    }
    
    
    // Callback for configuration changes the activity handles itself
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        
        vuforiaAppSession.onConfigurationChanged();
        
        if (isARInitialized)
        {
            VideoBackgroundConfig vconfig = Renderer.getInstance()
                .getVideoBackgroundConfig();
            
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mRenderer
                .configureViewport(
                    (((int) (metrics.widthPixels - vconfig.getSize().getData()[0])) / (int) 2)
                        + vconfig.getPosition().getData()[0],
                    (((int) (metrics.heightPixels - vconfig.getSize().getData()[1])) / (int) 2)
                        + vconfig.getPosition().getData()[1], vconfig.getSize()
                        .getData()[0], vconfig.getSize().getData()[1]);
        }
    }
    
    
    // Called when the system is about to start resuming a previous activity.
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
        
        try
        {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
    }
    
    
    // The final call you receive before your activity is destroyed.
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        try
        {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Unload texture:
        mTextures.clear();
        mTextures = null;
        
        System.gc();
    }
    
    
    private void startLoadingAnimation()
    {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
            null);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        
        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
            .findViewById(R.id.loading_indicator);
        
        // Shows the loading indicator at start
        loadingDialogHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
        
        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    }
    
    
    // Initializes AR application components.
    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);
        
        mRenderer = new BackgroundTextureAccessRenderer(vuforiaAppSession);
        mRenderer.mActivity = this;
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
    }
    
    
    // Returns the number of registered textures.
    public int getTextureCount()
    {
        return mTextures.size();
    }
    
    
    // Returns the texture object at the specified index.
    public Texture getTexture(int i)
    {
        return mTextures.elementAt(i);
    }
    
    
    public boolean onTouchEvent(MotionEvent event)
    {
        float x, y;
        
        // Process the Gestures
        if (mSampleAppMenu != null)
            mSampleAppMenu.processEvent(event);
        
        if (mRenderer == null)
            return false;
        
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_UP:
                mRenderer.onTouchEvent(-100.0f, -100.0f);
                break;
            
            case MotionEvent.ACTION_POINTER_UP:
                mRenderer.onTouchEvent(-100.0f, -100.0f);
                break;
            
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            
            case MotionEvent.ACTION_MOVE:
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                
                x = Math.abs(event.getX(0)) / metrics.widthPixels;
                y = Math.abs(event.getY(0)) / metrics.heightPixels;
                
                if(x > 1.0f)
                    x = 1.0f;
                if(y > 1.0f)
                    y = 1.0f;
                
                mRenderer.onTouchEvent(x, y);
                break;
            
            default:
                break;
        }
        
        // Indicate that event was handled:
        return true;
    }
    
    
    @Override
    public boolean doStartTrackers()
    {
        // Indicate if the trackers were started correctly
        boolean result = true;
        
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
            ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();
        
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
            ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();
        
        return result;
    }
    
    
    @Override
    public void onInitARDone(SampleApplicationException exception)
    {
        
        if (exception == null)
        {
            initApplicationAR();
            
            mRenderer.mIsActive = true;
            isARInitialized = true;
            
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
            
            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();
            
            try
            {
                vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
            } catch (SampleApplicationException e)
            {
                Log.e(LOGTAG, e.getString());
            }
            
            // Hides the Loading Dialog
            loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            
            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            
            VideoBackgroundConfig config = Renderer.getInstance()
                .getVideoBackgroundConfig();
            
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mRenderer
                .configureViewport(
                    (((int) (metrics.widthPixels - config.getSize().getData()[0])) / (int) 2)
                        + config.getPosition().getData()[0],
                    (((int) (metrics.heightPixels - config.getSize().getData()[1])) / (int) 2)
                        + config.getPosition().getData()[1], config.getSize()
                        .getData()[0], config.getSize().getData()[1]);
            
            boolean result = CameraDevice.getInstance().setFocusMode(
                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
            
            if (!result)
                Log.e(LOGTAG, "Unable to enable continuous autofocus");
            
            mSampleAppMenu = new SampleAppMenu(this, this, "Background Texture",
                mGlView, mUILayout, null);
            setSampleAppMenuSettings();
            
        } else
        {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
        
    }
    
    
    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message)
    {
        final String errorMessage = message;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (mErrorDialog != null)
                {
                    mErrorDialog.dismiss();
                }
                
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    BackgroundTextureAccess.this);
                builder
                    .setMessage(errorMessage)
                    .setTitle(getString(R.string.INIT_ERROR))
                    .setCancelable(false)
                    .setIcon(0)
                    .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        });
                
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    
    
    @Override
    public void onQCARUpdate(State state)
    {
    }
    

    final public static int CMD_BACK = -1;
    
    
    // This method sets the menu's settings
    private void setSampleAppMenuSettings()
    {
        SampleAppMenuGroup group;
        
        group = mSampleAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);
        
        mSampleAppMenu.attachMenu();
    }
    
    
    @Override
    public boolean menuProcess(int command)
    {
        
        boolean result = true;
        
        switch (command)
        {
            case CMD_BACK:
                finish();
                break;
            
        }
        
        return result;
    }
    
}
