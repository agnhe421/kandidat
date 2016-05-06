package com.qualcomm.vuforia.samples.Network;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Vector;

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
    private Vector<String> takenIPs;
    private Boolean failure;

    //Constructor
    public SendPacket()
    {
        failure = false;
    }

    //The thread will only run once, compared to the server thread that will loop perpetually,
    //listening for incoming packets.
    @Override
    public void run()
    {
        takenIPs = new Vector<String>();
        System.setProperty("java.net.preferIPv4Stack", "true");
        try
        {
            //Create new datagram socket.
            dSocket = new DatagramSocket();
            //Set broadcast state to true;
            dSocket.setBroadcast(true);
            //Create new datapacket to send, checking for servers.
            byte[] sendData = "SERVER_CONNECT_CHECK".getBytes();
            //Create variable for all available network interfaces.
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                //loop through all available network interfaces.
                NetworkInterface networkInterface = interfaces.nextElement();
                if(networkInterface.isLoopback() || !networkInterface.isUp())
                    continue;
                //Check the current interface address.
                msg = "Checking network interfaces...\n";
                //Den kommer bara att hitta en server för att alla paket går genom 172.20.10.15.
                //Du får de andra servrarnas IP adresser genom deras paket. Kom på nåt sett att se
                //hur många servrar som egentligen finns.
                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    //Get the current broadcast address.
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
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            Boolean looking = true;
            dSocket.setSoTimeout(1500);
            while(looking)
            {
                try
                {
                    dSocket.receive(receivePacket);
                    String message = new String(receivePacket.getData()).trim();
                    if(message.equals("SERVER_CONNECT_CONFIRMATION"))
                    {
                        serverIP = receivePacket.getAddress().getHostAddress();
                        takenIPs.add(serverIP);
                        msg += getClass().getName() + ">>>Broadcast response from server: " + receivePacket.getAddress().getHostAddress() + "\n";
                    }
                }catch(SocketTimeoutException e)
                {
                    looking = false;
                }
            }
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
    public Vector<String> getIPs() {return takenIPs;}
    public Boolean getErrorState() {return failure;}
}
