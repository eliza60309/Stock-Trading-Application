package com.skyhertz.hw9;

import android.widget.Toast;

import org.json.JSONObject;

public interface RequestCallbacks {
    void onSuccess(JSONObject result);
    //void onError(String result);
}
