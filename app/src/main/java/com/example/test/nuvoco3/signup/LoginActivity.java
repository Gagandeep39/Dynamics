package com.example.test.nuvoco3.signup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.MainActivity;
import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.helpers.UserInfoHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "Login Activity";
    public static final String LOGIN_KEY = "loggedin";    //Stores login status eg. Keep me Logged in
    public static final String CUSTOMER_DATA = "CustomerData";
    String mEmailLogin, mPasswordLogin;
    Button buttonSkip, mButtonLogin;
    TextView mTextViewSignUp;
    TextInputEditText mTextInputEditTextEmail, mTextInputEditTextPassword;
    RequestQueue queue;
    ProgressDialog progressDialog;
    ConstraintLayout mConstraintLayout;
    CheckBox mCheckBoxLogin;

    //User Helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        // Checks if User has an Account or Not
        if (checkLoggedIn() == 1) {
            mEmailLogin = new UserInfoHelper(this).getUserEmail();
            mPasswordLogin = new UserInfoHelper(this).getUserPassword();

            makeJsonObjReq();
//            finish();
        }
        //Skip Button to be removed in future
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        //Tries user to LogIn into the App
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoggingIn();
            }
        });
    }

    //    Sends Email ID and Password to Server for Authentication
    private void tryLoggingIn() {
        mEmailLogin = mTextInputEditTextEmail.getText().toString();
        mPasswordLogin = mTextInputEditTextPassword.getText().toString();
        if (TextUtils.isEmpty(mEmailLogin)) {
            mTextInputEditTextEmail.setError("Enter Email");

        } else if (!isEmailValid(mEmailLogin)) {
            mTextInputEditTextEmail.setError("Enter a valid Email");
        } else {
            if (TextUtils.isEmpty(mPasswordLogin)) {
                mTextInputEditTextPassword.setError("Enter Password");
            }

        }
        if (!TextUtils.isEmpty(mEmailLogin) && !TextUtils.isEmpty(mPasswordLogin)) {
            makeJsonObjReq();
        }


    }

    //    initialize Views
    private void findViews() {

        buttonSkip = findViewById(R.id.skip);
        mButtonLogin = findViewById(R.id.buttonLogin);
        mTextViewSignUp = findViewById(R.id.textViewSignUp);
        mTextInputEditTextEmail = findViewById(R.id.textInputEditEmail);
        mTextInputEditTextPassword = findViewById(R.id.textInputEditPassword);
        mCheckBoxLogin = findViewById(R.id.checkbox);
        mConstraintLayout = findViewById(R.id.constraintLayout);
    }

    //    If user doesn't have an account then he can Sign Up
    public void signUpFunction(View v) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    //    Actual authentication function which gives us a response code on connection to server
    private void makeJsonObjReq() {
        showProgressDialog();


        Map<String, String> postParam = new HashMap<>();


        postParam.put("1", mEmailLogin);  //1=>Name, 2.Email, 3.Phone, 4.Age, 5.Address
        postParam.put("2", mPasswordLogin);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + "/auth", new JSONObject(postParam),
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {

                            progressDialog.dismiss();
                        }
                        Log.i(TAG, "onResponse:  " + response);
                        try {


                            if (response.getString("status").equals("not_existing")) {
                                Toast.makeText(LoginActivity.this, "Account does't Exist", Toast.LENGTH_SHORT).show();
                                reenterData();
                            } else if (response.getString("status").equals("unsuccessful")) {
                                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                reenterData();
                            } else if (response.getString("status").contains("successful")) {
                                //Store Logged in User Data that is received from Response Code
                                ArrayList<String> set = new ArrayList<>();
                                set.add(response.getString("u_add"));
                                set.add(response.getString("u_age"));
                                set.add(response.getString("u_area"));
                                set.add(response.getString("u_city"));
                                set.add(response.getString("u_department"));
                                set.add(response.getString("u_email"));
                                set.add(response.getString("u_id"));
                                set.add(response.getString("u_name"));
                                set.add(response.getString("u_phone"));
                                set.add(response.getString("u_pincode"));
                                set.add(response.getString("u_status"));
                                set.add(mPasswordLogin);
                                set.add(response.getString("token"));
                                set.add(1 + "");

                                SharedPreferences sharedPreferences = getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

                                sharedPreferences.edit().putString(CUSTOMER_DATA, ObjectSerializer.serialize(set)).apply();
                                new UserInfoHelper(LoginActivity.this).getUserEmail();
                                sharedPreferences.edit().apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: " + response.getString("status"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onResponse: " + error.getMessage());
                VolleyLog.e(TAG, "Error while making database connection : " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                String credentials = mEmailLogin + ":" + mPasswordLogin;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", auth);
                return headers;
            }


        };
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    private void showProgressDialog() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    Snackbar snackbar = Snackbar.make(mConstraintLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeJsonObjReq();

                        }
                    });
                    snackbar.show();

                    progressDialog.dismiss();
                }

            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);

    }

    private void reenterData() {
        Log.i(TAG, "reenterData: " + "lol");
        mTextInputEditTextEmail.setText("");
        mTextInputEditTextPassword.setText("");
        mTextInputEditTextEmail.requestFocus();
    }

    // Allows user to login without credentials if already had Logged in before
    private int checkLoggedIn() {
        ArrayList<String> newArralist = new ArrayList<>();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        newArralist = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        if (newArralist.size() != 0)
            return Integer.parseInt(newArralist.get(13));
        return 0;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


}
