package com.networksockettest.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Andreas on 2016-03-16.
 * Heartbeat funktionen existerar för att simulera när data skickas mellan enheterna i höga hastigheter.
 * Det kan också användas för att kontrollera om kopplingen fortfarande är aktiv, men i detta fall
 * så används read funktionen för detta.
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
                strConv = "";
                //Initial condition for setting player name.
                if(name == "player")
                    msgsend = name;
                //Send message statement.
                if(connected && !msgsend.equals(""))
                {
                    sendMessage(dataOutputStream, msgsend);
                    ++msgnr;
                    msgsend = "";
                }
                //Read the input stream for data, if the server closes, the stream returns -1.
                reads = dataInputStream.read(buffer, 0, SIZE);
                //Create temporary string containing the converted buffer data.
                String temp = new String(buffer).trim();
                //Copy characters until logical terminator is found.
                for(int idt = 0; idt < temp.length(); ++idt)
                {
                    if(temp.charAt(idt) == '/')
                        break;
                    else
                        strConv += temp.charAt(idt);
                }
                //Check if the server has closed.
                if(reads == -1)
                {
                    msgtake = "Server is offline.";
                    connected = false;
                    break;
                }
                //Check for heartbeat message, if so return it immediately.
                else if(strConv.equals("heartbeat"))
                {
                    msgsend = "heartbeat";
                    msgtake = "heartbeat received from server";
                }
                //Check for a name change request.
                else if(strConv.equals("NAME_CHANGE"))
                {
                    msgsend = "NAME_CHANGE";
                    setJoinName("player");
                }
                //Otherwise, handle message.
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
        //Send message to server.
        try
        {
            //Add logical terminator to end of string.
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
