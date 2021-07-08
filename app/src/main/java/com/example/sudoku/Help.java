package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Help extends AppCompatActivity {
    //https://www.tutorialspoint.com/how-to-read-a-simple-text-file-in-android-app
    private TextView textView, xView;
    private String x_rule = "\n\nWhat is X-Sudoku? \n In an x-sudoku, the numbers 1-9 also appear once on both diagonals. " +
            "This variant is very common in sudoku magazines. " +
            "The diagonals are usually shaded as in the picture below.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button
        textView = findViewById(R.id.help_sudoku_textView);
        xView = findViewById(R.id.help_x_sudoku_textView);
        try {
            ReadTextFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //When back button is pressed
            //Toast.makeText(this, "Back arrow pressed", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ReadTextFile() throws IOException {
        String string = "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = this.getResources().openRawResource(R.raw.rules);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null)
                    break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(string).append("\n");
            //textView.setText(stringBuilder);
        }
        is.close();
        textView.setText("What is Sudoku? \n" + stringBuilder.toString());
        xView.setText(x_rule);
        Toast.makeText(getBaseContext(), stringBuilder.toString(), Toast.LENGTH_LONG).show();
    }
}