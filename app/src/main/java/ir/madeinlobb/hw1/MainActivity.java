package ir.madeinlobb.hw1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button openSecondActivity;
    //    CircularProgressButton circularProgressButton;
    private Button refreshButton;
    private int mProgressStatus = 0;
    ProgressBar progressBar;
    LinearLayout mainLayout;
    ImageButton imageButton;
    ScrollView scrollView;
    LinearLayout coinsLayout;
    TextView textView;
    HandlerThread handlerThread = new HandlerThread("handlerThread");
    Gson gson;
    Button addCoins;
    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Handler handler = new Handler();
        private Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                //
            }
        };
        handler.postDelayed(runnableCode, 2000);
 */

        imageButton = findViewById(R.id.coin_image);

        mainLayout = findViewById(R.id.main_layout);
        scrollView = findViewById(R.id.scroll_view);

        coinsLayout = findViewById(R.id.coin_layouts);

//        circularProgressButton = (CircularProgressButton)findViewById(R.id.refresh);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SecondActivity.class));

            }
        });

        progressBar = findViewById(R.id.progress_circular);

        refreshButton = findViewById(R.id.refresh);


        addCoins = findViewById(R.id.add_coin);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                }).start();
            }
        });

        addCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebService();
            }
        });


//        CountDownTimer countDownTimer = new CountDownTimer(10000,1000) {
//            @Override
//            public void onTick(long remainingTime) {
//                Log.d("TAG","countDownTimer" + remainingTime);
//            }
//
//            @Override
//            public void onFinish() {
//                Log.d("TAG","countDownTimer onFinish");
//            }
//        };
//        countDownTimer.start();
//
//        textView = findViewById(R.id.text);
//        button = findViewById(R.id.buttonPanel);
//        first = findViewById(R.id.myEditText);
//
//        Log.d("NUM",textView.getText().toString());
//
//        Toast toast = Toast.makeText(MainActivity.this,"ready",Toast.LENGTH_LONG);
//        toast.show();
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = first.getText().toString();
//                textView.setText("salam" + name);
//            }
//        });
    }

    private void getWebService() {
        final boolean[] firstTime = {true};
        final TextView coinName = findViewById(R.id.coin_name);
        final TextView coinPrice = findViewById(R.id.coin_price);
        final TextView hc = findViewById(R.id.hour_changes);
        final TextView dc = findViewById(R.id.day_changes);
        final TextView wc = findViewById(R.id.week_changes);
        //                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
        Thread thread = new Thread() {
            @Override
            public void run() {
                client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=10&aux=platform&cryptocurrency_type=coins")
                        .method("GET", null)
                        .addHeader("X-CMC_PRO_API_KEY", "32d8965f-ed31-4925-975b-da24cf243138")
                        .addHeader("Cookie", "__cfduid=d27e1c676eafe6c7134bd57d707fcae1c1615039668")
                        .build();

//                String imageUrl = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/info?id=" + id + "&aux=logo";
//                Request requestForImage = new Request.Builder()
//                        .url(imageUrl)
//                        .addHeader("X-CMC_PRO_API_KEY", "32d8965f-ed31-4925-975b-da24cf243138")
//                        .addHeader("Cookie", "__cfduid=d27e1c676eafe6c7134bd57d707fcae1c1615039668")
//                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("data");

//                    Response responseForImage = client.newCall(requestForImage).execute();
//                    Jobject = new JSONObject(response.body().string());
//                    Jarray = Jobject.getJSONArray("data");


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


                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstTime[0]) {
//                                finalLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    coinName.setText(symbol + "|" + name);
                                    coinPrice.setText(price + "$");
                                    hc.setText("1h: " + changeHour + "%");
                                    dc.setText("1d: " + changeDay + "%");
                                    wc.setText("1w: " + changeWeek + "%");
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

                                    params.gravity = Gravity.CENTER_VERTICAL;
                                    mainLayout.setOrientation(LinearLayout.VERTICAL);

                                    ((TextView) linearLayout.findViewById(R.id.coin_name)).setText(symbol + "|" + name);
                                    ((TextView) linearLayout.findViewById(R.id.coin_price)).setText(price + "$");
                                    ((TextView) linearLayout.findViewById(R.id.hour_changes)).setText("1h: " + changeHour + "%");
                                    ((TextView) linearLayout.findViewById(R.id.day_changes)).setText("1d: " + changeDay + "%");
                                    ((TextView) linearLayout.findViewById(R.id.week_changes)).setText("1w: " + changeWeek + "%");
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
        };
        thread.start();
    }

    private void getWebService2() {
        final TextView coinName = findViewById(R.id.coin_name);
        TextView coinPrice = findViewById(R.id.coin_price);

        OkHttpClient client = new OkHttpClient();
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=2&aux=platform&cryptocurrency_type=coins";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONObject Jobject = null;
                    try {
                        Jobject = new JSONObject(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray Jarray = Jobject.getJSONArray("data");
                        for (int i = 0; i < Jarray.length(); i++) {
                            JSONObject object = Jarray.getJSONObject(i);
                            String name = object.getString("name");
                            String symbol = object.getString("symbol");
                            Log.d("TAG", name + " : " + symbol);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            coinName.setText();
//                        }
//                    });
                }
            }
        });

    }

    private void updateLinearLayout() {


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