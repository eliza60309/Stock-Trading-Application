package com.skyhertz.hw9;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

//Hint 6.23 https://www.journaldev.com/23208/android-recyclerview-drag-and-drop
public class PortfolioListRecyclerViewAdapter extends RecyclerView.Adapter<PortfolioListRecyclerViewAdapter.MyViewHolder> implements PortfolioEntryMoveCallback.ItemTouchHelperContract {

    private ArrayList<PortfolioEntry> data;
    boolean rowMoved = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView stock_id;
        private TextView name;
        private TextView price;
        private TextView priceChange;
        private ImageView trend;
        private ImageButton goBtn;
        View rowView;

        public MyViewHolder(View itemView) {
            super(itemView);
            rowView = itemView;
            stock_id = itemView.findViewById(R.id.stock_id);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            priceChange = itemView.findViewById(R.id.price_change);
            trend = itemView.findViewById(R.id.trend);
            goBtn = itemView.findViewById(R.id.go_btn);
        }
    }

    public PortfolioListRecyclerViewAdapter(ArrayList<PortfolioEntry> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.stock_id.setText(data.get(position).get_stock_id());
        holder.name.setText(data.get(position).get_hold() + " shares");
        holder.goBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("STOCK_ID", holder.stock_id.getText());
                v.getContext().startActivity(intent);
            }
        });
        data.get(position).timer = new Timer();
        data.get(position).timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RequestCallbacks callbacks = new RequestCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            int position = holder.getAdapterPosition();
                            data.get(position).price = result.getDouble("c");
                            holder.price.setText("$" + Math.round(result.getDouble("c") * data.get(position).get_hold() * 100.0) / 100.0);
                            double d_total = Math.round((result.getDouble("c") - data.get(position).get_average()) * data.get(position).get_hold() * 100.0) / 100.0;
                            double cost_total = data.get(position).get_average() * data.get(position).get_hold() * 100.0 / 100.0;
                            holder.priceChange.setText("$" + d_total  + " (" + Math.round(d_total / cost_total * 10000.0) / 100.0 + "%)");
                            boolean trend_up = d_total > 0;
                            boolean trend_down = d_total < 0;
                            holder.trend.setVisibility(View.VISIBLE);
                            if(trend_up) {
                                holder.trend.setImageDrawable(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.trending_up));
                                holder.trend.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                                holder.priceChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                            }
                            else if(trend_down) {
                                holder.trend.setImageDrawable(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.trending_down));
                                holder.trend.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                                holder.priceChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                            }
                            else {
                                holder.trend.setVisibility(View.INVISIBLE);
                                holder.trend.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
                                holder.priceChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                GetRequest.get("quote", holder.stock_id.getText().toString(), new JSONObject(), callbacks, holder.itemView.getContext());
            }
        }, 0, 15000);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        rowMoved = true;
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        //myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        //myViewHolder.rowView.setBackgroundColor(Color.WHITE);
        if(rowMoved) {
            rowMoved = false;
            LocalStorage.savePortfolioStorage(MainActivity.portfolioList);
        }
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void delete(String stock_id) {
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).get_stock_id().equals(stock_id)) {
                data.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
        //data.remove(position);
        //notifyItemRemoved(position);
    }

    public void add(PortfolioEntry portfolioEntry) {
        data.add(portfolioEntry);
        notifyItemInserted(data.indexOf(portfolioEntry));
    }
}