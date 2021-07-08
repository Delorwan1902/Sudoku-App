package com.example.sudoku;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment implements ScoreBoard, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button beginnerBtn, easyBtn, mediumBtn, hardBtn, expertBtn, resetBtn;
    private TextView gamesDifficultyTxt, gamesPlayedTxt, gamesWonTxt, gamesLostTxt, winRateTxt, lossRateTxt, topScoreTxt, lowScoreTxt;
    private Chronometer quicketGameChrono, longestGameChrono;
    private Map<String, Score> scoreBoardMap = new HashMap<>();
    private ProgressBar winRateBar, lossRateBar;
    private boolean canReset = false;
    private FragmentActivity context;
    private Score score = null;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        context = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Timer timer = new Timer();
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (ResetDialog.canReset && canReset) {
                        String difficulty = gamesDifficultyTxt.getText().toString().toLowerCase();
                        Score score = new Score();
                        scoreBoardMap.put(difficulty, score);
                        saveScore();
                        Button tempBtn = null;
                        switch(difficulty) {
                            case "beginner":
                                tempBtn = getView().findViewById(R.id.beginner_btn);
                                break;
                            case "easy":
                                tempBtn = getView().findViewById(R.id.easy_btn);
                                break;
                            case "medium":
                                tempBtn = getView().findViewById(R.id.medium_btn);
                                break;
                            case "hard":
                                tempBtn = getView().findViewById(R.id.hard_btn);
                                break;
                            case "expert":
                                tempBtn = getView().findViewById(R.id.expert_btn);
                                break;
                            default:
                                break;
                        }
                        if(tempBtn != null) {
                            tempBtn.performClick();
                            tempBtn.setPressed(true);
                            tempBtn.invalidate();
                            tempBtn.setPressed(false);
                            tempBtn.invalidate();
                        }
                        ResetDialog.canReset = false;
                    }
                }); //Only decrement if more than 0
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        beginnerBtn = getView().findViewById(R.id.beginner_btn);
        beginnerBtn.setOnClickListener(this);
        easyBtn = getView().findViewById(R.id.easy_btn);
        easyBtn.setOnClickListener(this);
        mediumBtn = getView().findViewById(R.id.medium_btn);
        mediumBtn.setOnClickListener(this);
        hardBtn = getView().findViewById(R.id.hard_btn);
        hardBtn.setOnClickListener(this);
        expertBtn = getView().findViewById(R.id.expert_btn);
        expertBtn.setOnClickListener(this);
        resetBtn = getView().findViewById(R.id.reset_data_btn);
        resetBtn.setOnClickListener(this);

        gamesDifficultyTxt = getView().findViewById(R.id.games_difficulty);
        gamesPlayedTxt = getView().findViewById(R.id.games_played_textView);

        gamesWonTxt = getView().findViewById(R.id.games_won_textView);
        gamesLostTxt = getView().findViewById(R.id.games_lost_textView);

        winRateBar = getView().findViewById(R.id.win_rate_progressBar);
        winRateBar.setMax(100);
        lossRateBar = getView().findViewById(R.id.loss_rate_progressBar);
        lossRateBar.setMax(100);

        winRateTxt = getView().findViewById(R.id.win_rate_textView);
        lossRateTxt = getView().findViewById(R.id.loss_rate_textView);

        topScoreTxt = getView().findViewById(R.id.top_score_textView);
        lowScoreTxt = getView().findViewById(R.id.low_score_textView);

        quicketGameChrono = getView().findViewById(R.id.quickest_game_chrono);
        longestGameChrono = getView().findViewById(R.id.longest_game_chrono);

        scoreBoardMap = loadScore();
        if(isNullOrEmpty(scoreBoardMap))
            scoreBoardMap = defaultScores();

        beginnerBtn.performClick();
        beginnerBtn.setPressed(true);
        beginnerBtn.invalidate();
        beginnerBtn.setPressed(false);
        beginnerBtn.invalidate();
    }

    public Map<String, Score> loadScore() {
        Map<String, Score> temp = new HashMap<>();
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(SCORE_BOARD, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(SCORE_MAP, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Score>>(){}.getType();
            temp = gson.fromJson(serializedObject, type);
        }
        return temp;
    }

    public void saveScore() {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(SCORE_BOARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scoreBoardMap);
        editor.putString(SCORE_MAP, json);
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.beginner_btn:
                gamesDifficultyTxt.setText("BEGINNER");
                score = getScore("beginner");
                canReset = true;
                break;
            case R.id.easy_btn:
                gamesDifficultyTxt.setText("EASY");
                score = getScore("easy");
                canReset = true;
                break;
            case R.id.medium_btn:
                gamesDifficultyTxt.setText("MEDIUM");
                score = getScore("medium");
                canReset = true;
                break;
            case R.id.hard_btn:
                gamesDifficultyTxt.setText("HARD");
                score = getScore("hard");
                canReset = true;
                break;
            case R.id.expert_btn:
                gamesDifficultyTxt.setText("EXPERT");
                score = getScore("expert");
                canReset = true;
                break;
            case R.id.reset_data_btn:
                if (canReset == true && score != null) {
                    openResetDialog();
                    //score.resetValues();
                    //canReset = false;
                }
                break;
            default:
                break;
        }
        if(score != null) {
            gamesPlayedTxt.setText(Integer.toString(score.getGamesPlayed()));
            gamesWonTxt.setText(Integer.toString(score.getGamesWon()));
            gamesLostTxt.setText(Integer.toString(score.getGamesLost()));

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            double winRate = (double) score.getGamesWon() / score.getGamesPlayed();
            winRate *= 100;
            winRateTxt.setText(df.format(winRate) + "%");
            winRateBar.setProgress((int) winRate);

            double lossRate = (double) score.getGamesLost() / score.getGamesPlayed();
            lossRate *= 100;
            lossRateTxt.setText(df.format(lossRate) + "%");
            lossRateBar.setProgress((int) lossRate);

            topScoreTxt.setText(Integer.toString(score.getTopScore()));
            lowScoreTxt.setText(Integer.toString(score.getLowScore()));

            quicketGameChrono.setBase(SystemClock.elapsedRealtime() - score.getQuickestGame());
            longestGameChrono.setBase(SystemClock.elapsedRealtime() - score.getLongestGame());
        }
    }

    public void openResetDialog() {
        if(canReset) {
            ResetDialog resetDialog = new ResetDialog(gamesDifficultyTxt.getText().toString(), scoreBoardMap);
            resetDialog.show(context.getSupportFragmentManager(), "Reset Dialog");
        }
    }

    public Score getScore(String difficulty) {
        return scoreBoardMap.get(difficulty);
    }
}