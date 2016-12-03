package com.avamobile.ava;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    final CountDownLatch latch = new CountDownLatch(10);
    private int seconds = 5;
    private int minutes = 0;

    AnimatorSet animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_logo);
        animation = new AnimatorSet();

        // Initiate the animation for the application.
        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //set the new Content of your activity
                MainActivity.this.setContentView(R.layout.activity_main);

                run_countdown();
            }
        }.start();


        // Handle the countdown for the application. Comes from REST Call
        //run_countdown();



    }

    private void run_countdown(){
        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.count_down);
                        tv.setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                        seconds -= 1;

                        if( minutes < 0){
                            tv.setText("NOW!");
                            signal_alert(true);
                        }
                        if(seconds == 0)
                        {
                            tv.setText(String.valueOf(minutes)+" : "+String.valueOf(seconds));
                            seconds=60;
                            minutes=minutes-1;
                        }
                    }

                });
            }

        }, 0, 1000);
    }

    private void signal_alert(boolean is_alert){
        LinearLayout count_down = (LinearLayout) findViewById(R.id.reminder);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(count_down, "backgroundColor", new ArgbEvaluator(), Color.parseColor("#F9423A"), Color.parseColor("#ffffff"));
        colorFade.setDuration(1000);
        colorFade.start();

    }
}
