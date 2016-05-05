package com.mygdx.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

public class ReceivePacket extends Thread
{
    DatagramSocket dSocket;
    public static final int SOCKETSERVERPORT = 8081;
    private String msg = "msg", error = "No error", serverID = "";
    private Boolean threadRun, activeRequests;
    private Boolean serverAccepting;
    private Vector<Thread> responseVector;
    private Cleanup clean;

    //Constructor
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
            //Start the cleanup thread handling the response vector.
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
                    //Create a new response thread and add it to the response vector.
                    responseVector.add(0, new Thread(new responseThread(dSocket, sendPacket)));
                    //Start the new thread.
                    responseVector.get(0).start();
                    //Send response packet.
                    //dSocket.send(sendPacket);
                    msg += getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress() + "\n";
                }
            }
            //Stop cleanup and make sure that the response vector is empty.
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
    //Response thread, sends a UDP packet as response for an asking client.
    public class responseThread implements Runnable
    {
        private DatagramSocket dSocket;
        private DatagramPacket response;
        responseThread(DatagramSocket sock, DatagramPacket pack)
        {
            //Use the existing datagram socket and packet.
            dSocket = sock;
            response = pack;
        }
        @Override
        public void run()
        {
            //Set active requests to true and send the response.
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
    //Cleanup thread for the response vector.
    public class Cleanup extends Thread
    {
        Boolean running;
        @Override
        public void run()
        {
            running = true;
            while(running)
            {
                //If any there are active requests and the vector isn't empty, check for dead threads.
                if(activeRequests && !responseVector.isEmpty())
                {
                    for(Thread t: responseVector)
                    {
                        //If the thread is dead, remove it from the vector.
                        if(!t.isAlive())
                            responseVector.remove(t);
                    }
                    if(responseVector.isEmpty() && !serverAccepting)
                    {
                        //If there are no active response threads, and the server is not waiting for
                        //incoming connection requests, set active requests to false.
                        activeRequests = false;
                    }
                }
            }
        }
        //Stop the cleanup process.
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