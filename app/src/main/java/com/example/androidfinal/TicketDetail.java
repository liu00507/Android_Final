package com.example.androidfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.net.URL;

public class TicketDetail extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    String name1;
    String startDateTime1;
    String priceMin1;
    String priceMax1;
    String ticketURL1;
    String image1;
    long id;
    ImageView imageView;
    ProgressBar progressBar;
    SQLiteDatabase db3;

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String ITEM_IS_SAVE = "IS_SAVE";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);


        Toolbar tBar = (Toolbar)findViewById(R.id.ticketDetailToolBar);
        setSupportActionBar(tBar);
        DrawerLayout drawer = findViewById(R.id.ticket_detail_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.ticketopen, R.string.ticketclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.ticket_detail_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get all the date from intent
        Intent fromIntent = getIntent();
        name1 = fromIntent.getStringExtra("name1");
        startDateTime1 = fromIntent.getStringExtra("startDateTime1");
        priceMin1 = fromIntent.getStringExtra("priceMin1");
        priceMax1= fromIntent.getStringExtra("priceMax1");
        ticketURL1 = fromIntent.getStringExtra("URL1");
        image1 = fromIntent.getStringExtra("image1");

        Boolean isFavourite = fromIntent.getBooleanExtra("isSaved",false);

        //find all the views
        TextView nameView = findViewById(R.id.ticket_name);
        TextView startDateView=findViewById(R.id.ticket_startDateTime);
        TextView priceMinView=findViewById(R.id.ticket_priceMin);
        TextView priceMaxView=findViewById(R.id.ticket_priceMax);
        TextView URLView=findViewById(R.id.ticket_URL);

         imageView= findViewById(R.id.ticket_image_view);
        progressBar =findViewById(R.id.image_progressBar);

        //display the image of event by event's image url
        Image img =new Image();
        progressBar.setVisibility(View.VISIBLE);
        img.execute(image1);

        nameView.setText(name1);
        startDateView.setText(startDateTime1);
        priceMinView.setText(priceMin1);
        priceMaxView.setText(priceMax1);
        URLView.setText(ticketURL1);

        Ticket ticket =new Ticket(nameView.getText().toString(),startDateView.getText().toString(),priceMinView.getText().toString(),priceMaxView.getText().toString(),URLView.getText().toString(),image1);


        Button saveButton=findViewById(R.id.buttonDetail);
        if(isFavourite) {
            saveButton.setText("REMOVE");
        }else {
            saveButton.setText("SAVE");
        }
        saveButton.setOnClickListener(click->{
            if(isFavourite) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Save Request")
                        .setMessage("Do you want to delete this from  your favourite? ")
                        .setPositiveButton("YES", (c, arg) -> {
                            DeleteTicket(ticket);
                            this.setResult(Activity.RESULT_OK);
                            this.finish();

                        })
                        .setNegativeButton("No", (c, arg) -> {
                            this.setResult(RESULT_CANCELED);
                        })
                        .create().show();
            }else{
                InsertTickect(ticket);
                Context context =this.getApplicationContext();
                Toast toast = Toast.makeText(context, "Save successful", Toast.LENGTH_LONG );
                toast.show();
            }






        });




    }


/*
* delete the events from database by events's start date*/
    public void DeleteTicket(Ticket ticket){
        MyTicketOpener dbOpener=new MyTicketOpener(this);
        db3=dbOpener.getWritableDatabase();
        db3.delete(MyTicketOpener.TABLE_NAME,MyTicketOpener.COL_START_DATE_TIME+"=?",new String[]{ticket.getStartDateTime()});
        db3.close();

    }
/*
* insert the details of event to database */
    protected void InsertTickect(Ticket ticket ){
        MyTicketOpener dbOpener=new MyTicketOpener(this);
        db3=dbOpener.getWritableDatabase();
        ContentValues newRowValues =new ContentValues();

        newRowValues.put(MyTicketOpener.COL_NAME, ticket.getName());
        newRowValues.put(MyTicketOpener.COL_START_DATE_TIME, ticket.getStartDateTime());

        newRowValues.put(MyTicketOpener.COL_PRICE_MAX, ticket.getPriceMax());
        newRowValues.put(MyTicketOpener.COL_PRICE_MIN, ticket.getPriceMin());
        newRowValues.put(MyTicketOpener.COL_URL, ticket.getURL());
        newRowValues.put(MyTicketOpener.COL_IMAGE, ticket.getImage());


        db3.insert( MyTicketOpener.TABLE_NAME, null, newRowValues );
        db3.close();


    }


/*
* get the image by imgae url*/
    private class Image extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            progressBar.setProgress(100);
            return getBitmapFromURL(url);

        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap (result);
            super.onPostExecute(result);
        }
    }

    private static Bitmap getBitmapFromURL(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }

        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.ticket_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String message=null;

        switch(item.getItemId()){

            case R.id.homeItem:
                startActivity(new Intent(TicketDetail.this, MainMenu.class));
                message="Go to Home Page";
                break;



            case R.id.manual:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.ticket_help_text)
                        .setMessage(R.string.ticket_detail_help_content)
                        .setPositiveButton(R.string.ticket_yes, (c, arg) -> {

                        })
                        .create().show();
                message="User Manual";
                break;

            case R.id.helpItem:
                message="TicketMaster event search By Chi Luo ";
                break;
        }
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){

        String message=null;
        switch(item.getItemId()){
            //Go to Main Page
            case R.id.homeItem:
                startActivity(new Intent(TicketDetail.this, MainMenu.class));
                message="Go to Home Page";
                break;



            case R.id.manual:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.ticket_help_text)
                        .setMessage(R.string.ticket_detail_help_content)
                        .setPositiveButton(R.string.ticket_yes, (c, arg) -> {

                        })
                        .create().show();
                message="User Manual";
                break;

            case R.id.helpItem:
                message="TicketMaster event search By Chi Luo ";
                break;
        }

        DrawerLayout drawerLayout=findViewById(R.id.ticket_detail_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();



        return false;
    }


}