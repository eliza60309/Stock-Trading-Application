package com.skyhertz.hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    //Instance
    static MainActivity mainActivity;
    LocalStorage localStorage;

    //Search Function
    ArrayAdapter<String> autoCompleteAdapter;
    ArrayList<String> autoCompleteList = new ArrayList<String>();
    SearchView.SearchAutoComplete searchAutoComplete;
    SearchView searchView;
    int index = 0;

    //Preference List Function
    RecyclerView recyclerView;
    _RecyclerViewAdapter recyclerViewAdapter;
    static ArrayList<PreferenceEntry> preferenceList;

    public class LocalStorage {
        public ArrayList<PreferenceEntry> loadPreference() {
            SharedPreferences getter = getSharedPreferences("Data", 0);
            ArrayList<PreferenceEntry> arrayList = new ArrayList<PreferenceEntry>();
            if (getter.getString("Preference", null) == null || getter.getString("Preference", null).isEmpty()) {
                return arrayList;
            }
            System.out.println("GET PREFERENCE: " + getter.getString("Preference", null));
            try {
                JSONArray jsonArray = new JSONArray(getter.getString("Preference", null));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PreferenceEntry preferenceEntry = new PreferenceEntry(jsonObject.getString("stock_id"), jsonObject.getString("name"));
                    arrayList.add(preferenceEntry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }
        public void savePreference(ArrayList<PreferenceEntry> arrayList) {
            SharedPreferences.Editor setter = getSharedPreferences("Data", 0).edit();
            try {
                JSONArray jsonArray = new JSONArray();
                for(int i = 0; i < arrayList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stock_id", arrayList.get(i).get_stock_id());
                    jsonObject.put("name", arrayList.get(i).get_name());
                    jsonArray.put(jsonObject);
                }
                setter.putString("Preference", jsonArray.toString());
                setter.commit();
                System.out.println("SET PREFERENCE: " + jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public void clearPreference() {
            SharedPreferences.Editor setter = getSharedPreferences("Data", 0).edit();
            setter.remove("Preference");
            setter.commit();
        }
    }

    public void updatePreferenceList() {
        localStorage.savePreference(preferenceList);

        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.preferenceListView);
        localStorage = new LocalStorage();
        localStorage.clearPreference();
        preferenceList = localStorage.loadPreference();
        preferenceList.add(new PreferenceEntry("AAPL", "Expensive Device Company"));
        preferenceList.add(new PreferenceEntry("TSLA", "Iron Ball to the Window"));
        preferenceList.add(new PreferenceEntry("MSFT", "Windows 10 is the last Windows"));



        recyclerViewAdapter = new _RecyclerViewAdapter(preferenceList);

        ItemTouchHelper.Callback callback = new _ItemMoveCallback(recyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(recyclerViewAdapter);

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
        //TODO: HOW TO ADD TO PREFERENCE LIST
        preferenceList.add(new PreferenceEntry(query, query));
        System.out.println(preferenceList.toString());
        recyclerViewAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

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