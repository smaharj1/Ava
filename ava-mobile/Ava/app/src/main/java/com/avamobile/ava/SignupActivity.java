package com.avamobile.ava;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class holds the sign up functionality for the user.
 */
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    // Private string constants representing the info
    private final String FIRST_NAME = "firstname";
    private final String LAST_NAME = "lastname";
    private final String USER_NAME = "username";
    private final String PASSWORD = "password";


    @InjectView(R.id.first_name) EditText firstNameText;
    @InjectView(R.id.last_name) EditText lastNameText;
    @InjectView(R.id.input_username) EditText usernameText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.emergency_contact_name) EditText emergencyContNameText;
    @InjectView(R.id.emergency_contact_number) EditText emergencyContPhoneText;
    @InjectView(R.id.btn_signup) Button signupButton;
    @InjectView(R.id.link_login) TextView loginLink;


    private RequestQueue requestQueue;

    private boolean loadSuccess;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        loadSuccess = false;


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        sendInfoToServer(firstName, lastName, username, password);


    }

    /**
     * Sends the sign up information to the server
     * @param firstname It holds the first and middle name
     * @param lastName It holds the last name
     * @param username It holds the new username
     * @param password It holds the password
     */
    private void sendInfoToServer(final String firstname, final String lastName, final String username,
                                  final String password) {

        // Initiates the progress dialog to show the load screen.
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String sendingURL = ClientServer.URL + "/signup";
        StringRequest signupRequest = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // No response needed for this.
                        JsonElement jsonElement = new JsonParser().parse(res);
                        JsonObject jobject = jsonElement.getAsJsonObject();
                        //jobject = jobject.getAsJsonObject("items");
                        boolean signupSuccess = jobject.get("status").getAsBoolean();

                        if (signupSuccess) {
                            onSignupSuccess();
                        }
                        else {
                            onSignupFailed();
                        }
                        loadSuccess = true;

                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");


                    }
                }){
            /**
             * Sends the username and password info as a parameter
             * @return
             * @throws AuthFailureError
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding the parameters and sending the user info to the server.
                params.put(USER_NAME, username);
                params.put(FIRST_NAME, firstname);
                params.put(LAST_NAME, lastName);
                params.put(PASSWORD, password);

                //returning parameters
                return params;
            }
        };

        // This helps not make more than one call to the server.
        signupRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(signupRequest);

        // Initiate time out so that if there is something wrong with the server and it doesn't
        // respond, then it gives out the error message.
        initiateTimeout(progressDialog);
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed. The username/email might have been taken.",
                Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
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
                            Toast.makeText(getBaseContext(), "Sorry. Server might be facing some issues",
                                    Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();
                        }
                    }
                }, 4000);
    }

    /**
     * Validates the user input before making server call.
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        // Gets the string representation of the user input.
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String emergencyContName = emergencyContNameText.getText().toString();
        String emergencyContContact = emergencyContPhoneText.getText().toString();

        if (firstName == null || firstName.isEmpty() || firstName.length() < 3) {
            firstNameText.setError("at least 3 characters");
            valid = false;
        } else {
            firstNameText.setError(null);
        }

        if (lastName == null || lastName.isEmpty()) {
            lastNameText.setError("at least 3 characters");
            valid = false;
        } else {
            lastNameText.setError(null);
        }

        if (username == null || username.isEmpty()) {
            usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if ( password == null || password.isEmpty() || password.length() < 3 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (emergencyContName == null || emergencyContName.isEmpty()) {
            emergencyContNameText.setError("Fill out emergency contact name");
            valid = false;
        }
        else {
            emergencyContNameText.setError(null);
        }

        if (emergencyContContact == null || emergencyContContact.isEmpty()) {
            emergencyContPhoneText.setError("Fill out emergency contact number");
            valid = false;
        }
        else {
            emergencyContPhoneText.setError(null);
        }

        return valid;
    }
}
