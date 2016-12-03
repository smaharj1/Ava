package com.avamobile.ava;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_logo);

        // Initiate the animation for the application.
        new CountDownTimer(3000, 1000) {
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
            }
        }.start();
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
