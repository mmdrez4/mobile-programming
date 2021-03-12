package ir.madeinlobb.hw1;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ApiReq {

    public enum Range {
        weekly,
        oneMonth,
    }

    public static String startTime;
    public static String endTime;
    public static String openTime;
    public static String closeTime;
    public static String openPrice;
    public static String closePrice;
    public static String highPrice;
    public static String lowPrice;
    public static String tradedVolume;
    public static String tradesCount;
    static Log log;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void getCandles(final String symbol, final Range range) throws IOException {

        Thread thread = new Thread() {

            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String miniUrl;
                final String description;

                switch (range) {

                    case weekly:
                        miniUrl = "period_id=1DAY".concat("&time_start=2021-03-10").concat("&time_end=".concat(date).concat("&limit=7"));
                        description = "Daily candles from now";
                        break;

                    case oneMonth:
                        miniUrl = "period_id=1DAY".concat("&time_start=2021-03-10T00:00:00").concat("&time_end=".concat(date).concat("&limit=30"));
                        description = "Daily candles from now";
                        break;

                    default:
                        miniUrl = "";
                        description = "";

                }

                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(symbol).concat("/history?".concat(miniUrl)))
                        .newBuilder();

                String url = urlBuilder.build().toString();

                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .addHeader("X-CoinAPI_key", "7A140A0C-551B-4652-AF33-CA5DF0482FAC")
                        .build();


                try {
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject jsonData = new JSONObject(response.body().string());
                    System.out.println("youuuuuuseeffffff");
                    System.out.println(jsonData.toString());
                    log.d("TAG", response.body().toString());
                    startTime = jsonData.getString("time_period_start");
                    endTime = jsonData.getString("time_period_end");
                    openTime = jsonData.getString("time_open");
                    closeTime = jsonData.getString("time_close");
                    openPrice = jsonData.getString("price_open");
                    closePrice = jsonData.getString("price_close");
                    highPrice = jsonData.getString("price_high");
                    lowPrice = jsonData.getString("price_low");
                    tradedVolume = jsonData.getString("volume_traded");
                    tradesCount = jsonData.getString("trades_count");
                } catch (
                        JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}