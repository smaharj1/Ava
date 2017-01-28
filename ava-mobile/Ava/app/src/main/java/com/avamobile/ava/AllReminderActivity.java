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

/**
 * This activity will display all the reminders there are.
 */
public class AllReminderActivity extends AppCompatActivity {
    // It holds all the prescriptions the client has.
    private ArrayList<Medicine> prescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reminder);

        // Gets all the data from the server.
        getAllData();
    }

    /**
     * Gets all the data from the server for the user.
     */
    public void getAllData() {
        // Makes an API call to the server requesting for all the reminders.
        RequestQueue queue = Volley.newRequestQueue(this);;
        // Make REST call here to get all the prescriptions and populate the array.
        String requestURL = ClientServer.URL+"/getReminders";
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

    /**
     * Converts the medicine to the string.
     * @return Returns the array of medicine in the form of string.
     */
    private ArrayList<String> converToString() {
        ArrayList<String> result = new ArrayList<>();

        for (int i =0; i < prescriptions.size(); i++) {
            Medicine temp = prescriptions.get(i);

            result.add(temp.getMedicineName()+" "+ temp.getTime());
        }

        return result;
    }

    /**
     * Parses the JSON response and puts it in the form of array of Medicine object.
     * @param jsonLine It holds the JSON response.
     * @return Returns the arraylist of Medicine.
     */
    public ArrayList<Medicine> parse(String jsonLine) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("reminders");

        ArrayList<Medicine> result = new ArrayList<>();
        String drugName;
        int nextTime;

        // Loops through the JSON array from the API call response and then adds them to the main
        // arraylist.
        for (int i = 0; i < jarray.size(); i++) {
            jobject = jarray.get(i).getAsJsonObject();
            drugName = jobject.get("name").toString();
            nextTime = jobject.get("targetTime").getAsInt();

            result.add(new Medicine(drugName, nextTime));
        }
        return result;
    }
}
