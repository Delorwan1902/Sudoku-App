package com.example.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private List<Integer> listGrid = new ArrayList<>();
    private Button resumeBtn, newGameBtn, rulesBtn, optionsBtn;
    private Button proceedBtn, cancelBtn, confirmBtn, cancelBtn2, xSudokuBtn; //proceedBtn and cancelBtn are for the pop-up warning message
    private SeekBar seekBar;
    private TextView difficultyText;
    boolean openPopupWindow = false, openDifficultySettings = false, difficultySet = false;
    private Dialog warningMessage, difficultyMessage;
    public static String[] difficultyTextOptions = {"beginner", "easy", "medium", "hard", "expert"};
    private String difficultyPicked;
    private static boolean playModeX = false;
    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(Context context) {
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseDifficulty();
        createPopup();

        xSudokuBtn = getView().findViewById(R.id.x_sudoku_btn);
        xSudokuBtn.setOnClickListener(v -> {
            playModeX = true;
            if (!openPopupWindow) { //False
                difficultyMessage.show();
            }
            else {
                warningMessage.show();
            }
        });

        newGameBtn = getView().findViewById(R.id.new_game_btn);
        newGameBtn.setOnClickListener(v -> {
            //newGame();
            playModeX = false;
            if (!openPopupWindow) { //False
                difficultyMessage.show();
                //chooseDifficulty();
            }
            else {
                warningMessage.show();
                //createPopup();
            }
        });

        resumeBtn = getView().findViewById(R.id.resume_btn);
        resumeBtn.setOnClickListener(v -> {
            resumeGame();
        });

        loadData();

        rulesBtn = getView().findViewById(R.id.rules_btn);
        rulesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Help.class);
            startActivity(intent);
        });

        optionsBtn = getView().findViewById(R.id.options_btn);
        optionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Options.class);
            startActivity(intent);
        });

        changeColours();
        //calendarView = getView().findViewById(R.id.challenge_calendarView);
        //calendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);
    }

    public void changeColours() {
        resumeBtn.setTextColor(Color.parseColor(Customisation.textColour));
        newGameBtn.setTextColor(Color.parseColor(Customisation.textColour));
        rulesBtn.setTextColor(Color.parseColor(Customisation.textColour));
        optionsBtn.setTextColor(Color.parseColor(Customisation.textColour));
    }

    public void resumeGame() {
        Intent intent = new Intent(getActivity(), ResumeGame.class);
        startActivity(intent);
        Toast.makeText(getActivity(), "Resume Game", Toast.LENGTH_SHORT).show();
    }

    public void newGame() {
        //https://stackoverflow.com/questions/11741270/android-sharedpreferences-in-fragment
        Intent intent = new Intent(getActivity(), NewGame.class);
        intent.putExtra("DIFFICULTY", difficultyPicked);
        intent.putExtra("X", playModeX);
        startActivity(intent); //Starts instance of the class in intent
    }

    public void loadData() {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(ExtraFunctions.SHARED_PREF, Context.MODE_PRIVATE);
        String serializedObject = sharedPref.getString(ExtraFunctions.LIST, null);
        if (serializedObject == null) {
            resumeBtn.setClickable(false);
            resumeBtn.getBackground().setAlpha(64); //25% transparent
        }
        else
            openPopupWindow = true;
    }

    public void chooseDifficulty() {
        difficultyMessage = new Dialog(getActivity());
        difficultyMessage.setContentView(R.layout.difficulty_settings_pop_up_box);

        difficultyText = difficultyMessage.findViewById(R.id.difficulty_textView);
        difficultyText.setText("beginner");

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
            }
        );

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

    /**
     * Message pops up when player wants to a new game when they have a previously saved game which they can resume
     */
    public void createPopup() {
        warningMessage = new Dialog(getActivity());
        warningMessage.setContentView(R.layout.pop_up_box);

        proceedBtn = warningMessage.findViewById(R.id.proceed_button);
        proceedBtn.setOnClickListener(v -> {
            openPopupWindow = false;
            difficultyMessage.show();

            //chooseDifficulty();
            //newGame();
        });
        cancelBtn = warningMessage.findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(v -> {
            openPopupWindow = true;
            warningMessage.dismiss();
        });
        //warningMessage.show();
    }
}