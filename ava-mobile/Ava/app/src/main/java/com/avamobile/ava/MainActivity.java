package com.avamobile.ava;

<<<<<<< HEAD
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
=======
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
>>>>>>> 16cb2ad9f6a7d29a50bf4f2dcb86f7449ff309d7

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.activity_main);

        // On clicking the camera button, start the CameraActivity
        Button add_medicare = (Button)findViewById(R.id.add_medicare);
        add_medicare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent cameraIntent =  new Intent(v.getContext(), CameraActivity.class);
                //cameraIntent.putExtra("startCamera", "true");
                //startActivity(cameraIntent);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }) ;
    }

    /**
     * Serves as a callback for the startActivityForResult()
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //startCamera = false;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            System.out.println("Got picture");
            Bitmap bmapPhoto = (Bitmap)data.getExtras().get("data");
            //imgViewResult.setImageBitmap(bmapPhoto);
            //System.out.println();
        }
=======
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

>>>>>>> 16cb2ad9f6a7d29a50bf4f2dcb86f7449ff309d7
    }
}
