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
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.xml.crypto.Data;

//import org.jbox2d.common.Vec3;

/**
 * Created by Andreas on 2016-03-02.
 * As far as i can tell the server does not actually connect
 * with external units. What it does is wait for a packet of
 * information that can be sent by all units within the local
 * area network (Andreass phone in this case). When one is received
 * the server checks the packet for validity, and if correct, sends
 * a response packet to the corresponding unit by IP address.
 * The server loops in perpetuity, the join button only sends one
 * packet and then exits immediately. Question is, will this be viable
 * in the actual game later on?
 */
public class JoinServer extends Thread
{
    protected String dstAdress;
    protected int dstPort;
    protected String name;
    protected Socket socket;
    DatagramSocket c;
    //boolean connected = false;
    //Vec3 position;
    private String msg = "msg", error = "No error", serverIP = "";

    public JoinServer(String dstAdress, int dstPort, String name)
    {
        this.dstAdress = dstAdress;
        this.dstPort = dstPort;
        this.name = name;
    }

    @Override
    public void run()
    {
        try
        {
            //Create new socket handling incoming/outgoing packets.
            c = new DatagramSocket();
            //Activate broadcast.
            c.setBroadcast(true);
            //Create request packet.
            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();
            //Send packet to default IP.
            try
            {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8080);
                c.send(sendPacket);
                msg = getClass().getName() + ">>>Request packet sent to: 255.255.255.255 (DEFAULT)";
            }catch(UnknownHostException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            //Create variable containing all interfaces (IP adresses connected to the local network.
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            //Loop through all interfaces.
            while(interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                if(networkInterface.isLoopback() || !networkInterface.isUp())
                    continue;
                //Loop through all IP addresses.
                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if(broadcast == null)
                        continue;
                    //Send the packet to connected IPs.
                    try
                    {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8080);

                        c.send(sendPacket);
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                    //State which addresses the packet has been sent to.
                    msg = "";
                    msg += getClass().getName() + ">>>Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName() + "\n";
                }
            }

            msg += getClass().getName() + ">>>Done looping over all network interfaces. Now waiting for a reply!\n";
            //Create buffer for response packet.
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            //Wait for incoming packet.
            c.receive(receivePacket);
            msg += getClass().getName() + ">>>Broadcast response from server: " + receivePacket.getAddress().getHostAddress();
            //Get contained string data.
            String message = new String(receivePacket.getData()).trim();
            //Check if correct packet has been received.
            if(message.equals("DISCOVER_FUIFSERVER_RESPONSE"))
            {
                serverIP = receivePacket.getAddress().toString();
            }
            //Wait, so that the answer can be displayed. Otherwise it will exit quicker than one can read the result.
            try
            {
                this.sleep(1000);
            }catch(InterruptedException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            c.close();
        }catch(SocketException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }


    //Old network socket code, may come in handy.
    /*@Override
    public void run()
    {
        error = "Init. ";
        socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        ObjectOutputStream objOutputStream = null;
        ObjectInputStream objInputStream = null;
        error = "Init 2.";
        try
        {
            error = "Try1.";
            socket = new Socket(dstAdress, dstPort);
            error = "Try2.";
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            connected = true;

            while(connected)
            {
                if(dataInputStream.available() > 0)
                {

                }
                if()
                {

                }
                error = "Connected!";
            }
        }catch(UnknownHostException e)
        {
            e.printStackTrace();
            error += "Exception: " + e.toString() + "\n";
        }catch(IOException e)
        {
            e.printStackTrace();
            error += "Exception: " + e.toString() + "\n";
        } finally
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error += "Exception: " + e.toString() + "\n";
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
    }*/

    //private void sendData() {}

    public String getError() {return error;}

    public String getMsg() {return msg;}

    //public Boolean isConnected() {return connected;}

    public void disconnect()
    {
        //connected = false;
        c.close();
    }

}
