package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockHolder extends RecyclerView.ViewHolder {
    TextView stockname, compname, value, vdiff;

    public StockHolder(View view){
        super(view);
        stockname = view.findViewById(R.id.sname);
        compname = view.findViewById(R.id.cname);
        value = view.findViewById(R.id.value);
        vdiff = view.findViewById(R.id.diff);
    }
}
