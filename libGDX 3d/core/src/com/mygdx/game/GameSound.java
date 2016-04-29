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

/**
 * Created by Jinwoo on 2016-04-08.
 */
public class GameSound implements Audio {

    private Sound ballPlaceHolderSound, applePlaceHolderSound;
    private Music backgroundMusic;
    private float musicVolume;

    /*
    Nätverk, vad behövs skickas?

    - För surroundljud behövs en vec3 skickas med varje persons camera position.


  Eller kanske inte, varje client kan få
  - en position där kollisionen hände
  - Clienten räknar distansen till kollisionen och justerar volymen i GameSound-klassen.
  - Men vilket ljud ska spelas? måste nåt slags id skickas med?

  Eller varje client sköter sitt ljud själv

     */


    public GameSound(){
        // Sounds
        ballPlaceHolderSound = Gdx.audio.newSound(Gdx.files.internal("sound/ballph.wav"));
        applePlaceHolderSound = Gdx.audio.newSound(Gdx.files.internal("sound/appleph.wav"));

        // Music
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
    // TODO: Fixa så att den tar emot vilket id bollen har, och med vad den krockat med för att spela det ljudet.
    public void playCollisionSound(Vector3 player1, Vector3 player2){ // BORDE SÄTTA 3:E Arg om vilket ljud

        // Slutpunkt blir då isåfall där kollisionen sker. Kanske räcker att bara ta en av
        // Spelarnas position för att slippa onödiga beräkningar. Båda bollarna är ju ändå där?
        // Men det blir nog inte tillräckligt när du har flera enheter.

        // Temporär "öron"-position. Denna ska ersättas med vart användaren fysiskt står eller
        // eventuellt spelarposition i spelvärlden.
        // TODO: Ersätt alltså med Vuforias camera view position.
        Vector3 tempEar = new Vector3(0f, 0f, 0f);

        // Räkna distansen utifrån deras Vec3 positioner.
        Vector3 distance = new Vector3( player1.x - tempEar.x, 0, player1.z - tempEar.z );

        // Justera volymen.
        float volumeFactor = 1 / distance.len();
//        ballPlaceHolderSound.play(volumeFactor);
        applePlaceHolderSound.play(volumeFactor);
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
