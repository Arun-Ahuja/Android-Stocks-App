package com.example.stocks;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class SymboDownload {
    private static final String TAG = "SymboDownload";

    private static HashMap<String, String> mapname = new HashMap<>();
    private final String urltouse = "https://cloud.iexapis.com/stable/ref-data/symbols?token=pk_d73bd9ce4b21457a916103e8b05d8537";

    public void dodownload(MainActivity mainActivity){
        Log.d(TAG, "dodownload: ");
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(urltouse).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONArray> listener =
                response -> handleResults(mainActivity,response.toString());


        Response.ErrorListener error = error1 ->  {
            Log.d(TAG, "dodownload: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                handleResults(mainActivity, null);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        };
        JsonArrayRequest jsonArrayRequest =
                new JsonArrayRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        queue.add(jsonArrayRequest);

    }
    private static void handleResults(MainActivity mainActivity, String strr) {

        if (strr == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.downloadFailed();
            return;
        }
        mapname = parseJSON(strr);
        /* //mapname = parsjson();
        if (mapname != null)
            Toast.makeText(mainActivity, "Loaded " + mapname.size() + " countries.", Toast.LENGTH_LONG).show();
        mainActivity.updateData(mapname);*/
    }


    private static HashMap<String,String> parseJSON (String s){
        Log.d(TAG, "parsjson: this is check" + s);

        try {
            JSONArray obj = new JSONArray(s);
            for (int i = 0; i<obj.length(); i++){
                JSONObject jSto = (JSONObject) obj.get(i);
                String sym = jSto.getString("symbol");
                String name = jSto.getString("name");
                mapname.put(sym, name);
            }
            Log.d(TAG, "parsjson: "+ mapname);
        }
        catch (Exception e){
            Log.d(TAG, "Parse: "+e.getMessage());
        }
        return mapname;
    }

    public static HashMap<String, String> getMapname() {
        return mapname;
    }


    public static ArrayList<String> findMatches(String str) {
        String strToMatch = str.toLowerCase().trim();
        HashSet<String> matchSet = new HashSet<>();

        for (String sym : mapname.keySet()) {
            if (sym.toLowerCase().trim().contains(strToMatch)) {
                matchSet.add(sym + " - " + mapname.get(sym));
            }
            String name = mapname.get(sym);
            if (name != null &&
                    name.toLowerCase().trim().contains(strToMatch)) {
                matchSet.add(sym + " - " + name);
            }
        }
        ArrayList<String> results = new ArrayList<>(matchSet);
        Collections.sort(results);
        return results;
    }
}
