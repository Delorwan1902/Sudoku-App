package com.example.sudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Chronometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtraFunctions extends AppCompatActivity {
    protected List<Integer> listGrid = new ArrayList<>();
    public static final String SHARED_PREF = "sharedPref"; //Store the data of the Sudoku numbers
    public static final String ORIGINAL_GRID = "originalGrid";
    public static final String LIST = "list"; //Variable where the Sudoku is referenced
    public static final String ORIGINAL_LIST = "originalList", BOOLEAN_LIST = "booleanList";

    public static final String VARIABLES = "variables", STOPWATCH = "stopwatch", DIFFICULTY = "difficulty",
            SCORE = "score", SCORE_MULTIPLIER = "scoreMultiplier", MISTAKES_MULTIPLIER = "mistakesMultiplier", X_MODE = "xMode";

    public static final String OPTIONS = "options", MISTAKES = "mistakes", SCORE_FIELD = "scoreField", TIME = "time", HINTS = "hints", MUSIC = "music";

    public static List<Boolean> canChangeCell = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu); //Referring to the settings.xml in menu file
        return true;
    }

    public static boolean validateGrid(int[][] grid) {
        Set<String> seen = new HashSet<>(); //No duplicates are allowed in a set
        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[0].length; j++)
                if(!seen.add(grid[i][j] + "in row " + i)
                        || !seen.add(grid[i][j] + "in column " + j)
                        || !seen.add(grid[i][j] + "in block " + i/3 + ", " + j/3))
                    return false;
        return true;
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

    //https://stackoverflow.com/questions/37048731/gson-library-in-android-studio
    /*public void saveData() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listGrid);
        editor.putString(LIST, json);
        //editor.putString(STOPWATCH, Long.toString(pauseOffset));
        editor.commit();
    }*/

    public void loadData(){
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(LIST, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Integer>>() {
            }.getType();
            listGrid = gson.fromJson(serializedObject, type);
        }
    }
}
