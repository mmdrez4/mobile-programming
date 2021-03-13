package ir.madeinlobb.hw1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.EventLogTags;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static int start = 1;
    public static String symbol;

    private static int cores = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executor = Executors.newFixedThreadPool(cores + 1);

    private int mProgressStatus = 0;
    ProgressBar progressBar;
    LinearLayout mainLayout;
    final boolean[] firstTime = {true};
    LinearLayout barLayout;
    ImageButton imageButton;
    ScrollView scrollView;
    LinearLayout coinsLayout;
    HandlerThread handlerThread = new HandlerThread("handlerThread");
    Button addCoins;
    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barLayout = findViewById(R.id.bar);
        imageButton = findViewById(R.id.coin_image);

        mainLayout = findViewById(R.id.main_layout);
        scrollView = findViewById(R.id.scroll_view);

        coinsLayout = findViewById(R.id.coin_layouts);

        progressBar = findViewById(R.id.progress_circular);

        Button refreshButton = findViewById(R.id.refresh);

        addCoins = findViewById(R.id.add_coin);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressStatus = 0;
                mainLayout.removeAllViews();
                mainLayout.addView(barLayout);
                mainLayout.addView(coinsLayout);

                progressBar.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        getWebService(2);
                        while (mProgressStatus < 100) {
                            mProgressStatus++;
                            android.os.SystemClock.sleep(50);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(mProgressStatus);
                                }
                            });
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });

            }
        });

        addCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    Log.d("CONNECTION", " ok");
                    getWebService(1);
                }else {
                    Log.d("CONNECTION", "not ok");
                    updateLinearLayoutFromFile(MainActivity.this);
                }
            }
        });

    }

    public void stop(){
        executor.shutdown();
    }

    private synchronized void getWebService(final int status) {

        final TextView coinName = findViewById(R.id.coin_name);
        final TextView coinPrice = findViewById(R.id.coin_price);
        final TextView hc = findViewById(R.id.hour_changes);
        final TextView dc = findViewById(R.id.day_changes);
        final TextView wc = findViewById(R.id.week_changes);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                int end = start + 4;
                String url;
                if (status == 1) {
                    Log.d("START1", String.valueOf(start));
                    url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=" + start + "&limit=" + end + "&aux=platform&cryptocurrency_type=coins";
                    Log.d("START2", String.valueOf(start));
                } else {
                    url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=" + 1 + "&limit=" + end + "&aux=platform&cryptocurrency_type=coins";
                }
                client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .addHeader("X-CMC_PRO_API_KEY", "32d8965f-ed31-4925-975b-da24cf243138")
                        .addHeader("Cookie", "__cfduid=d27e1c676eafe6c7134bd57d707fcae1c1615039668")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("data");

                    JSONObject object2;
                    for (int i = 0; i < Jarray.length(); i++) {

                        JSONObject object = Jarray.getJSONObject(i);
                        final String name = object.getString("name");
                        final String symbol = object.getString("symbol");
                        int id = object.getInt("id");
                        object2 = object.getJSONObject("quote").getJSONObject("USD");
                        final int price = object2.getInt("price");
                        final int changeHour = object2.getInt("percent_change_1h");
                        final int changeDay = object2.getInt("percent_change_24h");
                        final int changeWeek = object2.getInt("percent_change_7d");
                        final String logo = "https://s2.coinmarketcap.com/static/img/coins/64x64/" + id + ".png";

                        String data = logo + "-" + symbol + "-" + name + "-" + price + "-" + changeHour + "-" + changeDay + "-" + changeWeek;
                        writeToFile(data, MainActivity.this);

                        if (status == 2) {
                            firstTime[0] = true;
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstTime[0]) {
                                    if (status == 2) {
                                        coinsLayout.setBackgroundColor(Color.GREEN);
                                    }
                                    coinName.setText(symbol + "|" + name);
                                    coinPrice.setText(price + "$");
                                    hc.setText("1h: " + changeHour + "%");
                                    dc.setText("1d: " + changeDay + "%");
                                    wc.setText("1w: " + changeWeek + "%");
                                    coinsLayout.setVisibility(View.VISIBLE);
                                    Glide.with(MainActivity.this)
                                            .load(logo)
                                            .into(imageButton);
                                    imageButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            setSymbol(symbol);
                                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                        }
                                    });
                                    firstTime[0] = false;

                                } else {
                                    LayoutInflater vi = (LayoutInflater) MainActivity.this
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = vi.inflate(R.layout.activity_main, null);

                                    final LinearLayout linearLayout = v.findViewById(R.id.coin_layouts);

                                    if (linearLayout.getParent() != null) {
                                        ((ViewGroup) linearLayout.getParent()).removeView(linearLayout); // <- fix
                                    }
                                    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    params.setMargins(0, 10, 0, 10);

                                    params.gravity = Gravity.CENTER_VERTICAL;
                                    mainLayout.setOrientation(LinearLayout.VERTICAL);

                                    ((TextView) linearLayout.findViewById(R.id.coin_name)).setText(symbol + " | " + name);
                                    ((TextView) linearLayout.findViewById(R.id.coin_price)).setText(price + " $");
                                    ((TextView) linearLayout.findViewById(R.id.hour_changes)).setText("1h: " + changeHour + "%");
                                    ((TextView) linearLayout.findViewById(R.id.day_changes)).setText("1d: " + changeDay + "%");
                                    ((TextView) linearLayout.findViewById(R.id.week_changes)).setText("1w: " + changeWeek + "%");
                                    Glide.with(MainActivity.this)
                                            .load(logo)
                                            .into(((ImageButton) linearLayout.findViewById(R.id.coin_image)));
                                    ((ImageButton) linearLayout.findViewById(R.id.coin_image)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            setSymbol(symbol);
                                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                        }
                                    });
                                    linearLayout.setVisibility(View.VISIBLE);
                                    if (status == 2) {
                                        linearLayout.setBackgroundColor(Color.GREEN);
                                    }
                                    mainLayout.addView(linearLayout, params);
                                }
                            }
                        });

                        Log.d("MMD2", name + "-" + symbol + "-" + price + "-" + changeHour + "-" + changeDay + "-" + changeWeek);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        start += 5;

    }

    private synchronized void writeToFile(final String data, final Context context) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("coins.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
            }
        });
    }

    private String readFromFile(Context context) {
        Log.d("READ-FROM-FILE", "im here");
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("coins.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void updateLinearLayoutFromFile(MainActivity mainActivity) {
        Log.d("UPDATE-LAYOUT-FILE", "im here");
        String[] string = readFromFile(mainActivity).split("\n");

        final TextView coinName = findViewById(R.id.coin_name);
        final TextView coinPrice = findViewById(R.id.coin_price);
        final TextView hc = findViewById(R.id.hour_changes);
        final TextView dc = findViewById(R.id.day_changes);
        final TextView wc = findViewById(R.id.week_changes);

        for (String s : string) {
            final String[] coinInfo = s.split("-");

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (firstTime[0]) {
                        coinName.setText(coinInfo[1] + "|" + coinInfo[2]);
                        coinPrice.setText(coinInfo[3] + "$");
                        hc.setText("1h: " + coinInfo[4] + "%");
                        dc.setText("1d: " + coinInfo[5] + "%");
                        wc.setText("1w: " + coinInfo[6] + "%");
                        coinsLayout.setVisibility(View.VISIBLE);
                        Glide.with(MainActivity.this)
                                .load(coinInfo[0])
                                .into(imageButton);
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setSymbol(symbol);
                                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                            }
                        });
                        firstTime[0] = false;

                    } else {
                        LayoutInflater vi = (LayoutInflater) MainActivity.this
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = vi.inflate(R.layout.activity_main, null);

                        final LinearLayout linearLayout = v.findViewById(R.id.coin_layouts);

                        if (linearLayout.getParent() != null) {
                            ((ViewGroup) linearLayout.getParent()).removeView(linearLayout); // <- fix
                        }
                        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        params.setMargins(0, 10, 0, 10);

                        params.gravity = Gravity.CENTER_VERTICAL;
                        mainLayout.setOrientation(LinearLayout.VERTICAL);

                        ((TextView) linearLayout.findViewById(R.id.coin_name)).setText(coinInfo[1] + "|" + coinInfo[2]);
                        ((TextView) linearLayout.findViewById(R.id.coin_price)).setText(coinInfo[3] + "$");
                        ((TextView) linearLayout.findViewById(R.id.hour_changes)).setText("1h: " + coinInfo[4] + "%");
                        ((TextView) linearLayout.findViewById(R.id.day_changes)).setText("1d: " + coinInfo[5] + "%");
                        ((TextView) linearLayout.findViewById(R.id.week_changes)).setText("1w: " + coinInfo[6] + "%");
                        Glide.with(MainActivity.this)
                                .load(coinInfo[0])
                                .into(((ImageButton) linearLayout.findViewById(R.id.coin_image)));
                        ((ImageButton) linearLayout.findViewById(R.id.coin_image)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setSymbol(symbol);
                                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                            }
                        });
                        linearLayout.setVisibility(View.VISIBLE);
                        mainLayout.addView(linearLayout, params);
                    }
                }
            });
        }


    }

    private boolean checkConnection(){
//        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
//        return netInfo == null;

        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            return netInfo.isConnected();
        } else {
            return false;
        }
    }

    private static void setSymbol(String symbol){
        MainActivity.symbol = symbol;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}