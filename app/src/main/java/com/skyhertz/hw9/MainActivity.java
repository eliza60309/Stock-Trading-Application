package com.skyhertz.hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    RecyclerView preferenceListRecyclerView;
    PreferenceListRecyclerViewAdapter preferenceListRecyclerViewAdapter;
    static ArrayList<PreferenceEntry> preferenceList;

    //Portfolio List Function
    RecyclerView portfolioListRecyclerView;
    PortfolioListRecyclerViewAdapter portfolioListRecyclerViewAdapter;
    static ArrayList<PortfolioEntry> portfolioList;

    //Net worth
    TextView netWorth;
    double cash;

    // Moved to individual file
    // public class LocalStorage {}

    public PortfolioEntry findPortfolio(String stock_id) {
        for(int i = 0; i < portfolioList.size(); i++) {
            if(portfolioList.get(i).get_stock_id().equals(stock_id)) {
                return portfolioList.get(i);
            }
        }
        return null;
    }

    public double sumPortfolio() {
        double sum = 0;
        for(int i = 0; i < portfolioList.size(); i++) {
            sum += portfolioList.get(i).price * portfolioList.get(i).get_hold();
        }
        return sum;
    }

    private void hookNetworth() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    runOnUiThread(() -> MainActivity.mainActivity.netWorth.setText("$" + String.format("%.2f", cash + sumPortfolio())));
                    runOnUiThread(() -> MainActivity.mainActivity.findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE));
                    runOnUiThread(() -> MainActivity.mainActivity.findViewById(R.id.mainView).setVisibility(View.VISIBLE));

                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        preferenceListRecyclerView = findViewById(R.id.preferenceListView);
        localStorage = new LocalStorage(this);
        //LocalStorage.clearPreferenceStorage();
        preferenceList = LocalStorage.loadPreferenceStorage();
        /*preferenceList.add(new PreferenceEntry("AAPL", "Expensive Device Company"));
        preferenceList.add(new PreferenceEntry("TSLA", "Iron Ball to the Window"));
        preferenceList.add(new PreferenceEntry("MSFT", "Windows 10 is the last Windows"));*/


        preferenceListRecyclerViewAdapter = new PreferenceListRecyclerViewAdapter(preferenceList);
        ItemTouchHelper.Callback callback = new PreferenceEntryMoveCallback(preferenceListRecyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(preferenceListRecyclerView);
        preferenceListRecyclerView.setAdapter(preferenceListRecyclerViewAdapter);
        //LocalStorage.savePreferenceStorage(preferenceList);


        portfolioListRecyclerView = findViewById(R.id.portfolioListView);
        //LocalStorage.clearPortfolioStorage();
        portfolioList = LocalStorage.loadPortfolioStorage();
        /*portfolioList.add(new PortfolioEntry("AAPL", "Expensive Device Company", 1, 165));
        portfolioList.add(new PortfolioEntry("TSLA", "Iron Ball to the Window", 1, 1011));
        portfolioList.add(new PortfolioEntry("MSFT", "Windows 10 is the last Windows", 1, 1000));*/


        portfolioListRecyclerViewAdapter = new PortfolioListRecyclerViewAdapter(portfolioList);
        ItemTouchHelper.Callback callback2 = new PortfolioEntryMoveCallback(portfolioListRecyclerViewAdapter);
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(callback2);
        touchHelper2.attachToRecyclerView(portfolioListRecyclerView);
        portfolioListRecyclerView.setAdapter(portfolioListRecyclerViewAdapter);


        //LocalStorage.resetCashStorage();
        cash = LocalStorage.loadCashStorage();
        ((TextView) findViewById(R.id.cash)).setText("$" + String.format("%.2f", cash));
        netWorth = MainActivity.mainActivity.findViewById(R.id.networth);
        hookNetworth();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        LocalDateTime now = LocalDateTime.now();
        ((TextView) findViewById(R.id.date)).setText(dtf.format(now));

        findViewById(R.id.finnhub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //6.13 https://www.tutorialkart.com/kotlin-android/android-open-url-in-browser-activity/
                Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                openURL.setData(Uri.parse("https://finnhub.io/"));
                startActivity(openURL);
            }
        });
    }

    public void updateCash() {
        cash = LocalStorage.loadCashStorage();
        ((TextView) findViewById(R.id.cash)).setText("$" + String.format("%.2f", cash));
        MainActivity.mainActivity.netWorth.setText("$" + String.format("%.2f", cash + sumPortfolio()));
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

        if(query.equals("!reset")) {
            LocalStorage.clearPortfolioStorage();
            LocalStorage.clearPreferenceStorage();
            LocalStorage.resetCashStorage();
            Toast.makeText(MainActivity.this, "Reset Local Storage", Toast.LENGTH_LONG).show();
        }
        //preferenceList.add(new PreferenceEntry(query, query));
        //System.out.println(preferenceList.toString());
        //preferenceListRecyclerViewAdapter.notifyDataSetChanged();
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