package com.networksockettest.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Andreas on 2016-03-18.
 */
public class ReceivePacket extends Thread
{
    DatagramSocket dSocket;
    public static final int SOCKETSERVERPORT = 8081;
    private String msg = "msg", error = "No error";
    private Boolean threadRun, connectTrue;

    public ReceivePacket()
    {
        connectTrue = false;
    }

    @Override
    public void run()
    {
        threadRun = true;
        connectTrue = false;
        try
        {
            dSocket = new DatagramSocket(SOCKETSERVERPORT, InetAddress.getByName("0.0.0.0"));
            dSocket.setBroadcast(true);
            while(threadRun)
            {
                msg = getClass().getName() + ">>>Ready to receive broadcast packets!";
                byte[] recvBuf = new byte[15000];
                DatagramPacket dPacket = new DatagramPacket(recvBuf, recvBuf.length);
                dSocket.receive(dPacket);
                msg = getClass().getName() + ">>>Discovery packet received from: " + dPacket.getAddress().getHostAddress() + "\n";
                msg += getClass().getName() + ">>>Packet received; data: " + new String(dPacket.getData()).trim() + "\n";
                String message = new String(dPacket.getData()).trim();
                if(message.equals("SERVER_CONNECT_CHECK"))
                {
                    connectTrue = true;
                    byte[] sendData = "SERVER_CONNECT_CONFIRMATION".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dPacket.getAddress(), dPacket.getPort());
                    dSocket.send(sendPacket);
                    msg += getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress();
                }
                /*if(message.equals("SERVER_CONNECT_REQUEST"))
                {

                }*/
            }
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public String getMsg() {return msg;}
    public String getError() {return error;}
    public Boolean connectState() {return connectTrue;}
    public void confirmConnection() {connectTrue = false;}
    public void stopCatch()
    {
        threadRun = false;
        dSocket.close();
    }
}
