package ir.madeinlobb.hw1;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SecondActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
       final TextView textView = findViewById(R.id.T022);
       final LinearLayout linearLayout = findViewById(R.id.T023);
        try {
            ApiReq.getCandles("BITSTAMP_SPOT_BTC_USD", ApiReq.Range.weekly);

            textView.setText("heeeeee");

            textView.setText(ApiReq.highPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        TopFragment topFragment = new TopFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.top_fragment_container, topFragment).commit();
//
//        BottomFragment bottomFragment = new BottomFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.bottom_fragment_container, bottomFragment).commit();

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
