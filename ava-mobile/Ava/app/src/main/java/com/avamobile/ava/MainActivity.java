package com.avamobile.ava;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
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


import com.android.volley.DefaultRetryPolicy;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.android.volley.AuthFailureError;

import java.io.ByteArrayOutputStream;

import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the main activity where magic happens. It handles some logic of the page like reminders and camera activity.
 */
public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;

    //Details to upload picture to the server
    private final String UPLOAD_URL = ClientServer.URL + "/medicine";
    private final String KEY_IMAGE = "image";
    private final String KEY_NAME = "name";
    private final String DRUG_NAME ="name";
    private final String DATE = "date";
    private final String TIME = "time";

    // Holds the seconds and minutes.
    private int seconds = 4;
    private int minutes = 0;

    // It holds the closest medicine that needs to be reminded of.
    Medicine closestMedicine = null;

    private String responseData = "Empty";   //To restore responses from the server

    private RequestQueue queue;
    private Queue<Medicine> reminderInQueue = new LinkedList<>() ;

    // It holds the countdown view and everything.
    private TextView countDownView;
    private TextView clickReminderView;

    boolean medicineTaken = false;
    private int missedTime = -1;

    // Handles the color fading alert once the reminder is hit.
    private ObjectAnimator colorFade;

    // It holds the timer.
    private Timer timer;

    // It does the magic animation.
    private AnimatorSet animation;

    private String userID;

    private boolean loadSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animation = new AnimatorSet();

        // Initiate the animation for the application. It initiates the app with 3 second delay.
        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //set the new Content of your activity
                //MainActivity.this.setContentView(R.layout.activity_main);

                queue = Volley.newRequestQueue(getApplicationContext());

                String extraMessage = getIntent().getStringExtra(StaticNames.USER_ID);

                if (!extraMessage.isEmpty()) {
                    userID = extraMessage;
                }

                LinearLayout count_down = (LinearLayout) findViewById(R.id.reminder);
                colorFade = ObjectAnimator.ofObject(count_down, "backgroundColor", new ArgbEvaluator(), Color.parseColor("#F9423A"), Color.parseColor("#ffffff"));
                colorFade.setDuration(1000);

                countDownView = (TextView) findViewById(R.id.count_down);
                clickReminderView = (TextView) findViewById(R.id.click_message);

                // Runs the count down on the screen
                run_countdown();
            }
        }.start();

    }

    /**
     * Event Handler: When adding medicine button is clicked.
     * @param view
     */
    public void addMedicine(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // TODO: Creating notification
    public void triggerAlarm(){
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Ava's message")
                            .setContentText("Please take your medicine");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MainActivity.class);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());

    }

    /**
     * Triggers when the medicine is missed and sends the data to server.
     */
    private void triggerMissMedicine() {
        if (closestMedicine != null) {
            String drugName = closestMedicine.getMedicineName();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String time = Integer.toString((int)getCurrentTime());
            sendMedicationData(drugName, date, time);

        }
    }

    /**
     * Triggers when the reminder on the screen is clicked. It will only make changes if medicine is due.
     * @param view
     */
    public void onReminderClicked(View view){
        if (medicineTaken) {

            closestMedicine = null;
            signal_alert(false);

            timer.cancel();
            //countDownRunner();
            run_countdown();

            medicineTaken = false;
        }
        else {
            // Open the new activity to show all the reminders
            Intent allReminders = new Intent(getApplicationContext(), AllReminderActivity.class);
            //allReminders.putExtra(ALL_PRESCRIPTIONS, prescriptions);
            startActivity(allReminders);
        }
    }

    /**
     * Sends the medication data that was missed to the server.
     * @param drugName It holds the name of the drug.
     * @param date1 It holds the date
     * @param time It holds the time
     */
    private void sendMedicationData(final String drugName, final String date1, final String time){

        System.out.println("Sending the medication data...");
        String sendingURL = ClientServer.URL + "/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // No response needed for this.

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");
                    }
                }){
            /**
             * Sends the drug data as a body parameter for date, name and time.
             * @return
             * @throws AuthFailureError
             */
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

        // Prevents from sending the request twice in slow connection. This seems to be bug otherwise.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * Parses the response to get one medication details
     * @param response It holds the response from the REST call.
     * @return
     */
    public Medicine parseOne(String response) {

        JsonElement jelement = new JsonParser().parse(response);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("items");
        int time = jobject.get("time").getAsInt();
        JsonArray medicationArray = jobject.getAsJsonArray("medications");

        String medication ="";

        for (int i =0 ; i < medicationArray.size(); i++) {
            medication = medication+  medicationArray.get(i).getAsString();
        }

        int totalTime = 0;

        totalTime += (time/100) * 3600;
        totalTime += (time % 100) * 60;

        //totalTime += timeArray.get(0).getAsInt()*3600 ;
        //totalTime += timeArray.get(1).getAsInt()*60;


        return new Medicine(medication, totalTime);
    }

    /**
     * Makes REST call to get one prescription and populates the array.
     */
    private void getNewData() {
        // Make REST call here to get all the prescriptions and populate the array.
        String requestURL = ClientServer.URL+"/nextReminder";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println("The response is "+ res);

                        // Gets the closest one of the reminder drug from the list and displays the countdown
                        Medicine temp = parseOne(res);

                        reminderInQueue.add(temp);

                        int difference = (int) closestMedicine.getTimeDifference(getCurrentTime());
                        minutes = difference / 60;
                        seconds = difference - (minutes*60);


                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
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

        queue.add(stringRequest);
    }

    /**
     * Runs the main countdown.
     */
    private void run_countdown(){
        // Make REST call here to get all the prescriptions and populate the array.
        String requestURL = ClientServer.URL+"/nextReminder";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println("The response is "+ res);

                        // Checks if the response is correct. If not, then just return.
                        if (res.isEmpty()) {
                            return;
                        }
                        //Medicine receivedDrug = parse(res);
                        closestMedicine = parseOne(res);
                        reminderInQueue.add(closestMedicine);

                        int difference = (int) closestMedicine.getTimeDifference(getCurrentTime());
                        minutes = difference / 60;
                        seconds = difference - (minutes*60);

                        while(!reminderInQueue.isEmpty()) {
                            closestMedicine = reminderInQueue.remove();
                            //Declare the timer
                            timer = new Timer();
                            //Set the schedule function and rate
                            timer.scheduleAtFixedRate(new TimerTask() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            seconds -= 1;

                                            if (minutes > 30) {
                                                countDownView.setText(closestMedicine.getTime());
                                                displayTime(closestMedicine.getTargetTime());
                                                return;

                                            } else {
                                                countDownView.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));

                                            }
                                            if (minutes < 0) {
                                                countDownView.setText("NOW!");
                                                clickReminderView.setVisibility(View.VISIBLE);
                                                medicineTaken = true;
                                                signal_alert(true);
                                            }

                                            if (minutes == 0 && seconds == 0) {
                                                // This is when we run alert notification
                                                //startAlert();
                                                triggerAlarm();
                                                missedTime = (int) getCurrentTime();
                                                getNewData();

                                            }
                                            if (seconds == 0) {
                                                clickReminderView.setVisibility(View.INVISIBLE);
                                                countDownView.setText(String.valueOf(minutes) + " : " + String.valueOf(seconds));
                                                seconds = 60;
                                                minutes = minutes - 1;
                                            }

                                            if (getCurrentTime() - missedTime > 1800) {
                                                triggerMissMedicine();
                                            }
                                        }

                                    });
                                }

                            }, 0, 1000);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
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

        queue.add(stringRequest);
    }

    /**
     * Displays the time in AM or PM.
     * @param targetTime It is the target time to take the medicine.
     */
    public void displayTime(int targetTime) {
        String text = "AM";
        int hours = targetTime /3600;

        if (hours > 12) {
            text = "PM";

        }

        clickReminderView.setVisibility(View.VISIBLE);
        clickReminderView.setText(text);
    }

    /**
     * Parses all the medicine from JSON response and puts it in the arraylist.
     * @param jsonLine
     * @return
     */
    public ArrayList<Medicine> parse(String jsonLine) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
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

    /**
     * It signals the alert.
     * @param is_alert
     */
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
            //System.out.println("Got picture");
            Bitmap bmapPhoto = (Bitmap) data.getExtras().get("data");

            //setContentView(R.layout.layout_load_screen);
            //System.out.println("Triggering");

            //Response of image processing from the server
            uploadImageAndGetResponse(bmapPhoto);
        }
    }


    /**
     *  ploads image to the server, gets the response and stores it in the global response variable
     */
    private void uploadImageAndGetResponse(Bitmap photo) {
        final String encodedImage = encodeImage(photo);

        // The progress load bar given.
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        //Making requests to server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println("Response is " + res);
                        JsonElement jelement = new JsonParser().parse(res);
                        JsonObject jsonObject = jelement.getAsJsonObject();


                        boolean success= jsonObject.get("status").getAsBoolean();
                        if (!success) {
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.mainLayout),
                                    "Picture was not clear. Please take the picture again", Snackbar.LENGTH_LONG);
                            mySnackbar.show();
                            progressDialog.dismiss();
                            return;
                        }

                        responseData = jsonObject.get("medicine").getAsString();

                        //Put the photo data returned into the server and start a new activity
                        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                        intent.putExtra("photoData", responseData);
                        intent.putExtra(StaticNames.USER_ID, userID);

                        progressDialog.dismiss();
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");

                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.mainLayout),
                                "Server was not that friendly this time. Try again", Snackbar.LENGTH_LONG);
                        mySnackbar.show();
                        progressDialog.dismiss();

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

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //Creating parameters
                Map<String,String> headers = new Hashtable<String, String>();

                //Adding parameters
                System.out.println("ADD MED: " + userID);
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
    }

    /**
     * This is an error handling case when the server does not respond for 3 seconds.
     * @param progressDialog
     */
    public void initiateTimeout(final ProgressDialog progressDialog) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // If load was unsuccessful from the server side, then dismiss the progressDialog.
                        if (!loadSuccess) {
                            progressDialog.dismiss();
                        }
                    }
                }, 3000);
    }

    /**
     * Encodes the Bitmap image to a string
     * @param photo It is the bitmap image.
     * @return Returns the base 64 equivalent of the image.
     */
    private String encodeImage(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * Handles the emergency situation.
     * @param view
     */
    public void panic_clicked(View view){
        // Prompts the patient for if they are sure and not just pressed by mistake.
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing.

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent alertActivity = new Intent(getApplicationContext(),PanicActivity.class);
                        alertActivity.putExtra(StaticNames.USER_ID, userID);
                        startActivity(alertActivity);

                    }
                });

        alertDialog.show();

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
