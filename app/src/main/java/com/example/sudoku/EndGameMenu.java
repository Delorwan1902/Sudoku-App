package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EndGameMenu extends AppCompatActivity implements ScoreBoard {
    private Button homeBtn, newGameBtn;
    private TextView message, scoreValue, bestScoreValue, timeValue, difficultyValue, difficultyText;
    private ImageView logo;
    private Dialog difficultyMessage;
    private SeekBar seekBar;
    private Button confirmBtn, cancelBtn2;
    private boolean difficultySet = false, openDifficultySettings = false, XMode = false;
    private String difficultyPicked;
    public static String[] difficultyTextOptions = { "beginner", "easy", "medium", "hard", "expert" };
    private Chronometer currentTime, bestTime;
    private Map<String, Score> scoreBoardMap = new HashMap<>();
    public static final String SHARED_PREF = "sharedPref";
    private long bestTimeLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_menu);

        SharedPreferences pref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE); //Remove saved data on the app since we are starting a new game
        pref.edit().clear().commit(); //Clear and commit on the same line

        //message = findViewById(R.id.end_game_message_textView);
        logo = findViewById(R.id.end_game_logo);
        scoreValue = findViewById(R.id.end_game_score_value_textView);
        bestScoreValue = findViewById(R.id.end_game_best_score_value_textView);
        difficultyValue = findViewById(R.id.end_game_mode_textView);
        currentTime = findViewById(R.id.end_game_time_chrono);
        bestTime = findViewById(R.id.end_game_best_time_chrono);

        String difficulty = getIntent().getStringExtra("difficulty");
        difficulty = difficulty.toLowerCase(); //Convert text to lowercase just to be sure
        long elapsedMillis = Long.parseLong(getIntent().getStringExtra("time")); //get time
        //boolean win = getIntent().getBooleanExtra("win", true); //get win or lose
        XMode = getIntent().getBooleanExtra("X", false);
        int score = Integer.parseInt(getIntent().getStringExtra("score"));

        scoreBoardMap = loadScore();
        if (isNullOrEmpty(scoreBoardMap)) {
            scoreBoardMap = defaultScores();
            //Toast.makeText(this, "Default score values used", Toast.LENGTH_SHORT).show();
            //bestTime.setBase(SystemClock.elapsedRealtime() - Long.parseLong(getIntent().getStringExtra("time")));
            //int seconds = (int) (elapsedMillis / 1000) % 60;
            //scoreValue.setText(Integer.toString(seconds));
        }
        updateScore(difficulty, elapsedMillis, score);
        saveScore();
        chooseDifficulty(); //Create pop-up message if player wants to start a new game

        scoreValue.setText(Integer.toString(score));
        //Toast.makeText(this, Integer.toString(scoreBoardMap.get(difficulty).getTopScore()) + " LOL", Toast.LENGTH_SHORT).show();
        bestScoreValue.setText(Integer.toString(scoreBoardMap.get(difficulty).getTopScore()));

        //if (win) {
            //message.setText("AWESOME");
            //logo.setImageResource(R.drawable.win_logo);
        //    currentTime.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
        //    currentTime.stop();
        //}
        //else {
            //logo.setImageResource(R.drawable.lose_logo);
         //   message.setText("GAME OVER");
        //}
        //logo.getLayoutParams().height = 150; // OR
        //logo.getLayoutParams().width = 150;
        /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) logo.getLayoutParams();
        params.width = 150;
        params.height = 150;
        logo.setLayoutParams(params);*/

        difficultyValue.setText(difficulty);
        bestTime.setBase(SystemClock.elapsedRealtime() - bestTimeLng);
        bestTime.stop();

        homeBtn = findViewById(R.id.end_game_home_btn);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        newGameBtn = findViewById(R.id.end_game_new_game_btn);
        newGameBtn.setOnClickListener(v -> {
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
            difficultyMessage.show();
        });

        RelativeLayout relativeLayout = findViewById(R.id.end_game_relativeLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    public void updateScore(String difficulty, long time, int currentScore) {
        String[] difficulties = new String[] { "beginner", "easy", "medium", "hard", "expert" };
        //Toast.makeText(this, "difficulty achieved: " + difficulty, Toast.LENGTH_SHORT).show();
        for (String d : difficulties) {
            if (difficulty == d || difficulty.equals(d)) { //check if difficulty text given is valid
                Score score = scoreBoardMap.get(d);
                bestTimeLng = score.getQuickestGame();
                score.incrementGamesPlayed();

                if (getIntent().getBooleanExtra("win", true)) {
                    score.incrementGamesWon();
                    score.calculateTimes(time);
                    score.calculateScores(currentScore);
                }
                else {
                    score.incrementGamesLost();
                    score.calculateLongestTime(time);
                }
                score.calculateRates();                 //Calculate win rate and lost rate
                //Toast.makeText(this, score.getWinRate() + " " + score.getLossRate(), Toast.LENGTH_SHORT).show();
                scoreBoardMap.put(difficulty, score);
            }
        }
        /*if(difficulty == "easy") {
            Toast.makeText(this, "Easy accomplished", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void saveScore() {
        SharedPreferences sharedPref = getSharedPreferences(SCORE_BOARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scoreBoardMap);
        editor.putString(SCORE_MAP, json);
        editor.commit();
    }

    public void newGame() {
        //https://stackoverflow.com/questions/11741270/android-sharedpreferences-in-fragment
        Intent intent = new Intent(this.getApplicationContext(), NewGame.class);
        intent.putExtra("DIFFICULTY", difficultyPicked);
        intent.putExtra("X", XMode);
        startActivity(intent); //Starts instance of the class in intent
    }

    public Map<String, Score> loadScore() {
        Map<String, Score> temp = new HashMap<>();
        SharedPreferences sharedPref = getSharedPreferences(SCORE_BOARD, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(SCORE_MAP, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Score>>(){}.getType();
            temp = gson.fromJson(serializedObject, type);
        }
        return temp;
    }

    public void chooseDifficulty() {
        difficultyMessage = new Dialog(this);
        difficultyMessage.setContentView(R.layout.difficulty_settings_pop_up_box);

        difficultyText = difficultyMessage.findViewById(R.id.difficulty_textView);
        difficultyText.setText("Beginner");

        seekBar = difficultyMessage.findViewById(R.id.difficulty_seekBar);
        seekBar.setMax(4);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {}

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {}

           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               difficultyText.setText(difficultyTextOptions[progress]);
               difficultyPicked = difficultyTextOptions[progress];
               difficultySet = true;
               //Toast.makeText(getActivity(), "New Game difficulty: " + difficultyPicked, Toast.LENGTH_SHORT).show();
               //seekBarValue.setText(String.valueOf(progress));
           }
           });

        confirmBtn = difficultyMessage.findViewById(R.id.difficulty_confirm_btn);
        confirmBtn.setOnClickListener(v -> {
            openDifficultySettings = false;
            //if (difficultyPicked == "beginner" || difficultyPicked == "easy" || difficultyPicked == "medium" || difficultyPicked == "hard" || difficultyPicked == "expert") {
            if(difficultySet == false)
                difficultyPicked = "beginner";
            newGame();
            //}
        });
        cancelBtn2 = difficultyMessage.findViewById(R.id.difficulty_cancel_btn);
        cancelBtn2.setOnClickListener(v -> {
            openDifficultySettings = false;
            difficultyMessage.dismiss();
        });
        //difficultyMessage.show();
    }

    //This is essentially the action-listener for the buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //When back button is pressed
            Toast.makeText(this, "Back arrow pressed", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(this, MainActivity.class);
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