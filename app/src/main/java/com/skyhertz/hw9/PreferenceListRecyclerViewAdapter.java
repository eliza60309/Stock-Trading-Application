package com.skyhertz.hw9;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
public class PreferenceListRecyclerViewAdapter extends RecyclerView.Adapter<PreferenceListRecyclerViewAdapter.MyViewHolder> implements PreferenceEntryMoveCallback.ItemTouchHelperContract {

    private ArrayList<PreferenceEntry> data;
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

    public PreferenceListRecyclerViewAdapter(ArrayList<PreferenceEntry> data) {
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
        holder.name.setText(data.get(position).get_name());
        holder.goBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("STOCK_ID", holder.stock_id.getText());
                v.getContext().startActivity(intent);
            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RequestCallbacks callbacks = new RequestCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            holder.price.setText("$" + Math.round(result.getDouble("c") * 100.0) / 100.0);
                            holder.priceChange.setText("$" + Math.round(result.getDouble("d") * 100.0) / 100.0 + " (" + Math.round(result.getDouble("dp") * 100.0) / 100.0 + "%)");
                            boolean trend_up = Math.round(result.getDouble("d") * 100.0) > 0;
                            boolean trend_down = Math.round(result.getDouble("d") * 100.0) < 0;
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
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
        if(rowMoved) {
            rowMoved = false;
            //MainActivity.mainActivity.updatePreferenceList();

        }
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        //LocalStorage.savePreferenceStorage(MainActivity.preferenceList);
    }

    public void add(PreferenceEntry preferenceEntry) {
        data.add(preferenceEntry);
        notifyItemInserted(data.indexOf(preferenceEntry));
        //LocalStorage.savePreferenceStorage(MainActivity.preferenceList);
    }
}