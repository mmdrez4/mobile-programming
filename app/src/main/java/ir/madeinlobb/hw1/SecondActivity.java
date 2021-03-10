package ir.madeinlobb.hw1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

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
