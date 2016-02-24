package com.andreas.servertest1;

/**
 * Created by Andreas on 2016-02-22.
 */

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.support.v7.app.*;

public class MainActivity extends Activity
{
    Server server;
    TextView infoip, msg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView)findViewById(R.id.infoip);
        msg = (TextView)findViewById(R.id.msg);
        server = new Server(this);
        infoip.setText(server.getIpAdress() + ":" + server.getPort());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        server.onDestroy();
    }
}
