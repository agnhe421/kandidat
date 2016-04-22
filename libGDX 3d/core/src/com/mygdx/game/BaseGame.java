package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class BaseGame extends Game {

    public AssetManager assets;
    @Override
    public void create() {
        assets = new AssetManager();
        setScreen(new GameScreen(this));
    }

}