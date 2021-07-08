package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NewGame extends ExtraFunctions implements View.OnClickListener {
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private int[][] grid = new int[9][9];
    private List<Integer> unalteredGrid = new ArrayList<>();
    private Map<String, Score> scoreMap = new HashMap<>();
    private MusicPlayer mPlayer;
    protected Chronometer stopWatch;
    private long pauseOffset = 0;
    public static Button numberSelected = null;
    private TextView difficultyText, mistakesText, scoreText, countBadgeText;
    private String gameDifficulty;
    private Button submitBtn;
    private CardView countBadgeIcon;
    private ConstraintLayout countBadgeLayout;
    private ImageButton undoBtn, eraseBtn, notesBtn, hintsBtn;
    private int mistakes = 0, scoreMultiplier = 21, score = 0, hintsAvailable = 3;
    private double mistakesMultiplier = 0;
    private boolean limitMistakes = true, showScore = true, showTimer = true, playMusic = true, modeX = false, useXPattern = false;
    private static boolean useNotes = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        getSupportActionBar().setTitle("Sudoku"); //Set title in the app bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button

        SharedPreferences pref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE); //Remove saved data on the app since we are starting a new game
        pref.edit().clear().commit(); //Clear and commit on the same line

        loadSettings(); //limitMistakes, showScore & showTimer
        context = this;

        mistakesText = findViewById(R.id.new_game_mistakes_textView);
        if (!limitMistakes)
            mistakesText.setVisibility(View.INVISIBLE);

        scoreText = findViewById(R.id.new_game_score_textView);
        if(!showScore)
            scoreText.setVisibility(View.INVISIBLE);

        setDifficultyText();
        stopWatch = findViewById(R.id.new_game_chronometer);
        startChronometer();
        if(!showTimer)
            stopWatch.setVisibility(View.INVISIBLE);

        Timer timer = new Timer();
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    scoreMultiplier = scoreMultiplier > 0 ? scoreMultiplier -= 1 : scoreMultiplier;
                    mistakesMultiplier += 0.25;
                }); //Only decrement if more than 0
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 20000);
        //grid = new int[9][9];
        modeX = getIntent().getBooleanExtra("X", false);

        if(modeX == false) {
            //grid = generateSudoku();
            grid = SudokuGenerator.convertTo2DArray(SudokuGenerator.generateGrid());
            for(int[] i: grid) { //Place values from array into list
                for (int j : i) {
                    listGrid.add(j);
                    unalteredGrid.add(j);
                }
            }
        }
        else { //Playing X-Sudoku
            listGrid.addAll(SudokuXData.generateX());
            useXPattern = true;
            //unalteredGrid.addAll(listGrid);
            for(int i : listGrid) {
                unalteredGrid.add(i);
            }
            Toast.makeText(this, "X Mode activated", Toast.LENGTH_SHORT).show();
        }
        //grid = generateNumbers(grid); //Create the the grid where each box has the numbers 1 to 9

        listGrid = eraseCells(listGrid);
        MyAdaptor myAdaptor = new MyAdaptor(this, listGrid, unalteredGrid, useXPattern);
        myAdaptor.selectedButton = null;
        myAdaptor.index = null;

        recyclerView = findViewById(R.id.new_game_recycler_view);
        recyclerView.setAdapter(myAdaptor);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(5));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 9));

        countBadgeIcon = findViewById(R.id.new_game_count_badge_cardView);
        countBadgeText = findViewById(R.id.new_game_count_badge_textView);
        countBadgeText.setText(Integer.toString(hintsAvailable));

        submitBtn = findViewById(R.id.new_game_submit_btn);
        submitBtn.setOnClickListener(v -> {
            if(myAdaptor.checkGridFilled()) {
                int[][] to2DArray = listTo2DArray(MyAdaptor.grid, 9);
                if(validateGrid(to2DArray)) {
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
        imagesButtons();
    }

    public void imagesButtons() {
        undoBtn = findViewById(R.id.new_game_undo_btn);
        undoBtn.setOnClickListener(v -> {
            if(MyAdaptor.selectedButton != null && MyAdaptor.grid.get(MyAdaptor.index) != 0) {
                MyAdaptor.grid.set(MyAdaptor.index, 0);
                MyAdaptor.selectedButton.setText(" ");
            }
        });
        notesBtn = findViewById(R.id.new_game_notes_btn);
        notesBtn.setOnClickListener(v -> {
            useNotes = !useNotes;
            //Toast.makeText(this, "Notes: " + useNotes, Toast.LENGTH_SHORT).show();
            int[] arr = new int[] {R.id.new_game_one_btn, R.id.new_game_two_btn, R.id.new_game_three_btn,
                    R.id.new_game_four_btn, R.id.new_game_five_btn, R.id.new_game_six_btn,
                    R.id.new_game_seven_btn, R.id.new_game_eight_btn, R.id.new_game_nine_btn};
            if(useNotes) {
                for(int i : arr) {
                    Button temp = findViewById(i);
                    //temp.setTextColor(Color.parseColor("#2F486D"));
                    temp.setTextColor(Color.RED);
                    //temp.setBackgroundColor(Color.WHITE);
                }
            }
            else {                                      //Deactivate notes mode - switch button to original state
                for(int i : arr) {
                    Button temp = findViewById(i);
                    temp.setTextColor(Color.BLACK);
                    //temp.setBackgroundColor(Color.WHITE);
                }
            }
        });
        eraseBtn = findViewById(R.id.new_game_erase_btn);
        eraseBtn.setOnClickListener(v -> {
            //resetGrid();
            ResetBoardDialog resetDialog = new ResetBoardDialog();
            resetDialog.show(getSupportFragmentManager(), "Reset Board Dialog");
        });
        hintsBtn = findViewById(R.id.new_game_hints_btn);
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
        /*String str = countBadgeText.getText().toString();
        if(str != null && !str.trim().isEmpty()) {
            int count = Integer.parseInt(str);
            if (count > 1) {
                count--;
                countBadgeText.setText(Integer.toString(count));
            }
            else {
                countBadgeText.setText("");
                countBadgeText.setVisibility(View.INVISIBLE);
                countBadgeIcon.setVisibility(View.INVISIBLE);
            }
        }*/

        if(hintsAvailable != 0) {
            hintsAvailable--;
            countBadgeText.setText(Integer.toString(hintsAvailable));
            MyAdaptor.useHint();

            if(hintsAvailable == 0) {
                countBadgeText.setText("");
                countBadgeText.setVisibility(View.INVISIBLE);
                countBadgeIcon.setVisibility(View.INVISIBLE);
            }
        }
        /*else {  //Turn badge invisible is the player uses up all available hint points
            if(hintsAvailable == 0)
                hintsAvailable = 0;
            countBadgeText.setText("");
            countBadgeText.setVisibility(View.INVISIBLE);
            countBadgeIcon.setVisibility(View.INVISIBLE);
        }*/
    }

    /**
     * Load saves pre-settings from the settings page
     */
    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences(OPTIONS, Context.MODE_PRIVATE);
        limitMistakes = sharedPref.getBoolean(MISTAKES, true);
        showScore = sharedPref.getBoolean(SCORE_FIELD, true);
        showTimer = sharedPref.getBoolean(TIME, true);
        playMusic = sharedPref.getBoolean(MUSIC, true);
    }

    public void calculateFinalScore() {
        score = (int) (score - (mistakes * 50 * mistakesMultiplier));
    }

    public void endGameMenu(long time) {
        Intent intent = new Intent(this, EndGameMenu.class);
        calculateFinalScore();

        intent.putExtra("time", Long.toString(time));
        intent.putExtra("difficulty", gameDifficulty);
        intent.putExtra("score", Integer.toString(score));
        intent.putExtra("mistakes", Integer.toString(mistakes));
        intent.putExtra("X", modeX);
        if(mistakes < 3) {
            intent.putExtra("win", true);
            Toast.makeText(this, "You Won!", Toast.LENGTH_SHORT).show();
        }
        else {
            intent.putExtra("win", false);
        }
        startActivity(intent); //Starts instance of the class in intent
    }

    public int[][] generateSudoku() {
        int k = 0;
        int fillCount = 1;
        int subGrid = 1;
        int N = 3;
        int[][] sudokuGrid = new int[N * N][N * N];
        for (int i = 0; i < N * N; i++) {
            if (k == N) {
                k = 1;
                subGrid++;
                fillCount = subGrid;
            }
            else {
                k++;
                if (i != 0)
                    fillCount = fillCount + N;
            }
            for (int j = 0; j < N * N; j++) {
                if (fillCount == N * N) {
                    sudokuGrid[i][j] = fillCount;
                    fillCount = 1;
                }
                else
                    sudokuGrid[i][j] = fillCount++;
            }
        }
        return sudokuGrid;
    }

    public List<Integer> eraseCells(List<Integer> grid) {
        canChangeCell = new ArrayList<>();
        for(int i = 0; i < grid.size(); i++)
            canChangeCell.add(false);

        List<Integer> temp = grid;
        int noOfCellsErased;
        switch(gameDifficulty) {
            case "easy":
                noOfCellsErased = 15;
                break;
            case "medium":
                noOfCellsErased = 20;
                break;
            case "hard":
                noOfCellsErased = 25;
                break;
            case "expert":
                noOfCellsErased = 35;
                break;
            default:
                noOfCellsErased = 10;
                break;
        }
        while(noOfCellsErased > 0) {
            Random random = new Random();
            int index = random.nextInt(grid.size());
            if(temp.get(index) != 0) {
                temp.set(index, 0);
                noOfCellsErased--;
                canChangeCell.add(index, true);
            }
        }
        return temp;
    }

    public void setDifficultyText() {
        gameDifficulty = getIntent().getStringExtra("DIFFICULTY");
        difficultyText = findViewById(R.id.new_game_difficulty_textView);
        difficultyText.setText(gameDifficulty.substring(0, 1).toUpperCase() + gameDifficulty.substring(1));
    }

    public void updateMistakes() {
        if(limitMistakes == true) {
            mistakes++;
            String string = "Mistakes: " + mistakes + "/3";
            mistakesText.setText(string);

            if(mistakes >= 3) {
                pauseChronometer();
                long elapsedMillis = SystemClock.elapsedRealtime() - stopWatch.getBase();
                endGameMenu(elapsedMillis);
            }
        }
    }

    /**
     * Stopwatch is saved under pauseChronometer() method under STRINGS
     */
    public void saveData() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listGrid);
        editor.putString(LIST, json);
        editor.putString(ORIGINAL_LIST, new Gson().toJson(unalteredGrid));
        editor.putString(BOOLEAN_LIST, new Gson().toJson(MyAdaptor.canChangeCell));
        editor.commit();

        sharedPref = getSharedPreferences(VARIABLES, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putInt(SCORE, score);
        editor.putInt(MISTAKES, mistakes);
        editor.putInt(HINTS, hintsAvailable);
        editor.putInt(SCORE_MULTIPLIER, scoreMultiplier);
        editor.putFloat(MISTAKES_MULTIPLIER, (float) mistakesMultiplier);
        editor.putString(DIFFICULTY, difficultyText.getText().toString());
        editor.putBoolean(X_MODE, useXPattern);
        editor.commit();
    }

    /**
     * Start the stopwatch
     * Different from the method of the same name in the ResumeGame class
     * */
    public void startChronometer() {
        //if (!running) {
        stopWatch.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        stopWatch.start();
        //}
    }

    /**
     * Stop the stopwatch
     * */
    public void pauseChronometer() {
        stopWatch.stop();
        pauseOffset = SystemClock.elapsedRealtime() - stopWatch.getBase();
        SharedPreferences sharedPref = getSharedPreferences(VARIABLES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(STOPWATCH, Long.toString(pauseOffset));
        editor.commit();
    }

    /**
     * Add functionality for some of the buttons
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save) { //Executes when the SAVE button is pressed
            saveData();
            pauseChronometer();
        }
        else if(item.getItemId() == android.R.id.home) { //Executes when the back button is pressed
            saveData();
            finish();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
        }
        else if(item.getItemId() == R.id.play_music) //Executes when the Play Music button is pressed
            mPlayer.startMusic();
        else if(item.getItemId() == R.id.change_music) //Executes when the Change Music button is pressed
            mPlayer.changeMusic();
        else if(item.getItemId() == R.id.pause_music) //Executes when the Pause Music button is pressed
            mPlayer.pauseMusic();
        else if(item.getItemId() == R.id.stop_music) //Executes when the Stop Music button is pressed
            mPlayer.stopMusic();
        return super.onOptionsItemSelected(item);
    }

    //Allow menu to the top toolbar to be shown
    //https://www.youtube.com/watch?v=oh4YOj9VkVE
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }
    //https://stackoverflow.com/questions/37048731/gson-library-in-android-studio*/

    //https://github.com/mfgravesjr/finished-projects/blob/master/SudokuGridGenerator/SudokuGridGenerator.java
    public static int[][] generateNumbers(int[][] grid) {
        List<Integer> values = new ArrayList<>(9);

        for(int i = 1; i <= 9; i++)
            values.add(i);

        int rowNo = 0, colNo = 0;
        for (int i = 0; i < grid.length; i++) {
            if (i % 3 == 0 && i != 0) {
                rowNo += 3;
                colNo -= 9;
            }
            Collections.shuffle(values);
            for (int j = 0; j < grid[0].length; j++) {
                grid[rowNo][colNo] = values.get(j);
                if ((colNo + 1) % 3 == 0) {
                    colNo -= 2; //not -2 because of the colNo++ below
                    rowNo++; //Move to next row in the block
                    continue;
                }
                colNo++;
            }
            rowNo -= 3; //Not -2 because of rowNo++ above continue;
            colNo += 3;
        }
        return grid;
    }

    public static int[][] eraseCells(int[][] grid, int numOfBlocks) {
        Random random = new Random();
        int row, column, numOfEmptyCells = 0 , limit = 0;
        switch (numOfBlocks) {
            case 4:
                limit = 6;
                break;
            case 9:
                limit = 9;
                break;
        }
        for (int i = 0; i < numOfEmptyCells; i++) {
            row = random.nextInt(limit);
            column = random.nextInt(limit);
            if(grid[row][column] != 9)
                grid[row][column] = 9;
            else
                i--;
        }
        return grid;
    }

    public static void resetNumberSelected() {
        if(numberSelected != null) {
            numberSelected.setBackgroundColor(Color.WHITE);
            numberSelected.setPressed(false);
            numberSelected = null;
        }
    }

    public void resetGrid() {
        int index = 0;
        for(boolean i : MyAdaptor.canChangeCell) {
            if(i) {
                Button temp = MyAdaptor.btnList.get(index);
                temp.setText(" ");
                //MyAdaptor.btnList.set(index, temp);
                MyAdaptor.grid.set(index, 0);
                Toast.makeText(this, "method working " + index, Toast.LENGTH_SHORT).show();
            }
            index++;
        }
    }

    /**
     * Method assigns functionality to the buttons 1-9
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(MyAdaptor.selectedButton != null) { //refers to the cell selected in the grid - A sudoku cell must be selected before proceeding
            if (numberSelected != null) { //Reset the number button as it was used in the previous action
                //numberSelected.setBackgroundColor(Color.WHITE);
                numberSelected.setPressed(false);
            }
            //numberSelected = null;

            switch(v.getId()) {
                case R.id.new_game_one_btn:
                    //if(numberSelected == findViewById(R.id.new_game_one_btn))
                    //return;
                    numberSelected = findViewById(R.id.new_game_one_btn);
                    break;
                case R.id.new_game_two_btn:
                    numberSelected = findViewById(R.id.new_game_two_btn);
                    break;
                case R.id.new_game_three_btn:
                    numberSelected = findViewById(R.id.new_game_three_btn);
                    break;
                case R.id.new_game_four_btn:
                    numberSelected = findViewById(R.id.new_game_four_btn);
                    break;
                case R.id.new_game_five_btn:
                    numberSelected = findViewById(R.id.new_game_five_btn);
                    break;
                case R.id.new_game_six_btn:
                    numberSelected = findViewById(R.id.new_game_six_btn);
                    break;
                case R.id.new_game_seven_btn:
                    numberSelected = findViewById(R.id.new_game_seven_btn);
                    break;
                case R.id.new_game_eight_btn:
                    numberSelected = findViewById(R.id.new_game_eight_btn);
                    break;
                case R.id.new_game_nine_btn:
                    numberSelected = findViewById(R.id.new_game_nine_btn);
                    break;
            }

            if(numberSelected != null && MyAdaptor.selectedButton != null) {
                //Random rnd = new Random();
                //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                //numberSelected.setBackgroundColor(color);
                //numberSelected.setPressed(true);
                //Toast.makeText(this, numberSelected.getText().toString(), Toast.LENGTH_SHORT).show();

                //MyAdaptor.selectedButton
                //Toast.makeText(this, "Grid value: " + MyAdaptor.grid.get(MyAdaptor.index), Toast.LENGTH_SHORT).show();

                //MyAdaptor.grid.set(MyAdaptor.index, Integer.parseInt(numberSelected.getText().toString()));
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
                //MyAdaptor.selectedButton.setBackgroundColor(Color.WHITE);
                //Toast.makeText(this, "Grid update: " + MyAdaptor.grid.get(MyAdaptor.index), Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Select cell in grid first", Toast.LENGTH_SHORT).show();
    }
}