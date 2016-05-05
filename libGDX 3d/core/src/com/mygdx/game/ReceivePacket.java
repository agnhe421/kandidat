package com.mygdx.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

import javax.xml.crypto.Data;

/**
 * Created by Andreas on 2016-03-18.
 * The listener thread, checking for incoming packets containing connection requests. This is controlled
 * from the CreateServer thread, and terminated there as well.
 */
public class ReceivePacket extends Thread
{
    DatagramSocket dSocket;
    public static final int SOCKETSERVERPORT = 8081;
    private String msg = "msg", error = "No error", serverID = "";
    private Boolean threadRun, activeRequests;
    private Boolean serverAccepting;
    private Vector<Thread> responseVector;
    private Cleanup clean;

    public ReceivePacket(String id)
    {
        serverAccepting = false;
        activeRequests = false;
        serverID = id;
        responseVector = new Vector<Thread>();
    }

    @Override
    public void run()
    {
        threadRun = true;
        try
        {
            //Create a new DatagramSocket.
            dSocket = new DatagramSocket(SOCKETSERVERPORT, InetAddress.getByName("0.0.0.0"));
            //Activate socket broadcast.
            dSocket.setBroadcast(true);
            //While the server is active, keep looking for connect request packets.
            msg = getClass().getName() + ">>>Ready to receive broadcast packets!";
            clean = new Cleanup();
            clean.start();
            while(threadRun)
            {
                msg = getClass().getName() + ">>>Ready to receive broadcast packets!";
                //Create a packet buffer for incoming packets.
                byte[] recvBuf = new byte[15000];
                DatagramPacket dPacket = new DatagramPacket(recvBuf, recvBuf.length);
                //Look for incoming packets.
                dSocket.receive(dPacket);
                msg = getClass().getName() + ">>>Discovery packet received from: " + dPacket.getAddress().getHostAddress() + "\n";
                msg += getClass().getName() + ">>>Packet received; data: " + new String(dPacket.getData()).trim() + "\n";
                //Get packet data.
                String message = new String(dPacket.getData()).trim();
                //Check packet data for validity.
                if(message.equals("SERVER_CONNECT_CHECK"))
                {
                    //Packet is valid, set connection status to true.
                    //Create data packet to send to requesting unit.
                    //String fullmsg = "SERVER_CONNECT_CONFIRMATION|" + serverID;
                    //byte[] sendData = fullmsg.getBytes();
                    byte[] sendData = "SERVER_CONNECT_CONFIRMATION".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dPacket.getAddress(), dPacket.getPort());
                    responseVector.add(0, new Thread(new responseThread(dSocket, sendPacket)));
                    responseVector.get(0).start();
                    //Send response packet.
                    //dSocket.send(sendPacket);
                    msg += getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress() + "\n";
                }
            }
            clean.stopClean();
            for(Thread t: responseVector)
            {
                try
                {
                    t.join();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
                responseVector.remove(t);
            }
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public class responseThread implements Runnable
    {

        private DatagramSocket dSocket;
        private DatagramPacket response;

        responseThread(DatagramSocket sock, DatagramPacket pack)
        {
            dSocket = sock;
            response = pack;
        }

        @Override
        public void run()
        {
            activeRequests = true;
            try
            {
                dSocket.send(response);
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }

        }
    }

    public class Cleanup extends Thread
    {
        Boolean running;
        @Override
        public void run()
        {
            running = true;
            while(running)
            {
                if(activeRequests && !responseVector.isEmpty())
                {
                    for(Thread t: responseVector)
                    {
                        if(!t.isAlive())
                            responseVector.remove(t);
                    }
                    if(responseVector.isEmpty() && !serverAccepting)
                    {
                        activeRequests = false;
                    }
                }
            }
        }
        public void stopClean()
        {
            running = false;
        }
    }
    //Check connection state.
    public Boolean connectState() {return activeRequests;}
    public void setServerAccepting(Boolean state) {serverAccepting = state;}
    //Stop the broadcast.
    public void stopCatch()
    {
        threadRun = false;
        dSocket.close();
    }
}