package com.example.androidfinal;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidfinal.ReceipeDetail;
import com.example.androidfinal.ReceipeModel;
import com.example.receipeSearch.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavFragment extends Fragment {

    // Listiew for Favourite List
    ListView listView;
    // Adapter for Listview
    ArrayAdapter adapter;
    // List for titles of all the receipes
    List<String> str = new ArrayList<>();
    // List for the whole object of receipes
    List<ReceipeModel> mainList = new ArrayList<>();
    // String varianble to fetch the shared prefrences
    public static final String RECIEPE = "RECIEPE";
    // Oncreate method for Fragment
    SharedPreferences sharedPreferences;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Fetching the view
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        sharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(getContext());

        // Intialise the adapter
        adapter = new ArrayAdapter<String>(getContext(),R.layout.listviewhome,str);
        // Initialize the listview
        listView = (ListView) root.findViewById(R.id.listviewfavlist);
        // Set adapter inside listview
        listView.setAdapter(adapter);
        // Add click listner to listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // When clicked on listitem, nelow function is executed
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
        // Show favourite list
        showFav();
        // Return the view group
        return root;
    }
    //Function to show favourite list
    public void showFav(){
        // Get the stored string of json
        String connectionsJSONString = sharedPreferences.getString(RECIEPE, null);
        if(connectionsJSONString!= null){
            Log.d("Shared Prefrences",connectionsJSONString);
        }else{
            Log.d("Shared Prefrences","Null");
        }

        // Check if string is not null
        if(connectionsJSONString!= null){
            // Creation of list from string
            Type type = new TypeToken< List < ReceipeModel >>() {}.getType();
            List < ReceipeModel > connections = new Gson().fromJson(connectionsJSONString, type);
            // Add title to list for showing in fav list
            for(ReceipeModel con : connections){
                str.add(con.getTitle());
                mainList.add(new ReceipeModel(con.getTitle(),con.getHref(),con.getIngredients(),con.getThumbnail()));
            }
            // Notify the dataset adapter
            adapter.notifyDataSetChanged();
        }

    }

}