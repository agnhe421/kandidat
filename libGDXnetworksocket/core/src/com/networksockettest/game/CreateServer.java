package com.networksockettest.game;

import com.badlogic.gdx.Gdx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
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
    private int currentListSize;

    @Override
    public void run()
    {
        //Instantiate the broadcast receiver, the vector of connected users and the socket.
        receiver = new ReceivePacket();
        userList = new Vector<User>();
        threadRun = true;
        currentListSize = 0;
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
                    currentListSize = userList.size();
                    //Create and start a connection thread for this specific user.
                    ConnectThread connectThread = new ConnectThread(user, socket);
                    connectThread.start();
                    //Tell the receiver the connection has been made, so that it can look for new requests.
                    receiver.confirmConnection();
                }
                if(userList.size() != currentListSize)
                {
                    reassignNames();
                    currentListSize = userList.size();
                }
            }
            //Close the connection threads of all users. At the end of the connection thread, the user is removed.
            for(int idx = 0; idx < getConnections(); ++idx)
            {
                userList.get(idx).conThread.stopConThread();
                try
                {
                    userList.get(idx).conThread.join();
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

    private void reassignNames()
    {
        for(int idx = 0; idx < userList.size(); ++idx)
        {
            String newName = "Player " + (idx + 1);
            userList.get(idx).conThread.nameChange(newName);
        }
    }

    //The thread handling the actual connection part.
    private class ConnectThread extends Thread
    {
        public Socket socket;
        public User user;
        private String strConv = "";
        private Boolean runcon, changeName;
        private static final int SIZE = 1024;
        private byte[] buffer;
        private int reads;
        private Timer timer;
        private DataOutputStream dataOutputStream;
        private DataInputStream dataInputStream;

        ConnectThread(User usr, Socket socket)
        {
            //Connect user and socket to the passed input.
            this.socket = socket;
            user = usr;
            user.socket = socket;
            user.conThread = this;
        }

        private void sendMessage(String msg)
        {
            try
            {
                String temp = msg + '/';
                dataOutputStream.writeUTF(temp);
                dataOutputStream.flush();
                error = "Sending: " + temp;
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
        }

        public void nameChange(String newName)
        {
            user.setName(newName);
            changeName = true;
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
            timer = new Timer();
            changeName = false;
            dataInputStream = null;
            dataOutputStream = null;
            buffer = new byte[SIZE];

            this.user.setName("Player " + getConnections());
            try
            {
                //Connect the streams to the threads socket streams.
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                //socket.setSoTimeout(5000);

                while(runcon)
                {
                    strConv = "";
                    if(changeName)
                    {
                        sendMessage("NAME_CHANGE");
                        changeName = false;
                        timer.cancel();
                        timer = new Timer();
                    }
                    //If input data is available, accept the data and prepare a response.
                    /*if(dataInputStream.available() > 0)
                    {
                        incoming = dataInputStream.readUTF();
                        //if(incoming.equals("CONNECTION_SHUTDOWN"))
                        //    runcon = false;
                        if(incoming == "player")
                            msgtake = "Player" + getConnections() + " has connected!";
                        else
                            msgsend = this.user.id;
                        msgtake = incoming + "\n";
                        msgsend = "Player " + getConnections();
                    }*/

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
                        msgtake = user.id + "has disconnected!";
                        runcon = false;
                        break;
                    }
                    else if(strConv.equals("heartbeat"))
                    {
                        msgtake = "heartbeat received from: " + user.id;
                        timer.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                sendMessage("heartbeat");
                            }
                        }, 1);
                        strConv = "";
                    }
                    else if(strConv.equals("NAME_CHANGE"))
                    {
                        msgsend = user.id;
                    }
                    else
                    {
                        if(strConv.equals("player"))
                        {
                            user.setName("Player " + getConnections());
                            msgsend = "Player " + getConnections();
                        }
                        else if(strConv.equals(user.id))
                        {
                            msgtake = user.id + "has connected!";
                            msgsend = "heartbeat";
                        }
                        else
                        {
                            msgsend = this.user.id;
                        }
                        /*sendMessage(msgsend);
                        msgsend = "";*/
                    }
                    //If response is available, send it.
                    if(runcon && !msgsend.equals(""))
                    {
                        sendMessage(msgsend);
                        msgsend = "";
                    }

                }
                /*if(!incoming.equals("CONNECTION_SHUTDOWN"))
                {
                    dataOutputStream.writeUTF("SERVER_SHUTDOWN");
                    dataOutputStream.flush();
                }*/
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
        private String id;
        public Socket socket;
        public ConnectThread conThread;

        public void setName(String id) {this.id = id;}
    }

}
