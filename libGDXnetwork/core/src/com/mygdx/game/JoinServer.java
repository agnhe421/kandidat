package com.mygdx.game;


import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

//import org.jbox2d.common.Vec3;

/**
 * Created by Andreas on 2016-03-02.
 */
public class JoinServer extends Thread
{
    protected String dstAdress;
    protected int dstPort;
    protected String name;
    protected Socket socket;
    boolean connected = false;
    //Vec3 position;
    String error;

    public JoinServer(String dstAdress, int dstPort, String name)
    {
        this.dstAdress = dstAdress;
        this.dstPort = dstPort;
        this.name = name;
    }

    @Override
    public void run()
    {
        socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        ObjectOutputStream objOutputStream = null;
        ObjectInputStream objInputStream = null;

        try
        {
            socket = new Socket(dstAdress, dstPort);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            connected = socket.isConnected();
            while(connected)
            {
                /*if(dataInputStream.available() > 0)
                {

                }
                if()
                {

                }*/
            }
        }catch(UnknownHostException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        } finally
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
            }
            if(dataOutputStream != null)
            {
                try
                {
                    dataOutputStream.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
            }
            if(dataInputStream != null)
            {
                try
                {
                    dataInputStream.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
            }
        }
    }

    private void sendData() {}

    private void disconnect() { connected = false; }

}
