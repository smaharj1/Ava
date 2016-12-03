package com.avamobile.ava;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    private static final int CAMERA_REQUEST = 1888;


    AnimatorSet animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_logo);
        animation = new AnimatorSet();

        // Initiate the animation for the application.

        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //set the new Content of your activity
                MainActivity.this.setContentView(R.layout.activity_main);


                // On clicking the camera button, start the Camera and wait for user to take a picture
                Button add_medicine = (Button) findViewById(R.id.add_medicine);
                add_medicine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent cameraIntent =  new Intent(v.getContext(), CameraActivity.class);
                        //cameraIntent.putExtra("startCamera", "true");
                        //startActivity(cameraIntent);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });


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

    /**
     * Serves as a callback for the startActivityForResult()
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //startCamera = false;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            System.out.println("Got picture");
            Bitmap bmapPhoto = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(this, ResultsActivity.class);
            startActivity(intent);
            //imgViewResult.setImageBitmap(bmapPhoto);
            //System.out.println();
        }

    }
}
