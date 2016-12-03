package com.avamobile.ava;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //Parse the boolean value passed by previous activity on whether to start camera
        //startCamera = Boolean.parseBoolean(getIntent().getStringExtra("startCamera"));

        //For purposes of displaying the taken picture
        //imgViewResult = (ImageView)findViewById(R.id.imgViewResult);

        //Directly start the camera as soon as the activity starts
        /*
        if (startCamera) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        */
    }


}
