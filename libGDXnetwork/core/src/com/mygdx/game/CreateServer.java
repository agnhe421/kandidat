package com.mygdx.game;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.jbox2d.common.Vec3;

/**
 * Created by Andreas on 2016-03-02.
 */
public class CreateServer extends Thread
{
    static final int SOCKETSERVERPORT = 8080;

    DatagramSocket dSocket;

    //Vector<User> userList;

    private String msg = "msg", error = "No Error", data;

    ServerSocket serverSocket;

    ServerInterface sInterface;

    private Boolean threadRun;

    @Override
    public void run()
    {
        threadRun = true;
        try
        {
            dSocket = new DatagramSocket(SOCKETSERVERPORT, InetAddress.getByName("0.0.0.0"));
            dSocket.setBroadcast(true);

            while(threadRun)
            {
                try
                {
                    this.sleep(1000);
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
                msg = getClass().getName() + ">>>Ready to receive broadcast packets!";
                byte[] recvbuf = new byte[15000];
                DatagramPacket dPacket = new DatagramPacket(recvbuf, recvbuf.length);
                dSocket.receive(dPacket);

                msg = getClass().getName() + ">>>Discovery packet recieved from: " + dPacket.getAddress().getHostAddress() + "\n";
                msg += getClass().getName() + ">>>Packet received; data: " + new String(dPacket.getData()) + "\n";

                String message = new String(dPacket.getData()).trim();
                if(message.equals("DISCOVER_FUIFSERVER_REQUEST"))
                {
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dPacket.getAddress(), dPacket.getPort());
                    dSocket.send(sendPacket);
                    msg += getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress();
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }

    }

    public String getMsg(){ return msg; }

    public String getError() { return error; }

    public void stopServer()
    {
        threadRun = false;
        dSocket.close();
    }

    /*public void run()
    {
        Socket socket = null;
        threadRun = true;
        try
        {
            serverSocket = new ServerSocket(SOCKETSERVERPORT);

            while(threadRun)
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
           // if(!userList.isEmpty())
           //     userList.clear();
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
                if(dataInputStream.available() > 0)
                {

                }
                if()
                {

                }
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

    }*/

    public String getIpAddress()
    {
        String ip = "";
        Enumeration<NetworkInterface>enumNetworkInterfaces;
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    /*public class User
    {
        public String name;
        public Socket socket;
        public ConnectThread conThread;

    }*/
}
