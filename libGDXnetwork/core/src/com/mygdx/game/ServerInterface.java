package com.mygdx.game;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Andreas on 2016-03-07.
 * This was an attempt to create an android specific code interface.
 * It may be necessary later if the current client/server code is
 * insufficient.
 */
public interface ServerInterface {
    public void sendPacket();
    public InetAddress getBroadcastAddress() throws IOException;


}
