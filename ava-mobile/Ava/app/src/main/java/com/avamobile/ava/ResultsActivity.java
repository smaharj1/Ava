package com.avamobile.ava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ResultsActivity extends AppCompatActivity {

    String dataToDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        dataToDisplay = getIntent().getStringExtra("photoData");
        System.out.println("Inside Results Activity");
        System.out.println(dataToDisplay);
    }
}
