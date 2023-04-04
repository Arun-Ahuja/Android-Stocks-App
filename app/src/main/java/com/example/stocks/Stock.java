package com.example.stocks;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;

public class Stock implements Serializable, Comparable<Stock>{
     private  String stocks, comname;
     private  double value, vdiff, vpercent;

    public Stock (String stocks, String comname, double value, double vdiff, double vpercent){
        this.stocks=stocks;
        this.comname = comname;
        this.vdiff = vdiff;
        this.vpercent = vpercent;
        this.value = value;
    }
    /*private static final String[] SNames =
            {"Liam", "Olivia","Noah","Emma","Oliver","Charlotte","Elijah","Amelia"};
    private static final String[] CNames =
            {"Smith", "Brown","Jones","Garcia","Patel","Zhang","Martinez","Lee"};
    public Stock() {
        this.stocks = SNames[(int) (Math.random() * SNames.length)];
        this.comname = CNames[(int) (Math.random() * CNames.length)];

        int scale = (int) Math.pow(10, 2);
        this.value = (double) Math.round(Math.random() * 4.0 * scale) / scale;
        this.value = (double) Math.round(Math.random() * 4.0 * scale) / scale;
        this.value = (double) Math.round(Math.random() * 4.0 * scale) / scale;
    }*/

    public String getStocks() {
        return stocks;
    }

    public void setStocks(String stocks) {
        this.stocks = stocks;
    }

    public String getComname() {
        return comname;
    }

    public void setComname(String comname) {
        this.comname = comname;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getVdiff() {
        return vdiff;
    }

    public void setVdiff(double vdiff) {
        this.vdiff = vdiff;
    }

    public double getVpercent() {
        return vpercent;
    }

    public void setVpercent(double vpercent) {
        this.vpercent = vpercent;
    }


    @NonNull
    @Override
    public String toString() {
        return "Stock{" +
                "stocks'" + stocks + '\''+
                ",comname'" + comname + '\'' +
                ", value=" + value +
                ",vdiff" + vdiff +
                ",vpercent" + vpercent +
                '}';
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("stocks", stocks);
        obj.put("comname", comname);
        obj.put("value", value);
        obj.put("vdiff", vdiff);
        obj.put("vpercent", vpercent);
        return obj;
    }

    public static Stock createFromJSON(JSONObject jsonObject) throws JSONException {
        String stocks = jsonObject.getString("stocks");
        String comname = jsonObject.getString("comname");
        double value = jsonObject.getInt("value");
        double vdiff = jsonObject.getInt("vdiff");
        double vpercent = jsonObject.getInt("vpercent");
        return new Stock(stocks,comname,value,vdiff,vpercent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stocks, comname);
    }

    @Override
    public int compareTo(Stock stock) {
        return comname.compareTo(stock.getComname());
    }


}
