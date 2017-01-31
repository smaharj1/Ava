package com.avamobile.ava;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class ResultsActivity extends AppCompatActivity {

    private String medicineName;
    TextView medicineDetails;

    //RequestQueue requestQueue;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Getting intents
        medicineName = getIntent().getStringExtra("photoData");
        System.out.println("Inside Results Activity");
        System.out.println(medicineName);

        //requestQueue = Volley.newRequestQueue(getApplicationContext());
        String extraMessage = getIntent().getStringExtra(StaticNames.USER_ID);
        System.out.println("Extra message is: " + extraMessage);
        if (extraMessage != null) {
            userID = extraMessage;
        }

        //Displaying captured medicine details by camera in previous activity
        medicineDetails = (TextView)findViewById(R.id.medicineDetails);
        medicineDetails.append("\n\n" + medicineName);

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
                    String selectedTime = time.getSelectedItem().toString();

                    //Converting time to military format and adding it to the vector
                    SimpleDateFormat standardFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                    SimpleDateFormat militaryFormat = new SimpleDateFormat("HH:mm", Locale.US);

                    try {
                        selectedTimes.add(militaryFormat.format(standardFormat.parse(selectedTime)));
                    }
                    catch (Exception e){
                        //It shouldn't come to this since the time selection dropdown is pre-specified
                        System.out.println("Couldn't parse the selected time");
                    }
                }
            }
        }

        final String selec_weekdays = selectedWeekdays.toString();
        final String selec_times = selectedTimes.toString();

        //Sending request to server with the data
        String requestURL = ClientServer.URL+"/addMedication";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {

                        JsonElement jsonElement = new JsonParser().parse(res);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        boolean isSuccess = jsonObject.get("status").getAsBoolean();

                        if (isSuccess) {
                            Toast.makeText(ResultsActivity.this, "The reminders have been set", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(ResultsActivity.this, "Please try it with fields.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ResultsActivity.this, "Server error. Please try again", Toast.LENGTH_LONG).show();

                        //Showing toast
                        //Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            /**
             * It holds parameters that need to be passed in the request call.
             * @return
             * @throws AuthFailureError
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("mongoid", userID);
                params.put("medicine", medicineName);
                params.put("weekdays", selec_weekdays);
                params.put("times", selec_times);


                //returning parameters
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //Creating parameters
                Map<String,String> headers = new Hashtable<String, String>();

                //Adding parameters
                headers.put(StaticNames.HEADER_USER_ID, userID);

                //returning parameters
                return headers;
            }
        };

        // Prevents from sending the request twice in slow connection. This seems to be bug otherwise.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }
}
