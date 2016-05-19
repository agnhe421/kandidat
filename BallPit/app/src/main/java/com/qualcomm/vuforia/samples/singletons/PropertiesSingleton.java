package com.qualcomm.vuforia.samples.singletons;

import com.badlogic.gdx.assets.AssetManager;

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
        this.round = round + 1;
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



}