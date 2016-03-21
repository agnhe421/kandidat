package com.mygdx.game;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Andreas on 2016-03-07.
 * This was an attempt to create an android specific code interface.
 * It may be necessary later if the current client/server code is
 * insufficient.
 */
public class ServerCodeAndroid implements ServerInterface
{
    final int SERVERPORT = 8080;
    int DISCOVERY_PORT;
    WifiManager wifi;
    DatagramSocket dSocket;
    DatagramPacket dPacket;
    String data, error = "";
    Context context;

    public ServerCodeAndroid(Context context)
    {
        this.context = context;
    }

    public void sendPacket()
    {
        try
        {
            dSocket = new DatagramSocket(SERVERPORT);
            dSocket.setBroadcast(true);
            dPacket = new DatagramPacket(data.getBytes(), data.length(), getBroadcastAddress(), DISCOVERY_PORT);
            dSocket.send(dPacket);
            byte[] buf = new byte[1024];
            dPacket = new DatagramPacket(buf, buf.length);
            dSocket.receive(dPacket);
        }catch(SocketException e)
        {
            e.printStackTrace();
            error += "Exception: " + e.toString() + "\n";
        }catch(IOException e)
        {
            e.printStackTrace();
            error += "Exception: " + e.toString() + "\n";
        }
    }

    public InetAddress getBroadcastAddress() throws IOException
    {
        wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for(int id = 0; id < 4; ++id)
            quads[id] = (byte)((broadcast >> id * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
