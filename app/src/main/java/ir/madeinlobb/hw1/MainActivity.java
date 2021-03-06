package ir.madeinlobb.hw1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button openSecondActivity;
    //    CircularProgressButton circularProgressButton;
    private Button refreshButton;
    private int mProgressStatus = 0;
    ProgressBar progressBar;
    ImageButton imageButton;
    ScrollView scrollView;
    LinearLayout coinsLayout;
    GetExample getExample;
    Gson gson;
    Button addCoins;
    OkHttpClient client;
    Integer number;
    TextView textView;
    EditText first;
    EditText second;

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

//        circularProgressButton = (CircularProgressButton)findViewById(R.id.refresh);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        progressBar = findViewById(R.id.progress_circular);

        refreshButton = findViewById(R.id.refresh);

        scrollView = findViewById(R.id.scroll_view);

        coinsLayout = findViewById(R.id.coin_layouts);

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
        gson = new Gson();
        //                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
        Thread thread = new Thread() {
            @Override
            public void run() {
                client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=2&aux=platform&cryptocurrency_type=coins")
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
                        String name = object.getString("name");
                        String symbol = object.getString("symbol");
                        object2 = object.getJSONObject("quote").getJSONObject("USD");
                        int price = object2.getInt("price");
                        double changeHour = object2.getDouble("percent_change_1h");
                        double changeDay = object2.getDouble("percent_change_24h");
                        double changeWeek = object2.getDouble("percent_change_7d");


                        Log.d("MMD2", name + "-" + symbol + "-" + price + "-" + changeHour + "-" + changeDay + "-" + changeWeek);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
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