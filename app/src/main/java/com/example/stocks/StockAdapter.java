package com.example.stocks;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockAdapter extends RecyclerView.Adapter<StockHolder>{
    private final ArrayList<Stock> stocklist;
    private final MainActivity  mainActivity;

    public StockAdapter(MainActivity mainActivity, ArrayList<Stock> stocklist){
        this.mainActivity=mainActivity;
        this.stocklist = stocklist;

    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_list,parent,false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new StockHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        Stock s = stocklist.get(position);
        holder.stockname.setText(s.getStocks());
        holder.compname.setText(s.getComname());
        holder.value.setText(String.format(Locale.getDefault(),"%.2f",s.getValue()));
        Double vdiff = s.getVdiff();
        Double vpercent = s.getVpercent();
        String vvdiff = String.format(Locale.getDefault(), "%.2f",vdiff);
        String vvpercent = String.format(Locale.getDefault(), "%.2f",vpercent);
        if (vdiff < 0){
            holder.vdiff.setText("▼ " + vvdiff+ "("+ vvpercent+"%"+")");
            holder.vdiff.setTextColor(Color.RED);
            holder.stockname.setTextColor(Color.RED);
            holder.compname.setTextColor(Color.RED);
            holder.value.setTextColor(Color.RED);
        }
        else if (vdiff > 0){
            holder.vdiff.setText("▲ " + vvdiff+ "("+ vvpercent+"%"+")");
            holder.vdiff.setTextColor(Color.GREEN);
            holder.stockname.setTextColor(Color.GREEN);
            holder.compname.setTextColor(Color.GREEN);
            holder.value.setTextColor(Color.GREEN);
        }
        else if  (vdiff == 0){
            holder.vdiff.setText(vvdiff+ "("+ vvpercent+"%"+")");
            holder.vdiff.setTextColor(Color.WHITE);
            holder.stockname.setTextColor(Color.WHITE);
            holder.compname.setTextColor(Color.WHITE);
            holder.value.setTextColor(Color.WHITE);
        }
    }
    @Override
    public int getItemCount() {
        return stocklist.size();
    }
}
