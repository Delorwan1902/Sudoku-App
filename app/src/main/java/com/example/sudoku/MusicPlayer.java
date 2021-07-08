package com.example.sudoku;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * Music: Better Days (bensound_beterdays) from Bensound.com
 * Music: Memories (bensound_memories) from Bensound.com
 * Music: Tomorrow (bensound_tomorrow) from Bensound.com
 * Music: Acoustic Breeze (bensound_accousticbreeze) from Bensound.com
 * Music: Love (bensound_love) from Bensound.com
 */
public class MusicPlayer extends AppCompatActivity {
    private final int[] musicArr = {R.raw.bensound_betterdays, R.raw.bensound_memories,
            R.raw.bensound_tomorrow, R.raw.bensound_acousticbreeze, R.raw.bensound_love};
    public static int musicIndex = 0;
    MediaPlayer player = null;
    Context context;

    public MusicPlayer(Context context) {
        this.context = context;
        Random random = new Random();
        musicIndex = random.nextInt(musicArr.length);
    }

    public void startMusic() {
        if(player == null) {
            player = MediaPlayer.create(context, musicArr[0]); //bensound_betterdays

            //Release player when music ends
            player.setOnCompletionListener( v -> {
                changeMusic();
            });
        }
        player.start();
    }

    public void changeMusic() {
        musicIndex++;
        if(musicIndex == (musicArr.length-1))
            musicIndex = 0;
        if(player.isPlaying())
            player.stop();
        player.release();
        player = MediaPlayer.create(context, musicArr[musicIndex]);
        player.start();
    }

    public void pauseMusic() {
        if(player != null)
            player.pause();
    }

    public void stopMusic() {
        stopPlayer();
    }

    public void stopPlayer() {
        if(player != null) {
            player.stop();
            player.release();
            player = null;
            Toast.makeText(context, "Music player released", Toast.LENGTH_LONG).show();
        }
    }
}
