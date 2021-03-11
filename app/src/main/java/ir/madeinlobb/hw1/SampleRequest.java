import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public enum Range {
		weekly,
		oneMonth,
	}
	
// برای دریافت کندل های روزانه به مدت یک هفته پارامتر دوم را "هفته ای" بدهید و
// برای دریافت کندل های روزانه به مدت یک ماه پارامتر دوم را "یک ماه" بدهید
// پارامتر اول هم نماد سکه مورد نظر خواهد بود

    public void getCandles(String symbol,Range range) {

        OkHttpClient okHttpClient = new OkHttpClient();

        String miniUrl;
        final String description;
        switch (range) {

            case weekly:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(getCurrentDate()).concat("&limit=7"));
                description = "Daily candles from now";
                break;

            case oneMonth:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(getCurrentDate()).concat("&limit=30"));
                description = "Daily candles from now";
                break;

            default:
                miniUrl = "";
                description = "";

        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(symbol).concat("/USD/history?".concat(miniUrl)))
            .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
            .addHeader("X-CoinAPI-Key", YOUR_COIN_IO_API_KEY)
            .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    //extractCandlesFromResponse(response.body().string(), description);
                }
            }
        });

    }

