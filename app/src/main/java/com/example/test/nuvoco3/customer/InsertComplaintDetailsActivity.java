package com.example.test.nuvoco3.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.example.test.nuvoco3.helpers.MasterHelper;
import com.example.test.nuvoco3.helpers.UserInfoHelper;
import com.example.test.nuvoco3.signup.ObjectSerializer;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_COMPLAINT_DETAILS;

/**
 * DEPRECATED
 */


public class InsertComplaintDetailsActivity extends AppCompatActivity {

    private static final String TAG = "InsertComplaintDetails";
    String mComplaintId, mDate, mRepresentative, mCustomerId, mCustomerName, mStatus, mComplaintDetails, mComplaintRemark, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy, mClosedOn;
    SearchableSpinner mSearchStatus, mSearchCustomer;
    TextInputEditText mEditTextComplaintId, mEditTextDate, mEditTextRepresentative, mEditTextCustomerName, mEditTextCustomerId, mEditTextComplaintDetails, mEditTextComplaintRemark;
    FloatingActionButton fab;
    ImageView mImageViewCalendar;
    RequestQueue queue;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;

    //Master Helper
    MasterHelper mComplaintTypeHelper;
    ArrayList<String> mComplaintTypeArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details);
        initializeViews();
        initializeVariables();
        populateSpinners();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void initializeVariables() {

        queue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);

        mComplaintTypeHelper = new MasterHelper(this, "ComplaintStatus");
        mComplaintTypeArray = mComplaintTypeHelper.getRecordName();
    }

    private void validateData() {
        mDate = mEditTextDate.getText().toString();
        mRepresentative = getUserId();
        mCustomerName = mEditTextCustomerName.getText().toString();
        mCustomerId = mEditTextCustomerId.getText().toString();
        mComplaintDetails = mEditTextComplaintDetails.getText().toString();
        mComplaintRemark = mEditTextComplaintRemark.getText().toString();
        mComplaintId = mEditTextCustomerId.getText().toString();
        mCreatedOn = getDateTime();
        mCreatedBy = getUserId();
        mUpdatedOn = getDateTime();
        mUpdatedBy = getUserId();

        if (TextUtils.isEmpty(mDate))
            mEditTextDate.setError("Enter Date");
        if (TextUtils.isEmpty(mRepresentative))
            mEditTextRepresentative.setError("Enter User ID");
        if (TextUtils.isEmpty(mCustomerName))
            mEditTextCustomerName.setError("Enter Name");
        if (TextUtils.isEmpty(mCustomerId))
            mEditTextCustomerId.setError("Enter Customer ID");
        if (TextUtils.isEmpty(mComplaintDetails))
            mEditTextComplaintDetails.setError("Enter Details");
        if (TextUtils.isEmpty(mComplaintRemark))
            mEditTextComplaintRemark.setError("Enter Remark");
        if (TextUtils.isEmpty(mComplaintId))
            mEditTextComplaintId.setError("Enter Complaint ID");

        if (mStatus.equals(getString(R.string.default_name)))
            Toast.makeText(this, "Select Status", Toast.LENGTH_SHORT).show();


        if (!TextUtils.isEmpty(mDate) && !TextUtils.isEmpty(mRepresentative) && !TextUtils.isEmpty(mCustomerName) && !TextUtils.isEmpty(mCustomerId) && !TextUtils.isEmpty(mComplaintDetails) && !TextUtils.isEmpty(mComplaintRemark) && !TextUtils.isEmpty(mComplaintId) && !TextUtils.equals(mStatus, getString(R.string.default_name)))
            saveDataToServer();
    }

    private void saveDataToServer() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(mCoordinaterLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        validateData();
                    }
                });
                snackbar.show();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 20000);

        Map<String, String> postParam = new HashMap<>();

//
        postParam.put("2", mComplaintId);
        postParam.put("3", mDate);
        postParam.put("4", mRepresentative);
        postParam.put("5", mCustomerId);
        postParam.put("6", mCustomerName);
        postParam.put("7", mStatus);
        postParam.put("8", mComplaintDetails);
        postParam.put("9", mComplaintRemark);
        postParam.put("10", mCreatedOn);
        postParam.put("11", mCreatedBy);
        postParam.put("12", mUpdatedOn);
        postParam.put("13", mUpdatedBy);
        postParam.put("14", "2017-01-01");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_COMPLAINT_DETAILS, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {

                                progressDialog.dismiss();
                                Toast.makeText(InsertComplaintDetailsActivity.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(InsertComplaintDetailsActivity.this, MainActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage() + "lol");
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertComplaintDetailsActivity.this).getUserToken());
                return headers;
            }


        };

        jsonObjReq.setTag("LOL");
        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void populateSpinners() {
        ArrayAdapter mStatusAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mComplaintTypeArray);
        mSearchStatus.setAdapter(mStatusAdapter);
        mSearchStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStatus = mComplaintTypeArray.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = getString(R.string.default_name);

            }
        });
    }


    private void initializeViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        mImageViewCalendar = findViewById(R.id.imageViewCalendar);
        mEditTextComplaintId = findViewById(R.id.textInputEditComplaintId);
        mEditTextDate = findViewById(R.id.editTextDate);
        mEditTextRepresentative = findViewById(R.id.textInputEditRepresentative);
        mEditTextCustomerName = findViewById(R.id.textInputEditCustomerName);
        mEditTextCustomerId = findViewById(R.id.textInputEditCustomerId);
        mEditTextComplaintDetails = findViewById(R.id.textInputEditDetails);
        mEditTextComplaintRemark = findViewById(R.id.textInputEditRemark);
        mSearchCustomer = findViewById(R.id.searchCustomer);
        mSearchStatus = findViewById(R.id.searchStatus);
        mCoordinaterLayout = findViewById(R.id.coordinator);


        if (getIntent().getStringExtra("date") != null) {
            mEditTextDate.setText(getIntent().getStringExtra("date"));
            mEditTextCustomerId.setText(getIntent().getStringExtra("customerId"));
            mEditTextCustomerName.setText(getIntent().getStringExtra("customerName"));
            mEditTextComplaintDetails.setText(getIntent().getStringExtra("complaintDetails"));
            mEditTextComplaintId.setText(getIntent().getStringExtra("complaintId"));
            mEditTextRepresentative.setText(getUserId());
            mEditTextComplaintDetails.requestFocus();


            mEditTextDate.setKeyListener(null);
            mEditTextCustomerId.setKeyListener(null);
            mEditTextCustomerName.setKeyListener(null);
            mEditTextComplaintDetails.setKeyListener(null);
            mEditTextComplaintId.setKeyListener(null);
            mEditTextRepresentative.setKeyListener(null);


            mEditTextDate.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
            mEditTextCustomerId.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
            mEditTextCustomerName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
            mEditTextComplaintDetails.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
            mEditTextComplaintId.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
            mEditTextRepresentative.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);

        }

    }

    //  Function to provide current data and time
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Get LoggedIn users ID
    private String getUserId() {
        ArrayList<String> newArralist = new ArrayList<>();
        // Creates a shared preferences variable to retrieve the logeed in users IDs and store it in Updated By Section
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        newArralist = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        if (newArralist.size() > 0)
            return newArralist.get(6);

        return "Invalid User";

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showProgress() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(mCoordinaterLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                snackbar.show();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 20000);
    }

}
