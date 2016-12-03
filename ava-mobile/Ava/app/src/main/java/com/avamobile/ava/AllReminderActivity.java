package com.avamobile.ava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class AllReminderActivity extends AppCompatActivity {
    private ArrayList<Medicine> prescriptions;
    private final String URL = "http://569859e8.ngrok.io";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reminder);

        getAllData();



    }

    public void getAllData() {

        RequestQueue queue = Volley.newRequestQueue(this);;
        // Make REST call here to get all the prescriptions and populate the array.
        String requestURL = URL+"/getReminders";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println("The response is "+ res);
                        //Medicine receivedDrug = parse(res);
                        // Gets the closest one of the reminder drug from the list and displays the countdown
                        prescriptions = parse(res);

                        ArrayList<String> allMeds = converToString();

                        ListView listView = (ListView) findViewById(R.id.reminderList);

                        String[] oo = new String[] {"http://compass.xbox.com/assets/23./0d/230dc52a-8f0e-40bf-bbd1-c51fdb8371e3.png?n=Homepage-360-UA_Upgrade-big_1056x594.png", "https://pbs.twimg.com/profile_images/515112446898368512/oQSyacEo.jpeg","https://pbs.twimg.com/profile_images/515112446898368512/oQSyacEo.jpeg","https://pbs.twimg.com/profile_images/515112446898368512/oQSyacEo.jpeg"};
                        // This string array shows how many pictures to put.
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getApplicationContext(), android.R.layout.simple_list_item_1, oo);

                        listView.setAdapter(adapter);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private ArrayList<String> converToString() {
        ArrayList<String> result = new ArrayList<>();

        for (int i =0; i < prescriptions.size(); i++) {
            Medicine temp = prescriptions.get(i);

            result.add(temp.getMedicineName()+" "+ temp.getTime());
        }

        return result;
    }

    public ArrayList<Medicine> parse(String jsonLine) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("items");
        JsonArray jarray = jobject.getAsJsonArray("reminders");

        ArrayList<Medicine> result = new ArrayList<>();
        //if(result.isEmpty()) return null;
        String drugName;
        int nextTime;

        for (int i = 0; i < jarray.size(); i++) {
            jobject = jarray.get(i).getAsJsonObject();
            drugName = jobject.get("name").toString();
            nextTime = jobject.get("targetTime").getAsInt();

            result.add(new Medicine(drugName, nextTime));
        }
        return result;
    }
}
