package com.example.androidfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.receipeSearch.R;
import com.example.androidfinal.ReceipeDetail;
import com.example.androidfinal.ReceipeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Progress Dialog
    ProgressDialog progressDialog;
    // Listiew for Search List
    ListView listView;
    // Adapter for Listview
    ArrayAdapter adapter;
    // List for titles of all the receipes
    List<String> str = new ArrayList<>();
    // List for the whole object of receipes
    List<ReceipeModel> mainList = new ArrayList<>();

    public static final String RECIEPE = "RECIEPE";

    // Edittext for reciepe name
    EditText first;
    // editext for ingredient
    EditText second;
    // button for searching the receipe
    Button button;
    // Oncreate method of fragment

    SharedPreferences sharedPreferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // View
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(getContext());
        String connectionsJSONString = sharedPreferences.getString(RECIEPE, null);
        if(connectionsJSONString!= null){
            Log.d("Shared Prefrences",connectionsJSONString);
        }else{
            Log.d("Shared Prefrences","Null");
        }


        // intialize the editext for name
        first = (EditText)root.findViewById(R.id.edittexthomesearch);
        // intialize the editext for ingredients
        second = (EditText)root.findViewById(R.id.edittexthomesearching);
        // intialize the button
        button = (Button)root.findViewById(R.id.buttonhomesearch);
        // intialize the adapter using the layut file
        adapter = new ArrayAdapter<String>(getContext(),R.layout.listviewhome,str);
        // intialise the listview using the listview object
        listView = (ListView) root.findViewById(R.id.listviewhomelist);
        //set adapter to list
        listView.setAdapter(adapter);
        //add click listener on the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On click of button execute the search api
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            }
        });
        // Add the click listener on the search list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create intent
                Intent i = new Intent(getActivity(), ReceipeDetail.class);
                // Add title to intent
                i.putExtra("title",mainList.get(position).getTitle());
                // Add ingredients to intent
                i.putExtra("ingredients",mainList.get(position).getIngredients());
                // Add href to intent
                i.putExtra("href",mainList.get(position).getHref());
                // Add thumbnail image to intent
                i.putExtra("thumnail",mainList.get(position).getThumbnail());
                // Go to Details page
                startActivity(i);
            }
        });


        return root;
    }

    // Async Task for executing the apin on search
    public class MyAsyncTasks extends AsyncTask<String, String, String> {


        // Preexecute function which starts the progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            //show preogress bar
            progressDialog.show();
        }

        // async fucntion executing and fetching the data
        @Override
        protected String doInBackground(String... params) {

            // implement API in background and store the response in current variable
            String current = "";
            try {
                // URL object
                URL url;
                // httpurlconnection object
                HttpURLConnection urlConnection = null;
                try {
                    // Get the ingredients
                    String ing = second.getText().toString().trim();
                    // Get the name of search
                    String se = first.getText().toString().trim();
                    // Create the url
                    url = new URL("http://www.recipepuppy.com/api/?i="+ing+"&q="+se+"&p=3");
                    // Call the connection
                    urlConnection = (HttpURLConnection) url
                            .openConnection();
                    // Get input from connection
                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);
                    // Get the data
                    int data = isw.read();
                    // While data can be taken
                    while (data != -1) {
                        // append the data and make whole string
                        current += (char) data;
                        data = isw.read();
                        System.out.print(current);

                    }
                    // return the data to onPostExecute method
                    return current;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("data", s.toString());
            // dismiss the progress dialog after receiving data from API
            progressDialog.dismiss();
            try {
                // JSON Parsing of data
                JSONObject obj = new JSONObject(s.toString());
                //Clear the lists
                mainList.clear();
                str.clear();
                // Get the array for parsing
                JSONArray array = obj.getJSONArray("results");
                for (int i=0; i<array.length(); i++) {
                    //Parsing of the object to form required data
                    JSONObject obbjj = (array.getJSONObject(i));
                    ReceipeModel rm = new ReceipeModel();
                    rm.setTitle(obbjj.getString("title"));
                    rm.setHref(obbjj.getString("href"));
                    rm.setIngredients(obbjj.getString("ingredients"));
                    rm.setThumbnail(obbjj.getString("thumbnail"));
                    // add items to list of title and objects
                    mainList.add(rm);
                    str.add(obbjj.getString("title").trim());
                }
                // Notify the data changed
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}