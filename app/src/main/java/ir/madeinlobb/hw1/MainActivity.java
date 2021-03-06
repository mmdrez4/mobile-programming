package ir.madeinlobb.hw1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button openSecondActivity;
//    CircularProgressButton circularProgressButton;
    private Button refreshButton;
    ImageButton imageButton;
    Integer number;
    TextView textView;
    Button button;
    EditText first;
    EditText second;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openSecondActivity = findViewById(R.id.open_second_activity);

        ProgressBar progressBar = findViewById(R.id.progress_circular);

        imageButton = findViewById(R.id.coin_image);

//        circularProgressButton = (CircularProgressButton)findViewById(R.id.refresh);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });


        openSecondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
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