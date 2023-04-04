package com.example.stocks;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FinanceDownload {
    private static final String url1 = "https://cloud.iexapis.com/stable/stock/";
    private static final String url2 = "/quote?token=pk_d73bd9ce4b21457a916103e8b05d8537";
    private final MainActivity mainActivity;
    private  final String find;
    private static final String TAG = "FinanceDownload";

    public FinanceDownload(MainActivity mainActivity, String find){
        this.mainActivity = mainActivity;
        this.find = find;
    }

    public static void alldownload(MainActivity mainActivity, String sname){
        Log.d(TAG, "alldownload: ");
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(url1).buildUpon();
        String urlToUse = buildURL.build().toString();
        urlToUse = urlToUse +  sname + url2;
        Log.d(TAG, "alldownload: " +urlToUse);

        Response.Listener<JSONObject> listener =
                response -> parsfinace(mainActivity,response.toString());


        Response.ErrorListener error = error1 ->  {
            Log.d(TAG, "dodownload: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                parsfinace( mainActivity,null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        };
        JsonObjectRequest jsonArrayRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        queue.add(jsonArrayRequest);

    }

    private static void parsfinace (MainActivity mainActivity, String s){
        try {
            JSONObject objt = new JSONObject(s);


            String name = "None";
            if (objt.has("companyName"))
                name = objt.getString("companyName");

            String symbol="None";
            if(objt.has("symbol"))
                symbol = objt.getString("symbol");


            String lv = objt.getString("latestPrice");
            Double lvalue = 0.0;
            if (objt.has("latestPrice"))
                lvalue = Double.parseDouble(lv);


            String cp = objt.getString("changePercent");
            Double chvalue = 0.0;
            if (objt.has("changePercent"))
                chvalue = Double.parseDouble(cp);


            String change = objt.getString("change");
            Double changevalu = 0.0;
            if (objt.has("change"))
                changevalu = Double.parseDouble(change);


            Stock st = new Stock(symbol,name,lvalue,changevalu,chvalue);
       // mainActivity.updateData(st);
        mainActivity.addStock(st);
        }

        catch (Exception e){
            Log.d(TAG, "parsfinace: "+ e.getMessage());
        }
    }





}
