package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.awt.Font;

public class BaseGame extends Game {

    public AssetManager assets;
    public BitmapFont font40, font120;

    @Override
    public void create() {
        assets = new AssetManager();
        initFonts();

        setScreen(new GameScreen(this));
    }

    private void initFonts(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copyfonts.com_gulim.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 40;
        params.color = Color.BLACK;
        font40 = generator.generateFont(params);

        params.size = 120;
        params.color = Color.BLACK;
        font120 = generator.generateFont(params);
        generator.dispose();
    }

}