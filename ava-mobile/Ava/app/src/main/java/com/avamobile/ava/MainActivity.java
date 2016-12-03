package com.avamobile.ava;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_logo);

        // Initiate the animation for the application.
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //set the new Content of your activity
                MainActivity.this.setContentView(R.layout.activity_main);
            }
        }.start();


        // Handle the countdown for the application. Comes from REST Call


    }

    private void run_countdown(){

    }
}
