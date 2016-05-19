package com.qualcomm.vuforia.samples.libGDX.classes;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;

public class GameSound implements Audio {

    private static final GameSound holder = new GameSound();
    public static GameSound getInstance() {return holder;}

    private Sound ballPlaceHolderSound, applePlaceHolderSound;
    private Music menuMusic;
    private Music gameMusic;
    private float musicVolume = 0.5f;
    private float soundVolume = 0.5f;

    //TODO: Klipp in denna kod i collision listener i din game screen för att spela rätt ljud.
    //TODO: Du måste ha playerList vectorn för att det ska fungera. Det finns i GameScreen, 160502
    /*
    if(userValue0 <= playerList.size() && userValue1 <= playerList.size()){
        gameSound.playCollisionSound(p1Position, playerList.get(userValue0-1).getModelName(), playerList.get(userValue1-1).getModelName());
        Gdx.app.log("userValue0 = ", "" + playerList.get(userValue0 - 1).getModelName());
        Gdx.app.log("userValue1 = ", "" + playerList.get(userValue1 - 1).getModelName());
    }
    */

    public GameSound(){
        // Load the sounds
        ballPlaceHolderSound = Gdx.audio.newSound(Gdx.files.internal("sound/yarn_football.ogg"));
        applePlaceHolderSound = Gdx.audio.newSound(Gdx.files.internal("sound/appleph.wav"));

        // Load the music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music/menu.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music/Davicii - Levels.mp3"));
    }

    // TODO: Skriv om så att tar emot volymen från optionsmenyn.
    public void playMusic(String musicName){

        if(musicName == "menu")
        {
            if(!menuMusic.isPlaying()) {
                menuMusic.play();
                menuMusic.setLooping(true);
            }
            menuMusic.setVolume(musicVolume);
        }

        if(musicName == "game")
        {
            if(!menuMusic.isPlaying()) {
                gameMusic.play();
                gameMusic.setLooping(true);
            }
            gameMusic.setVolume(musicVolume);
        }


    }

    public void stopMusic(String musicName){
        if(menuMusic.isPlaying()){ menuMusic.stop(); }
    }

    public float getMusicVolume(){
        return musicVolume;
    }

    public void setMusicVolume(float volume) {

      musicVolume = volume;

        menuMusic.setVolume(musicVolume);
        gameMusic.setVolume(musicVolume);
    }

    // Calculate distances and adjust volumes.
    public void playCollisionSound(Vector3 player1, String p1ModelName, String p2ModelName, Vector3 cameraPos){


        // Calculate distance from the camera and the collision.
        Vector3 distance = new Vector3( player1.x - cameraPos.x, 0, player1.z - cameraPos.z );

        // Scale the volume.
        float volumeFactor = 30 / distance.len();

        Gdx.app.log("Volume factor:", volumeFactor + "");

        // Play the correct sound based on the collision.
        if(p1ModelName.equals("football")){
            if(p2ModelName.equals("football")){ballPlaceHolderSound.play(soundVolume*volumeFactor);}
            if(p2ModelName.equals("peach")) {}
            if(p2ModelName.equals("apple")){applePlaceHolderSound.play(soundVolume*volumeFactor);}
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

    public float getSFXVolume(){

        return soundVolume;
    }

    public void setSFXVolume(float volume) {

        soundVolume = volume;

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
