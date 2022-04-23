package com.skyhertz.hw9;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocalStorage {
    public static MainActivity mainActivity;

    public LocalStorage(MainActivity instance) {
        mainActivity = instance;
    }

    public static ArrayList<PreferenceEntry> loadPreferenceStorage() {
        SharedPreferences getter = mainActivity.getSharedPreferences("Data", 0);
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

    public static void savePreferenceStorage(ArrayList<PreferenceEntry> arrayList) {
        SharedPreferences.Editor setter = mainActivity.getSharedPreferences("Data", 0).edit();
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

    public static void clearPreferenceStorage() {
        SharedPreferences.Editor setter = mainActivity.getSharedPreferences("Data", 0).edit();
        setter.remove("Preference");
        setter.commit();
        System.out.println("REMOVE PREFERENCE");
    }



    public static ArrayList<PortfolioEntry> loadPortfolioStorage() {
        SharedPreferences getter = mainActivity.getSharedPreferences("Data", 0);
        ArrayList<PortfolioEntry> arrayList = new ArrayList<PortfolioEntry>();
        if (getter.getString("Portfolio", null) == null || getter.getString("Portfolio", null).isEmpty()) {
            return arrayList;
        }
        System.out.println("GET PORTFOLIO: " + getter.getString("Portfolio", null));
        try {
            JSONArray jsonArray = new JSONArray(getter.getString("Portfolio", null));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PortfolioEntry portfolioEntry = new PortfolioEntry(jsonObject.getString("stock_id"), jsonObject.getString("name"), jsonObject.getInt("hold"), jsonObject.getDouble("average"));
                arrayList.add(portfolioEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static void savePortfolioStorage(ArrayList<PortfolioEntry> arrayList) {
        SharedPreferences.Editor setter = mainActivity.getSharedPreferences("Data", 0).edit();
        try {
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < arrayList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("stock_id", arrayList.get(i).get_stock_id());
                jsonObject.put("name", arrayList.get(i).get_name());
                jsonObject.put("hold", arrayList.get(i).get_hold());
                jsonObject.put("average", arrayList.get(i).get_average());
                jsonArray.put(jsonObject);
            }
            setter.putString("Portfolio", jsonArray.toString());
            setter.commit();
            System.out.println("SET PORTFOLIO: " + jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void clearPortfolioStorage() {
        SharedPreferences.Editor setter = mainActivity.getSharedPreferences("Data", 0).edit();
        setter.remove("Portfolio");
        setter.commit();
        System.out.println("REMOVE PORTFOLIO");

    }



    public static double loadCashStorage() {
        SharedPreferences getter = mainActivity.getSharedPreferences("Data", 0);
        if (getter.getString("Cash", null) == null) {
            return 25000;
        }
        return Float.valueOf(getter.getString("Cash", null));
    }

    public static void saveCashStorage(double cash) {
        SharedPreferences.Editor setter = mainActivity.getSharedPreferences("Data", 0).edit();
        setter.putString("Cash", String.valueOf(cash));
        setter.commit();
        System.out.println("SET CASH: " + cash);
    }

    public static void resetCashStorage() {
        SharedPreferences.Editor setter = mainActivity.getSharedPreferences("Data", 0).edit();
        setter.remove("Cash");
        setter.commit();
        System.out.println("REMOVE CASH");

    }

}
