package com.mygdx.game;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Andreas on 2016-03-07.
 */
public interface ServerInterface {
    public void sendPacket();
    public InetAddress getBroadcastAddress() throws IOException;


}
