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
            coinPositions[0] = new Vector3(0,0,0);
            coinPositions[1] = new Vector3(0,0,0);
            coinPositions[2] = new Vector3(0,0,0);
            coinPositions[3] = new Vector3(0,0,0);
            coinPositions[4] = new Vector3(0,0,0);
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
            coinPositions[0] = new Vector3(0,0,0);
            coinPositions[1] = new Vector3(0,0,0);
            coinPositions[2] = new Vector3(0,0,0);
            coinPositions[3] = new Vector3(0,0,0);
            coinPositions[4] = new Vector3(0,0,0);
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
            powerupPositions[0] = new Vector3(0,0,0);
            powerupPositions[1] = new Vector3(0,0,0);
            powerupPositions[2] = new Vector3(0,0,0);
        }
        else if(choosenIsland == "greek")
        {
            powerupPositions[0] = new Vector3(0,0,0);
            powerupPositions[1] = new Vector3(0,0,0);
            powerupPositions[2] = new Vector3(0,0,0);
        }
        else if(choosenIsland == "darkice")
        {
            powerupPositions[0] = new Vector3(0,0,0);
            powerupPositions[1] = new Vector3(0,0,0);
            powerupPositions[2] = new Vector3(0,0,0);
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