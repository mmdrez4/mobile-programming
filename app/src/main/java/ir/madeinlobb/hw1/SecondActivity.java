package ir.madeinlobb.hw1;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SecondActivity extends AppCompatActivity {
    final boolean[] firstTime = {true};
    Button week;
    Button month;
    LinearLayout mainLayout;

    private static int cores = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executor = Executors.newFixedThreadPool(cores + 1);

    ScrollView scrollView;
    LinearLayout statusLayout;
    int status;
//    public ArrayList<ArrayList> response = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        mainLayout = findViewById(R.id.main_linear_layout);
        scrollView = findViewById(R.id.scroll_view);
        statusLayout = findViewById(R.id.status_layout);

        week = findViewById(R.id.week_button);

        month = findViewById(R.id.month_button);

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d("SYMBOL", MainActivity.symbol);
                    getWebService(7, MainActivity.symbol);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getWebService(30, MainActivity.symbol);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private synchronized void getWebService(final int limit, final String symbol) throws IOException, JSONException {
//        Log.d("SYMBOL: ", MainActivity.symbol);
//        response = ApiReq.getCandles(MainActivity.symbol, limit);

        final Button dayNum = findViewById(R.id.day_num);
        final TextView openPrice = findViewById(R.id.open_price);
        final TextView closePrice = findViewById(R.id.close_price);
        final TextView lowPrice = findViewById(R.id.low_price);
        final TextView highPrice = findViewById(R.id.high_price);


        executor.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String miniUrl = "period_id=1DAY".concat("&limit=").concat(String.valueOf(limit));
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(symbol).concat("/USD/latest?".concat(miniUrl)))
                        .newBuilder();

                String url = urlBuilder.build().toString();

                final Request request = new Request.Builder().url(url).method("GET", null).addHeader("X-CoinPI-Key", "E5CB2574-A0D2-4A8F-96A9-B0FF6FF42162").build();

                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    Log.d("SHIT: ", jsonData);
                    JSONArray Jarray =  new JSONArray(jsonData);

                    for (int i = 0; i < limit; i++) {
                        JSONObject object = (JSONObject) Jarray.get(i);
//                        highPrices.set(i, object.getDouble("price_high"));
//                        lowPrices.set(i, object.getDouble("price_low"));
//                        closePrices.set(i, object.getDouble("price_close"));
//                        openPrices.set(i, object.getDouble("price_open"));

                        final int high = object.getInt("price_high");
                        final int low = object.getInt("price_low");
                        final int close = object.getInt("price_close");
                        final int open = object.getInt("price_open");

//                        final String highPrice = object.getString("name");
//                        final String lowPrice = object.getString("symbol");
//                        int id = object.getInt("id");
//                        object2 = object.getJSONObject("quote").getJSONObject("USD");
//                        final int price = object2.getInt("price");
//                        final int changeHour = object2.getInt("percent_change_1h");
//                        final int changeDay = object2.getInt("percent_change_24h");
//                        final int changeWeek = object2.getInt("percent_change_7d");

                        final int finalI = i + 1;
                        SecondActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstTime[0]) {
                                    dayNum.setText("DAY" + finalI);
                                    openPrice.setText((int) open);
                                    closePrice.setText((int) close);
                                    lowPrice.setText((int) low);
                                    highPrice.setText((int) high);
                                    statusLayout.setVisibility(View.VISIBLE);
                                    firstTime[0] = false;
                                } else {
                                    LayoutInflater vi = (LayoutInflater) SecondActivity.this
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = vi.inflate(R.layout.activity_second, null);

                                    final LinearLayout linearLayout = v.findViewById(R.id.status_layout);

                                    if (linearLayout.getParent() != null) {
                                        ((ViewGroup) linearLayout.getParent()).removeView(linearLayout); // <- fix
                                    }
                                    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    params.setMargins(0, 10, 0, 10);

                                    params.gravity = Gravity.CENTER_VERTICAL;
                                    mainLayout.setOrientation(LinearLayout.VERTICAL);

                                    ((TextView) linearLayout.findViewById(R.id.day_num)).setText("DAY" + finalI);
                                    ((TextView) linearLayout.findViewById(R.id.open_price)).setText((int) open);
                                    ((TextView) linearLayout.findViewById(R.id.close_price)).setText((int) close);
                                    ((TextView) linearLayout.findViewById(R.id.low_price)).setText((int) low);
                                    ((TextView) linearLayout.findViewById(R.id.high_price)).setText((int) high);

                                    linearLayout.setVisibility(View.VISIBLE);
                                    if (status == 2) {
                                        linearLayout.setBackgroundColor(Color.GREEN);
                                    }
                                    mainLayout.addView(linearLayout, params);

                                }
                            }
                        });
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });


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
