package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by Andreas on 2016-03-16.
 */
public class CreateServer extends Thread
{
    ReceivePacket receiver;
    ServerSocket serverSocket;
    static final int SOCKETSERVERPORT = 8081;
    private String msgtake = "msgtake", msgsend = "msgsend", error = "No Error", msglog = "";
    private String srvrName = "";
    private Boolean threadRun, nameAssignmentDone, allReady;
    private Vector<User> userList;
    private int currentListSize;
    private User srvrUser;
    DataHandler handler;

    //public CreateServer(String name) {setServerName(name);}
    public CreateServer()
    {
        srvrUser = new User();
        srvrUser.setName("Player 1");
        srvrUser.setPosition(new Vector3(0.0f, 0.0f, 0.0f));
    }

    @Override
    public void run()
    {
        //Instantiate the broadcast receiver, the vector of connected users and the socket.
        receiver = new ReceivePacket(srvrName);
        userList = new Vector<User>();
        threadRun = true;
        nameAssignmentDone = false;
        currentListSize = 0;
        Socket socket = null;
        //Create and start the datahandler, when activated it will request positional data from all
        //connected users, and perform collision calculations.
        handler = new DataHandler();
        //The handler will be activated later, when the game is integrated.
        //Preferably after the countdown.
        //activatePosTransfer();
        try
        {
            //Bind the server to the static port.
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SOCKETSERVERPORT));
            //Activate the receiver.
            receiver.start();
            msgtake = "Waiting for connection...";
            while(threadRun)
            {
                if(!nameAssignmentDone)
                {

                    Boolean tempCheck = false;
                    for(int idu = 0; idu < userList.size(); ++idu)
                    {
                        if(userList.get(idu).conThread.getNameStatus())
                            tempCheck = true;
                        else
                        {
                            //Gdx.app.log("Naming", "Player " + idu + " name not assigned.");
                            tempCheck = false;
                            break;
                        }
                    }
                    if(tempCheck)
                        nameAssignmentDone = true;
                }
                //If the receiver has received a connection request, activate the socket, wait for the unit to connect.
                if(receiver.connectState())
                {
                    //TODO Den nuvarande refresh listan gör så att man får refresha en i taget. Annars
                    //TODO connectar inte användaren ordentligt. Temporär fix: receiver lyssnar bara om
                    //TODO huvudtråden säger åt den att lyssna via confirmConnection().
                    //TODO Nackdel: om svaret från servern försvinner så kan den inte ta emot nåt nytt.
                    //This loop prevents the main thread from accepting new users until the
                    //handler has finished sending data.
                    while(handler.getSendState() && userList.size() != 0)
                    {

                    }
                    nameAssignmentDone = false;
                    socket = serverSocket.accept();
                    //Add the user to the vector.
                    User user = new User();
                    userList.add(user);
                    if(!handler.getSendState())
                        handler.changeState();
                    currentListSize = userList.size();
                    //Create and start a connection thread for this specific user.
                    ConnectThread connectThread = new ConnectThread(user, socket);
                    connectThread.start();
                    //Tell the receiver the connection has been made, so that it can look for new requests.
                    receiver.confirmConnection();
                }
                //Check for disconnections. The only reason currentListSize does not equal to
                //userList.size() is if someone disconnects as connections are handled in the if
                //statement above.
                if(userList.size() != currentListSize)
                {
                    reassignNames();
                    currentListSize = userList.size();
                }
            }
            handler.shutdown();
            //Close the connection threads of all users. At the end of the connection thread, the user is removed.
            for(int idx = 0; idx < getConnections(); ++idx)
            {
                userList.get(idx).conThread.stopConThread();
                try
                {
                    userList.get(idx).conThread.join();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }finally
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
            }
        }
    }
    public void stopServer()
    {
        //Stop the main server thread, deactivate the broadcast and close the server socket.
        threadRun = false;
        receiver.stopCatch();
        if(handler.getSendState())
            handler.changeState();
        try
        {
            receiver.join();
            serverSocket.close();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public String getIpAddress()
    {
        //Get the host IP-address.
        String ip = "";
        Enumeration<NetworkInterface> enumNetworkInterfaces;
        try {

            enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {

                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
    //Reassign all usernames whenever someone disconnects.
    private void reassignNames()
    {
        nameAssignmentDone = false;
        for(int idx = 0; idx < userList.size(); ++idx)
        {
            String newName = "Player " + (idx + 1);
            userList.get(idx).conThread.nameChange(newName);
        }
    }

    private void sendDataFromClient(String data, int clientID)
    {
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            if(idu == clientID)
                ++idu;
            if(idu == userList.size())
                break;
            userList.get(idu).conThread.sendMessage(data);
        }
    }

    //Gather up all positional data, turn it into a string and ship it to all users.
    private void gatherData()
    {
        //When multiple users connect, this stops sending data across. It works when someone disconnects again.
        int vecSize = userList.size();
        Vector<Vector3> tempData = new Vector<Vector3>();
        for(int idu = 0; idu < vecSize; ++idu)
        {
            tempData.add(userList.get(idu).conThread.user.getPosition());
        }
        String dataString = "";
        for(int ids = 0; ids < tempData.size(); ++ids)
        {
            String temp = tempData.get(ids).toString();
            if(ids != tempData.size() - 1)
                dataString += temp + '|';
            else
                dataString += temp;
        }
        for(int idu = 0; idu < vecSize; ++idu)
        {
            Gdx.app.log("Sending Data: ", dataString);
            Gdx.app.log("Sending to: ", userList.get(idu).id + ".");
            userList.get(idu).conThread.sendData(dataString);
        }
        //TODO Perform calculations for collisions.
    }

    /**
     * Datahandler thread, responsible for sending all positional data to every user.
     */
    private class DataHandler extends Thread
    {
        private Boolean sendState, close;

        public DataHandler()
        {
            sendState = false;
            close = false;
        }

        @Override
        public void run()
        {
            while(!close)
            {
                //Check if all users have their names assigned properly.
                if(!nameAssignmentDone)
                {
                    //Gdx.app.log("Processlog", "Names not assigned.");
                    continue;
                }


                //Check if all users have processed their data.
                Boolean allclear = true;
                for(int idu = 0; idu < userList.size(); ++idu)
                {
                    if(!userList.get(idu).conThread.getDataProcessed())
                    {
                        //Gdx.app.log("Process2", userList.get(idu).id + " is not ready.");
                        String e = userList.get(idu).id + " is not ready.";
                        allclear = false;
                        break;
                    }
                }
                if(allclear)
                    changeState();
                //If the send state is on, gather up and send data to all users.
                while(sendState)
                {
                    Gdx.app.log("Notice: ", "All clear.");
                    //Gdx.app.log("Process", "Set to false.");
                    //Set dataProcess to false, to ensure that all users must finish processing.
                    for(int idu = 0; idu < userList.size(); ++idu)
                    {
                        Gdx.app.log("Processlog", userList.get(idu).id + " Set to false");
                        userList.get(idu).conThread.setDataProcess();
                    }
                    gatherData();
                    srvrUser.setPosition(srvrUser.getPosition().add(1.0f, 0.0f, 0.0f));
                    //Switch sendstate to off.
                    changeState();
                }
            }
        }
        //Close handler by setting close state, terminating the loop.
        public void shutdown() {close = true;}
        //Change state to on/off.
        public void changeState()
        {
            if(sendState)
                sendState = false;
            else
                sendState = true;
        }
        public Boolean getSendState() {return sendState;}

    }

    //The thread handling the actual connection part.
    private class ConnectThread extends Thread
    {
        public Socket socket;
        public User user;
        private Vector<String> strConv;
        private Boolean runCon, changeName, nameGet, dosIsClosed, disIsClosed, dataProcessed;
        private static final int SIZE = 1024;
        private byte[] buffer;
        private int reads;
        private BufferedOutputStream bufferedOutputStream;
        private BufferedInputStream bufferedInputStream;

        ConnectThread(User usr, Socket socket)
        {
            //Connect user and socket to the passed input.
            strConv = new Vector<String>();
            this.socket = socket;
            try
            {
                this.socket.setTcpNoDelay(true);
            }catch(SocketException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            user = usr;
            user.socket = socket;
            try
            {
                user.socket.setTcpNoDelay(true);
            }catch(SocketException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            user.conThread = this;
            user.setPosition(new Vector3());
            nameGet = false;
            dosIsClosed = true;
            disIsClosed = true;
            dataProcessed = true;
        }

        public Boolean getNameStatus() {return nameGet;}
        public void setDataProcess() {dataProcessed = false;}
        public Boolean getDataProcessed() {return dataProcessed;}

        //Send positional data.
        private void sendData(String posData)
        {
            sendMessage("POS_DATA_INCOMING|" + posData);
            msglog = "Sending message: \n" + posData;
        }

        private void sendMessage(String msg)
        {
            if(!dosIsClosed)
            {
                //Send a message to the unit.
                try
                {
                    //Add logical terminator.
                    String temp = msg + '/';
                    byte[] tempbuf;
                    tempbuf = temp.getBytes();
                    //Send message.
                    bufferedOutputStream.write(tempbuf, 0, tempbuf.length);
                    bufferedOutputStream.flush();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
            }
        }
        public void nameChange(String newName)
        {
            //Change the id, and set change name state to true.
            user.setName(newName);
            changeName = true;
            nameGet = false;
        }

        public void stopConThread()
        {
            //Stop the connection thread.
            runCon = false;
            try
            {
                Gdx.app.log("Errorlog", "Closing socket for " + user.id);
                socket.close();
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }

        }

        private Vector<String> readData(int readStatus, byte[] buff)
        {
            if(!disIsClosed)
            {
                Vector<String> msg = new Vector<String>();
                Boolean allRead = false;
                String element = "";
                while(!allRead)
                {
                    try
                    {
                        Gdx.app.log("Errorlog", "Reading data");
                        readStatus = bufferedInputStream.read(buff, 0, SIZE);
                        Gdx.app.log("Errorlog", "Readstatus is: " + readStatus);
                        Gdx.app.log("Errorlog", "runCon is: " + runCon.toString());
                        if(readStatus == -1 || !runCon)
                        {
                            Gdx.app.log("Errorlog", "Cancelling read.");
                            msgtake = user.id + " has disconnected";
                            runCon = false;
                            allRead = true;
                            break;
                        }
                        else
                        {
                            String temp = new String(buff).trim();
                            for(int idt = 0; idt < readStatus; ++idt)
                            {
                                if(temp.charAt(idt) == '/')
                                {
                                    msg.add(element);
                                    allRead = true;
                                    break;
                                }
                                else if(temp.charAt(idt) == '|')
                                {
                                    msg.add(element);
                                    element = "";
                                }
                                else
                                    element += temp.charAt(idt);
                            }
                        }
                    }catch(IOException e)
                    {
                        allRead = true;
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                        Gdx.app.log("Errorlog", "Exception! Error:" + error);
                    }
                }
                return msg;
            }
            threadRun = false;
            return new Vector<String>();
        }

        @Override
        public void run()
        {
            //Instantiate data input/output streams, set thread state to running, initialize buffer.
            runCon = true;
            changeName = false;
            bufferedInputStream = null;
            bufferedOutputStream = null;
            buffer = new byte[SIZE];

            this.user.setName("Player " + (1 + getConnections()));
            try
            {
                //Connect the streams to the threads socket streams.
                bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                disIsClosed = false;
                bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                dosIsClosed = false;
                //socket.setSoTimeout(5000);
                while(runCon)
                {
                    Gdx.app.log("Errorlog", "Checkpoint 1");
                    //Reset capture string.
                    strConv.clear();
                    //Check for a namechange request.
                    if(changeName)
                    {
                        sendMessage("NAME_CHANGE");
                        changeName = false;
                    }
                    //Read the stream for incoming data. If a unit disconnects, the stream will return -1.
                    //reads = dataInputStream.read(buffer, 0, SIZE);
                    strConv = readData(reads, buffer);
                    Gdx.app.log("Errorlog", "Checkpoint 2");
                    if(!runCon)
                        break;
                    //Incoming positional data from user. This is the answer to the data sent by the handler.
                    if(strConv.get(0).equals("POS_DATA_INCOMING"))
                    {
                        user.setPosition(new Vector3().fromString(strConv.get(1)));
                        dataProcessed = true;
                        //handler.changeState(); The user thread cant do this, the handler can only send
                        //data if it has received the all clear from ALL user threads. Otherwise, all
                        //threads will tell it to switch state, which will have unforseen consequenses.
                    }
                    else if(strConv.get(0).equals("CLICK_POS_INCOMING"))
                    {
                        user.setNormVec(new Vector3().fromString(strConv.get(1)));
                        sendDataFromClient("CLICK_POS_INCOMING" + strConv.get(1), Character.getNumericValue(user.id.charAt(user.id.length() - 1)) - 2);
                    }
                    else if(strConv.get(0).equals("SCORE_INCOMING"))
                    {
                        user.setScore(Integer.parseInt(strConv.get(1)));
                        sendDataFromClient("SCORE_INCOMING" + strConv.get(1), Character.getNumericValue(user.id.charAt(user.id.length() - 1)) - 2);
                    }
                    //If the name change request is given, send the new name to the unit.
                    else if(strConv.get(0).equals("NAME_CHANGE"))
                    {
                        msgsend = user.id;
                    }
                    else if(strConv.get(0).equals("READY_CHECK"))
                        user.setReadyState(true);
                    //All other messages will be handled appropriately.
                    else if(!strConv.get(0).equals(""))
                    {
                        //Set the initial player name.
                        if(strConv.get(0).equals("player"))
                        {
                            user.setName("Player " + (1 + getConnections()));
                            msgsend = "Player " + (1 + getConnections());
                        }
                        //Connection confirmation received from player.
                        else if(strConv.get(0).equals(user.id))
                        {
                            nameGet = true;
                            msgtake = user.id + " has connected!";
                        }
                        //All other messages will get this player id as a response.
                        else
                        {
                            msgsend = this.user.id;
                        }
                    }
                    else
                    {
                        msgtake = "Empty message";
                    }
                    //The standard send message statement. Only send message if thread is still running.
                    if(runCon && !msgsend.equals(""))
                    {
                        msglog = "Sending message: " + msgsend;
                        sendMessage(msgsend);
                        msgsend = "";
                    }
                    Gdx.app.log("Errorlog", "Checkpoint 3");
                    //msglog += strConv + "\n";
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }finally
            {
                Gdx.app.log("Errorlog", "Exiting loop.");
                //Freeze the userthread until the datahandler no longer sends data.
                while(handler.getSendState())
                {

                }
                //Make sure that the names must be reset again.
                nameAssignmentDone = false;
                //If streams and the socket are open, close them.
                if(bufferedInputStream != null)
                {
                    try
                    {
                        bufferedInputStream.close();
                        disIsClosed = true;
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                }
                if(bufferedOutputStream != null)
                {
                    try
                    {
                        bufferedOutputStream.close();
                        dosIsClosed = true;
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                        error = "Exception: " + e.toString();
                    }
                }
                //Remove the user from the list.
                userList.remove(user);
                Gdx.app.log("Errorlog", "User thread closed.");
            }
        }
    }

    public String getlog() {return msglog;}
    public String getMsg() {return msgtake;}
    public String getError() {return error;}
    public int getConnections() {return userList.size();}
    public Boolean checkIfVectorNull() {return userList == null;}
    public String getUserId(int idx) {return userList.get(idx).id;}
    public String getUserPosition(int idx) {return userList.get(idx).getPosition().toString();}
    public void setServerName(String name) {srvrName = name;}
    public Boolean checkReadyState()
    {
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            if(!userList.get(idu).readyState)
            {
                allReady = false;
                break;
            }
            allReady = true;
        }
        return allReady;
    }
    public void sendReadyMsg()
    {
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage("ALL_READY_NOW");
        }
    }
    public void activatePosTransfer()
    {
        if(handler != null)
        {
            handler.start();
            handler.changeState();
        }
    }
    public void deactivatePosTrasfer()
    {
        handler.shutdown();
        try
        {
            handler.join();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
        handler = new DataHandler();
    }

    public class User
    {
        //The ID will be used later to identify which unit will receive data.
        //The ID will be specified before connecting by that player. Such as Manly Banger, the Rock God!
        private String id;
        public Socket socket;
        public ConnectThread conThread;
        private Vector3 posData, clickPos;
        private int score;
        private Boolean readyState;

        public Vector3 getPosition() {return posData;}                          //Return position.
        public void setPosition(Vector3 newPos) {posData = newPos;}             //Set new position.
        public void setName(String id) {this.id = id;}                          //Set new name.
        public void setNormVec(Vector3 newClickPos) {clickPos = newClickPos;}   //Set new impulse vector.
        public void setScore(int newScore) {score = newScore;}                  //Set new score.
        public void setReadyState(Boolean rdy) {readyState = rdy;}
    }

    public Vector3 getSrvrPos() {return srvrUser.getPosition();}
    public String getSrvrName() {return srvrUser.id;}

}
