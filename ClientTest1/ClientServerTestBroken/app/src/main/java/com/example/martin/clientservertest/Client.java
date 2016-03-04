package com.example.martin.clientservertest;

import android.os.AsyncTask;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by Martin on 2016-02-22.
 */
public class Client extends Thread {

    String dstAddress;
    int dstPort;
    String response = "";
    boolean connected = false;
    Socket socket;
    MainActivity activity;

    Client(String addr, int port){
        dstAddress = addr;
        dstPort = port;
    }

    @Override
    public void run()
    {

        socket = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        InputStream inputStream = null;
        byte[] buffer = null;
        int bytesRead;

        try{
            socket = new Socket(dstAddress, dstPort);           //Funkar inte på LiU och eduroam.
            connected = socket.isConnected();

            activity.runOnUiThread(new Runnable(){
                public void run()
                {
                    activity.debugMsg.setText("Debug Test.");
                }});
            /*if(connected)
            {

            }*/
            try
            {
                this.sleep(1000);
            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            while(connected) {

                byteArrayOutputStream = new ByteArrayOutputStream(1024);
                buffer = new byte[1024];
                bytesRead = 0;
                inputStream = socket.getInputStream();
                //inputStream.read() will block if no data return
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
            }
        }catch(UnknownHostException e)
        {
            //
            e.printStackTrace();
            response = "UnkownHostException: " + e.toString();
        }catch(IOException e)
        {
            //
            e.printStackTrace();
            response = response +  "IOException: " + e.toString();
        }finally
        {
            try
            {
                socket.close();

            }catch (IOException e)
            {
                //
                e.printStackTrace();
            }
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.response.setText(response);
            }
        });
        socket = null;
    }

    public boolean isSocketClosed()
    {
        return socket.isClosed();
    }

    public void disconnect()
    {
        connected = false;
    }
}
