package com.avamobile.ava;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Vector;

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

        //Button
        Button saveButton = (Button)findViewById(R.id.btn_Save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSaveRequestToServer();
            }
        });
    }

    private void sendSaveRequestToServer() {
        //String vector to store values to send
        Vector<String> selectedWeekdays = new Vector<>();
        //String vector to store the time
        Vector<String> selectedTimes = new Vector<>();
        int count;
        View view;
        int index;

        //Get selected Weekdays
        //Since the weekdays are in separated in two linear layouts, go through them both
        for (int i = 0; i < 2; i++) {
            LinearLayout weekdaysLayout = (i == 0) ? (LinearLayout) findViewById(R.id.weekdaysLayout1) : (LinearLayout) findViewById(R.id.weekdaysLayout2);

            count = weekdaysLayout.getChildCount();
            view = null;

            for (index = 0; index < count; index++) {
                view = weekdaysLayout.getChildAt(index);
                CheckBox weekday = (CheckBox) view;
                if (weekday.isChecked()) {
                    selectedWeekdays.add(weekday.getText().toString());
                }
                System.out.println();
                //do something with your child element
            }
        }

        // Get selected Times
        LinearLayout timesLayout = (LinearLayout)findViewById(R.id.timesLayout);
        count = timesLayout.getChildCount();
        view = null;

        for (index = 0; index < count; index++) {
            view = timesLayout.getChildAt(index);
            if (view.getClass().getName().contains("Spinner")) {
                Spinner time = (Spinner) view;
                if (!time.getSelectedItem().toString().equals("None")) {
                    selectedTimes.add(time.getSelectedItem().toString());
                }
            }
        }

        System.out.println("Vector size weekdays: " + selectedWeekdays.size());
        System.out.println("Vector size times: " + selectedTimes.size());

        String selec_weekdays = selectedWeekdays.toString();
        String selec_times = selectedTimes.toString();

        System.out.println(selec_weekdays);
        System.out.println(selec_times);

        Toast.makeText(ResultsActivity.this, "The reminders have been set", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
