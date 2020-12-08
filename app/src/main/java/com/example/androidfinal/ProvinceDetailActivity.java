package com.example.androidfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProvinceDetailActivity extends AppCompatActivity {

    String latitude;
    String longitude;
    String country;
    String countryCode;
    String provinceName;
    String caseNumber;
    String date;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_detail);
        //get all the date from intent
        Intent Fromprevious = getIntent();
        latitude = Fromprevious.getStringExtra("Lat");
        longitude = Fromprevious.getStringExtra("Lon");
        country = Fromprevious.getStringExtra("Country");
        provinceName = Fromprevious.getStringExtra("Province");
        countryCode = Fromprevious.getStringExtra("CountryCode");
        caseNumber = Fromprevious.getStringExtra("Cases");
        date = Fromprevious.getStringExtra("Date");
        id = Fromprevious.getLongExtra("id", -1);

        //find all the views
        TextView countryTextView = findViewById(R.id.country_text_view);
        TextView countryCodeTextView = findViewById(R.id.countrycode_text_view);
        TextView provinceTextView = findViewById(R.id.province_text_view);
        TextView caseTextView = findViewById(R.id.case_text_view);
        TextView dateTextView = findViewById(R.id.date_text_view);
        TextView latitudeTextView = findViewById(R.id.latitude_text_view);
        TextView longitudeTextView = findViewById(R.id.longitude_text_view);

        //set text and display
        countryTextView.setText(country);
        countryCodeTextView.setText(countryCode);
        provinceTextView.setText(provinceName);
        caseTextView.setText(caseNumber);
        dateTextView.setText(date);
        latitudeTextView.setText(latitude);
        longitudeTextView.setText(longitude);

    }
}