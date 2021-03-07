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

import com.bumptech.glide.Glide;
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

    static int start = -9;

    private Button openSecondActivity;
    //    CircularProgressButton circularProgressButton;
    private Button refreshButton;
    private int mProgressStatus = 0;
    ProgressBar progressBar;
    LinearLayout mainLayout;
    final boolean[] firstTime = {true};
    LinearLayout barLayout;
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
        barLayout = findViewById(R.id.bar);
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
                mProgressStatus = 0;
                mainLayout.removeAllViews();
                mainLayout.addView(barLayout);
                mainLayout.addView(coinsLayout);

                progressBar.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        getWebService(1,2);
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
                };
                thread.start();
            }
        });

        addCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebService(start, 1);
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

    private void getWebService(final int startPoint, final int status) {
        start += 10;
        final TextView coinName = findViewById(R.id.coin_name);
        final TextView coinPrice = findViewById(R.id.coin_price);
        final TextView hc = findViewById(R.id.hour_changes);
        final TextView dc = findViewById(R.id.day_changes);
        final TextView wc = findViewById(R.id.week_changes);
        //                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
        Thread thread = new Thread() {
            @Override
            public void run() {
                int end = start + 9;
                Log.d("START", String.valueOf(start));
                String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=" + startPoint + "&limit=" + end + "&aux=platform&cryptocurrency_type=coins";
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

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstTime[0]) {
                                    if (status == 2){
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
                                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                        }
                                    });
                                    linearLayout.setVisibility(View.VISIBLE);
                                    if (status == 2){
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