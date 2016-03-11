/*===============================================================================
Copyright (c) 2012-2015 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/


package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationControl;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationException;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargets;
import com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.MyGdxGame;


// This activity starts activities which demonstrate the Vuforia features
public class ActivityLauncher extends Activity implements SampleApplicationControl
{

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//
//        SampleApplicationSession vuforiaSession = new SampleApplicationSession(this);
//        vuforiaSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRT);
//
//
//        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//
//        config.r = config.g = config.b = config.a = 8;
//
//        initializeForView(new com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.MyGdxGame(), config);
////OCH EXTENDS AndroidApplication!!
//
        Intent intent = new Intent(this,
                com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargets.class);
        startActivity(intent);


    }


    @Override
    public boolean doInitTrackers() {
        return false;
    }

    @Override
    public boolean doLoadTrackersData() {
        return false;
    }

    @Override
    public boolean doStartTrackers() {
        return false;
    }

    @Override
    public boolean doStopTrackers() {
        return false;
    }

    @Override
    public boolean doUnloadTrackersData() {
        return false;
    }

    @Override
    public boolean doDeinitTrackers() {
        return false;
    }

    @Override
    public void onInitARDone(SampleApplicationException e) {

    }

    @Override
    public void onQCARUpdate(State state) {

    }
}
