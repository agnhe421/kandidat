package com.mygdx.game;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

//import org.jbox2d.common.Vec3;

/**
 * Created by Andreas on 2016-03-02.
 */
public class CreateServer extends Thread
{
    static final int SOCKETSERVERPORT = 8080;

    Vector<User> userList;

    String msg, error;

    ServerSocket serverSocket;

    public void run()
    {
        Socket socket = null;

        try
        {
            serverSocket = new ServerSocket(SOCKETSERVERPORT);

            while(true)
            {
                socket = serverSocket.accept();
                User user = new User();
                userList.add(user);
                ConnectThread connectThread = new ConnectThread(user, socket);
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

    private class ConnectThread extends Thread
    {
        Socket socket;
        User user;
        String msg;

        ConnectThread(User usr, Socket socket)
        {
            this.socket = socket;
            user = usr;
            user.socket = socket;
            user.conThread = this;
        }

        public void run()
        {
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try
            {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                //user.name = ;

                while(true)
                {
                /*if(dataInputStream.available() > 0)
                {

                }
                if()
                {

                }*/
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            } finally
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

        private void sendData() {}

    }

    public class User
    {
        public String name;
        public Socket socket;
        public ConnectThread conThread;

    }
}
