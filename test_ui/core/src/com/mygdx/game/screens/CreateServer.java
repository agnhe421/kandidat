package com.mygdx.game.screens;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    ReceivePacket receiver;
    ServerSocket serverSocket;
    static final int SOCKETSERVERPORT = 8081;
    private String msgtake = "msgtake", msgsend = "msgsend", error = "No Error";
    private Boolean threadRun;
    private Vector<User> userList;

    @Override
    public void run()
    {
        //Instantiate the broadcast receiver, the vector of connected users and the socket.
        receiver = new ReceivePacket();
        userList = new Vector<User>();
        threadRun = true;
        Socket socket = null;
        try
        {
            //Bind the server to the static port.
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SOCKETSERVERPORT));
            //Activate the receiver.
            receiver.start();
            msgtake = "Waiting for connection...";
            while(threadRun)
            {
                //If the receiver has received a connection request, activate the socket, wait for the unit to connect.
                if(receiver.connectState())
                {
                    socket = serverSocket.accept();
                    //Add the user to the vector.
                    User user = new User();
                    userList.add(user);
                    //Create and start a connection thread for this specific user.
                    ConnectThread connectThread = new ConnectThread(user, socket);
                    connectThread.start();
                    //Tell the receiver the connection has been made, so that it can look for new requests.
                    receiver.confirmConnection();
                }
            }
            //Close the connection threads of all users. At the end of the connection thread, the user is removed.
            for(User it : userList)
            {
                it.conThread.stopConThread();
                try
                {
                    it.conThread.join();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
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
        //Stop the main server thread, deactivate the broadcast and close the server socket.
        threadRun = false;
        receiver.stopCatch();
        try
        {
            receiver.join();
            serverSocket.close();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public String getIpAddress()
    {
        //Get the host IP-address.
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
    //The thread handling the actual connection part.
    private class ConnectThread extends Thread
    {
        public Socket socket;
        public User user;
        private String incoming;
        private Boolean runcon;

        ConnectThread(User usr, Socket socket)
        {
            //Connect user and socket to the passed input.
            this.socket = socket;
            user = usr;
            user.socket = socket;
            user.conThread = this;
        }

        public void stopConThread()
        {
            //Stop the connection thread. The socket will not freeze due to not using any functions
            //that blocks its progress.
            runcon = false;
        }

        @Override
        public void run()
        {
            //Instantiate data input/output streams, and set thread state to running.
            runcon = true;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            this.user.setName("Player " + getConnections());
            try
            {
                //Connect the streams to the threads socket streams.
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while(runcon)
                {
                    //If input data is available, accept the data and prepare a response.
                    if(dataInputStream.available() > 0)
                    {
                        incoming = dataInputStream.readUTF();
                        if(incoming.equals("CONNECTION_SHUTDOWN"))
                            runcon = false;
                        msgtake = incoming + "\n";
                        msgsend = "Player " + getConnections();
                    }
                    //If response is available, send it.
                    if(runcon && !msgsend.equals(""))
                    {
                        dataOutputStream.writeUTF(msgsend);
                        dataOutputStream.flush();
                        msgsend = "";
                    }
                }
                if(!incoming.equals("CONNECTION_SHUTDOWN"))
                {
                    dataOutputStream.writeUTF("SERVER_SHUTDOWN");
                    dataOutputStream.flush();
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }finally
            {
                //If streams and the socket are open, close them.
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
                //Remove the user from the list.
                userList.remove(user);
            }
        }
    }

    public String getMsg() {return msgtake;}
    public String getError() {return error;}
    public int getConnections() {return userList.size();}
    public Boolean checkIfVectorNull() {return userList == null;}
    public String getUserId(int idx) {return userList.get(idx).id;}

    public class User
    {
        //The ID will be used later to identify which unit will receive data.
        //The ID will be specified before connecting by that player. Such as Manly Banger, the Rock God!
        public String id;
        public Socket socket;
        public ConnectThread conThread;

        public void setName(String id) {this.id = id;}
    }

}
