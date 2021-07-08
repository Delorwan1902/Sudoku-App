package com.example.sudoku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResetDialog extends AppCompatDialogFragment implements ScoreBoard {
    private TextView resetWarningTxt;
    private String difficulty;
    public static boolean canReset = false;
    private static Map<String, Score> scoreBoardMap = new HashMap<>();

    public ResetDialog(String difficulty) {
        this.difficulty = difficulty;
        canReset = false;
        //loadScore();
    }

    public ResetDialog(String difficulty, Map<String, Score> scoreBoardMap) {
        this.difficulty = difficulty;
        this.scoreBoardMap = scoreBoardMap;
    }

    public void loadScore() {
        scoreBoardMap = new HashMap<>();
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(SCORE_BOARD, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(SCORE_MAP, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Score>>(){}.getType();
            scoreBoardMap = gson.fromJson(serializedObject, type);
        }
    }

    public void saveScore() {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(SCORE_BOARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scoreBoardMap);
        editor.putString(SCORE_MAP, json);
        editor.commit();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //loadScore();
        //Score score = scoreBoardMap.get(difficulty);
        //AtomicBoolean canReset = new AtomicBoolean(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.reset_score_pop_up, null);

        builder.setView(view).setTitle("Reset Stats")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    //assert score != null;
                    canReset = true;
                    dialog.dismiss();
                    //scoreBoardMap.put(difficulty, score);
                    //saveScore();
                    //dialog.dismiss();
                });
        resetWarningTxt = view.findViewById(R.id.reset_warning_textView);
        resetWarningTxt.setText("Are you sure you want to reset stats for \"" + difficulty + "\" mode?");
        return builder.create();
    }
}
