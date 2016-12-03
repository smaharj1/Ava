package com.avamobile.ava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class AllReminderActivity extends AppCompatActivity {
    private ArrayList<Medicine> prescriptions;

    private final String ALL_PRESCRIPTIONS = "prescriptions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reminder);

        prescriptions = (ArrayList<Medicine>) getIntent().getSerializableExtra(ALL_PRESCRIPTIONS);
    }
}
