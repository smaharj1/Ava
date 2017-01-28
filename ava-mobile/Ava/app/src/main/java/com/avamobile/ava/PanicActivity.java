package com.avamobile.ava;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Hashtable;
import java.util.Map;

/**
 * This activity handles the panic situation. If the patient has a medical emergency, then
 * this activity is triggered as a result of pressing the panic button.
 */

public class PanicActivity extends Activity {
    // REST call URI for panic.
    private final String PANIC_URL = ClientServer.URL + "/panic";

    // A easy REST call that can give the current location for the patient. It surpasses the
    // complexity of android built in system.
    private final String mapURL = "http://ip-api.com/json";

    // It is the URI for nearest hospital.
    private final String nearestHospitalURL = ClientServer.URL +"/nearestHospital";

    private String LONGITUDE = "lng";
    private String LATITUDE = "lat";

    // It holds the latitude and longitude of the patient's location.
    public static double latitude=0;
    public static double longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);


        // Making requests to server for Panic Info
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mapURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        populateLatLng(res);

                        // After the latitude and longitude is received, it finds the nearest hospital.
                        // This also serves as a panic response to avoid unnecessary server calls.
                        findNearestHospital(latitude, longitude);

                        //sendToServer(latitude, longitude);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed Panic Help Request from the server");

                        //Showing toast
                        //Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);



    }

    /**
     * Finds the nearest hospital from the patient's location.
     * @param latitude It holds the latitude value.
     * @param longitude It holds the longitude value.
     */
    public void findNearestHospital(final double latitude, final double longitude) {

        //Making requests to server for Panic Info
        StringRequest stringRequest = new StringRequest(Request.Method.POST, nearestHospitalURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {

                        if (true) {
                            System.out.println("RES:" + res);
                        }
                        else {
                            //System.out.println(res);
                            JsonElement jelement = new JsonParser().parse(res);
                            JsonObject jobject = jelement.getAsJsonObject();

                            String hospitalName = jobject.get("name").toString();
                            String loc = jobject.get("location").toString();
                            String phn = jobject.get("phone").toString();
                            String web = jobject.get("website").toString();

                            // Sets the text in the activity from the response of nearest hospital received.
                            TextView tv = (TextView) findViewById(R.id.hospital);
                            tv.setText(hospitalName);
                            tv = (TextView) findViewById(R.id.location);
                            tv.setText(loc);
                            tv = (TextView) findViewById(R.id.phone);
                            tv.setText(phn);
                            tv = (TextView) findViewById(R.id.web);
                            tv.setText(web);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed Panic Help Request from the server");

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
                params.put(LATITUDE, Double.toString(latitude));
                params.put(LONGITUDE, Double.toString(longitude));

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public void populateLatLng (String response){
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("items");
        latitude = jobject.get("lat").getAsDouble();
        longitude = jobject.get("lon").getAsDouble();
        System.out.println(latitude+" "+ longitude);

    }
}


/**************************************************************************
 *********** UNUSED FUNCTIONS ******************************************
 ************************************************************************/
/*
    public void sendToServer(final double lat,final  double lng) {
        //Making requests to server for Panic Info
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PANIC_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        findNearestHospital(latitude, longitude);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed Panic Help Request from the server");

                        //Showing toast
                        //Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(LATITUDE, Double.toString(lat));
                params.put(LONGITUDE, Double.toString(lng));

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    */