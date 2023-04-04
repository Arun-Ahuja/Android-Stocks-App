package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SwipeRefreshLayout.OnRefreshListener{
    public  ArrayList<Stock> stockList = new ArrayList<>();
    private int position;
    private final String tURL = "http://www.marketwatch.com/investing/stock/";
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private SwipeRefreshLayout swiper;
    private String text;
    private boolean first = true;
    private final String noData = "noData";
    private final String noStock = "noStock";
    private final String updatedStock = "Updated";
    private final String addedStock = "Added";
    private static final String TAG = "MainActivity";
    SymboDownload sb = new SymboDownload();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recler);;
        stockAdapter = new StockAdapter(this, stockList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(stockAdapter);
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this);

        if(!hasNetworkConnection()){
            loadDataFromFile();
            downloadFailed();
            noConnectionDialog(updatedStock);
            return;
        } else{
            loadDataFromFile();
            sb.dodownload(this);

        }





       // swiper.setOnRefreshListener(this);
        /*if (stockList.isEmpty()) { // Only does this if there is no file data
            for (int i = 0; i < 5; i++)
                stockList.add(new Stock());
            Collections.sort(stockList);
            stockAdapter.notifyItemRangeChanged(0, stockList.size());

        }*/

    }





    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addstack,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add){
            dialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        String sym = stockList.get(position).getStocks();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(tURL+sym));
        startActivity(i);

        /*mapnamee = sb.getMapname();
        Log.d(TAG, "onClick: "+ mapnamee);
        Toast.makeText(this, "this is undercontruction", Toast.LENGTH_SHORT).show();
*/    }

    @Override
    public boolean onLongClick(View v) {
        //Toast.makeText(this, "this is onlong press it is working", Toast.LENGTH_SHORT).show();
        int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock Symbol " + stockList.get(position).getStocks()+ "?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            stockList.remove(pos);
            saveDataToFile();
            stockAdapter.notifyItemRemoved(pos);
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public void onRefresh() {
        if(!hasNetworkConnection()){
            noConnectionDialog(updatedStock);
            swiper.setRefreshing(false);
            return;
        }
        stockList.clear();
        loadDataFromFile();
        swiper.setRefreshing(false);




        Toast.makeText(MainActivity.this, "It is working", Toast.LENGTH_SHORT).show();
        swiper.setRefreshing(false);
    }

    private void noConnectionDialog(String function){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks Cannot Be "+ function +" Without A Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void dialog() {

        if (!hasNetworkConnection()){
            noConnectionDialog(addedStock);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);


        // lambda can be used here (as is below)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                text = et.getText().toString();
                final ArrayList<String> results = sb.findMatches(text);
                Toast.makeText(MainActivity.this, "Hello: " + results, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: this is posative"+results);

                if (results.size() == 0) {
                    doNoAnswer(text, noStock);
                } else if (results.size() == 1) {
                    doSelection(results.get(0));
                } else {
                    String[] array = results.toArray(new String[0]);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("Make a selection");
                    builder1.setItems(array, (dialog1, whch) -> {
                        String symbol = results.get(whch);
                        doSelection(symbol);
                    });
                    builder1.setNegativeButton("Nevermind", (dialog1, id1) -> {

                    });
                    AlertDialog dialog2 = builder1.create();
                    dialog2.show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> {
        });
        builder.setMessage("Stock Selection");
        builder.setTitle("Please enter a Stock symbol");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doNoAnswer(String symbol, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Symbol Not Found: " + symbol);
        if(message.equals(noStock)){
            builder.setMessage("Data for stock symbol");
        }else if(message.equals(noData)){
            builder.setMessage("No data for selection");
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSelection(String sym) {
        String[] data = sym.split("-");
        FinanceDownload finandown = new FinanceDownload(this, data[0].trim());
        finandown.alldownload(this,data[0].trim());

    }



    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void downloadFailed() {
        //stockList.clear();
        if(stockList.size() == 0)  return;
        for(Stock stk : stockList){
            stk.setVpercent(0.0);
            stk.setValue(0.00);
            stk.setVdiff(0.00);
        }
        stockAdapter.notifyItemRangeChanged(0, stockList.size());
    }

    public void updateData(Stock st) {
        stockList.add(st);
        stockAdapter.notifyItemRangeChanged(0, stockList.size());
    }

    private void saveDataToFile() {
        Log.d(TAG, "saveDataToFile: " + stockList.size());

        // Make JSONArray
        JSONArray jsonArray = new JSONArray();
        for (Stock n : stockList) {
            try {
                jsonArray.put(n.toJSON());
            } catch (JSONException e) {
                Log.d(TAG, "saveDataToFile: " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("JSONText.json", MODE_PRIVATE);
            PrintWriter pr = new PrintWriter(fos);
            pr.println(jsonArray);
            pr.close();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "saveDataToFile: " + e.getMessage());
            e.printStackTrace();
        }
        Log.e(TAG, "saveDataToFile: " + jsonArray);
    }

    private void loadDataFromFile() {
        FileInputStream fis;
        try {
            fis = getApplicationContext().openFileInput("JSONText.json");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadDataFromFile: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        StringBuilder fileContent = new StringBuilder();

        try {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            JSONArray jsonArray = new JSONArray(fileContent.toString());
            Log.d(TAG, "readFromFile: ");

            /*if(!hasNetworkConnection()){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject cObj = jsonArray.getJSONObject(i);
                    String name = cObj.getString("stocks");
                    String symbol = cObj.getString("comname");
                    Stock s = new Stock(symbol, name,0.0,0.0,0.0);
                    stockList.add(s);
                }
                return;
            }*/
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cObj = jsonArray.getJSONObject(i);

                Log.e(TAG, "loadDataFromFile: object " + cObj );


                // Access note data fields
                String symbol = cObj.getString("stocks");
                String  name = cObj.getString("comname");
               // FinanceDownload fd = new FinanceDownload(this,symbol);
                if(hasNetworkConnection()){
                    FinanceDownload.alldownload(this,symbol);
                }
                else{
                     addStock(new Stock(symbol, name, 0.00, 0.0,0.00));
                }
                Log.e(TAG, "loadDataFromFile: "+ stockList );

                //Log.d(TAG, "loadFile: " + name);
                // Create Stock and add to temporary ArrayList

                //Stock s = new Stock(symbol, name,latestPrice,changePercent,change);
               // stockList.add(fd);
                //tmpStockList.add(s);
              //  Log.e(TAG, "loadDataFromFile:"+ s.toJSON() );
            }

        } catch (Exception e) {
            Log.d(TAG, "loadDataFromFile: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        Log.e(TAG, "loadDataFromFile: " + stockList.size());
    }

    public void addStock(Stock stock){
        if (stock == null) {
            doNoAnswer(text, noData);
            return;
        }

        for(Stock stk : stockList){
            if(stk.getStocks().contains(stock.getStocks())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Stock Symbol " + stock.getStocks() + " is already displayed");
                builder.setTitle("Duplicate Stock");

                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
        }

        //if (stockList.contains(stock)) {
        //}
        stockList.add(stock);
        Collections.sort(stockList);
        stockAdapter.notifyDataSetChanged();
        saveDataToFile();
    }





}