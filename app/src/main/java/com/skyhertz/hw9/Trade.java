package com.skyhertz.hw9;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Trade {

    int input;

    public Trade(Dialog dialog, PortfolioEntry portfolioEntry, double price, ProfileActivity profileActivity) {
        ((EditText) dialog.findViewById(R.id.editTextNumber)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    input = Integer.parseInt(editable.toString());
                    ((TextView) dialog.findViewById(R.id.hint)).setText(input + "*" + Math.round(price * 100.0) / 100.0 + "/share = " + Math.round(price * input * 100.0) / 100.0);
                }
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((int) (profileActivity.getResources().getDisplayMetrics().widthPixels * 0.9), (int) (profileActivity.getResources().getDisplayMetrics().heightPixels * 0.5));
        ((TextView) dialog.findViewById(R.id.hint)).setText("0*" + Math.round(price * 100.0) / 100.0 + "/share = 0.00");
        ((TextView) dialog.findViewById(R.id.hint2)).setText("$" + Math.round(MainActivity.mainActivity.cash * 100.0) / 100.0 + " to buy " + portfolioEntry.get_stock_id());
        ((TextView) dialog.findViewById(R.id.title)).setText("Trade " + portfolioEntry.get_name() + " shares");
        dialog.findViewById(R.id.buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input <= 0) {
                    Toast.makeText(view.getContext(), "Cannot buy non-positive shares", Toast.LENGTH_LONG).show();
                }
                else if(input * price > MainActivity.mainActivity.cash) {
                    Toast.makeText(view.getContext(), "Not enough money to buy", Toast.LENGTH_LONG).show();
                }
                else {
                    MainActivity.mainActivity.cash -= input * price;
                    LocalStorage.saveCashStorage(MainActivity.mainActivity.cash);
                    MainActivity.mainActivity.updateCash();
                    portfolioEntry.buy(input, price);
                    if(MainActivity.mainActivity.findPortfolio(portfolioEntry.get_stock_id()) == null) {
                        MainActivity.mainActivity.portfolioListRecyclerViewAdapter.add(portfolioEntry);
                    }
                    LocalStorage.savePortfolioStorage(MainActivity.portfolioList);
                    profileActivity.setPortfolio();
                    Dialog dialog1 = new Dialog(profileActivity);
                    dialog1.setContentView(R.layout.dialog_congrats);
                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog1.getWindow().setLayout((int) (profileActivity.getResources().getDisplayMetrics().widthPixels * 0.9), (int) (profileActivity.getResources().getDisplayMetrics().heightPixels * 0.5));
                    dialog1.show();
                    ((TextView) dialog1.findViewById(R.id.log)).setText("You have successfully bought " + input + "\n share of " + portfolioEntry.get_stock_id());
                    dialog1.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });
                    dialog.dismiss();
                }
            }
        });

        dialog.findViewById(R.id.sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input <= 0) {
                    Toast.makeText(view.getContext(), "Cannot sell non-positive shares", Toast.LENGTH_LONG).show();
                }
                else if(input > portfolioEntry.get_hold()) {
                    Toast.makeText(view.getContext(), "Not enough shares to sell", Toast.LENGTH_LONG).show();
                }
                else {
                    MainActivity.mainActivity.cash += input * price;
                    LocalStorage.saveCashStorage(MainActivity.mainActivity.cash);
                    MainActivity.mainActivity.updateCash();
                    portfolioEntry.sell(input, price);
                    if(MainActivity.mainActivity.findPortfolio(portfolioEntry.get_stock_id()).get_hold() == 0) {
                        MainActivity.mainActivity.portfolioListRecyclerViewAdapter.delete(portfolioEntry.get_stock_id());
                    }
                    LocalStorage.savePortfolioStorage(MainActivity.portfolioList);
                    profileActivity.setPortfolio();
                    Dialog dialog1 = new Dialog(profileActivity);
                    dialog1.setContentView(R.layout.dialog_congrats);
                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog1.getWindow().setLayout((int) (profileActivity.getResources().getDisplayMetrics().widthPixels * 0.9), (int) (profileActivity.getResources().getDisplayMetrics().heightPixels * 0.5));
                    dialog1.show();
                    ((TextView) dialog1.findViewById(R.id.log)).setText("You have successfully sold " + input + "\n share of " + portfolioEntry.get_stock_id());
                    dialog1.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });
                    dialog.dismiss();
                }
            }
        });
    }
}
