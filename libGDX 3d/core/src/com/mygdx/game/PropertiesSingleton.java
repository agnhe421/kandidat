package com.mygdx.game;

/**
 * Created by sofiekhullar on 16-04-22.
 */

public class PropertiesSingleton {

    private static final PropertiesSingleton propertiesSingleton = new PropertiesSingleton();
    public static PropertiesSingleton getInstance() {return propertiesSingleton;}

    private int round = 0;
    public int getRound() {return round;}
    public void setRound(int round) {this.round = round +1;}
}
