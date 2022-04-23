package com.skyhertz.hw9;

import java.util.Timer;
import java.util.TimerTask;

public class PortfolioEntry {
    private String _name;
    private String _stock_id;
    private int _hold;
    private double _average;
    Timer timer;
    double price;
    public PortfolioEntry(String stock_id, String name, int amount, double average) {
        _name = name;
        _stock_id = stock_id;
        _hold = amount;
        _average = average;
    }

    public void buy(int amount, double price) {
        _average = _hold * _average + amount * price;
        _hold += amount;
        _average /= _hold;
    }

    public void sell(int amount, double price) {
        _average = _hold * _average - amount * price;
        _hold -= amount;
        _average /= _hold;
    }

    public int get_hold() { return _hold; }

    public double get_average() { return _average; }

    public String get_name() {
        return _name;
    }

    public String get_stock_id() {
        return _stock_id;
    }
}
