package com.mygdx.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by sofiekhullar on 16-03-04.
 */
public class SlideButton extends TextButton {
    // Classen ger varje knapp ett id
    private int id;

    public SlideButton(String text, Skin skin, String style, int id){
         super(text,skin,style);
         this.id = id;
    }

    public int getId(){
        return id;
    }
}
