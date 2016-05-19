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

    private Sound yarn_football, yarn_apple, yarn_bomb, yarn_peach, yarn_neon, yarn_earth, yarn_yarn, yarn_heart;
    private Sound football_apple, football_bomb, football_football, football_earth, football_neon, football_heart, football_peach;
    private Sound neon_neon, neon_heart, neon_bomb, neon_peach, neon_apple, neon_earth;
    private Sound heart_apple, heart_peach, heart_bomb, heart_earth, heart_heart;
    private Sound earth_apple, earth_peach, earth_earth, earth_bomb;
    private Sound apple_apple, apple_peach, apple_bomb;
    private Sound peach_peach, peach_bomb;
    private Sound bomb_bomb;
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
        yarn_football = Gdx.audio.newSound(Gdx.files.internal("sound/yarn_football.ogg"));
        yarn_apple = Gdx.audio.newSound(Gdx.files.internal("sound/apple_yarn.ogg"));
        yarn_peach = Gdx.audio.newSound(Gdx.files.internal("sound/yarn_peach.ogg"));
        yarn_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/bomb_yarn.ogg"));
        yarn_earth = Gdx.audio.newSound(Gdx.files.internal("sound/earth_yarn.ogg"));
        yarn_heart = Gdx.audio.newSound(Gdx.files.internal("sound/heart_yarn.ogg"));
        yarn_yarn = Gdx.audio.newSound(Gdx.files.internal("sound/yarn_yarn.ogg"));
        yarn_neon = Gdx.audio.newSound(Gdx.files.internal("sound/neon_yarn.ogg"));
        football_apple = Gdx.audio.newSound(Gdx.files.internal("sound/apple_football.ogg"));
        football_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/bomb_football.ogg"));
        football_peach = Gdx.audio.newSound(Gdx.files.internal("sound/peach_football.ogg"));
        football_heart = Gdx.audio.newSound(Gdx.files.internal("sound/heart_football.ogg"));
        football_earth = Gdx.audio.newSound(Gdx.files.internal("sound/earth_football.ogg"));
        football_football = Gdx.audio.newSound(Gdx.files.internal("sound/football_football.ogg"));
        football_neon = Gdx.audio.newSound(Gdx.files.internal("sound/neon_football.ogg"));
        neon_apple = Gdx.audio.newSound(Gdx.files.internal("sound/neon_apple.ogg"));
        neon_peach = Gdx.audio.newSound(Gdx.files.internal("sound/neon_peach.ogg"));
        neon_heart = Gdx.audio.newSound(Gdx.files.internal("sound/neon_heart.ogg"));
        neon_earth = Gdx.audio.newSound(Gdx.files.internal("sound/neon_earth.ogg"));
        neon_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/neon_bomb.ogg"));
        neon_neon = Gdx.audio.newSound(Gdx.files.internal("sound/neon_neon.ogg"));
        heart_apple = Gdx.audio.newSound(Gdx.files.internal("sound/heart_apple.ogg"));
        heart_peach = Gdx.audio.newSound(Gdx.files.internal("sound/heart_peach.ogg"));
        heart_earth = Gdx.audio.newSound(Gdx.files.internal("sound/heart_earth.ogg"));
        heart_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/heart_bomb.ogg"));
        heart_heart = Gdx.audio.newSound(Gdx.files.internal("sound/heart_heart.ogg"));
        earth_apple = Gdx.audio.newSound(Gdx.files.internal("sound/apple_earth.ogg"));
        earth_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/bomb_earth.ogg"));
        earth_peach = Gdx.audio.newSound(Gdx.files.internal("sound/earth_peach.ogg"));
        earth_earth = Gdx.audio.newSound(Gdx.files.internal("sound/earth_earth.ogg"));
        apple_apple = Gdx.audio.newSound(Gdx.files.internal("sound/apple_apple.ogg"));
        apple_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/apple_bomb.ogg"));
        apple_peach = Gdx.audio.newSound(Gdx.files.internal("sound/apple_peach.ogg"));
        peach_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/bomb_peach.ogg"));
        peach_peach = Gdx.audio.newSound(Gdx.files.internal("sound/peach_peach.ogg"));
        bomb_bomb = Gdx.audio.newSound(Gdx.files.internal("sound/bomb_bomb.ogg"));

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
        Vector3 distance = new Vector3( player1.x - cameraPos.x, player1.y - cameraPos.y, player1.z - cameraPos.z );

        // Scale the volume.
        float volumeFactor = 30 / distance.len();
        Gdx.app.log("HEJ!", "Playing collision sound. Models: " + p1ModelName + " and " + p2ModelName);
        Gdx.app.log("HEJ!", "Volume factor: " + volumeFactor);

        switch(p1ModelName)
        {
            case "football":
                switch(p2ModelName)
                {
                    case "football":
                        football_football.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        football_apple.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        football_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        football_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_football.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        football_neon.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        football_earth.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        football_heart.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "apple":
                switch(p2ModelName)
                {
                    case "football":
                        football_apple.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        apple_apple.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        apple_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        apple_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_apple.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        neon_apple.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        earth_apple.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        heart_apple.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "peach":
                switch(p2ModelName)
                {
                    case "football":
                        football_peach.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        apple_peach.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        peach_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        peach_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_peach.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        neon_peach.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        earth_peach.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        heart_peach.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "bomb":
                switch(p2ModelName)
                {
                    case "football":
                        football_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        apple_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        peach_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        bomb_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        neon_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        earth_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        heart_bomb.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "yarn":
                switch(p2ModelName)
                {
                    case "football":
                        yarn_football.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        yarn_apple.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        yarn_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        yarn_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_yarn.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        yarn_neon.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        yarn_earth.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        yarn_heart.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "neon":
                switch(p2ModelName)
                {
                    case "football":
                        football_neon.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        neon_apple.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        neon_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        neon_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_neon.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        neon_neon.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        neon_earth.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        neon_heart.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "earth":
                switch(p2ModelName)
                {
                    case "football":
                        football_earth.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        earth_apple.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        earth_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        earth_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_earth.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        neon_earth.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        earth_earth.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        heart_earth.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            case "heart":
                switch(p2ModelName)
                {
                    case "football":
                        football_heart.play(soundVolume*volumeFactor);
                        break;
                    case "apple":
                        heart_apple.play(soundVolume*volumeFactor);
                        break;
                    case "peach":
                        heart_peach.play(soundVolume*volumeFactor);
                        break;
                    case "bomb":
                        heart_bomb.play(soundVolume*volumeFactor);
                        break;
                    case "yarn":
                        yarn_heart.play(soundVolume*volumeFactor);
                        break;
                    case "neon":
                        neon_heart.play(soundVolume*volumeFactor);
                        break;
                    case "earth":
                        heart_earth.play(soundVolume*volumeFactor);
                        break;
                    case "heart":
                        heart_heart.play(soundVolume*volumeFactor);
                        break;
                    default:

                        break;
                }
                break;
            default:

                break;
        }
        Gdx.app.log("HEJ!", "after switch statement.");

        // Play the correct sound based on the collision.
//        if(p1ModelName.equals("football")){
//            if(p2ModelName.equals("football")){ballPlaceHolderSound.play(soundVolume*volumeFactor);}
//            if(p2ModelName.equals("peach")) {}
//            if(p2ModelName.equals("apple")){applePlaceHolderSound.play(soundVolume*volumeFactor);}
//            if(p2ModelName.equals("bomb")) {}
//        }
//
//        if(p1ModelName.equals("peach")) {
//            if(p2ModelName.equals("football")){}
//            if(p2ModelName.equals("peach")) {}
//            if(p2ModelName.equals("apple")) {}
//            if(p2ModelName.equals("bomb")) {}
//        }
//
//        if(p1ModelName.equals("apple")) {
//            if(p2ModelName.equals("football")){}
//            if(p2ModelName.equals("peach")) {}
//            if(p2ModelName.equals("apple")) {}
//            if(p2ModelName.equals("bomb")) {}
//        }
//
//        if(p1ModelName.equals("bomb")) {
//            if(p2ModelName.equals("football")){}
//            if(p2ModelName.equals("peach")) {}
//            if(p2ModelName.equals("apple")) {}
//            if(p2ModelName.equals("bomb")) {}
//        }
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
