package com.example.sudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {
    private static Context context;
    public static List<Integer> grid, unalteredGrid;
    public static Button selectedButton = null; //Buttons 1-9 assigned functionality by the NewGame or ResumeGame class
    public static List<Boolean> canChangeCell = null, useBlackFont = null;
    public static List<Button> btnList = new ArrayList<>();
    public static Integer index = null;
    public boolean useProvidedList = false, useXPattern = false;
    public static final int[] xPattern = {0, 8, 10, 16, 20, 24, 30, 32, 40, 48, 50, 56, 60, 64, 70, 72, 80};
    public List<Button> blueCells = new ArrayList<>();

    public MyAdaptor(Context context, List<Integer> grid, List<Integer> unalteredGrid, boolean useXPattern) {
        this.context = context;
        this.grid = grid;
        this.unalteredGrid = unalteredGrid;
        canChangeCell = new ArrayList<>();
        useProvidedList = false;
        this.useXPattern = useXPattern;
        index = null;
    }

    public MyAdaptor(Context context, List<Integer> grid, List<Integer> unalteredGrid, List<Boolean> canChangeCell, boolean useXPattern) {
        this.context = context;
        this.grid = grid;
        this.unalteredGrid = unalteredGrid;
        this.canChangeCell = canChangeCell;
        useProvidedList = true;
        this.useXPattern = useXPattern;
        index = null;
    }

    public static void useHint() {
        boolean editableCell, exitLoop = false;
        while(!exitLoop) {
            for(int i = 0; i < canChangeCell.size(); i++) {
                editableCell = canChangeCell.get(i);
                if(editableCell == true) {
                    if(unalteredGrid.get(i) != grid.get(i)) {
                        grid.set(i, unalteredGrid.get(i));
                        Button temp = btnList.get(i);
                        temp.setText(Integer.toString(unalteredGrid.get(i)));
                        Toast.makeText(context, "Hints used " + unalteredGrid.get(i), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            exitLoop = true;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sudoku_cell, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int num = grid.get(position);

        if(useProvidedList == false) {
            if (num <= 0) {
                holder.btn.setText(" ");
                canChangeCell.add(position, true);
            } else {
                holder.btn.setText(Integer.toString(grid.get(position)));
                canChangeCell.add(position, false);
            }
        }
        else {  //useProvideList == true
            if(num <= 0) {
                holder.btn.setText(" ");
            }
            else {
                holder.btn.setText(Integer.toString(grid.get(position)));
            }
        }

        holder.btn.setBackgroundColor(Color.WHITE); //Make all cells in the grid have a white background
        if(useXPattern == true) {
            for (int i : xPattern)
                if (position == i)
                    holder.btn.setBackgroundColor(Color.parseColor("#2F486D"));
        }

        holder.btn.setOnClickListener(view -> {
            //Toast.makeText(context, "Button number " + position, Toast.LENGTH_SHORT).show();
            if(canChangeCell.get(position) == true) {
                if (selectedButton != null) {                       //Highlighted cell released
                    selectedButton.setBackgroundColor(Color.WHITE);
                    for(Button btn : blueCells) {
                        if(selectedButton == btn) {
                            selectedButton.setBackgroundColor(Color.parseColor("#2F486D"));
                        }
                    }
                    selectedButton.setPressed(false);
                }
                if (selectedButton != holder.btn) {
                    holder.btn.setBackgroundColor(Color.GRAY);
                    holder.btn.setPressed(true);
                    selectedButton = holder.btn;
                    index = position;
                    /*if(useNotes) {
                        selectedButton.setBackgroundColor(Color.LTGRAY);
                        selectedButton.setTextColor(Color.DKGRAY);
                    }
                    else {
                        selectedButton.setBackgroundColor(Color.GRAY);
                        selectedButton.setTextColor(Color.BLACK);
                    }*/
                } else {
                    selectedButton.setBackgroundColor(Color.WHITE);
                    for(Button btn : blueCells) {
                        if(selectedButton == btn) {
                            selectedButton.setBackgroundColor(Color.parseColor("#2F486D"));
                        }
                    }
                    selectedButton.setPressed(false);
                    selectedButton = null;
                    index = null;
                }
            }
            else
                Toast.makeText(context, "Button is immutable", Toast.LENGTH_SHORT).show();
            //NewGame.resetNumberSelected();
        });
        btnList.add(holder.btn);
        if(useXPattern == true) {
            for (int i : xPattern)
                if (position == i)
                    blueCells.add(holder.btn);
        }
        /*holder.btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });*/
    }

    public static boolean checkGridFilled() {
        for (int i : grid) {
            if(i < 1 || i > 9)
                return false;
        }
        return true;
    }

    public void selectingCell() {

    }

    @Override
    public int getItemCount() {
        return grid.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btn;
        CardView cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.button); //Referencing sudoku_cells
            cv = itemView.findViewById(R.id.cardView); //Referencing sudoku_cells
        }
    }
}
