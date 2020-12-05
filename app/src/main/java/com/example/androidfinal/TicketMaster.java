package com.example.androidfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TicketMaster extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    public boolean isSaved;
    private SharedPreferences prefs;
    private TicketListAdapter ticketListAdapter;
    private List<Ticket> ticketList = new ArrayList<>();
    static final int REQUEST_FAVORITE_EDIT = 1;

    ProgressBar progressBar;
    SQLiteDatabase db3;


    ListView ticketListView;
    Intent gotoTicketDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);

        /*
        put and set toolbar:
         */
        Toolbar tBar = (Toolbar)findViewById(R.id.ticketToolBar);
        setSupportActionBar(tBar);
          /*
        put navigation view:
         */
        DrawerLayout drawer = findViewById(R.id.ticket_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.ticketopen, R.string.ticketclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.ticket_nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /*
        save into query
         */
        prefs = getSharedPreferences("ticket", Context.MODE_PRIVATE);
        String city = prefs.getString("city", "");
        String radius = prefs.getString("radius", "");
        EditText cityInput = findViewById(R.id.cityText);
        EditText radiusInput = findViewById(R.id.radiusText);
        cityInput.setText(city);


        ticketListView = findViewById(R.id.ticket_list);
        ticketListView.setAdapter(ticketListAdapter = new TicketListAdapter());

        /*ticket details*/
        gotoTicketDetail = new Intent(TicketMaster.this, TicketDetail.class);

          /*
        add and display progress bar
         */
        progressBar =findViewById(R.id.progress_horizontal);

        //function of search button
        Button searchButton = findViewById(R.id.ticket_search);
        searchButton.setOnClickListener(click -> {
            String cityIn = cityInput.getText().toString();
            String radiusIn = radiusInput.getText().toString();
            saveArguments(cityIn, radiusIn);
            AquireData(cityIn, radiusIn);
            ticketListAdapter.notifyDataSetChanged();
             isSaved = false;

        });
        //function of search saved events
        Button farvouriteButton=findViewById(R.id.ticket_collection);
        farvouriteButton.setOnClickListener(bt ->
        {
            loadDataFromDatabase();
            ticketListAdapter.notifyDataSetChanged();
            isSaved = true;



        });


        /* display ticket details when select item from viewlist*/
        ticketListView.setOnItemClickListener((parent, view, position, id) -> {
            Ticket ticket = ticketList.get(position);
            gotoTicketDetail.putExtra("name1", ticket.getName());
            gotoTicketDetail.putExtra("startDateTime1", ticket.getStartDateTime());
            gotoTicketDetail.putExtra("priceMin1", ticket.getPriceMin());
            gotoTicketDetail.putExtra("priceMax1", ticket.getPriceMax());
            gotoTicketDetail.putExtra("URL1", ticket.getURL());
            gotoTicketDetail.putExtra("image1", ticket.getImage());
            gotoTicketDetail.putExtra("isSaved",isSaved);

            if(isSaved){ startActivityForResult(gotoTicketDetail, REQUEST_FAVORITE_EDIT);
            }else { startActivity(gotoTicketDetail); }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_FAVORITE_EDIT && resultCode == RESULT_OK) {
            loadDataFromDatabase();
            ticketListAdapter.notifyDataSetChanged();
        }
    }

    private void saveArguments(String city, String radius) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("city", city);
        edit.putString("radius", radius);
        edit.commit();
    }

    /*
    parse and convert date
 */
    private void AquireData(String city, String radius) {
        ticketList.clear();
        progressBar.setVisibility(View.VISIBLE);
        TicketQuery ticketQuery = new TicketQuery();
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=Ltn3KoND3sJFSXCkSS3Vbg6B77VzG3mU&city=" + city + "&radius=" + radius;
        ticketQuery.execute(url);
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
                startActivity(new Intent(TicketMaster.this, MainMenu.class));
                message="Go to Home Page";
                break;



            case R.id.manual:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.ticket_help_text)
                        .setMessage(R.string.ticket_help_content)
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
    /* Needed for
     *the OnNavigationItemSelected interface
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item){

        String message=null;
        switch(item.getItemId()){
            //Go to Main Page
            case R.id.homeItem:
                startActivity(new Intent(TicketMaster.this, MainMenu.class));
                message="Go to Home Page";
                break;



            case R.id.manual:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.ticket_help_text)
                        .setMessage(R.string.ticket_help_content)
                        .setPositiveButton(R.string.ticket_yes, (c, arg) -> {

                        })
                        .create().show();
                message="User Manual";
                break;

            case R.id.helpItem:
                message="TicketMaster event search By Chi Luo ";
                break;
        }

        DrawerLayout drawerLayout=findViewById(R.id.ticket_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();



        return false;
    }



    private class TicketListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ticketList.size();
        }

        @Override
        public Object getItem(int position) {
            return ticketList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ticketList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Ticket ticket = (Ticket) getItem(position);
            LayoutInflater inflater = getLayoutInflater();

            //make a new row
            View rowView = inflater.inflate(R.layout.row_tickect_layout, parent, false);
            TextView nameView = rowView.findViewById(R.id.textDisplayName);
            nameView.setText(ticket.getName());
            return rowView;
        }
    }

    /*
     * load tickets's details from Database, show in listview
     */
    private void loadDataFromDatabase() {
        ticketList.clear();
        MyTicketOpener dbOpener = new MyTicketOpener(this);
        db3 = dbOpener.getWritableDatabase();
        String[] columns = {MyTicketOpener.COL_ID, MyTicketOpener.COL_NAME, MyTicketOpener.COL_START_DATE_TIME,MyTicketOpener.COL_PRICE_MIN,MyTicketOpener.COL_PRICE_MAX,MyTicketOpener.COL_URL,MyTicketOpener.COL_IMAGE};
        Cursor results = db3.query(false, MyTicketOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:

        int nameColumnIndex = results.getColumnIndex(MyTicketOpener.COL_NAME);
        int dateColumnIndex = results.getColumnIndex(MyTicketOpener.COL_START_DATE_TIME);
        int priceMinColumnIndex = results.getColumnIndex(MyTicketOpener.COL_PRICE_MIN);
        int priceMaxColumnIndex = results.getColumnIndex(MyTicketOpener.COL_PRICE_MAX);
        int urlColumnIndex = results.getColumnIndex(MyTicketOpener.COL_URL);
        int imgUrlColumnIndex = results.getColumnIndex(MyTicketOpener.COL_IMAGE);
        int idColumnIndex =results.getColumnIndex(MyTicketOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext()){
            String name = results.getString(nameColumnIndex);
            String date = results.getString(dateColumnIndex);
            String pricemin = results.getString(priceMinColumnIndex);
            String pricemax = results.getString(priceMaxColumnIndex);
            String url = results.getString(urlColumnIndex);
            String image = results.getString(imgUrlColumnIndex);


            //add the new Contact to the array list:
            long id = results.getLong(idColumnIndex);
            ticketList.add(new Ticket(id, name,date,pricemin,pricemax,url,image));

        }
    }



    private class TicketQuery extends AsyncTask<String, Integer, String> {
        private List<Ticket> TicketResults = new ArrayList();

        /**
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {

            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = urlConnection.getInputStream();
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                // convert string to JSON:
                JSONObject obj = new JSONObject(result);
                JSONObject embedded = obj.getJSONObject("_embedded");
                JSONArray events = embedded.getJSONArray("events");
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String name = event.getString("name");
                    String eventURL = event.getString("url");
                    String startDate = event.getJSONObject("dates").getJSONObject("initialStartDate").getString("localDate");
                    double minPrice = event.getJSONArray("priceRanges").getJSONObject(0).getDouble("min");
                    double maxPrice = event.getJSONArray("priceRanges").getJSONObject(0).getDouble("max");
                    String bitmap = event.getJSONArray("images").getJSONObject(0).getString("url");
                    Ticket ticket = new Ticket(name, startDate, minPrice + "", maxPrice + "", eventURL, bitmap);
                    TicketResults.add(ticket);
                    publishProgress(i * (100 / events.length()));
                    progressBar.setProgress(i * (100 / events.length()));


                }
                publishProgress(100);
                progressBar.setProgress(100);


            } catch (Exception e) {
                String msg = e.getMessage();
                Log.e("Search provinces", e.getMessage());

            }
            return "Data Acquisition done";
        }

        public void onPostExecute(String fromDoInBackground) {
            progressBar.setVisibility(View.INVISIBLE);
            ticketList.addAll(this.TicketResults);
            ticketListAdapter.notifyDataSetChanged();



        }
    }
}









