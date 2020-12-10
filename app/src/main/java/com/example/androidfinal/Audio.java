package com.example.androidfinal;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class Audio extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Toast.makeText(this, "This is a Toast message", Toast.LENGTH_LONG).show();
        Snackbar snackbar1 = Snackbar.make(findViewById(R.id.audioTitle), "Enter the name of an artist", 1);
        snackbar1.show();
        snackbar1.setAction("Erase input", new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                EditText audioInputArtist = findViewById(R.id.audioInputArtist);
                audioInputArtist.setText("");
            }
        });


    }
}
