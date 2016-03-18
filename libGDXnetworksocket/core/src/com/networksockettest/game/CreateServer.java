package com.networksockettest.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by Andreas on 2016-03-16.
 */
public class CreateServer extends Thread
{

    ServerSocket serverSocket;
    static final int SOCKETSERVERPORT = 8080;
    private String msgtake = "msgtake", msgsend = "msgsend", error = "No Error";
    private Boolean threadRun;
    Vector<User> userList;

    @Override
    public void run()
    {
        userList = new Vector<User>();
        threadRun = true;
        Socket socket = null;
        try
        {
            serverSocket = new ServerSocket(SOCKETSERVERPORT);
            msgtake = "Waiting for connection...";
            while(threadRun)
            {
                //msgtake = "Waiting for connection...";
                socket = serverSocket.accept();
                User user = new User();
                userList.add(user);
                ConnectThread connectThread = new ConnectThread(user, socket);
                connectThread.start();
                if(!threadRun)
                {
                    connectThread.stopConThread();
                    try
                    {
                        connectThread.join();
                    }catch(InterruptedException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                }

            }
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
        }
    }

    public void stopServer()
    {
        threadRun = false;
        try
        {
            serverSocket.close();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public String getIpAddress()
    {
        String ip = "";
        Enumeration<NetworkInterface> enumNetworkInterfaces;
        try {

            enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {

                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    private class ConnectThread extends Thread
    {
        Socket socket;
        User user;
        String msg;
        Boolean runcon;

        ConnectThread(User usr, Socket socket)
        {
            this.socket = socket;
            user = usr;
            user.socket = socket;
            user.conThread = this;
        }

        public void stopConThread()
        {
            runcon = false;
            try
            {
                socket.close();
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
        }

        @Override
        public void run()
        {
            runcon = true;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try
            {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while(runcon)
                {
                    if(dataInputStream.available() > 0)
                    {
                        String incoming = dataInputStream.readUTF();
                        msgtake = incoming;
                        msgsend = "Message Received!";
                    }
                    if(runcon && !msgsend.equals(""))
                    {
                        dataOutputStream.writeUTF(msgsend);
                        dataOutputStream.flush();
                        msgsend = "";
                    }
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }finally
            {
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

                userList.remove(user);
            }
        }

    }

    public String getMsg() {return msgtake;}
    public String getError() {return error;}

    public class User
    {
        public String id;
        public Socket socket;
        public ConnectThread conThread;
    }

}
