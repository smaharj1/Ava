package com.avamobile.ava;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.android.volley.AuthFailureError;

import java.io.ByteArrayOutputStream;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    final CountDownLatch latch = new CountDownLatch(10);
    private int seconds = 4;
    private int minutes = 0;
    private final String URL = "http://569859e8.ngrok.io";
    private final String ALL_PRESCRIPTIONS = "prescriptions";

    private ArrayList<Medicine> prescriptions;
    Medicine closestMedicine = null;
    private static final int CAMERA_REQUEST = 1888;
    //Details to upload picture to the server
    private String UPLOAD_URL = "http://569859e8.ngrok.io/medicine";
    //private String UPLOAD_URL = "http://69649754.ngrok.io/medicine";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private final String DRUG_NAME ="name";
    private final String DATE = "date";
    private final String TIME = "time";
    private String responseData = "Empty";   //To restore responses from the server

    private RequestQueue queue;

    private TextView countDownView;
    private TextView clickReminderView;

    boolean medicineTaken = false;
    boolean alarmTriggered = false;
    private int missedTime = -1;

    ObjectAnimator colorFade;

    Timer timer;

    AnimatorSet animation;
    AlarmService alert;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_logo);
        animation = new AnimatorSet();



        // Initiate the animation for the application.
        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //set the new Content of your activity
                MainActivity.this.setContentView(R.layout.activity_main);

                queue = Volley.newRequestQueue(getApplicationContext());

                LinearLayout count_down = (LinearLayout) findViewById(R.id.reminder);
                colorFade = ObjectAnimator.ofObject(count_down, "backgroundColor", new ArgbEvaluator(), Color.parseColor("#F9423A"), Color.parseColor("#ffffff"));
                colorFade.setDuration(1000);

                //alert = new AlarmService(getApplicationContext());
                // On clicking the camera button, start the Camera and wait for user to take a picture
                Button add_medicine = (Button) findViewById(R.id.add_medicine);
                add_medicine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });


                countDownView = (TextView) findViewById(R.id.count_down);
                clickReminderView = (TextView) findViewById(R.id.click_message);
                // Runs the count down on the screen
                //run_countdown();
                countDownRunner();
            }
        }.start();

    }



    // TODO: Creating notification
    public void triggerAlarm(){
//        Intent alertIntent = new Intent(MainActivity.this, AlertReceiver.class);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        alarmManager.set(AlarmManager.RTC_WAKEUP, 0,
//                PendingIntent.getBroadcast(MainActivity.this, 1, alertIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT));

        if(!alarmTriggered) {

            System.out.println("Staring the alarm notification");
            alert.startAlarm();
            alarmTriggered = true;
        }
    }



    // TODO: This is temporary
    private void countDownRunner() {
        //Declare the timer
        timer = new Timer();
        minutes = 0;
        seconds = 4;
        clickReminderView.setVisibility(View.INVISIBLE);

        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        countDownView.setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                        seconds -= 1;

                        if( minutes < 0){
                            countDownView.setText("NOW!");
                            clickReminderView.setVisibility(View.VISIBLE);
                            medicineTaken = true;
                            missedTime = (int) getCurrentTime();
                            //triggerAlarm();
                            //alarmTriggered = true;
                            signal_alert(true);
                        }
                        if (minutes == 0 && seconds == 0) {
                            // This is when we run alert notification
                            startAlert();
                        }
                        if(seconds == 0)
                        {
                            countDownView.setText(String.valueOf(minutes)+" : "+String.valueOf(seconds));
                            seconds=60;
                            minutes=minutes-1;
                        }

                        if (getCurrentTime() - missedTime > 1800) {
                            triggerMissMedicine();
                        }
                    }

                });
            }

        }, 0, 1000);
    }

    private void startAlert() {
        System.out.println("OOO: Startinng the alert");
        Long alertTime = new GregorianCalendar().getTimeInMillis()+1*1000;

        Intent alertIntent = new Intent(this, AlertReceiver.class);

        AlarmManager alarmManager = (AlarmManager)
                getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this, 1, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void triggerMissMedicine() {
        if (closestMedicine != null) {
            String drugName = closestMedicine.getMedicineName();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String time = Integer.toString((int)getCurrentTime());
            sendMedicationData(drugName, date, time);

        }
    }



    public void onReminderClicked(View view){
        if (medicineTaken) {
            // Make a rest call indicating the medicine indicated has been taken.
            if (closestMedicine != null){
                // Sends the request with closest Medicine name
                //sendMedicationData();

                closestMedicine = null;
                signal_alert(false);
                //TODO:run_countdown();

            timer.cancel();
            countDownRunner();
            }

            medicineTaken = false;
        }
        else {
            // Open the new activity to show all the reminders
            Intent allReminders = new Intent(getApplicationContext(), AllReminderActivity.class);
            allReminders.putExtra(ALL_PRESCRIPTIONS, prescriptions);
            startActivity(allReminders);
        }
    }

    private void sendMedicationData(final String drugName, final String date1, final String time){

        System.out.println("Sending the medication data...");
        String sendingURL = URL + "/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(DRUG_NAME, drugName);
                params.put(DATE, date1);
                params.put(TIME, time);

                //returning parameters
                return params;
            }
        };
    }

    private void run_countdown(){
        // Make REST call here to get all the prescriptions and populate the array.
        String requestURL = URL+"/reminders";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        prescriptions = parse(res);

                        // Gets the closest one of the reminder drug from the list and displays the countdown
                        closestMedicine = getClosestReminder();
                        int difference = (int) closestMedicine.getTimeDifference(getCurrentTime());
                        minutes = difference / 60;
                        seconds = difference - (minutes*60);

                        //Declare the timer
                        Timer t = new Timer();
                        //Set the schedule function and rate
                        t.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        countDownView.setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                                        seconds -= 1;

                                        if( minutes < 0){
                                            countDownView.setText("NOW!");
                                            clickReminderView.setVisibility(View.VISIBLE);
                                            medicineTaken = true;
                                            signal_alert(true);
                                        }
                                        if(seconds == 0)
                                        {
                                            clickReminderView.setVisibility(View.INVISIBLE);
                                            countDownView.setText(String.valueOf(minutes)+" : "+String.valueOf(seconds));
                                            seconds=60;
                                            minutes=minutes-1;
                                        }
                                    }

                                });
                            }

                        }, 0, 1000);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);



    }

    public ArrayList<Medicine> parse(String jsonLine) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("items");
        JsonArray jarray = jobject.getAsJsonArray("reminders");

        ArrayList<Medicine> result = new ArrayList<>();
        //if(result.isEmpty()) return null;
        String drugName;
        long nextTime;

        for (int i = 0; i < jarray.size(); i++) {
            jobject = jarray.get(i).getAsJsonObject();
            drugName = jobject.get("name").toString();
            nextTime = jobject.get("targetTime").getAsLong();

            result.add(new Medicine(drugName, nextTime));
        }
        return result;
    }

    private void signal_alert(boolean is_alert){

        if (is_alert) {
            colorFade.start();
        }
        else {
            colorFade.cancel();
        }
    }


    /**
     * Serves as a callback for the startActivityForResult()
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //startCamera = false;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            System.out.println("Got picture");
            Bitmap bmapPhoto = (Bitmap) data.getExtras().get("data");

            //Response of image processing from the server
            uploadImageAndGetResponse(bmapPhoto);
        }
    }





    //Uploads image to the server, gets the response and stores it in the global response variable
    private void uploadImageAndGetResponse(Bitmap photo) {
        final String encodedImage = encodeImage(photo);
        System.out.println("Encoded Image: " + encodedImage);

        //Making requests to server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println(res);
                        responseData = res;
                        //Showing toast message of the response
                        Toast.makeText(MainActivity.this, res , Toast.LENGTH_LONG).show();

                        //Put the photo data returned into the server and start a new activity
                        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                        intent.putExtra("photoData", responseData);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");

                        //Put the photo data returned into the server and start a new activity
                        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                        intent.putExtra("photoData", responseData);
                        startActivity(intent);
                        //Showing toast
                        //Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_NAME, "url");
                params.put(KEY_IMAGE, encodedImage);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }

    //Encodes the Bitmap image to string
    private String encodeImage(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void panic_clicked(View view){
        Intent alertActivity = new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(alertActivity);
    }

    private Medicine getClosestReminder() {
        Medicine temp = prescriptions.get(0);

        long currentTime = getCurrentTime();
        for (Medicine single: prescriptions){
            if (single.getTimeDifference(currentTime) < temp.getTimeDifference(currentTime)) {
                temp = single;
            }
        }
        return temp;
    }

    /**
     * Gets time in seconds.
     * @return
     */
    private long getCurrentTime(){
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long sinceMid = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);

        return sinceMid/1000;
    }
}
