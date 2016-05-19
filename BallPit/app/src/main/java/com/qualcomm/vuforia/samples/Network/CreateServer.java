package com.qualcomm.vuforia.samples.Network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.qualcomm.vuforia.Prop;
import com.qualcomm.vuforia.samples.libGDX.BaseGame;
import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;

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
import java.util.Random;
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
    private String serverName = "";
    private Boolean threadRun;
    private Boolean allReady;
    private volatile Boolean allIslandChosen, allBallsChosen, switchScreen;
    private Vector<User> userList;
    private int currentListSize;
    public User serverUser;
    BaseGame app;
    private IslandVote islandVote;
    private BallDistribute ballDistribute;

    //Constructor
    public CreateServer(final BaseGame app)
    {
        this.app = app;
        switchScreen = false;
        serverUser = new User();
        serverUser.setName("Player 1");
        islandVote = new IslandVote();
        ballDistribute = new BallDistribute();
        allIslandChosen = false;
        allBallsChosen = false;
        PropertiesSingleton.getInstance().setGameMode("");
    }


    @Override
    public void run()
    {
        //Instantiate the broadcast receiver, the vector of connected users and the socket.
        //Also initialize all checker boolean values.
        receiver = new ReceivePacket(serverName, app);
        userList = new Vector<User>();
        threadRun = true;
        currentListSize = 0;
        Socket socket = null;
        try
        {
            //Bind the server to the static port.
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SOCKETSERVERPORT));
            //Activate the receiver.
            receiver.start();
            msgtake = "Waiting for connection...";
            //Main server thread loop.
            while(threadRun)
            {
                //If the receiver has received a connection request, activate the socket, wait for the unit to connect.
                if(!receiver.connectState())
                {
                    synchronized (this)
                    {
                        this.wait();
                    }
                }

                //If the receiver has received a connection request, activate the socket, wait for the unit to connect.
                //This loop prevents the main thread from accepting new users until the
                //handler has finished sending data.
                receiver.setServerAccepting(true);
                socket = serverSocket.accept();
                receiver.setServerAccepting(false);
                //Add the user to the vector.
                User user = new User();
                userList.add(user);
                currentListSize = userList.size();
                //Create and start a connection thread for this specific user.
                ConnectThread connectThread = new ConnectThread(user, socket);
                connectThread.start();
                //Tell the receiver the connection has been made, so that it can look for new requests.
                if(userList.size() != 1)
                    userList.get(userList.size() - 1).conThread.setUpdateNeeded();

                //Check for disconnections. The only reason currentListSize does not equal to
                //userList.size() is if someone disconnects as connections are handled in the if
                //statement above.
                if(userList.size() != currentListSize)
                {
                    reassignNames();
                    currentListSize = userList.size();
                }
            }
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
        }catch(InterruptedException e)
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
    public void notifyServer()
    {
        synchronized (this)
        {
            this.notify();
        }
    }

    public void stopServer()
    {
        //Stop the main server thread, deactivate the broadcast and close the server socket.
        threadRun = false;
        receiver.stopCatch();
        try
        {
            receiver.join();
            serverSocket.close();
            notifyServer();
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
        for(int idx = 0; idx < userList.size(); ++idx)
        {
            String newName = "Player " + (idx + 1);
            userList.get(idx).conThread.nameChange(newName);
        }
    }
    //Send the character position and rotation data from gamescreen.
    public void sendCharData(Vector<Vector3> charPos, Vector<Vector3> charRot)
    {
        String posTotal = "";
        for(int idu = 0; idu < PropertiesSingleton.getInstance().getNrPlayers(); ++idu)
            posTotal += charPos.get(idu).toString() + "|";
        for(int idu = 0; idu < PropertiesSingleton.getInstance().getNrPlayers(); ++idu)
        {
            if(idu != PropertiesSingleton.getInstance().getNrPlayers() - 1)
                posTotal += charRot.get(idu).toString() + "|";
            else
                posTotal += charRot.get(idu).toString();
        }
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage("POSITION_INCOMING|" + posTotal);
        }
    }
    //Send new score to clients.
    public void sendScoresToClients(int index)
    {
        String msg = "SCORE_INCOMING|" + index + "|" + PropertiesSingleton.getInstance().getScore(index);
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage(msg);
        }
    }
    //Commence island voting process.
    public void startIslandVote()
    {
        islandVote.start();
    }
    //This class is used to select the islad based on all player votes.
    public class IslandVote extends Thread
    {
        @Override
        public void run()
        {
            Boolean running = true;
            allIslandChosen = false;
            Vector<String> islands = new Vector<String>();
            Vector<Integer> count = new Vector<Integer>();
            count.add(1);
            int indexMost = 0;
            while(running)
            {
                try
                {
                    synchronized (this)
                    {
                        this.wait();
                    }
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
                for(int idu = 0; idu <= userList.size(); ++idu)
                {
                    if(idu == userList.size())
                    {
                        if(!serverUser.chosen)
                        {
                            allIslandChosen = false;
                            break;
                        }
                    }
                    else if(!userList.get(idu).chosen)
                    {
                        allIslandChosen = false;
                        break;
                    }
                    allIslandChosen = true;
                }
                if(allIslandChosen)
                {
                    for(int idu = 0; idu <= userList.size(); ++idu)
                    {
                        if(idu == userList.size())
                        {
                            if(!islands.contains(serverUser.islandChoice))
                            {
                                islands.add(serverUser.islandChoice);
                                count.add(1);
                            }
                            else
                            {
                                int index = islands.indexOf(serverUser.islandChoice);
                                int value = count.get(index);
                                ++value;
                                count.set(index, value);
                            }
                        }
                        else if(userList.get(idu).chosen)
                        {
                            if (!islands.contains(userList.get(idu).islandChoice))
                            {
                                islands.add(userList.get(idu).islandChoice);
                                count.add(1);
                            }
                            else
                            {
                                int index = islands.indexOf(userList.get(idu).islandChoice);
                                int value = count.get(index);
                                ++value;
                                count.set(index, value);
                            }
                        }
                    }
                    int maxVal = 0, previousMax = 0, lastIndexMost = 0;
                    for(int idv = 0; idv < islands.size(); ++idv)
                    {
                        if(maxVal < count.get(idv))
                        {
                            previousMax = maxVal;
                            lastIndexMost = indexMost;
                            maxVal = count.get(idv);
                            indexMost = idv;
                        }
                    }
                    if(previousMax == maxVal)
                    {
                        Random rand = new Random();
                        int max = indexMost, min = lastIndexMost;
                        int n = rand.nextInt(max) + min;
                        Gdx.app.log("Randtest", "n: " + n);
                        int diff1 = max - n, diff2 = n - min;
                        if(diff1 <= diff2)
                            indexMost = min;
                        else
                            indexMost = max;
                    }
                    PropertiesSingleton.getInstance().setChosenIsland(islands.get(indexMost));
                    for(int idu = 0; idu < userList.size(); ++idu)
                        userList.get(idu).conThread.sendMessage("ISLAND_VOTE_RESULT|" + PropertiesSingleton.getInstance().getChosenIsland());
                    running = false;
                    switchScreen = true;
                }
            }
        }
    }

    public void startBallsDistribute()
    {
        ballDistribute.start();
    }

    public class BallDistribute extends Thread
    {
        @Override
        public void run()
        {
            Boolean running = true;
            Boolean allBallsGot = false;
            allBallsChosen = false;
            while(running)
            {
                try
                {
                    synchronized (this)
                    {
                        this.wait();
                    }
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                    error = "Exception: " + e.toString();
                }
                for(int idu = 0; idu <= userList.size(); ++idu)
                {
                    if(idu == userList.size())
                    {
                        if(!serverUser.chosen)
                        {
                            allBallsGot = false;
                            break;
                        }
                    }
                    else if(!userList.get(idu).chosen)
                    {
                        allBallsGot = false;
                        break;
                    }
                    allBallsGot = true;
                }
                if(allBallsGot)
                {
                    String ballList = "ALL_BALLS_CHOSEN|";
                    for(int idu = 0; idu <= userList.size(); ++idu)
                    {
                        if(idu == 0)
                        {
                            PropertiesSingleton.getInstance().setChosenBall(idu, serverUser.ballChoice);
                            ballList += serverUser.ballChoice + "|";
                        }
                        else if(idu != userList.size())
                        {
                            PropertiesSingleton.getInstance().setChosenBall(idu, userList.get(idu-1).ballChoice);
                            ballList += userList.get(idu - 1).ballChoice + "|";
                        }
                        else
                        {
                            PropertiesSingleton.getInstance().setChosenBall(idu, userList.get(idu - 1).ballChoice);
                            ballList += userList.get(idu-1).ballChoice;
                        }
                    }
                    for (User u:userList)
                    {
                        u.conThread.sendMessage(ballList);
                    }
                    running = false;
                    allBallsChosen = true;
                    switchScreen = true;
                }
            }
        }
    }

    public Boolean getSwitchScreen() {return switchScreen;}

    //Send user info to clients.
    private void sendUserInfoToClients(String userInfo, int index)
    {
        //String userInfo = "USER_DATA_INCOMING|" + serverUser.id + "|" + 0 + "|" + new Vector3().toString() + "|" + new Vector3().toString();
        userList.get(index).conThread.sendMessage(userInfo);
    }

    public void sendSoundPrompt(Vector3 pos, String m1, String m2)
    {
        String msg = "SOUND_PROMPT|" + pos.toString() + "|" + m1 + "|" + m2;
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage(msg);
        }
    }

    private void sendGameMode()
    {
        String msg = "GAME_MODE|" + PropertiesSingleton.getInstance().getGameMode();
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage(msg);
        }
    }

    public void sendRoundOverPrompt()
    {
        String msg = "ROUND_OVER";
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage(msg);
        }
    }

    //Update the playerlists of other users whenever a new client connects.
    private void updateOtherUsers(int thisIndex, String name, int score)
    {
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            if(idu != thisIndex)
            {
                sendUserInfoToClients("USER_DATA_INCOMING|" + name + "|" + score, idu);
            }
        }
    }

    //The thread handling the actual connection part.
    private class ConnectThread extends Thread
    {
        public Socket socket;
        public User user;
        private Vector<String> strConv;
        private Boolean runCon, changeName, nameGet, dosIsClosed, disIsClosed, updateNeeded;
        private static final int SIZE = 1024;
        private byte[] buffer;
        private int reads;
        private BufferedOutputStream bufferedOutputStream;
        private BufferedInputStream bufferedInputStream;

        ConnectThread(User usr, Socket socket)
        {
            //Connect user and socket to the passed input.
            //Also initialize all necessary variables.
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
            user.setReadyState(false);
            user.setChosen(false);
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
            nameGet = false;
            dosIsClosed = true;
            disIsClosed = true;
            updateNeeded = false;
        }

        //Check whether a proper name has been assigned.
        public Boolean getNameStatus() {return nameGet;}

        //Send message to client.
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
        //Change the current name.
        public void nameChange(String newName) {
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
        //Set whether this thread needs to update other users when its data has been assigned.
        public void setUpdateNeeded()
        {
            updateNeeded = true;
        }
        //Read incoming data.
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
                        readStatus = bufferedInputStream.read(buff, 0, SIZE);
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
                        runCon = false;
                        readStatus = -1;
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
            //The internal index is created to check whether or not to send additional user info to client.
            int internalIndex = 1;
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
                //Main connection loop.
                while(runCon)
                {
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
                    if(!runCon || reads == -1)
                        break;
                    //If incoming click positions are registered, update character impulse for that character.
                    if(strConv.get(0).equals("CLICK_POS_INCOMING") && app.gameScreen != null) {
                        app.gameScreen.updateImpulse(fromString(strConv.get(1)),
                                Character.getNumericValue(user.id.charAt(user.id.length() - 1)) - 1);
                    }
                    //If the name change request is given, send the new name to the unit.
                    else if(strConv.get(0).equals("NAME_CHANGE"))
                    {
                        msgsend = user.id;
                    }
                    //If a clients ready message is received, set the ready state to true.
                    else if(strConv.get(0).equals("READY_CHECK"))
                        user.setReadyState(true);
                    else if(strConv.get(0).equals("ISLAND_CHOSEN"))
                    {
                        user.setIslandChoice(strConv.get(1));
                        user.setChosen(true);
                        notifyIsland();
                    }
                    else if(strConv.get(0).equals("BALL_CHOSEN"))
                    {
                        user.setBallChoice(strConv.get(1));
                        user.setChosen(true);
                        notifyBalls();
                    }
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
                        //Send server info to player.
                        else if(strConv.get(0).equals(user.id))
                        {
                            nameGet = true;
                            sendUserInfoToClients("USER_DATA_INCOMING|" + serverUser.id + "|" + 0,
                                    Character.getNumericValue(user.id.charAt(user.id.length() - 1)) - 2);
                            msgtake = user.id + " has connected!";
                        }
                        //User info received. If more information is required, send that too.
                        else if(strConv.get(0).equals("USER_DATA_GOT"))
                        {
                            if(internalIndex < userList.size())
                            {
                                sendUserInfoToClients("USER_DATA_INCOMING|" + userList.get(internalIndex - 1).id + "|" + 0,
                                        Character.getNumericValue(user.id.charAt(user.id.length() - 1)) - 2);
                                ++internalIndex;
                            }
                            else
                            {
                                if(userList.size() > 1 && updateNeeded)
                                {
                                    updateOtherUsers(Character.getNumericValue(user.id.charAt(user.id.length() - 1)) - 2, user.id, 0);
                                    updateNeeded = false;
                                }
                                sendMessage("ALL_USERS_SENT");
                                internalIndex = 100;
                            }
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
                }
            }catch(IOException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }finally
            {
                Gdx.app.log("Errorlog", "Exiting loop.");
                //Freeze the userthread until the datahandler no longer sends data.
                //Make sure that the names must be reset again.
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
    public void resetUserChoiceState()
    {
        switchScreen = false;
        serverUser.setChosen(false);
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).setChosen(false);
        }
    }
    public void setServerName(String name) {
        serverName = name;}   //May be used to set a new name for the server.
    //Check whether client is ready.
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
    //    public Boolean checkIslandChosen()
//    {
//        for(int idu = 0; idu < userList.size(); ++idu)
//        {
//            if(!userList.get(idu).chosen)
//            {
//                allIslandChosen = false;
//                break;
//            }
//            allIslandChosen = true;
//        }
//        return allIslandChosen;
//    }
    public Boolean checkIslandChosen()
    {
        return allIslandChosen;
    }
    public void notifyIsland()
    {
        synchronized (islandVote)
        {
            islandVote.notify();
        }
    }

    public Boolean checkBallChosen()
    {
        return allBallsChosen;
    }
    public void notifyBalls()
    {
        synchronized (ballDistribute)
        {
            ballDistribute.notify();
        }
    }
    //Send the all clear message to let all clients know that the games can begin.
    public void sendReadyMsg()
    {
        for(int idu = 0; idu < userList.size(); ++idu)
        {
            userList.get(idu).conThread.sendMessage("ALL_READY_NOW");
        }
    }

    public class User
    {
        //The ID will be used later to identify which unit will receive data.
        //The ID will be specified before connecting by that player. Such as Manly Banger, the Rock God! Maybe.
        private String id;
        public Socket socket;
        public ConnectThread conThread;
        private Boolean readyState;
        private String islandChoice, ballChoice;
        private Boolean chosen;

        public void setChosen(Boolean state) {chosen = state;}
        public void setIslandChoice(String choice) {islandChoice = choice;}
        public void setBallChoice(String choice) {ballChoice = choice;}
        public void setName(String id) {this.id = id;}                          //Set new name.
        public void setReadyState(Boolean rdy) {readyState = rdy;}              //Set the ready state of user.
    }

    public String getServerName() {return serverUser.id;}                           //Return user name.

    public Vector3 fromString (String v) {
        int s0 = v.indexOf(',', 1);
        int s1 = v.indexOf(',', s0 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '[' && v.charAt(v.length() - 1) == ']') {
            try {
                float x = Float.parseFloat(v.substring(1, s0));
                float y = Float.parseFloat(v.substring(s0 + 1, s1));
                float z = Float.parseFloat(v.substring(s1 + 1, v.length() - 1));
                return new Vector3(x, y, z);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException
            }
        }
        throw new GdxRuntimeException("Malformed Vector3: " + v);
    }
}
