package com.networksockettest.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Andreas on 2016-03-16.
 */
public class JoinServer extends Thread
{
    protected String dstAdress, name;
    protected int dstPort;
    protected Socket socket;
    private String msgtake = "msgtake", msgsend = "msgsend", error = "No Error";
    private boolean connected;

    public JoinServer(String dstAdress, int dstPort, String name)
    {
        this.dstAdress = dstAdress;
        this.dstPort = dstPort;
        this.name = name;
        connected = false;
    }

    @Override
    public void run()
    {
        socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        try
        {
            socket = new Socket(dstAdress, dstPort);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            connected = true;
            msgsend = name + " has connected!";
            while(connected)
            {
                if(dataInputStream.available() > 0)
                {
                    msgtake = dataInputStream.readUTF();
                }
                if(connected && !msgsend.equals(""))
                {
                    dataOutputStream.writeUTF(msgsend);
                    dataOutputStream.flush();
                    msgsend = "";
                }
                //msg = "Connected!";
            }
        }catch(UnknownHostException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }finally
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
            if(dataInputStream != null)
            {
                try
                {
                    dataOutputStream.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error += "Exception: " + e.toString() + "\n";
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
                    error += "Exception: " + e.toString() + "\n";
                }
            }
        }
    }

    public String getError() {return error;}
    public String getMsg() {return msgtake;}

    public void disconnect()
    {
        connected = false;
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
    }

}
