package com.andreas.pingtest;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class PingActivity extends AppCompatActivity {

    EditText input;
    TextView ip, ping;
    Pinger pinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        input = (EditText)findViewById(R.id.editText);
        ip = (TextView)findViewById(R.id.textView);
        ip.setText(getIpAddress());
        ping = (TextView)findViewById(R.id.textView2);
        pinger = new Pinger();
        Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(listener);
    }

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!pinger.isAlive())
                pinger.start();
        }
    };

    private class Pinger extends Thread
    {
        Editable host;
        InetAddress address;
        @Override
        public void run()
        {
            host = input.getText();
            address = null;
            try
            {
                address = InetAddress.getByName(host.toString());
                //ping.setText(address.getByName(host.toString()).toString());
            }catch(UnknownHostException e)
            {
                e.printStackTrace();
                final String temp = e.toString();
                PingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ping.setText("Exception: " + temp);
                    }
                });
            }
            if(address != null)
            {
                try
                {
                    if(address.isReachable(5000))
                    {
                        PingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ping.append("\n" + host + " - Respond OK");
                            }
                        });
                    }
                    else {
                        PingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ping.append("\n" + host);
                            }
                        });
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    final String temp = e.toString();
                    PingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ping.append("\n" + temp);
                        }
                    });
                }
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

}
