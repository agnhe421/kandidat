package com.networksockettest.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
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
    private String msgtake = "msgtake", msgsend = "", error = "No Error", strConv = "";
    private boolean connected;
    private static final int SIZE = 1024;
    private byte[] buffer;
    private int reads, msgnr;

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
        buffer = new byte[SIZE];
        //Instantiate the socket and the input/output streams.
        socket = null;
        msgnr = 1;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        try
        {
            //Bind the socket to the given address and port.
            socket = new Socket(dstAdress, dstPort);
            //Set streams to read from the socket.
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //socket.setSoTimeout(5000);
            //Set connection state.
            connected = true;
            //Set the message to send to the server.
            while(connected)
            {
                if(!msgsend.equals(""))
                    //error = "Send: " + msgsend + " NR: " + msgnr + "\nName: " + name;
                strConv = "";
                //If available, read input from server.
                /*if(dataInputStream.available() > 0)
                {
                    msgtake = dataInputStream.readUTF();
                    //if(msgtake.equals("SERVER_SHUTDOWN"))
                    //    connected = false;
                    if(name.equals("player"))
                        setJoinName(msgtake);
                    else if(!msgtake.equals(name))
                        connected = false;
                }*/
                if(name == "player")
                    msgsend = name;
                //If there is a message to send available, send it to the server.

                if(connected && !msgsend.equals(""))
                {
                    error = "Sending1: " + msgsend + " NR: " + msgnr;
                    sendMessage(dataOutputStream, msgsend);
                    //error = "Sending2: " + msgsend + " NR: " + msgnr;
                    ++msgnr;
                    msgsend = "";
                }
                //msgtake = "Waiting for new message...";
                reads = dataInputStream.read(buffer, 0, SIZE);
                String temp = new String(buffer).trim();
                for(int idt = 0; idt < temp.length(); ++idt)
                {
                    if(temp.charAt(idt) == '/')
                        break;
                    else
                        strConv += temp.charAt(idt);
                }
                if(reads == -1)
                {
                    msgtake = "Server is offline.";
                    connected = false;
                    break;
                }
                else if(strConv.equals("heartbeat"))
                {
                    msgsend = "heartbeat";
//                    dataOutputStream.writeUTF(msgsend);
//                    dataOutputStream.flush();
//                    msgsend = "";
                    msgtake = "heartbeat received from server";
                }
                else if(strConv.equals("NAME_CHANGE"))
                {
                    msgsend = "NAME_CHANGE";
                    setJoinName("player");
                }
                else
                {
                    if(name.equals("player"))
                    {
                        setJoinName(strConv);
                        msgsend = name;
                    }
                    msgtake = "Receiving: " + strConv;
                }
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
    public Boolean connected() {return connected;}
    public void setJoinName(String id) {this.name = id;}
    private void sendMessage(DataOutputStream dos, String msg)
    {
        try
        {
            /*if(msg.equals(msgtake))
                error = msgsend + " == " + msgtake;
            else
                error = msgsend + " != " + msgtake;*/
            String temp = msg + '/';
            dos.writeUTF(temp);
            dos.flush();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public void disconnect()
    {
        //Set connected state to false.
        connected = false;
    }

}
