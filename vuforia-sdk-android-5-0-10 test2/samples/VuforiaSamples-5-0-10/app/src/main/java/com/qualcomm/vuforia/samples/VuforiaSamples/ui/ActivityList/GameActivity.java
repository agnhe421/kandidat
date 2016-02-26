package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Created by DJ on 2016-02-26.
 */
public class GameActivity extends AndroidApplication {

    public void onCreate (android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList.CustomTextureTest(), config);
    }

}
