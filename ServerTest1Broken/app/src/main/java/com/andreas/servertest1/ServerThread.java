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
import java.util.Vector;

/**
 * Created by Andreas on 2016-02-26.
 */
public class ServerThread extends Thread
{
    private Socket socket;
    private String message = "";
    int id;

    public ServerThread(Socket s, int c)
    {
        this.socket = s;
        id = c;
    }

    @Override
    public void run()
    {
        this.message += "#" + (id + 1) + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, id);
        socketServerReplyThread.run();
                    /*count = socketList.size();
                    message += "#" + count + " from " + socketList.get(socketId).getInetAddress() + ":" + socketList.get(socketId).getPort() + "\n";
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            activity.msg.setText(message);
                        }
                    });
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socketList.get(socketId), count);
                    socketServerReplyThread.run();*/
    }

    public String retrieveMessage()
    {
        return this.message;
    }

    public Boolean retrieveOpenClosed()
    {
        return socket.isClosed();
    }

    private class SocketServerReplyThread extends Thread
    {
        private Socket hostThreadSocket;
        int cnt;
        SocketServerReplyThread(Socket socket, int c)
        {
            hostThreadSocket = socket;
            cnt = c + 1;
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

            }catch(IOException e)
            {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

        }
    }

    public void command()
    {

    }

}
