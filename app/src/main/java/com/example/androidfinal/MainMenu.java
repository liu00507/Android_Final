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
        Button goToAudio = findViewById(R.id.btn_to_AudioApi);
        Intent GoToCovidActivityIntent = new Intent(MainMenu.this, Covid19Case.class);
        Intent GoToAudioActivityIntent = new Intent(MainMenu.this, Audio.class);
        goToCovidBtn.setOnClickListener(click -> {
            Toast.makeText(MainMenu.this, getResources().getString(R.string.covid_toast_message), Toast.LENGTH_LONG).show();
            startActivity(GoToCovidActivityIntent);
        });
        goToAudio.setOnClickListener(click->{
            Toast.makeText(MainMenu.this, getResources().getString(R.string.audio_toast_message), Toast.LENGTH_LONG).show();
            startActivity(GoToAudioActivityIntent);
        });
    }
}