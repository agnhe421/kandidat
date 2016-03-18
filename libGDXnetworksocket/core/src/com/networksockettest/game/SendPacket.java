package com.networksockettest.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Andreas on 2016-03-18.
 */
public class SendPacket extends Thread
{
    DatagramSocket dSocket;
    private String msg = "msg", error = "No Error", serverIP = "";

    @Override
    public void run()
    {
        try
        {
            dSocket = new DatagramSocket();
            dSocket.setBroadcast(true);
            byte[] sendData = "SERVER_CONNECT_CHECK".getBytes();
            try
            {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8081);
                dSocket.send(sendPacket);
                msg = getClass().getName() + ">>>Request packet sent to: 255.255.255.255 (DEFAULT)\n";

            }catch(UnknownHostException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                if(networkInterface.isLoopback() || !networkInterface.isUp())
                    continue;
                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if(broadcast == null)
                        continue;
                    try
                    {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8081);
                        dSocket.send(sendPacket);
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                    msg = "";
                    msg += getClass().getName() + ">>>Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName() + "\n";
                }
            }
            msg += getClass().getName() + ">>>Done looping over all network interfaces. Now waiting for a reply!\n";
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            dSocket.receive(receivePacket);
            msg += getClass().getName() + ">>>Broadcast response from server: " + receivePacket.getAddress().getHostAddress();
            String message = new String(receivePacket.getData()).trim();
            if(message.equals("SERVER_CONNECT_CONFIRMATION"))
            {
                serverIP = receivePacket.getAddress().getHostAddress();
                /*byte[] connectMessage = "SERVER_CONNECT_REQUEST".getBytes();
                DatagramPacket connectRequest = new DatagramPacket(connectMessage, connectMessage.length, InetAddress.getByName(serverIP), 8081);
                dSocket.send(connectRequest)*/
            }
            dSocket.close();
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

    public String getMsg() {return msg;}
    public String getError() {return error;}
    public String getIP() {return serverIP;}
    public void stopSend()
    {
        dSocket.close();
    }
}
