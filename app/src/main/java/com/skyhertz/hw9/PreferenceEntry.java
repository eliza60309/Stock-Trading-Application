package com.skyhertz.hw9;

public class PreferenceEntry {
    private String _name;
    private String _stock_id;
    public PreferenceEntry(String stock_id, String name) {
        _name = name;
        _stock_id = stock_id;
    }

    public String get_name() {
        return _name;
    }

    public String get_stock_id() {
        return _stock_id;
    }
}
