package com.andreas.servertest1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;


/**
 * Created by Andreas on 2016-02-22.
 */
public class Server
{
    MainActivity activity;
    ServerSocket serverSocket;
    Vector<ServerThread> socketList = new Vector<ServerThread>();
    static final int socketServerPORT = 8080;

    public Server(MainActivity activity)
    {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort()
    {
        return socketServerPORT;
    }

    public void onDestroy()
    {
        if(serverSocket != null)
        {
            try
            {
                serverSocket.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread
    {
        int socketId = 0;
        @Override
        public void run()
        {
            try
            {
                serverSocket = new ServerSocket(socketServerPORT);
                while(true)
                {
                    if(socketList.isEmpty())
                    {
                        activity.runOnUiThread(new Runnable(){
                            @Override
                            public void run()
                            {
                                activity.disMsg.setText("No Connections.");
                            }
                        });
                    }
                    else
                    {
                        activity.runOnUiThread(new Runnable(){
                            @Override
                            public void run()
                            {
                                activity.disMsg.setText("Connections.");
                            }
                        });
                    }

                    ServerThread newThread = new ServerThread(serverSocket.accept(), socketId);
                    socketList.add(newThread);
                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run()
                        {
                            while(socketList.get(socketId).retrieveMessage() != "")
                            {
                                activity.msg.setText(socketList.get(socketId).retrieveMessage());
                            }
                        }
                    });
                    for(int idx = 0; idx < socketList.size(); ++idx)
                    {
                        if(socketList.get(idx).retrieveOpenClosed())
                        {
                            socketList.remove(idx);
                        }
                    }
                }
            }catch(IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public String getIpAdress()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> enumNetworkInterface = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterface.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterface.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if(inetAddress.isSiteLocalAddress())
                    {
                        ip += "Server running at : " + inetAddress.getHostAddress();
                    }
                }
            }
        }catch(SocketException e)
        {
            e.printStackTrace();
            ip += "Something wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
