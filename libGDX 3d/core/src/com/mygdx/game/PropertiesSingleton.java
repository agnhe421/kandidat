package com.mygdx.game;

/**
 * Created by sofiekhullar on 16-04-22.
 */
public class PropertiesSingleton {

    private static final PropertiesSingleton propertiesSingleton = new PropertiesSingleton();
    public static PropertiesSingleton getInstance() {return propertiesSingleton;}

    // General game stuff.
    private int nrPlayers;
    public void setNrPlayers(int nrPlayers){this.nrPlayers = nrPlayers;}
    public int getNrPlayers(){return this.nrPlayers; }

    // Rounds.
    private int round = 0;
    public int getRound() {return round;}
    public void setRound(int round) {this.round = round +1;}

    // Scores.
    private int [] playerScores = new int[4];

    public void setPlayer1Score(int playerScore){this.playerScores[0] = playerScore + this.playerScores[0];}
    public void setPlayer2Score(int playerScore){this.playerScores[1] = playerScore + this.playerScores[1];}
    public void setPlayer3Score(int playerScore){this.playerScores[2] = playerScore + this.playerScores[2];}
    public void setPlayer4Score(int playerScore){this.playerScores[3] = playerScore + this.playerScores[3];}

    public  int [] getPlayerScores(){return this.playerScores;}
    public int getPlayer1Score(){return this.playerScores[0];}
    public int getPlayer2Score(){return this.playerScores[1];}
    public int getPlayer3Score(){return this.playerScores[2];}
    public int getPlayer4Score(){return this.playerScores[3];}

    // Balls.
    private String [] balls = new String[]{"", "", "", ""};
    private String ballsString = ""; // The ballsString will be separated by "/".

    public void setPlayer1Ball(String modelString){balls[0] = modelString;}
    public void setPlayer2Ball(String modelString){balls[1] = modelString;}
    public void setPlayer3Ball(String modelString){balls[2] = modelString;}
    public void setPlayer4Ball(String modelString){balls[3] = modelString;}

    public String [] getBallsArray(){return this.balls;}

    // Should be called in the GameScreen before sending it to the server?
    public void createBallString(){
        for(int i = 0; i < nrPlayers; i++){
            if(balls[i] != ""){
                ballsString = balls[i] + "/";
            }
        }
    }

    public String getBallString(){return this.ballsString;}
}