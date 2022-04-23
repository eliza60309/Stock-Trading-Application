package com.skyhertz.hw9;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class News extends Dialog {
    public News(@NonNull Context context, JSONObject jsonObject) {
        super(context);
        setContentView(R.layout.dialog_news);

        try {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout((int) (context.getResources().getDisplayMetrics().widthPixels * 0.9), (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5));
            ((TextView) getWindow().findViewById(R.id.provider)).setText(jsonObject.getString("source"));
            SimpleDateFormat dtf = new SimpleDateFormat("MMMM dd, yyyy");
            ((TextView) getWindow().findViewById(R.id.time)).setText(dtf.format(new Date(jsonObject.getLong("datetime") * 1000)));
            ((TextView) getWindow().findViewById(R.id.headline)).setText(jsonObject.getString("headline"));
            ((TextView) getWindow().findViewById(R.id.intel)).setText(jsonObject.getString("summary"));
            jsonObject.getString("url");
            getWindow().findViewById(R.id.chrome).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //6.13 https://www.tutorialkart.com/kotlin-android/android-open-url-in-browser-activity/
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    try {
                        openURL.setData(Uri.parse(jsonObject.getString("url")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    view.getContext().startActivity(openURL);
                }
            });

            getWindow().findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    try {
                        openURL.setData(Uri.parse("https://twitter.com/intent/tweet?text=Check out this link: " + jsonObject.getString("url")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    view.getContext().startActivity(openURL);
                }
            });

            getWindow().findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    try {
                        openURL.setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=" + jsonObject.getString("url")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    view.getContext().startActivity(openURL);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
