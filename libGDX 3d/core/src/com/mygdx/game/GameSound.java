package com.mygdx.game;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;

import javax.activation.DataHandler;

public class GameSound implements Audio {

    private Sound ballPlaceHolderSound, applePlaceHolderSound;
    private Music backgroundMusic;
    private float musicVolume;

    public GameSound(){
        // Load the sounds
        ballPlaceHolderSound = Gdx.audio.newSound(Gdx.files.internal("sound/ballph.wav"));
        applePlaceHolderSound = Gdx.audio.newSound(Gdx.files.internal("sound/appleph.wav"));

        // Load the music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Davicii - Levels.mp3"));
    }

    // TODO: Skriv om så att tar emot volymen från optionsmenyn.
    public void playBackgroundMusic(float theMusicVolume){
        musicVolume = theMusicVolume;

        if(!backgroundMusic.isPlaying()) { backgroundMusic.play(); }
        backgroundMusic.setVolume(musicVolume);
    }

    public void stopBackgroundMusic(){
        if(backgroundMusic.isPlaying()){ backgroundMusic.stop(); }
    }

    // Calculate distances and adjust volumes.
    public void playCollisionSound(Vector3 player1, String p1ModelName, String p2ModelName ){

        // Temporär "öron"-position. Denna ska ersättas med vart användaren fysiskt står från vuforias viewmatrix eller nåt.
        // TODO: Ersätt alltså med Vuforias camera view position.
        Vector3 tempEar = new Vector3(0f, 0f, 0f);

        // Räkna distansen utifrån deras Vec3 positioner.
        Vector3 distance = new Vector3( player1.x - tempEar.x, 0, player1.z - tempEar.z );

        // Justera volymen.
        float volumeFactor = 1 / distance.len();

        // Play the correct sound based on the collision.
        if(p1ModelName.equals("football")){
            if(p2ModelName.equals("football")){ballPlaceHolderSound.play(volumeFactor);}
            if(p2ModelName.equals("peach")) {}
            if(p2ModelName.equals("apple")){applePlaceHolderSound.play(volumeFactor);}
            if(p2ModelName.equals("bomb")) {}
        }

        if(p1ModelName.equals("peach")) {
            if(p2ModelName.equals("football")){}
            if(p2ModelName.equals("peach")) {}
            if(p2ModelName.equals("apple")) {}
            if(p2ModelName.equals("bomb")) {}
        }

        if(p1ModelName.equals("apple")) {
            if(p2ModelName.equals("football")){}
            if(p2ModelName.equals("peach")) {}
            if(p2ModelName.equals("apple")) {}
            if(p2ModelName.equals("bomb")) {}
        }

        if(p1ModelName.equals("bomb")) {
            if(p2ModelName.equals("football")){}
            if(p2ModelName.equals("peach")) {}
            if(p2ModelName.equals("apple")) {}
            if(p2ModelName.equals("bomb")) {}
        }
    }

    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        return null;
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        return null;
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return null;
    }

    @Override
    public Music newMusic(FileHandle file) {
        return null;
    }





}
