package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Music: Better Days (bensound_beterdays) from Bensound.com
 * Music: Memories (bensound_memories) from Bensound.com
 * Music: Tomorrow (bensound_tomorrow) from Bensound.com
 * Music: Acoustic Breeze (bensound_accousticbreeze) from Bensound.com
 * Music: Love (bensound_love) from Bensound.com
 */
public class MainActivity extends ExtraFunctions {
    private final int[] musicArr = {R.raw.bensound_betterdays, R.raw.bensound_memories,
            R.raw.bensound_tomorrow, R.raw.bensound_acousticbreeze, R.raw.bensound_love};
    public static int musicIndex = 0;
    private boolean playMusic;
    private BottomNavigationView bn;
    MediaPlayer player;
    MusicPlayer mPlayer = null;
    //https://github.com/mfgravesjr/finished-projects/blob/master/SudokuGridGenerator/SudokuGridGenerator.java
    //https://github.com/noah978/Java-Sudoku-Generator
    //https://github.com/Knutakir/Android-Sudoku
    //http://faculty.washington.edu/moishe/javademos/sudoku/Sudoku.java
    //https://www.101computing.net/sudoku-generator-algorithm/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Main Menu"); //Set title in the app bar

        //resumeBtn = findViewById(R.id.button);
        bn = findViewById(R.id.bottom_navigation);
        bn.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit(); //App opens with this fragment
        loadSettings();
        loadData();

        mPlayer = new MusicPlayer(this);
        if(playMusic)
            mPlayer.startMusic();

        ConstraintLayout constraintLayout = findViewById(R.id.main_constraintLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    //Allow menu to the top toolbar to be shown
    //https://www.youtube.com/watch?v=oh4YOj9VkVE
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_setting, menu); //Referring to the settings.xml in menu file
        return true;
    }

    //Add functionality for some of the buttons in the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*if (item.getItemId() == R.id.rules) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }*/
        if(item.getItemId() == R.id.play_music) {
            mPlayer.startMusic();
            //startMusic();
        }
        if(item.getItemId() == R.id.change_music)
            mPlayer.changeMusic();
            //changeMusic();
        if(item.getItemId() == R.id.pause_music)
            mPlayer.pauseMusic();
            //pauseMusic();
        if(item.getItemId() == R.id.stop_music)
            mPlayer.stopMusic();
            //stopMusic();
        return super.onOptionsItemSelected(item);
    }

    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences(OPTIONS, Context.MODE_PRIVATE);
        playMusic = sharedPref.getBoolean(MUSIC, true);
    }

    /*private void startMusic() {
        if(player == null) {
            player = MediaPlayer.create(this, musicArr[0]);

            //Release player when music ends
            player.setOnCompletionListener( v -> {
                stopPlayer();
                changeMusic();
            });
        }
        player.start();
    }*/

    public void changeMusic() {
        Random random = new Random();
        int temp = 0;
        while(true) {
            temp = random.nextInt(musicArr.length);
            if(musicIndex != temp) {
                musicIndex = temp;
                break;
            }
        }
        if(player.isPlaying()) {
            player.stop();
        }
        player.release();
        player = MediaPlayer.create(this, musicArr[musicIndex]);
        player.start();
    }

    private void pauseMusic() {
        if(player != null)
            player.pause();
    }

    private void stopMusic() {
        stopPlayer();
    }

    private void stopPlayer() {
        if(player != null) {
            player.stop();
            player.release();
            player = null;
            Toast.makeText(this, "Music player released", Toast.LENGTH_LONG).show();
        }
    }

    //End music upon exiting the app
    @Override
    protected void onStop() {
        super.onStop();
        if(mPlayer != null)
            mPlayer.stopMusic();
        //stopMusic();
    }

    /**
     * This can also be implemented by the class
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment(MainActivity.this);
            //} else if (item.getItemId() == R.id.nav_challenges) {
                //selectedFragment = new ChallengeFragment();
            } else if (item.getItemId() == R.id.nav_statistics) {
                selectedFragment = new StatisticsFragment();
            }
            MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
            return true;
        }
    };
}