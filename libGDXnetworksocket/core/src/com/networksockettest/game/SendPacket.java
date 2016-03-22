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
 * This class checks all available network interfaces for servers, and will retrieve the server
 * address for connection purposes. If no server is found within 5 seconds, the thread will return
 * an error message, making sure that the connecting unit will not crash, or try to connect to a
 * non-existant address.
 */

public class SendPacket extends Thread
{
    DatagramSocket dSocket;
    private String msg = "msg", error = "No Error", serverIP = "";
    private Boolean failure;

    public SendPacket()
    {
        failure = false;
    }
    //The thread will only run once, compared to the server thread that will loop perpetually,
    //listening for incoming packets.
    @Override
    public void run()
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try
        {
            //Create new datagram socket.
            dSocket = new DatagramSocket();
            //Set broadcast state to true;
            dSocket.setBroadcast(true);
            //Create new datapacket to send, checking for servers.
            byte[] sendData = "SERVER_CONNECT_CHECK".getBytes();
            try
            {
                //Set default address to send to.
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8081);
                //Send packet to default destination.
                dSocket.send(sendPacket);
                msg = getClass().getName() + ">>>Request packet sent to: 255.255.255.255 (DEFAULT)\n";

            }catch(UnknownHostException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
                failure = true;
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
                failure = true;
            }
            //Create variable for all available network interfaces.
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                //loop through all available network interfaces.
                NetworkInterface networkInterface = interfaces.nextElement();
                if(networkInterface.isLoopback() || !networkInterface.isUp())
                    continue;
                //Check the current interface address.
                msg = "";
                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    //Get the curret address broadcast.
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if(broadcast == null)
                        continue;
                    try
                    {
                        //Send the request packet to the broadcast address.
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8081);
                        dSocket.send(sendPacket);
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                        failure = true;
                    }
                    msg += getClass().getName() + ">>>Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName() + "\n";
                }
            }
            //Create a buffer for received data, and wait for a response.
            msg += getClass().getName() + ">>>Done looping over all network interfaces. Now waiting for a reply!\n";
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            dSocket.setSoTimeout(5000);
            dSocket.receive(receivePacket);
            msg += getClass().getName() + ">>>Broadcast response from server: " + receivePacket.getAddress().getHostAddress();
            //Get response string data.
            String message = new String(receivePacket.getData()).trim();
            //Check data for validity.
            if(message.equals("SERVER_CONNECT_CONFIRMATION"))
            {
                //Get the servers IP-address.
                serverIP = receivePacket.getAddress().getHostAddress();
            }
            //If no server is found, set default failure message.
            else
                serverIP = "FAILED_CONNECTION";
            //Close the datagram socket.
        }catch(SocketException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
            failure = true;
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
            failure = true;
        }
        //Keep these two outside the try/catch statement to ensure that the socket closes, and that
        //the serverIP variable receives the correct error message.
        if(failure)
            serverIP = "FAILED_CONNECTION";
        dSocket.close();
    }

    public String getMsg() {return msg;}
    public String getError() {return error;}
    public String getIP() {return serverIP;}
    public Boolean getErrorState() {return failure;}
}
