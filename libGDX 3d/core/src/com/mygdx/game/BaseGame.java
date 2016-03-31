package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.testing;

public class BaseGame extends Game {

    ScoreScreen scoreScreen = new ScoreScreen(this);


    @Override
    public void create() {
        setScreen(new testing(this));
    }
}