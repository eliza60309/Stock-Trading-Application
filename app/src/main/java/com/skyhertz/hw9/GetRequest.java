package com.skyhertz.hw9;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GetRequest {
    private static GetRequest instance;
    private RequestQueue requestQueue;
    private static Context app;

    /*TODO: Image Loader?*/

    private GetRequest(Context context) {
        app = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(app);
    }


    public static synchronized GetRequest getInstance(Context ctx) {
        if(instance == null) {
            instance = new GetRequest(ctx);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }

    public static void get(String method, String STOCK_ID, JSONObject OTHER_ARGS, RequestCallbacks callbacks, Context context) {
        String url = String.format("https://hw789etc-343408.wl.r.appspot.com/api/%1$s?STOCK_ID=%2$s",
            method,
            STOCK_ID);
        Iterator<String> keys = OTHER_ARGS.keys();
        try {
            while(keys.hasNext()) {
                String key = keys.next();
                String value = OTHER_ARGS.getString(key.toString());
                url = url + '&' + key + '=' + value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    callbacks.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.toString());
                    //callbacks.onError(error.toString());
                }
            });
        GetRequest.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void getArray(String method, String STOCK_ID, JSONObject OTHER_ARGS, RequestArrayCallbacks callbacks, Context context) {
        String url = String.format("https://hw789etc-343408.wl.r.appspot.com/api/%1$s?STOCK_ID=%2$s",
                method,
                STOCK_ID);
        Iterator<String> keys = OTHER_ARGS.keys();
        try {
            while(keys.hasNext()) {
                String key = keys.next();
                String value = OTHER_ARGS.getString(key.toString());
                url = url + '&' + key + '=' + value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println(url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callbacks.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                        //callbacks.onError(error.toString());
                    }
                });
        GetRequest.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
