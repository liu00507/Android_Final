package com.example.androidfinal;

import androidx.annotation.ContentView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinal.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.androidfinal.MainActivity;

public class Covid19Case extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private SharedPreferences prefs;

    private List<Province> provinceList = new ArrayList<>();
    private List<Database> databaseList = new ArrayList<>();

    ListView provinceListView = null;
    ListView databaseListView = null;

    public static final String ITEM_SELECTED = "ITEM";

    private ProvinceListAdapter provinceListAdapter;
    private DatabaseListAdapter databaseListAdapter;
    SQLiteDatabase db;
    ProgressBar progressBar;

    Intent gotoProvinceDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19_case);

        /*
        put and set toolbar:
         */
        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.covidToolBar);

        /*
        put navigation view:
         */
        DrawerLayout drawer = findViewById(R.id.covid_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.covidopen, R.string.covidclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);//this line avoids the icons to appear shaded gray
        navigationView.setNavigationItemSelectedListener(this);

        /*
        save into query
         */
        prefs = getSharedPreferences("searchArguments", Context.MODE_PRIVATE);
        String country = prefs.getString("country", "");
        String date = prefs.getString("date", "");

        EditText countryInput = findViewById(R.id.country_input);
        EditText dateInput = findViewById(R.id.date_input);

        countryInput.setText(country);
        dateInput.setText(date);

        provinceListView = findViewById(R.id.province_list_view);
        provinceListView.setAdapter(provinceListAdapter = new ProvinceListAdapter());
        databaseListView = findViewById(R.id.database_list_view);
        databaseListView.setAdapter(databaseListAdapter = new DatabaseListAdapter());


        /*
        province details
         */
        gotoProvinceDetail = new Intent(Covid19Case.this, ProvinceDetailActivity.class);

        /*
        add and display progress bar
         */
        progressBar = findViewById(R.id.progress_horizontal);
        //function of search button
        Button searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(click -> {
            String countryIn = countryInput.getText().toString();
            String dateIn = dateInput.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date searchdate = format.parse ( dateIn );
                saveArguments(countryIn, dateIn);
                AquireData(countryIn, searchdate);
                provinceListAdapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //ProvincesQuery provincesQuery = new ProvincesQuery();
            //provincesQuery.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");
        });

        //function of save button
        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(click -> {
            String message = null;
            saveData(provinceList);
            message = "Save search results to Database";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });

        /*
            display province details when select item from viewlist
         */
        provinceListView.setOnItemClickListener((parent, view, position, id) -> {
            Province province = provinceList.get(position);
            gotoProvinceDetail.putExtra("Country", province.getCountry());
            gotoProvinceDetail.putExtra("CountryCode", province.getCountryCode());
            gotoProvinceDetail.putExtra("Province", province.getProvince() );
            gotoProvinceDetail.putExtra("Cases", province.getCase());
            gotoProvinceDetail.putExtra("Date", province.getDate());
            gotoProvinceDetail.putExtra("Lat", province.getLatitude());
            gotoProvinceDetail.putExtra("Lon", province.getLongitude());
            startActivity(gotoProvinceDetail);
        });

    }

    private void saveArguments(String country, String date) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("country", country);
        edit.putString("date", date);
        edit.commit();
    }

    /*
        parse and convert date
     */
    private void AquireData(String country, Date date) {
        provinceList.clear();
        progressBar.setVisibility(View.VISIBLE);
        ProvincesQuery provincesQuery = new ProvincesQuery();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateplusone=new Date(date.getTime() + (1000 * 60 * 60 * 24));
        String searchdate= DateFormat.format("yyyy-MM-dd", date).toString();
        String searchdateplusone= DateFormat.format("yyyy-MM-dd", dateplusone).toString();
        String url="https://api.covid19api.com/country/"+country.toUpperCase()+"/status/confirmed/live?from="+searchdate+"T00:00:00Z&to="+searchdateplusone+"T00:00:00Z";
        provincesQuery.execute(url);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.covid_menu, menu);
        return true;
    }

    //toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.item1: //Go to Main page
                startActivity(new Intent(Covid19Case.this, MainActivity.class));
                message = "Go to Main Page";
                break;
            case R.id.item2: //show saved results in database
                //call loadDataFromDatabase()
                message = "Show records saved in database";
                break;
            case R.id.item3: //show user manual
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle(R.string.covid_help_text)
                        .setMessage(R.string.covid_help_content)
                        .setPositiveButton(R.string.covid_yes, (c, arg) -> {

                        })
                        .create().show();
                message = "show user manual";
                break;
            case R.id.item4: //about the covid19 topic
                message = "Covid-19 case data writen by Ivy Xue";
                break;

        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    /* Needed for
     *the OnNavigationItemSelected interface
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        String message = null;
        switch(item.getItemId())
        {
            case R.id.item1:
                //Go to Main Page
                startActivity(new Intent(Covid19Case.this, MainActivity.class));
                message = "Show main page";
                break;
            case R.id.item2:
                //show saved records in Database: country and date
                loadDataFromDatabase();
                message = "Show saved records in Database";
                break;
            case R.id.item3:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle(R.string.covid_help_text)
                        .setMessage(R.string.covid_help_content)
                        .setPositiveButton(R.string.covid_yes, (c, arg) -> {

                        })
                        .create().show();
                message = "Show User Manual";
                break;
            case R.id.item4:
                message = "Covid-19 activity";
                break;

        }
        DrawerLayout drawerLayout = findViewById(R.id.covid_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }
    /*
     * save search result to database
     */
    private void saveData( List<Province> resultList )
    {
        MyOpener provinceOpener = new MyOpener(this);
        db = provinceOpener.getWritableDatabase();

        for (Province result : resultList)
        {
            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in ProvinceOpener.java:
            //put string country in the COUNTRY column:
            newRowValues.put( MyOpener.COL_COUNTRY, result.getCountry() );
            //put string date in the DATE column:
            newRowValues.put( MyOpener.COL_DATE, result.getDate() );
            //put string province in the PROVINCE column:
            newRowValues.put( MyOpener.COL_PROVINCE, result.getProvince() );
            //put string case in the CASE column:
            newRowValues.put( MyOpener.COL_CASE, result.getCase() );

            //Now insert in the database:
            long newId = db.insert( MyOpener.TABLE_NAME, null, newRowValues );
        }
    }
    /*
     * load country & date from Database, show in listview
     */
    private void loadDataFromDatabase()
    {
        databaseList.clear();
        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        // We want to get distincet country column and date column.
        String [] columns = {MyOpener.COL_COUNTRY, MyOpener.COL_DATE};
        //query country and date from the database:
        Cursor results = db.query(true, MyOpener.TABLE_NAME, new String[]{"COUNTRY", "DATE"}, "COUNTRY not null and DATE not null", null,null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int countryColumnIndex = results.getColumnIndex(MyOpener.COL_COUNTRY);
        int dateColumnIndex = results.getColumnIndex(MyOpener.COL_DATE);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String country = results.getString(countryColumnIndex);
            String date = results.getString(dateColumnIndex);
            //add the new Contact to the array list:
            databaseList.add( new Database( country, date ) );
        }

//        provinceListView.setVisibility( View.INVISIBLE );
//        databaseListView.setVisibility( View.VISIBLE );
        databaseListAdapter.notifyDataSetChanged();

    }

     class DatabaseListAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return databaseList.size();
        }

        @Override
        public Object getItem(int position) {
            return databaseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return databaseList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Database databaseRec = (Database) getItem(position);
            LayoutInflater inflater = getLayoutInflater();

            //make a new row
            View rowView = inflater.inflate(R.layout.row_countrydate_layout, parent, false);

            TextView countryView = rowView.findViewById( R.id.country_name_text_view);
            countryView.setText(databaseRec.getCountry());

            TextView dateView = rowView.findViewById( R.id.date_text_view );
            dateView.setText( databaseRec.getDate() );

            return rowView;
        }
    }

    private class ProvinceListAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return provinceList.size();
        }

        @Override
        public Object getItem(int position) {
            return provinceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return provinceList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Province province = ( Province ) getItem(position);
            LayoutInflater inflater = getLayoutInflater();

            //make a new row
            View rowView = inflater.inflate(R.layout.row_province_layout, parent, false);
            if (position==0){
                TextView provinceView = rowView.findViewById( R.id.province_name_text_view );
                provinceView.setText("Provinces/States:");

                TextView caseView = rowView.findViewById( R.id.province_case_text_view );
                caseView.setText("Cases:");
            }
            else if (position==provinceList.size()-1){
                TextView provinceView = rowView.findViewById( R.id.province_name_text_view );
                provinceView.setText(null);

                TextView caseView = rowView.findViewById( R.id.province_case_text_view );
                caseView.setText(null);
            }
            else {
                TextView provinceView = rowView.findViewById(R.id.province_name_text_view);
                provinceView.setText(province.getProvince());

                TextView caseView = rowView.findViewById(R.id.province_case_text_view);
                caseView.setText(province.getCase());
            }

            return rowView;
        }
    }

    private class ProvincesQuery extends AsyncTask<String, Integer, String>
    {

        private List<Province> provinceResults = new ArrayList();

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
                String result = sb.toString(); //result is the whole string
                //System.out.println(result);

                // convert string to JSON: Look at slide 27:
                JSONArray provinceJSONArray = new JSONArray(result);
                JSONObject provinceJSONObject;


                for (int i = 0; i < provinceJSONArray.length(); i++) {
                    provinceJSONObject = provinceJSONArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvince(provinceJSONObject.getString("Province"));
                    province.setCountry(provinceJSONObject.getString("Country"));
                    province.setCase(provinceJSONObject.getString("Cases"));
                    province.setCountryCode(provinceJSONObject.getString("CountryCode"));
                    province.setDate(provinceJSONObject.getString("Date"));
                    province.setLatitude(provinceJSONObject.getString("Lat"));
                    province.setLongitude(provinceJSONObject.getString("Lon"));

                    provinceResults.add(province);
                    publishProgress(i * (100 / provinceJSONArray.length()));
                    progressBar.setProgress(i * (100 / provinceJSONArray.length()));
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
            provinceList.addAll(this.provinceResults);
            if (provinceList.size() == 0) {
                Toast.makeText(Covid19Case.this, "No case returned.", Toast.LENGTH_LONG).show();
            }
           // provinceListAdapter.notifyDataSetChanged();
        }
    }


}