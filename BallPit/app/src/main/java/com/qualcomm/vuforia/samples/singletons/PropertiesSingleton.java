package com.qualcomm.vuforia.samples.singletons;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class PropertiesSingleton {
    private String chosenIsland;
    public String getChosenIsland() {return chosenIsland;}
    public void setChosenIsland(String chosenIsland) {this.chosenIsland = chosenIsland;}

    private String[] chosenBalls;
    public String getChosenBall(int index) {return chosenBalls[index];}
    public void setChosenBall(int index, String chosenBall) {chosenBalls[index] = chosenBall;}

    public String[] getBallNames(){
        return chosenBalls;
    }

    private String gameMode;
    public String getGameMode() {return gameMode;}
    public void setGameMode(String newGameMode) {gameMode = newGameMode;}

    private AssetManager assets;
    public AssetManager getAssets() {return assets;}
    public void setAssets(AssetManager assets) {this.assets = assets;}

    private static final PropertiesSingleton holder = new PropertiesSingleton();
    public static PropertiesSingleton getInstance() {return holder;}

    // General game stuff.
    private int nrPlayers;

    public void setNrPlayers(int nrPlayers)
    {
        this.nrPlayers = nrPlayers;
        playerScores = new int[nrPlayers];
        chosenBalls = new String[nrPlayers];
    }

    public int getNrPlayers() {
        return this.nrPlayers;
    }

    // Rounds.
    private int round = 0;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    // Scores.
    private volatile int[] playerScores;

    public void setScore(int index, int score)
    {
        playerScores[index] += score;
    }

    public int[] getScores()
    {
       return playerScores;
    }

    public int getScore(int index) {return playerScores[index];}



    ////// COINS /////////////////////
    private Vector3 coinPosition;
    private Vector3[] coinPositions = new Vector3[5];

    public void initRandomCoinPosition(String choosenIsland)
    {

        if(choosenIsland == "island")
        {
            coinPositions[0] = new Vector3(38.9f,217,-74.2f);
            coinPositions[1] = new Vector3(-39.7f,283.6f,42.8f);
            coinPositions[2] = new Vector3(117,191.8f,128.3f);
            coinPositions[3] = new Vector3(-130.6f,188.3f,157.1f);
            coinPositions[4] = new Vector3(-169.6f,202.7f,-136.7f);
        }
        else if(choosenIsland == "greek")
        {
            coinPositions[0] = new Vector3(-37.96f,206.5f,37.7f);
            coinPositions[1] = new Vector3(-0.7f,206.5f,16.8f);
            coinPositions[2] = new Vector3(-7.9f,206.8f,-31);
            coinPositions[3] = new Vector3(23.4f,206.27f,31.56f);
            coinPositions[4] = new Vector3(-30.9f,207.1f,8.9f);
        }
        else if(choosenIsland == "darkice")
        {
            coinPositions[0] = new Vector3(141,164.1f,230);
            coinPositions[1] = new Vector3(-34.5f,201.2f,-232.6f);
            coinPositions[2] = new Vector3(94.7f,230.1f,-71.7f);
            coinPositions[3] = new Vector3(-34.7f,274,-104);
            coinPositions[4] = new Vector3(-183.9f,187.5f,-71.2f);
        }

    }

    public void setRandomCoinPosition()
    {

        Random rand = new Random();

        int  rnd = rand.nextInt(6);
        coinPosition = coinPositions[rnd];
    }

    public Vector3 getCoinPosition()
    {
        return coinPosition;
    }

    ///////////////////////////////////////////////


    /////////////GEM////////////////////////////////
    private Vector3 powerupPosition;
    private Vector3[] powerupPositions = new Vector3[3];
    private String powerupType;

    public void initRandomPowerupPosition(String choosenIsland)
    {

        if(choosenIsland == "island")
        {
            powerupPositions[0] = new Vector3(-51.1f,231.9f,-44.5f);
            powerupPositions[1] = new Vector3(-25.2f,235,116.9f);
            powerupPositions[2] = new Vector3(194.9f,180.3f,134);
        }
        else if(choosenIsland == "greek")
        {
            powerupPositions[0] = new Vector3(-222.3f,206.2f,66.6f);
            powerupPositions[1] = new Vector3(21.8f,206.3f,122.6f);
            powerupPositions[2] = new Vector3(-12.1f,206.7f,-134.8f);
        }
        else if(choosenIsland == "darkice")
        {
            powerupPositions[0] = new Vector3(99.3f,174.7f,133.4f);
            powerupPositions[1] = new Vector3(-154,199.8f,-168);
            powerupPositions[2] = new Vector3(111.2f,201.4f,-143.8f);
        }

    }

    public void setRandomPowerupPosition()
    {

        Random rand = new Random();

        int  rnd = rand.nextInt(6);
        powerupPosition = powerupPositions[rnd];
    }

    public Vector3 getPowerupPosition()
    {
        return powerupPosition;
    }

    public void setPowerupType()
    {

        Random rand = new Random();
        int  rnd = rand.nextInt(3);


        if(rnd == 0)
            powerupType = "strength";
        else if(rnd == 1)
            powerupType = "stealth";
        else if(rnd == 3)
            powerupType = "speedth";
        else
            powerupType = "randomth";
    }

    public String getPowerupType()
    {
        return powerupType;
    }

    //////////////////////////////////////////


}