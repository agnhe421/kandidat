package com.qualcomm.vuforia.samples.singletons;

import com.badlogic.gdx.assets.AssetManager;

public class PropertiesSingleton {
    private String choosenIsland;
    public String getChoosenIsland() {return choosenIsland;}
    public void setChoosenIsland(String choosenIsland) {this.choosenIsland = choosenIsland;}

    private String choosenBall;
    public String getChoosenBall() {return choosenBall;}
    public void setChoosenBall(String choosenBall) {this.choosenBall = choosenBall;}

    private AssetManager assets;
    public AssetManager getAssets() {return assets;}
    public void setAssets(AssetManager assets) {this.assets = assets;}

    private static final PropertiesSingleton holder = new PropertiesSingleton();
    public static PropertiesSingleton getInstance() {return holder;}




}