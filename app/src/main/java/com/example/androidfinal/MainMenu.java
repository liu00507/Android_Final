package com.example.androidfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToCovidBtn = findViewById(R.id.btn_to_Covid19Case);
        Intent GoToCovidActivityIntent = new Intent(MainMenu.this, Covid19Case.class);
        goToCovidBtn.setOnClickListener(click -> {
            Toast.makeText(MainMenu.this, getResources().getString(R.string.covid_toast_message), Toast.LENGTH_LONG).show();
            startActivity(GoToCovidActivityIntent);
        });

        Button goToReceipeBtn = findViewById(R.id.btn_to_RecipeSearch);
        Intent GoToReceipeReceipeActivityIntent = new Intent(MainMenu.this, MainActivityReceipe.class);
        goToReceipeBtn.setOnClickListener(click -> {
           // Toast.makeText(MainMenu.this, getResources().getString(R.string.covid_toast_message), Toast.LENGTH_LONG).show();
            startActivity(GoToReceipeReceipeActivityIntent);
        });
    }
}