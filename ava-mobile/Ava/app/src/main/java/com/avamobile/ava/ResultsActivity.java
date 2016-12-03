package com.avamobile.ava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultsActivity extends AppCompatActivity {

    String dataToDisplay;
    TextView medicineDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Getting intents
        dataToDisplay = getIntent().getStringExtra("photoData");
        System.out.println("Inside Results Activity");
        System.out.println(dataToDisplay);

        //Displaying captured medicine details by camera in previous activity
        medicineDetails = (TextView)findViewById(R.id.medicineDetails);
        medicineDetails.append("\n\n" + dataToDisplay);


    }
}
