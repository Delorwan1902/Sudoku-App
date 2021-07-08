package com.example.sudoku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ResetBoardDialog extends AppCompatDialogFragment {

    public ResetBoardDialog() {

    }

    public void resetGrid() {
        int index = 0;
        for(boolean i : MyAdaptor.canChangeCell) {
            if(i) {
                Button temp = MyAdaptor.btnList.get(index);
                temp.setText(" ");
                MyAdaptor.grid.set(index, 0);
            }
            index++;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.reset_board_pop_up, null);

        builder.setView(view).setTitle("Reset Board")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    resetGrid();
                    //dialog.dismiss();
                });
        //resetWarningTxt = view.findViewById(R.id.reset_warning_textView);
        //resetWarningTxt.setText("Are you sure you want to reset stats for \"" + difficulty + "\" mode?");
        return builder.create();
    }
}
