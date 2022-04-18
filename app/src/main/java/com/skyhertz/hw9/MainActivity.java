package com.skyhertz.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayAdapter<String> autoCompleteAdapter;
    ArrayList<String> autoCompleteList = new ArrayList<String>();
    SearchView.SearchAutoComplete searchAutoComplete;

    SearchView searchView;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoCompleteList);
        searchAutoComplete.setAdapter(autoCompleteAdapter);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //TODO: Text submitted
        /*RequestCallbacks callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(MainActivity.this, "[LOG]: " + result.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        GetRequest.get("query", query, new JSONObject(), callbacks, this);*/
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //TODO: Text changed
        newText = newText.replaceAll(" ", "");
        index++;
        int this_index = index;
        autoCompleteAdapter.clear();
        autoCompleteList.clear();
        if(!newText.equals("")) {
            RequestCallbacks callbacks = new RequestCallbacks() {
                @Override
                public void onSuccess(JSONObject result) {
                    JSONArray arrayList = new JSONArray();
                    if(index != this_index) {
                        return;
                    }
                    try {
                        arrayList = result.getJSONArray("result");
                        for (int i = 0; i < arrayList.length(); i++) {
                            String name = arrayList.getJSONObject(i).getString("description");
                            String stock_id = arrayList.getJSONObject(i).getString("symbol");
                            String type = arrayList.getJSONObject(i).getString("type");
                            if(type.equals("Common Stock") && !stock_id.contains(".")) {
                                autoCompleteList.add(stock_id + " | " + name);
                            }
                        }
                        autoCompleteAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, autoCompleteList);
                        searchAutoComplete.setAdapter(autoCompleteAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    searchAutoComplete.showDropDown();
                    searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                  @Override
                                                                  public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                                                                      String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                                                                      //searchAutoComplete.setText("" + queryString.substring(0, queryString.indexOf(' ')));
                                                                      //Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                                                                      Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                                                      intent.putExtra("STOCK_ID", queryString.substring(0, queryString.indexOf(' ')));
                                                                      startActivity(intent);
                                                                  }
                                                              });
                }
            };
            GetRequest.get("auto", newText, new JSONObject(), callbacks, this);
        }
        return false;
    }
}