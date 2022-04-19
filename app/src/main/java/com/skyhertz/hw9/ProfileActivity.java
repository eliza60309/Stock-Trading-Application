package com.skyhertz.hw9;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ProfileActivity extends AppCompatActivity {

    String stock_id;

    //special data
    boolean trend_up = false;
    boolean trend_down = false;
    final boolean[] preferred = {false};

    //states
    boolean doneRequests = false;
    boolean doneLogo = false;


    //Json Files
    JSONObject query;
    JSONObject quote;
    //JSONObject sma;
    JSONObject social;
    JSONArray recommendation;
    JSONArray earnings;
    JSONArray peers;
    JSONObject daily;
    JSONArray news;

    //data that have to be passed this way
    String url_daily;
    String url_history;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);

        //Setup star
        menu.findItem(R.id.star).setIcon(R.drawable.star_outline);
        for(int i = 0; i < MainActivity.preferenceList.size(); i++) {
            if(MainActivity.preferenceList.get(i).get_stock_id().equals(stock_id)) {
                menu.findItem(R.id.star).setIcon(R.drawable.star);
                preferred[0] = true;
                break;
            }
        }
        menu.findItem(R.id.star).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                preferred[0] = !preferred[0];
                if(preferred[0]) {
                    MainActivity.preferenceList.add(new PreferenceEntry(stock_id, ((TextView) findViewById(R.id.company)).getText().toString()));
                    MainActivity.mainActivity.updatePreferenceList();
                    menu.findItem(R.id.star).setIcon(R.drawable.star);
                }
                else {
                    menu.findItem(R.id.star).setIcon(R.drawable.star_outline);
                    for(int i = 0; i < MainActivity.preferenceList.size(); i++) {
                        if(MainActivity.preferenceList.get(i).get_stock_id().equals(stock_id)) {
                            MainActivity.preferenceList.remove(i);
                            MainActivity.mainActivity.updatePreferenceList();
                            break;
                        }
                    }
                }
                return false;
            }
        });



        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




        stock_id = getIntent().getExtras().getString("STOCK_ID");
        System.out.println(stock_id);

        //Setup toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stock_id);


        //Setup star
        /*final boolean[] preferred = {false};
        toolbar.getMenu().findItem(R.id.star).setIcon(R.drawable.star_outline);
        for(int i = 0; i < MainActivity.preferenceList.size(); i++) {
            if(MainActivity.preferenceList.get(i).get_stock_id() == stock_id) {
                toolbar.getMenu().findItem(R.id.star).setIcon(R.drawable.star);
                preferred[0] = true;
                break;
            }
        }
        System.out.println("safe");
        toolbar.getMenu().findItem(R.id.star).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                preferred[0] = !preferred[0];
                if(preferred[0]) {
                    MainActivity.preferenceList.add(new PreferenceEntry(stock_id, ((TextView) findViewById(R.id.company)).getText().toString()));
                    toolbar.getMenu().findItem(R.id.star).setIcon(R.drawable.star);
                }
                else {
                    toolbar.getMenu().findItem(R.id.star).setIcon(R.drawable.star_outline);
                    for(int i = 0; i < MainActivity.preferenceList.size(); i++) {
                        if(MainActivity.preferenceList.get(i).get_stock_id() == stock_id) {
                            MainActivity.preferenceList.remove(i);
                            break;
                        }
                    }
                }
                return false;
            }
        });*/

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //TODO: GetRequest.get("auto", newText, callbacks, this);
        RequestCallbacks callbacks;
        RequestArrayCallbacks arrayCallbacks;

        callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                query = result;
                setQuery();
                checkDone();
            }
        };
        GetRequest.get("query", stock_id, new JSONObject(), callbacks, this);


        callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                quote = result;
                setQuote();
                getDaily();
                checkDone();
            }
        };
        GetRequest.get("quote", stock_id, new JSONObject(), callbacks, this);


        /*callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                sma = result;
                checkDone();
            }
        };
        JSONObject sma_params = new JSONObject();
        try {
            sma_params.put("RESOLUTION", "D");
            sma_params.put("FROM", String.valueOf(System.currentTimeMillis() / 1000 - 60 * 60 * 24 * 370 * 2));
            sma_params.put("TO", String.valueOf(System.currentTimeMillis() / 1000));
            System.out.println(sma_params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetRequest.get("candle", stock_id, sma_params, callbacks, this);*/
        WebView webView = (WebView) findViewById(R.id.eps);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/eps.html?STOCK_ID=%1$s", stock_id));

/* TODO: moved to fragments
        WebView webView = (WebView) findViewById(R.id.chart2);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/sma.html?STOCK_ID=%1$s&RESOLUTION=D&FROM=%2$s&TO=%3$s",
                stock_id, System.currentTimeMillis() / 1000 - 60 * 60 * 24 * 370 * 2, System.currentTimeMillis() / 1000));
*/
        callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                social = result;
                checkDone();
            }
        };
        GetRequest.get("social", stock_id, new JSONObject(), callbacks, this);


        arrayCallbacks = new RequestArrayCallbacks() {
            @Override
            public void onSuccess(JSONArray result) {
                recommendation = result;
                checkDone();
            }
        };
        GetRequest.getArray("recommendation", stock_id, new JSONObject(), arrayCallbacks, this);


        arrayCallbacks = new RequestArrayCallbacks() {
            @Override
            public void onSuccess(JSONArray result) {
                earnings = result;
                checkDone();
            }
        };
        GetRequest.getArray("earnings", stock_id, new JSONObject(), arrayCallbacks, this);


        arrayCallbacks = new RequestArrayCallbacks() {
            @Override
            public void onSuccess(JSONArray result) {
                peers = result;
                checkDone();
            }
        };
        GetRequest.getArray("peers", stock_id, new JSONObject(), arrayCallbacks, this);


        arrayCallbacks = new RequestArrayCallbacks() {
            @Override
            public void onSuccess(JSONArray result) {
                news = result;
                checkDone();
            }
        };
        JSONObject news_params = new JSONObject();
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();
            news_params.put("FROM", dtf.format(now.minusDays(30)));
            news_params.put("TO", dtf.format(now));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetRequest.getArray("news", stock_id, news_params, arrayCallbacks, this);

        //TODO other request
    }

    public synchronized void getDaily() {
        int t = 0;
        try {
            t = quote.getInt("t");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestCallbacks callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                daily = result;
                checkDone();
            }
        };

        url_daily = String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/daily.html?STOCK_ID=%1$s&RESOLUTION=5&FROM=%2$s&TO=%3$s&COLOR=%4$s",
                stock_id, t - 21600, t,
                (trend_up? "GREEN": trend_down? "RED": "BLACK"));
        url_history = String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/sma.html?STOCK_ID=%1$s&RESOLUTION=D&FROM=%2$s&TO=%3$s",
                stock_id, System.currentTimeMillis() / 1000 - 60 * 60 * 24 * 370 * 2, System.currentTimeMillis() / 1000);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int position) {
                //Drawable daily = getDrawable(R.drawable.daily);
                //Drawable history = getDrawable(R.drawable.history);
                if(position == 0) {
                    //daily.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP));
                    tabLayout.getTabAt(0).setIcon(R.drawable.daily_blue);
                    tabLayout.getTabAt(1).setIcon(R.drawable.history);
                }
                else {
                    tabLayout.getTabAt(0).setIcon(R.drawable.daily);
                    tabLayout.getTabAt(1).setIcon(R.drawable.history_blue);
                }
            }
        });
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setIcon(R.drawable.daily);
                    } else {
                        tab.setIcon(R.drawable.history);
                    }
                }).attach();

/* TODO: moved to fragments
        WebView webView = (WebView) findViewById(R.id.chart);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/daily.html?STOCK_ID=%1$s&RESOLUTION=5&FROM=%2$s&TO=%3$s&COLOR=%4$s",
                stock_id, t - 21600, t,
                (trend_up? "GREEN": trend_down? "RED": "BLACK")));*/
        /*
        JSONObject daily_params = new JSONObject();
        try {
            daily_params.put("RESOLUTION", "5");
            daily_params.put("FROM", t - 21600);
            daily_params.put("TO", t);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetRequest.get("candle", stock_id, daily_params, callbacks, this);*/
    }

    public synchronized void checkDone() {
        if(query != null &&
                quote != null &&
                //sma != null &&
                social != null &&
                recommendation != null &&
                earnings != null &&
                peers != null &&
                daily != null &&
                news != null) {
            doneRequests = true;
            System.out.println(query.toString());
            System.out.println(quote.toString());
            //System.out.println(sma.toString());
            System.out.println(social.toString());
            System.out.println(recommendation.toString());
            System.out.println(earnings.toString());
            System.out.println(peers.toString());
            System.out.println(daily.toString());
            System.out.println(news.toString());

            System.out.println("DONE!!");
        }
    }

    public void setQuery() {
        try {
            Picasso.get().load(query.getString("logo")).into((ImageView) findViewById(R.id.logo), new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    doneLogo = true;
                }
                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
            ((TextView) findViewById(R.id.stock_id)).setText(query.getString("ticker"));
            ((TextView) findViewById(R.id.company)).setText(query.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setQuote() {
        try {
            ((TextView) findViewById(R.id.price)).setText("$" + String.valueOf(Math.round(quote.getDouble("c") * 100.0) / 100.0));
            ((TextView) findViewById(R.id.price_change)).setText("$" + String.valueOf(Math.round(quote.getDouble("d") * 100.0) / 100.0) + " (" + String.valueOf(Math.round(quote.getDouble("dp") * 100.0) / 100.0) + "%)");
            trend_up = Math.round(quote.getDouble("d") * 100.0) > 0;
            trend_down = Math.round(quote.getDouble("d") * 100.0) < 0;
            ((ImageView) findViewById(R.id.trend)).setVisibility(View.VISIBLE);
            if(trend_up) {
                ((ImageView) findViewById(R.id.trend)).setImageDrawable(getDrawable(R.drawable.trending_up));
                ((ImageView) findViewById(R.id.trend)).setColorFilter(getColor(R.color.green));
                ((TextView) findViewById(R.id.price_change)).setTextColor(getColor(R.color.green));
            }
            else if(trend_down) {
                ((ImageView) findViewById(R.id.trend)).setImageDrawable(getDrawable(R.drawable.trending_down));
                ((ImageView) findViewById(R.id.trend)).setColorFilter(getColor(R.color.red));
                ((TextView) findViewById(R.id.price_change)).setTextColor(getColor(R.color.red));
            }
            else {
                ((ImageView) findViewById(R.id.trend)).setVisibility(View.INVISIBLE);
                ((ImageView) findViewById(R.id.trend)).setColorFilter(getColor(R.color.black));
                ((TextView) findViewById(R.id.price_change)).setTextColor(getColor(R.color.black));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //page_viewer2 from https://developer.android.com/guide/navigation/navigation-swipe-view-2
    public class PagerAdapter extends FragmentStateAdapter {
        public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            Fragment fragment = new WebviewFragment();
            Bundle args = new Bundle();
            if(position == 0) {
                args.putString("url", url_daily);
            }
            else {
                args.putString("url", url_history);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}