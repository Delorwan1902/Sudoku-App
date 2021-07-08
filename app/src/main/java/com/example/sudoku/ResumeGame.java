package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResumeGame extends ExtraFunctions implements View.OnClickListener {
    private RecyclerView recyclerView;
    List<Integer> listGrid = new ArrayList<>();
    protected Chronometer stopWatch;
    public static Button numberSelected = null;
    private List<Integer> unalteredGrid = new ArrayList<>();
    private MusicPlayer mPlayer;
    private long pauseOffset = 0;
    private TextView difficultyText, scoreText, mistakesText, countBadgeText;
    private String difficulty;
    private Button submitBtn;
    private CardView countBadgeIcon;
    private ImageButton undoBtn, eraseBtn, notesBtn, hintsBtn;
    private int mistakes, scoreMultiplier = 21, score = 0, hintsAvailable = 3, hints = 3;
    private double mistakesMultiplier = 0;
    private boolean limitMistakes = true, showScore = true, showTimer = true, playMusic = true, useXPattern = false;
    private List<Boolean> canChangeCell = null;
    private static boolean useNotes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_game);
        getSupportActionBar().setTitle("Sudoku"); //Set title in the app bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button

        this.loadData(); //Load all variables in ExtraFunctions
        setDifficultyText();
        imagesButtons();
        loadSettings(); //limitMistakes, showTimer & playMusic

        mistakesText = findViewById(R.id.resume_game_mistakes_textView);
        mistakesText.setText("Mistakes: " + mistakes + "/3");   //loadData()
        if(!limitMistakes)
            mistakesText.setVisibility(View.INVISIBLE);

        scoreText = findViewById(R.id.resume_game_score_textView);
        if(!showScore)
            scoreText.setVisibility(View.INVISIBLE);

        stopWatch = findViewById(R.id.resume_game_chronometer);
        if(!showTimer)
            stopWatch.setVisibility(View.INVISIBLE);
        startChronometer();

        MyAdaptor myAdaptor = new MyAdaptor(this, listGrid, unalteredGrid, canChangeCell, useXPattern);
        myAdaptor.selectedButton = null;
        myAdaptor.index = null;

        recyclerView = findViewById(R.id.resume_game_recycler_view);
        recyclerView.setAdapter(myAdaptor);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(5));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 9));

        countBadgeIcon = findViewById(R.id.resume_game_count_badge_cardView);
        countBadgeText = findViewById(R.id.resume_game_count_badge_textView);
        countBadgeText.setText(Integer.toString(hintsAvailable));   //loadData()

        submitBtn = findViewById(R.id.resume_game_submit_btn);
        submitBtn.setOnClickListener(v -> {
            if(myAdaptor.checkGridFilled()) {
                int[][] to2DArray = listTo2DArray(MyAdaptor.grid, 9);
                if(validateGrid(to2DArray)) {
                    //Toast.makeText(this, "All cells in grid is filled correctly", Toast.LENGTH_SHORT).show();
                    pauseChronometer();
                    long elapsedMillis = SystemClock.elapsedRealtime() - stopWatch.getBase();
                    endGameMenu(elapsedMillis);
                }
            }
            else
                Toast.makeText(this, "Fill in all of the cells in grid", Toast.LENGTH_SHORT).show();
        });
        mPlayer = new MusicPlayer(this);
        if(playMusic)
            mPlayer.startMusic();
    }

    public void imagesButtons() {
        undoBtn = findViewById(R.id.resume_game_undo_btn);
        undoBtn.setOnClickListener(v -> {
            if(MyAdaptor.selectedButton != null && MyAdaptor.grid.get(MyAdaptor.index) != 0) {
                MyAdaptor.grid.set(MyAdaptor.index, 0);
                MyAdaptor.selectedButton.setText(" ");
            }
        });
        notesBtn = findViewById(R.id.resume_game_notes_btn);
        notesBtn.setOnClickListener(v -> {
            useNotes = !useNotes;
            int[] arr = new int[] {R.id.resume_game_one_btn, R.id.resume_game_two_btn, R.id.resume_game_three_btn,
                    R.id.resume_game_four_btn, R.id.resume_game_five_btn, R.id.resume_game_six_btn,
                    R.id.resume_game_seven_btn, R.id.resume_game_eight_btn, R.id.resume_game_nine_btn};
            if(useNotes) {
                for(int i : arr) {
                    Button temp = findViewById(i);
                    temp.setTextColor(Color.RED);
                    //temp.setBackgroundColor(Color.WHITE);
                }
            }
            else {
                for(int i : arr) {
                    Button temp = findViewById(i);
                    temp.setTextColor(Color.BLACK);
                    //temp.setBackgroundColor(Color.WHITE);
                }
            }
        });
        eraseBtn = findViewById(R.id.resume_game_erase_btn);
        eraseBtn.setOnClickListener(v -> {
            ResetBoardDialog resetDialog = new ResetBoardDialog();
            resetDialog.show(getSupportFragmentManager(), "Reset Board Dialog");
        });
        hintsBtn = findViewById(R.id.resume_game_hints_btn);
        hintsBtn.setOnClickListener(v -> {
            updateBadge();
            if(hintsAvailable <= 0) {
                hintsBtn.setClickable(false);
                hintsBtn.setBackgroundColor(Color.GRAY);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mPlayer != null)
            mPlayer.stopMusic();
    }

    public void updateBadge() {
        if(hintsAvailable != 0) {
            hintsAvailable--;
            countBadgeText.setText(Integer.toString(hintsAvailable));
        }
        else {
            countBadgeText.setText("");
            countBadgeText.setVisibility(View.INVISIBLE);
            countBadgeIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void updateMistakes() {
        if(limitMistakes == true) {
            mistakes++;
            String string = "Mistakes: " + mistakes + "/3";
            mistakesText.setText(string);

            if (mistakes >= 3) {
                pauseChronometer();
                long elapsedMillis = SystemClock.elapsedRealtime() - stopWatch.getBase();
                endGameMenu(elapsedMillis);
            }
        }
    }

    public void setDifficultyText() {
        difficultyText = findViewById(R.id.resume_game_difficulty_textView);
        difficultyText.setText(difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1));
    }

    public static int[][] listTo2DArray(List<Integer> list, int arraySize) {
        int[][] arr2D = new int[arraySize][];
        int[] temp = new int[arraySize];
        int index = 0, rowIndex = 0;

        for(int i : list) {
            temp[index] = i;
            index++;
            if(index > (arraySize-1)) {
                arr2D[rowIndex] = temp;
                temp = new int[arraySize];
                index = 0;
                rowIndex++;
            }
        }
        return arr2D;
    }

    public void calculateFinalScore() {
        score = (int) (score - (mistakes * 50 * mistakesMultiplier));
    }

    public void endGameMenu(long time) {
        Intent intent = new Intent(this, EndGameMenu.class);
        calculateFinalScore();

        intent.putExtra("time", Long.toString(time));
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("score", Integer.toString(score));
        intent.putExtra("mistakes", Integer.toString(mistakes));
        if(mistakes < 3) {
            intent.putExtra("win", true);
            Toast.makeText(this, "You Won!", Toast.LENGTH_SHORT).show();
        }
        else {
            intent.putExtra("win", false);
        }
        startActivity(intent); //Starts instance of the class in intent
    }

    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences(OPTIONS, Context.MODE_PRIVATE);
        limitMistakes = sharedPref.getBoolean(MISTAKES, true);
        showScore = sharedPref.getBoolean(SCORE_FIELD, true);
        showTimer = sharedPref.getBoolean(TIME, true);
        playMusic = sharedPref.getBoolean(MUSIC, true);
    }

    public void saveData() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listGrid);
        editor.putString(LIST, json);
        editor.commit();

        sharedPref = getSharedPreferences(VARIABLES, Context.MODE_PRIVATE); //STOPWATCH saved in the pauseChronometer() method
        editor = sharedPref.edit();
        //editor.putString(DIFFICULTY, difficultyText.getText().toString());
        editor.putInt(SCORE, score);
        editor.putInt(MISTAKES, mistakes);
        editor.putString(DIFFICULTY, difficulty);
        editor.putInt(HINTS, hintsAvailable);
        editor.putInt(SCORE_MULTIPLIER, scoreMultiplier);
        editor.putFloat(MISTAKES_MULTIPLIER, (float) mistakesMultiplier);
        editor.commit();
    }

    @Override
    public void loadData(){
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(LIST, null);
        if(serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Integer>>() {}.getType();
            listGrid = gson.fromJson(serializedObject, type);
        }

        String originalList = sharedPref.getString(ORIGINAL_LIST, null);
        if(originalList != null) {
            Type type2 = new TypeToken<List<Integer>>() {}.getType();
            unalteredGrid = new Gson().fromJson(originalList, type2);
        }

        String booleanList = sharedPref.getString(BOOLEAN_LIST, null);
        if(booleanList != null) {
            Type type3 = new TypeToken<List<Boolean>>() {}.getType();
            canChangeCell = new Gson().fromJson(booleanList, type3);
        }

        SharedPreferences sharedPref2 = getSharedPreferences(VARIABLES, Context.MODE_PRIVATE);
        difficulty = sharedPref2.getString(DIFFICULTY, "");
        hintsAvailable = sharedPref2.getInt(HINTS, 3);
        score = sharedPref2.getInt(SCORE, 0);
        hints = sharedPref2.getInt(HINTS, 0);
        mistakes = sharedPref2.getInt(MISTAKES, 0);
        scoreMultiplier = sharedPref2.getInt(SCORE_MULTIPLIER, 0);
        mistakesMultiplier = (double) sharedPref2.getFloat(MISTAKES_MULTIPLIER, 0);
        useXPattern = sharedPref2.getBoolean(X_MODE, false);
    }

    public void startChronometer() {
        //if (!running) {
        //stopWatch.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        SharedPreferences sharedPref = getSharedPreferences(VARIABLES, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(STOPWATCH, null);
        Long temp = Long.parseLong(serializedObject);
        stopWatch.setBase(SystemClock.elapsedRealtime() - temp);
        stopWatch.start();
        //}
    }

    public void pauseChronometer() {
        //if (running) {
        stopWatch.stop();
        pauseOffset = SystemClock.elapsedRealtime() - stopWatch.getBase();
        SharedPreferences sharedPref = getSharedPreferences(VARIABLES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(STOPWATCH, Long.toString(pauseOffset));
        editor.commit();
        //}
    }

    //This is essentially the action-listener for the buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            saveData();
            pauseChronometer();
            Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (item.getItemId() == android.R.id.home) { //When back button is pressed
            saveData();
            Toast.makeText(this, "Back arrow pressed", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
            return true;
        }
        /*else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.rules) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }*/

        else if(item.getItemId() == R.id.play_music)
            mPlayer.startMusic();
        else if(item.getItemId() == R.id.change_music)
            mPlayer.changeMusic();
        else if(item.getItemId() == R.id.pause_music)
            mPlayer.pauseMusic();
        else if(item.getItemId() == R.id.stop_music)
            mPlayer.stopMusic();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(MyAdaptor.selectedButton != null) { //Cell in the grid from the MyAdaptor class must be selected first before assigning a new number to the cell
            if (numberSelected != null) {
                numberSelected.setBackgroundColor(Color.WHITE);
                numberSelected.setPressed(false);
            }

            switch (v.getId()) {
                case R.id.resume_game_one_btn:
                    numberSelected = findViewById(R.id.resume_game_one_btn);
                    break;
                case R.id.resume_game_two_btn:
                    numberSelected = findViewById(R.id.resume_game_two_btn);
                    break;
                case R.id.resume_game_three_btn:
                    numberSelected = findViewById(R.id.resume_game_three_btn);
                    break;
                case R.id.resume_game_four_btn:
                    numberSelected = findViewById(R.id.resume_game_four_btn);
                    break;
                case R.id.resume_game_five_btn:
                    numberSelected = findViewById(R.id.resume_game_five_btn);
                    break;
                case R.id.resume_game_six_btn:
                    numberSelected = findViewById(R.id.resume_game_six_btn);
                    break;
                case R.id.resume_game_seven_btn:
                    numberSelected = findViewById(R.id.resume_game_seven_btn);
                    break;
                case R.id.resume_game_eight_btn:
                    numberSelected = findViewById(R.id.resume_game_eight_btn);
                    break;
                case R.id.resume_game_nine_btn:
                    numberSelected = findViewById(R.id.resume_game_nine_btn);
                    break;
            }

            if(numberSelected != null && MyAdaptor.selectedButton != null) {
                MyAdaptor.selectedButton.setText(numberSelected.getText().toString());
                if(!useNotes) { //False
                    MyAdaptor.selectedButton.setTextColor(Color.BLACK);
                    //MyAdaptor.selectedButton.setBackgroundColor(Color.WHITE);
                    MyAdaptor.grid.set(MyAdaptor.index, Integer.parseInt(numberSelected.getText().toString()));

                    if (Integer.parseInt(numberSelected.getText().toString()) != unalteredGrid.get(MyAdaptor.index)) {
                        updateMistakes();
                    }
                    else {
                        if(scoreMultiplier >= 0) {
                            score += (scoreMultiplier * 5);
                            scoreMultiplier++;
                            int currentScore = (int) (score - (mistakes * 50 * mistakesMultiplier));
                            scoreText.setText("Score: " + Integer.toString(currentScore));
                        }
                    }
                }
                else {
                    MyAdaptor.selectedButton.setTextColor(Color.RED);
                    //MyAdaptor.selectedButton.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
        else
            Toast.makeText(this, "Select cell in grid first", Toast.LENGTH_SHORT).show();
    }

    //Allow menu to the top toolbar to be shown
    //https://www.youtube.com/watch?v=oh4YOj9VkVE
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu); //Referring to the settings.xml in menu file
        return true;
    }

    //https://stackoverflow.com/questions/37048731/gson-library-in-android-studio
    public void saveData() {
        SharedPreferences sharedPref = getSharedPreferences(NewGame.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listGrid);
        editor.putString(NewGame.LIST, json);
        editor.commit();
    }

    public void loadData() {
        SharedPreferences sharedPref = getSharedPreferences(NewGame.SHARED_PREF, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(NewGame.LIST, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Integer>>() {
            }.getType();
            listGrid = gson.fromJson(serializedObject, type);
        }
    }*/
}