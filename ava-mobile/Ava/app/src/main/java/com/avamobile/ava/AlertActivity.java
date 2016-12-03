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

public class AlertActivity extends Activity {

    private final String PANIC_URL = "http://569859e8.ngrok.io/panic";
    private final String URL = "http://569859e8.ngrok.io";
    private String responseData = "Empty Panic Response";
    private String LONGITUDE = "lng";
    private String LATITUDE = "lat";

    public static double latitude=0;
    public static double longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        String mapURL = "http://ip-api.com/json";
        //Making requests to server for Panic Info
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mapURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        populateLatLng(res);

                        sendToServer(latitude, longitude);
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

    public void findNearestHospital(final double latitude, final double longitude) {
        String reqURL = URL+"/nearestHospital";
        //Making requests to server for Panic Info
        StringRequest stringRequest = new StringRequest(Request.Method.POST, reqURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println(res);
                        JsonElement jelement = new JsonParser().parse(res);
                        JsonObject jobject = jelement.getAsJsonObject();

                        String hospitalName = jobject.get("name").toString();
                        String loc = jobject.get("location").toString();
                        String phn = jobject.get("phone").toString();
                        String web = jobject.get("website").toString();

                        TextView tv = (TextView) findViewById(R.id.hospital);
                        tv.setText(hospitalName);
                        tv = (TextView) findViewById(R.id.location);
                        tv.setText(loc);
                        tv = (TextView) findViewById(R.id.phone);
                        tv.setText(phn);
                        tv = (TextView) findViewById(R.id.web);
                        tv.setText(web);

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

    public void populateLatLng (String response){
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("items");
        latitude = jobject.get("lat").getAsDouble();
        longitude = jobject.get("lon").getAsDouble();
        System.out.println(latitude+" "+ longitude);

    }




}