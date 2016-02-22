package com.ebookfrency.threadexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Message;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            TextView myTextView = (TextView)findViewById(R.id.myTextView);
            myTextView.setText("Button Pressed");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View view)
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                long endTime = System.currentTimeMillis() + 20 * 1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try
                        {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e)
                        {

                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
