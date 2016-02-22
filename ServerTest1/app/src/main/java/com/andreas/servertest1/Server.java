package com.andreas.servertest1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Andreas on 2016-02-22.
 */
public class Server
{
    MainActivity activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8080;

    public Server(MainActivity activity)
    {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort()
    {
        return socketServerPORT;
    }

    public void onDestroy()
    {
        if(serverSocket != null)
        {
            try
            {
                serverSocket.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread
    {
        int count = 0;

        @Override
        public void run()
        {
            try
            {
                serverSocket = new ServerSocket(socketServerPORT);
                while(true)
                {
                    Socket socket = serverSocket.accept();
                    ++count;
                    message += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            activity.msg.setText(message);
                        }
                    });
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
                }
            }catch(IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    private class SocketServerReplyThread extends Thread
    {
        private Socket hostThreadSocket;
        int cnt;
        SocketServerReplyThread(Socket socket, int c)
        {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run()
        {
            OutputStream outputStream;
            String msgReply = "Hello from server, you are #" + cnt;

            try
            {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        activity.msg.setText(message);
                    }
                });
            }catch(IOException e)
            {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    activity.msg.setText(message);
                }
            });
        }
    }

    public String getIpAdress()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> enumNetworkInterface = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterface.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterface.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if(inetAddress.isSiteLocalAddress())
                    {
                        ip += "Server running at : " + inetAddress.getHostAddress();
                    }
                }
            }
        }catch(SocketException e)
        {
            e.printStackTrace();
            ip += "Something wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
