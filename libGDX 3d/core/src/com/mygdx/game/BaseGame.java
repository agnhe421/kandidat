package com.mygdx.game;

import com.badlogic.gdx.Game;

public class BaseGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

}