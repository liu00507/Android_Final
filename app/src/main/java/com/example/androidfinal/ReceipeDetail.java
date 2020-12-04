package com.example.androidfinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receipeSearch.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReceipeDetail extends AppCompatActivity {


    //    Textview for title
    TextView titleview;
    //Textview for href
    TextView hrefview;
    //TextView for ingredients
    TextView ingview;
    //Button for saving or removing
    Button button;
    // Varible for storing in local satorag
    public static final String RECIEPE = "RECIEPE";
    SharedPreferences sharedPreferences;

    // Arraylist for data
//    ArrayList<ReceipeModel> dataModels;
    // Varible to check if it is favourite or not
    int inside=0;
    // Oncreate for Detail activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipe_detail);
        // Lines to add back arrrow at toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Get the shared prefrence editor
        sharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        // Fetch the list in string
        String connectionsJSONString = sharedPreferences.getString(RECIEPE, null);
        if(connectionsJSONString!= null){
            Log.d("Shared Prefrences",connectionsJSONString);
        }else{
            Log.d("Shared Prefrences","Null");
        }

        // intialist the list of datamodel
//        dataModels= new ArrayList<>();
        // Get the title from intent
        final String title = getIntent().getExtras().getString("title");
        // Get the ingredients from intent
        final String ingredients = getIntent().getExtras().getString("ingredients");
        // Get the href from intent
        final String href = getIntent().getExtras().getString("href");
        // Get the thumnail from intent
        final String thumnail = getIntent().getExtras().getString("thumnail");
        // Check if image is present
        if(thumnail.length() > 0){
            // Show image
            ImageView ivBasicImage = (ImageView) findViewById(R.id.imageviewdetail);
            Picasso.get().load(thumnail).into(ivBasicImage);
        }
        // If there is favourites check for it
        if(connectionsJSONString != null) {
            Type type = new TypeToken<List<ReceipeModel>>() {
            }.getType();
            List<ReceipeModel> connections = new Gson().fromJson(connectionsJSONString, type);
            for (ReceipeModel con : connections) {
                Log.d("Titles",con.getTitle());
                if(con.getTitle().equals(title)){
                    // Got the favourite
                    Log.d("Got the Same","Yes");
                    inside = 1;
                    //break now
                    break;
                }
            }
        }
        // initialie the title textview
        titleview = (TextView) findViewById(R.id.textviewtitle);
        // Set the value of title textview
        titleview.setText(title);
        // initialie the href textview
        hrefview = (TextView)findViewById(R.id.textviewhref);
        // Set the value of href textview
        hrefview.setText(href);
        // initialie the ingredients textview
        ingview = (TextView)findViewById(R.id.textviewing);
        // Set the value of ingredients textview
        ingview.setText("Ingredients : " + ingredients);
        button = (Button)findViewById(R.id.buttondetail);
        // Change the text of button depending upon the favourites list
        if(inside == 0){
            // Set text to save
            button.setText("Save");
        }else{
            // set text to remove
            button.setText("Remove");
        }
        // Function triggers on click of button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inside == 0){
                    //Save
                    addReceipe(editor,title,href,ingredients,thumnail);
                }else{
                    //Remove
                    removeReceive(editor,title);
                }
            }
        });

    }
    // This is inbuilt function for adding back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    // Function to add receipe
    public void addReceipe(SharedPreferences.Editor editor, String title, String href, String ingredients, String thumnail){
        // Create object
        ReceipeModel pmm = new ReceipeModel(title,href,ingredients,thumnail);
        // Get the  list in string
        String connectionsJSONString = sharedPreferences.getString(RECIEPE, null);
//        Log.d("String",connectionsJSONString);
        // Check if list id empty
        if(connectionsJSONString!= null){
            Log.d("Connection",connectionsJSONString);
            // Convert the string to list
            Type type = new TypeToken< List < ReceipeModel >>() {}.getType();
            List < ReceipeModel > connections = new Gson().fromJson(connectionsJSONString, type);
            // add model to list
            connections.add(pmm);
            //convert list to string again
            connectionsJSONString = new Gson().toJson(connections);
            Log.d("Connection Add",connectionsJSONString);
        }else{
            List < ReceipeModel > connections = new ArrayList<>();
            // add model to list
            connections.add(pmm);
            //convert list to string again
            connectionsJSONString = new Gson().toJson(connections);
            Log.d("Connection Add",connectionsJSONString);
        }
//        // Added item to fav
        inside = 1;
//        // Set text to remove
        button.setText("Remove");
//        // Savethe SP editor
        editor.putString(RECIEPE, connectionsJSONString);
        editor.commit();
    }
    // Remove Receipe function
    public void removeReceive(SharedPreferences.Editor editor, String title){
        // Get the  list in string
        String connectionsJSONString = sharedPreferences.getString(RECIEPE, null);
        // Check if list id empty
        if(connectionsJSONString!= null){
            Log.d("Connection",connectionsJSONString);
            // Convert the string to list
            Type type = new TypeToken< List < ReceipeModel >>() {}.getType();
            List < ReceipeModel > connections = new Gson().fromJson(connectionsJSONString, type);
            // Create new list
            List<ReceipeModel> newList = new ArrayList<>();
            for(ReceipeModel con : connections){
                // Add all items to list except given title item
                if(!con.getTitle().equals(title)){
                    newList.add(con);
                }
            }
            //convert list to string again
            connectionsJSONString = new Gson().toJson(newList);

        }
        // Removed item to fav
        inside = 0;
        // Set text to save
        button.setText("Save");
        // Savethe SP editor
        editor.putString(RECIEPE, connectionsJSONString);
        editor.commit();
    }
}