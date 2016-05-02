package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * Created by Andreas on 2016-03-16.
 */
public class JoinServer extends Thread
{
    protected String dstAdress;
    protected int dstPort;
    protected Socket socket;
    private String msgtake = "msgtake", msgsend = "", error = "No Error", msglog = "";
    private Vector<String> strConv;
    private boolean connected, ready, allready;
    private static final int SIZE = 1024;
    private byte[] buffer;
    private int reads, msgnr;
    public User unitUser;
    private Vector<User> playerList;
    BufferedOutputStream bufferedOutputStream;
    BufferedInputStream bufferedInputStream;
    BaseGame app;

    public JoinServer(String dstAdress, int dstPort, String name, final BaseGame app)
    {
        this.app = app;
        unitUser = new User(name, 0, new Vector3(), new Vector3());
        playerList = new Vector<User>();
        this.dstAdress = dstAdress;
        this.dstPort = dstPort;
        //The name is placeholder, you should be able to enter it yourself when it is integrated with the UI.
        unitUser.setId(name);
        connected = false;
        ready = false;
        allready = false;
        unitUser.setPosition(new Vector3());
        strConv = new Vector<String>();
    }

    @Override
    public void run()
    {
        buffer = new byte[SIZE];
        //Instantiate the socket and the input/output streams.
        socket = null;
        msgnr = 1;
        bufferedOutputStream = null;
        bufferedInputStream = null;
        try
        {
            //Bind the socket to the given address and port.
            socket = new Socket(dstAdress, dstPort);
            try
            {
                socket.setTcpNoDelay(true);
            }catch(SocketException e)
            {
                e.printStackTrace();
                error = "Exception: " + e.toString();
            }
            //Set streams to read from the socket.
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            //socket.setSoTimeout(5000);
            //Set connection state.
            connected = true;
            //Set the message to send to the server.
            while(connected)
            {
                //Clear the receiving vector.
                strConv.clear();
                //Initial condition for setting player name.
                if(unitUser.getId().equals("player"))
                    msgsend = unitUser.id;
                if(unitUser.getId() != "player" && !ready && !playerList.isEmpty())
                {
                    ready = true;
                    sendMessage("READY_CHECK");
                }
                //Send message statement.
                if(connected && !msgsend.equals(""))
                {
                    Gdx.app.log("HEJ!", "Sending message: " + msgsend);
                    sendMessage(msgsend);
                    ++msgnr;
                    msgsend = "";
                }
                //Read the input stream for data, if the server closes, the stream returns -1.
                /**As long as the position data is sent with the right format (toString() will fix it)
                 * one can build a new vector using fromString() for the calculations, therefore using
                 * a DataInputStream instead of an ObjectInputStream is possible.
                 */
                strConv = readData(bufferedInputStream, reads, buffer);
                //Check if the server has closed.
                if(!connected)
                {
                    break;
                }
                Gdx.app.log("strConv", "Data received. Message: " + strConv.get(0));
                //Receive string containing all position data for all users.
                if(strConv.get(0).equals("POS_DATA_INCOMING"))
                {
                    /*if(strConv.equals("POS_DATA_INCOMING"))
                        continue;*/
                    if(!connected)
                        break;
                    //Extract this users data from list.
                    char playerNr = unitUser.getId().charAt(unitUser.getId().length() - 1);
                    int thisPlayerId = Character.getNumericValue(playerNr);
                    //Gdx.app.log("Msglog: ", allData.get(thisPlayerId));

                    for(int idu = 1, idp = 0; idu <= playerList.size(); ++idu)
                    {
                        if(idu != Character.getNumericValue(unitUser.getId().charAt(unitUser.getId().length() - 1)))
                        {
                            playerList.get(idp).setPosition(new Vector3().fromString(strConv.get(idu)));
                            ++idp;
                        }
                        else
                            unitUser.setPosition(new Vector3().fromString(strConv.get(idu)));
                    }
                    unitUser.setPosition(new Vector3().fromString(strConv.get(thisPlayerId)));

                    //Simulate movement to display changes to server, and check network speed.
                    //posData.add(1.0f, 0.0f, 0.0f);
                    msglog = unitUser.getPosition().toString() + "\n";
                    sendData(unitUser.getPosition().toString());
                }
                else if(strConv.get(0).equals("USER_DATA_INCOMING"))
                {
                    Gdx.app.log("HEJ!", "user data got.");
                    User usr = new User(strConv.get(1), Integer.parseInt(strConv.get(2)), new Vector3(), new Vector3());
                    //usr.setPosition(new Vector3().fromString(strConv.get(3)));
                    //usr.setClickPos(new Vector3().fromString(strConv.get(4)));
                    playerList.add(usr);
                }
                //Check for a name change request.
                else if(strConv.get(0).equals("NAME_CHANGE"))
                {
                    msgsend = "NAME_CHANGE";
                    setJoinName("player");
                }
                else if(strConv.get(0).equals("READY_CHECK"))
                {
                    if(ready)
                        sendMessage("READY_TRUE");
                    else
                        sendMessage("READY_FALSE");
                }
                else if(strConv.get(0).equals("POSITION_INCOMING") && app.gameScreen != null)
                {
                    Gdx.app.log("HEJ!", "Size of strConv: " + strConv.size());
                    Vector<Vector3> rec_pos = new Vector<Vector3>(), rec_rot = new Vector<Vector3>();
                    Vector<Float> rec_deg = new Vector<Float>();
                    for(int idv = 1; idv <= playerList.size() + 1; ++idv)
                        rec_pos.add(new Vector3().fromString(strConv.get(idv)));
                    for(int idv = 1 + rec_pos.size(); idv <= playerList.size() + rec_pos.size() + 1; ++idv)
                        rec_rot.add(new Vector3().fromString(strConv.get(idv)));
                    app.gameScreen.updatePositions(rec_pos, rec_rot);
                }
                else if(strConv.get(0).equals("ALL_READY_NOW"))
                {
                    allready = true;
                    Gdx.app.log("Ready?", "allready now");
                }
                //Otherwise, handle message.
                else if(!strConv.get(0).equals(""))
                {
                    if(unitUser.getId().equals("player"))
                    {
                        setJoinName(strConv.get(0));
                        msgsend = unitUser.getId();
                        Gdx.app.log("HEJ!", "New name:" + unitUser.getId());
                    }
                    msgtake = "Receiving: " + strConv.get(0);
                }
                else
                {
                    msgtake = "Current name: " + unitUser.getId();
                }
            }
        }catch(UnknownHostException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }finally
        {
            //Close all streams and the socket.
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
            if(bufferedInputStream != null)
            {
                try
                {
                    bufferedOutputStream.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error += "Exception: " + e.toString() + "\n";
                }
            }
            if(bufferedInputStream != null)
            {
                try
                {
                    bufferedInputStream.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                    error += "Exception: " + e.toString() + "\n";
                }
            }
        }
    }

    //Read incoming data from input stream.
    private Vector<String> readData(BufferedInputStream bis, int readStatus, byte[] buff)
    {
        Vector<String> msg = new Vector<String>();
        String element = "";
        Boolean allRead = false;
        while(!allRead)
        {
            try
            {
                readStatus = bis.read(buff, 0, SIZE);
                if(readStatus == -1)
                {
                    msgtake = "Server is offline.";
                    connected = false;
                    allRead = true;
                }
                else
                {
                    String temp = new String(buff).trim();
                    for(int idt = 0; idt < temp.length(); ++idt)
                    {
                        //Stop at logical terminator '/'.
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
            }
        }

        return msg;
    }
    //Send positional data.
    private void sendData(String data)
    {
        sendMessage("POS_DATA_INCOMING|" + data);
    }
    //Extract this users position from data string.
    /*private Vector<String> extractData(String fullData)
    {
        Vector<String> allPosData = new Vector<String>();
        String temp = "";
        for(int ids = 0; ids < fullData.length(); ++ids)
        {
            if(fullData.charAt(ids) == '|')
            {
                allPosData.add(temp);
                temp = "";
            }
            else
                temp += fullData.charAt(ids);
        }
        allPosData.add(temp);
        return allPosData;
    }*/
    public void setScore(int newScore) {unitUser.setScore(newScore);}
    public String getPlayerId(int index) {return playerList.get(index).getId();}
    public String getUnitUserId() {return unitUser.getId();}
    public String getLog() {return msglog;}
    public String getError() {return error;}
    public String getMsg() {return msgtake;}
    public int getPlayerAmount() {return playerList.size() + 1;}
    public Boolean connected() {return connected;}
    public Boolean getAllReadyState() {return allready;}
    public void setJoinName(String id) {unitUser.setId(id);}
    public void setClickPosVector(Vector3 newClickPos)
    {
        unitUser.setClickPos(newClickPos);
        sendMessage(newClickPos.toString());
    }
    public void sendClickPosVector(Vector3 normVec)
    {
        Gdx.app.log("HEJ!", "Sending impulse vector.");
        sendMessage("CLICK_POS_INCOMING|" + normVec.toString());
    }
    public void sendNewScore() { sendMessage("SCORE_INCOMING|" + unitUser.getScore());}
    //Send message via output stream.
    private void sendMessage(String msg)
    {
        //Send message to server.
        try
        {
            //Add logical terminator to end of string.
            String temp = msg + '/';
            byte[] tempbuf;
            tempbuf = temp.getBytes();
            bufferedOutputStream.write(tempbuf, 0, tempbuf.length);
            bufferedOutputStream.flush();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    public void disconnect()
    {
        //Set connected state to false.
        connected = false;
        try
        {
            Gdx.app.log("Errorlog", "Closing socket");
            socket.close();
        }catch(IOException e)
        {
            e.printStackTrace();
            error = "Exception: " + e.toString();
        }
    }

    private class User //extends BaseBulletTest
    {

        public User(String _name, int _score, Vector3 _clickPos, Vector3 _position)
        {
            setScore(_score);
            setId(_name);
            setClickPos(_clickPos);
            setPosition(_position);
        }

        private Vector3 clickPos, position;
        private int score;
        private String id;
        //private BulletEntity playerChar;
        //private BulletConstructor constructor;

        public Vector3 getPosition() {return position;}
        public Vector3 getClickPos() {return clickPos;}
        public String getId() {return id;}
        public int getScore() {return score;}
        public void setScore(int newScore) {score = newScore;}
        public void setId(String newId) {id = newId;}
        public void setClickPos(Vector3 newClickPos) {clickPos = newClickPos;}
        public void setPosition(Vector3 newPosition) {position = newPosition;}
        /*public void applyCharMovement(Vector3 normVec)
        {
            playerChar.body.activate();
            ((btRigidBody) playerChar.body).applyCentralImpulse(normVec);
        }
        public BulletConstructor initConstructor(Model model, float weight)
        {
            disposables.add(model);
            BulletConstructor bulletConstructor = (new BulletConstructor(model, weight, new btSphereShape(0.8f)));
            return bulletConstructor;
        }*/
    }

}