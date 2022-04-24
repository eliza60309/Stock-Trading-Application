package com.skyhertz.hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Callback;
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
    int redditTotal = 0;
    int twitterTotal = 0;
    int redditPositive = 0;
    int twitterPositive = 0;
    int redditNegative = 0;
    int twitterNegative = 0;
    String url = "";
    PortfolioEntry portfolioEntry;

    //states
    boolean doneRequests = false;
    boolean doneLogo = false;


    //Json Files
    JSONObject query;
    JSONObject quote;
    //JSONObject sma;
    JSONObject social;
    //JSONArray recommendation;
    //JSONArray earnings;
    JSONArray peers;
    //JSONObject daily;
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
                    //MainActivity.preferenceList.add(new PreferenceEntry(stock_id, ((TextView) findViewById(R.id.company)).getText().toString()));
                    //MainActivity.mainActivity.updatePreferenceList();
                    Toast.makeText(ProfileActivity.this, stock_id + " is added to favorites", Toast.LENGTH_LONG).show();
                    MainActivity.mainActivity.preferenceListRecyclerViewAdapter.add(new PreferenceEntry(stock_id, ((TextView) findViewById(R.id.company)).getText().toString()));
                    LocalStorage.savePreferenceStorage(MainActivity.preferenceList);
                    menu.findItem(R.id.star).setIcon(R.drawable.star);
                }
                else {
                    menu.findItem(R.id.star).setIcon(R.drawable.star_outline);
                    Toast.makeText(ProfileActivity.this, stock_id + " is removed from favorites", Toast.LENGTH_LONG).show();
                    for(int i = 0; i < MainActivity.preferenceList.size(); i++) {
                        if(MainActivity.preferenceList.get(i).get_stock_id().equals(stock_id)) {
                            //MainActivity.preferenceList.remove(i);
                            //MainActivity.mainActivity.updatePreferenceList();
                            MainActivity.mainActivity.preferenceListRecyclerViewAdapter.delete(i);
                            LocalStorage.savePreferenceStorage(MainActivity.preferenceList);
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
        portfolioEntry = MainActivity.mainActivity.findPortfolio(stock_id);

        //Setup toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stock_id);

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


        WebView webView = findViewById(R.id.eps);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/eps.html?STOCK_ID=%1$s", stock_id));

        WebView webView2 = findViewById(R.id.recommendation);
        webView2.setWebViewClient(new WebViewClient());
        webView2.getSettings().setDomStorageEnabled(true);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl(String.format("https://hw789etc-343408.wl.r.appspot.com/hw9/recommendation.html?STOCK_ID=%1$s", stock_id));

        callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                social = result;
                setSocial();
                checkDone();
            }
        };
        GetRequest.get("social", stock_id, new JSONObject(), callbacks, this);


        arrayCallbacks = new RequestArrayCallbacks() {
            @Override
            public void onSuccess(JSONArray result) {
                peers = result;
                setPeers();
                checkDone();
            }
        };
        GetRequest.getArray("peers", stock_id, new JSONObject(), arrayCallbacks, this);


        arrayCallbacks = new RequestArrayCallbacks() {
            @Override
            public void onSuccess(JSONArray result) {
                news = result;
                setNews();
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
        /*RequestCallbacks callbacks = new RequestCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                daily = result;
                checkDone();
            }
        };*/

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
    }

    public synchronized void checkDone() {
        if(query != null &&
                quote != null &&
                //sma != null &&
                social != null &&
                //recommendation != null &&
                //earnings != null &&
                peers != null &&
                //daily != null &&
                news != null) {
            doneRequests = true;
            //System.out.println(query.toString());
            //System.out.println(quote.toString());
            //System.out.println(sma.toString());
            //System.out.println(social.toString());
            //System.out.println(recommendation.toString());
            //System.out.println(earnings.toString());
            //System.out.println(peers.toString());
            //System.out.println(daily.toString());
            //System.out.println(news.toString());

            findViewById(R.id.trade).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(ProfileActivity.this);
                    dialog.setContentView(R.layout.dialog_trade);
                    try {
                        if(portfolioEntry == null)
                            new Trade(dialog, new PortfolioEntry(stock_id ,query.getString("name"), 0, quote.getDouble("c")), quote.getDouble("c"), ProfileActivity.this);
                        else
                            new Trade(dialog, portfolioEntry, quote.getDouble("c"),ProfileActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.show();
                }
            });

            findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
            findViewById(R.id.scrollable).setVisibility(View.VISIBLE);

        }
    }

    public void setQuery() {
        try {
            Picasso.get().load(query.getString("logo")).into(findViewById(R.id.logo), new com.squareup.picasso.Callback() {
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
            ((TextView) findViewById(R.id.social_name)).setText(query.getString("name"));
            ((TextView) findViewById(R.id.about_ipo)).setText(query.getString("ipo"));
            ((TextView) findViewById(R.id.about_industry)).setText(query.getString("finnhubIndustry"));

            SpannableString content = new SpannableString(query.getString("weburl"));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            ((TextView) findViewById(R.id.about_webpage)).setText(content);
            url = query.getString("weburl");
            findViewById(R.id.about_webpage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //6.13 https://www.tutorialkart.com/kotlin-android/android-open-url-in-browser-activity/
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    openURL.setData(Uri.parse(url));
                    startActivity(openURL);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setQuote() {
        try {
            ((TextView) findViewById(R.id.price)).setText("$" + String.format("%.2f", quote.getDouble("c")));
            ((TextView) findViewById(R.id.price_change)).setText("$" + String.format("%.2f", quote.getDouble("d")) + " (" +  String.format("%.2f", quote.getDouble("dp")) + "%)");
            ((TextView) findViewById(R.id.stats_open)).setText("$" + String.format("%.2f", quote.getDouble("o")));
            ((TextView) findViewById(R.id.stats_high)).setText("$" + String.format("%.2f", (quote.getDouble("h"))));
            ((TextView) findViewById(R.id.stats_low)).setText("$" + String.format("%.2f",(quote.getDouble("l"))));
            ((TextView) findViewById(R.id.stats_close)).setText("$" + String.format("%.2f",(quote.getDouble("pc"))));
            trend_up = Math.round(quote.getDouble("d") * 100.0) > 0;
            trend_down = Math.round(quote.getDouble("d") * 100.0) < 0;
            findViewById(R.id.trend).setVisibility(View.VISIBLE);
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

            setPortfolio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPortfolio () {
        //Setup Portfolio
        portfolioEntry = MainActivity.mainActivity.findPortfolio(stock_id);
        if(portfolioEntry != null) {
            try {
                ((TextView) findViewById(R.id.portfolio_count)).setText(String.valueOf(portfolioEntry.get_hold()));
                ((TextView) findViewById(R.id.portfolio_average)).setText("$" +  String.format("%.2f",portfolioEntry.get_average()));
                ((TextView) findViewById(R.id.portfolio_cost)).setText("$" +  String.format("%.2f",portfolioEntry.get_average() * portfolioEntry.get_hold()));
                ((TextView) findViewById(R.id.portfolio_change)).setText("$" +  String.format("%.2f",(quote.getDouble("c") - portfolioEntry.get_average()) * portfolioEntry.get_hold()));
                ((TextView) findViewById(R.id.portfolio_value)).setText("$" +  String.format("%.2f",quote.getDouble("c") * portfolioEntry.get_hold()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            ((TextView) findViewById(R.id.portfolio_count)).setText("0");
            ((TextView) findViewById(R.id.portfolio_average)).setText("$0.00" );
            ((TextView) findViewById(R.id.portfolio_cost)).setText("$0.00");
            ((TextView) findViewById(R.id.portfolio_change)).setText("$0.00");
            ((TextView) findViewById(R.id.portfolio_value)).setText("$0.00");
        }
    }


    public void setPeers() {
        try {
            LinearLayout peersView = findViewById(R.id.about_peers);
            for (int i = 0; i < peers.length(); i++) {
                SpannableString content = new SpannableString(peers.get(i).toString() + ",");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                TextView textView = new TextView(this);
                textView.setText(content);
                textView.setTooltipText(peers.get(i).toString());
                textView.setTextColor(getColor(R.color.blue));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        intent.putExtra("STOCK_ID", view.getTooltipText());
                        startActivity(intent);
                    }
                });
                peersView.addView(textView);
                textView = new TextView(this);
                textView.setText("  ");
                peersView.addView(textView);
            }
            TextView textView = new TextView(this);
            textView.setText(" ");
            peersView.addView(textView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSocial() {
        try {
            JSONArray jsonArray = social.getJSONArray("reddit");
            for(int i = 0; i < jsonArray.length(); i++) {
                redditTotal += jsonArray.getJSONObject(i).getInt("mention");
                redditNegative += jsonArray.getJSONObject(i).getInt("positiveMention");
                redditPositive += jsonArray.getJSONObject(i).getInt("negativeMention");
            }
            jsonArray = social.getJSONArray("twitter");
            for(int i = 0; i < jsonArray.length(); i++) {
                twitterTotal += jsonArray.getJSONObject(i).getInt("mention");
                twitterNegative += jsonArray.getJSONObject(i).getInt("positiveMention");
                twitterPositive += jsonArray.getJSONObject(i).getInt("negativeMention");
            }
            ((TextView) findViewById(R.id.social_twitter_total)).setText(String.valueOf(twitterTotal));
            ((TextView) findViewById(R.id.social_twitter_positive)).setText(String.valueOf(twitterPositive));
            ((TextView) findViewById(R.id.social_twitter_negative)).setText(String.valueOf(twitterNegative));
            ((TextView) findViewById(R.id.social_reddit_total)).setText(String.valueOf(redditTotal));
            ((TextView) findViewById(R.id.social_reddit_positive)).setText(String.valueOf(redditPositive));
            ((TextView) findViewById(R.id.social_reddit_negative)).setText(String.valueOf(redditNegative));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNews() {
        try {
            JSONObject jsonObjects[] = new JSONObject[20];
            int id = 0;
            for(int i = 0; i < news.length() && id < 20; i++) {
                if(!news.getJSONObject(i).getString("image").isEmpty()) {
                    jsonObjects[id] = news.getJSONObject(i);
                    id++;
                }
            }
            if(jsonObjects[0] == null)
                return;
            Glide.with(ProfileActivity.this).load(jsonObjects[0].getString("image")).into((ImageView) ProfileActivity.this.findViewById(R.id.news_image));
            Picasso.get().load(jsonObjects[0].getString("image")).into(findViewById(R.id.news_image), new Callback.EmptyCallback());
            ((TextView)findViewById(R.id.news_provider)).setText(jsonObjects[0].getString("source"));
            findViewById(R.id.headline).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    News news = new News(ProfileActivity.this, jsonObjects[0]);
                    news.show();
                }
            });
            long time = System.currentTimeMillis() / 1000;
            long newstime = jsonObjects[0].getLong("datetime");
            if(time - newstime > 3600)
                ((TextView)findViewById(R.id.news_time)).setText(((int) (time - newstime) / 3600) + " hours ago");
            else if (time - newstime > 60)
                ((TextView)findViewById(R.id.news_time)).setText(((int) (time - newstime) / 60) + " minutes ago");
            else
                ((TextView)findViewById(R.id.news_time)).setText((time - newstime) + " seconds ago");
            ((TextView)findViewById(R.id.news_title)).setText(jsonObjects[0].getString("headline"));
            for(int i = 1; i < 20; i++) {
                if(jsonObjects[i] == null)
                    return;
                else {
                    News news = new News(ProfileActivity.this, jsonObjects[i]);
                    View cardView = View.inflate(ProfileActivity.this, R.layout.news_layout, null);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            news.show();
                        }
                    });
                    Glide.with(cardView).load(jsonObjects[i].getString("image")).into((ImageView) cardView.findViewById(R.id.news_image));
                    ((TextView) cardView.findViewById(R.id.news_provider)).setText(jsonObjects[i].getString("source"));
                    time = System.currentTimeMillis() / 1000;
                    newstime = jsonObjects[i].getLong("datetime");
                    if(time - newstime > 3600)
                        ((TextView) cardView.findViewById(R.id.news_time)).setText(((int) (time - newstime) / 3600) + " hours ago");
                    else if (time - newstime > 60)
                        ((TextView) cardView.findViewById(R.id.news_time)).setText(((int) (time - newstime) / 60) + " minutes ago");
                    else
                        ((TextView) cardView.findViewById(R.id.news_time)).setText((time - newstime) + " seconds ago");
                    ((TextView) cardView.findViewById(R.id.news_title)).setText(jsonObjects[i].getString("headline"));
                    ((LinearLayout)findViewById(R.id.newslist)).addView(cardView);
                }
            }
        } catch (JSONException e) {
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