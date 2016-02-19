/*===============================================================================
Copyright (c) 2012-2015 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/


package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.MyGdxGame;


// This activity starts activities which demonstrate the Vuforia features
public class ActivityLauncher extends AndroidApplication
{

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.MyGdxGame(), config);
    }


}
