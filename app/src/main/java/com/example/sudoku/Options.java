package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

//Class called Options and not Settings
public class Options extends AppCompatActivity {
    private final String OPTIONS = "options", MISTAKES = "mistakes", SCORE_FIELD = "scoreField", TIME = "time", MUSIC = "music";
    private Switch mistakesSwitch, scoreSwitch, timeSwitch, musicSwitch;
    private boolean limitMistakes = true, showScore = true, showTimer = true, playMusic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setTitle("Options"); //Set the title of the page
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button

        mistakesSwitch = findViewById(R.id.options_hints_switch);
        scoreSwitch = findViewById(R.id.options_score_switch);
        timeSwitch = findViewById(R.id.options_time_switch);
        musicSwitch = findViewById(R.id.options_music_switch);
        loadData();

        mistakesSwitch.setChecked(limitMistakes);
        mistakesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            limitMistakes = isChecked;
        });

        scoreSwitch.setChecked(showScore);
        scoreSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showScore = isChecked;
        });

        timeSwitch.setChecked(showTimer);
        timeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showTimer = isChecked;
        });

        musicSwitch.setChecked(playMusic);
        musicSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            playMusic = isChecked;
        }));
    }

    public void loadData() {
        SharedPreferences sharedPref = getSharedPreferences(OPTIONS, Context.MODE_PRIVATE);
        limitMistakes = sharedPref.getBoolean(MISTAKES, true);
        showScore = sharedPref.getBoolean(SCORE_FIELD, true);
        showTimer = sharedPref.getBoolean(TIME, true);
        playMusic = sharedPref.getBoolean(MUSIC, true);
    }

    public void saveData() {
        SharedPreferences sharedPref = getSharedPreferences(OPTIONS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(MISTAKES, limitMistakes);
        editor.putBoolean(SCORE_FIELD, showScore);
        editor.putBoolean(TIME, showTimer);
        editor.putBoolean(MUSIC, playMusic);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            //saveData();
            //pauseChronometer();
            //Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (item.getItemId() == android.R.id.home) { //When back button is pressed
            saveData();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
            return true;
        }
        /*else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }*/
        return super.onOptionsItemSelected(item);
    }
}