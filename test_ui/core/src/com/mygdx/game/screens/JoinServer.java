package com.mygdx.game.screens;


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
        //The name is placeholder, you should be able to enter it yourself when it is integrated with the UI.
        this.name = name;
        connected = false;
    }

    @Override
    public void run()
    {
        //Instantiate the socket and the input/output streams.
        socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        try
        {
            //Bind the socket to the given address and port.
            socket = new Socket(dstAdress, dstPort);
            //Set streams to read from the socket.
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //Set connection state.
            connected = true;
            //Set the message to send to the server.
            msgsend = name + " has connected!";
            while(connected)
            {
                //If available, read input from server.
                if(dataInputStream.available() > 0)
                {
                    msgtake = dataInputStream.readUTF();
                    if(msgtake.equals("SERVER_SHUTDOWN"))
                        connected = false;
                }
                //If there is a message to send available, send it to the server.
                if(connected && !msgsend.equals(""))
                {
                    dataOutputStream.writeUTF(msgsend);
                    dataOutputStream.flush();
                    msgsend = "";
                }
            }
            if(!msgtake.equals("SERVER_SHUTDOWN"))
            {
                dataOutputStream.writeUTF("CONNECTION_SHUTDOWN");
                dataOutputStream.flush();
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
            //Close all streams and the socket.
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
        //Set connected state to false.
        connected = false;
    }

}
