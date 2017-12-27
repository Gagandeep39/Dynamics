package com.example.test.nuvoco3.market;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.signup.ObjectSerializer;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.signup.LoginActivity.DATABASE_URL;

public class InsertCampaignActivity extends AppCompatActivity {
    public static final String URL_ADD_CAMPAIGN = "/insertCamp";
    private static final String TAG = "InsertCampaign Activity";
    String mRepresentative, mCounter, mDate, mCompany, mCampaignDetail, mCreatedOn, mCreatedBy, mUpdatedBy, mUpdatedOn;
    TextInputEditText mEditTextRepresentative, mEditTextDetails;
    SearchableSpinner mSearchContact, mSearchCompany;
    String mCustomerArray[] = {"Customer 1", "Customer 2", "Customer 3"};
    String mContactArray[] = {"Contact 1", "Contact 2", "Contact 3"};
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_campaign);
        initializeViews();
        queue = Volley.newRequestQueue(this);
        populateSpinners();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void populateSpinners() {
        ArrayAdapter mCustomerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mCustomerArray);
        final ArrayAdapter mContactAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mContactArray);
        mSearchCompany.setAdapter(mCustomerAdapter);
        mSearchContact.setAdapter(mContactAdapter);
        mSearchCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCompany = mCustomerArray[position];
                mSearchCompany.setPadding(0, 0, 0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCompany = getString(R.string.default_name);
            }
        });
        mSearchContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCounter = mContactArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCounter = getString(R.string.default_name);
            }
        });
    }

    private void validateData() {
        mRepresentative = mEditTextRepresentative.getText().toString();
        mCampaignDetail = mEditTextDetails.getText().toString();
        mDate = getDateTime();
        mCreatedBy = getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = getUserId();
        mUpdatedOn = getDateTime();

        if (TextUtils.isEmpty(mRepresentative)) {
            mEditTextRepresentative.setError("Enter LoggedIn User's ID");
        }
        if (TextUtils.isEmpty(mCampaignDetail)) {
            mEditTextDetails.setError("Enter Details");
        }

        if (TextUtils.equals(mCompany, getString(R.string.default_name)))
            Toast.makeText(this, "Select a Company", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mCounter, getString(R.string.default_name)))
            Toast.makeText(this, "Select a Contact", Toast.LENGTH_SHORT).show();


        if (!TextUtils.isEmpty(mRepresentative) && !TextUtils.isEmpty(mCampaignDetail) && !TextUtils.equals(mCompany, getString(R.string.default_name)) && !TextUtils.equals(mCounter, getString(R.string.default_name)))
            storeDataOnServer();
    }

    private void storeDataOnServer() {

        Map<String, String> postParam = new HashMap<>();


        postParam.put("2", mRepresentative);
        postParam.put("3", mCounter);
        postParam.put("4", mDate);
        postParam.put("5", mCompany);
        postParam.put("6", mCampaignDetail);
        postParam.put("7", mCreatedOn);
        postParam.put("8", mCreatedBy);
        postParam.put("9", mUpdatedOn);
        postParam.put("10", mUpdatedBy);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, DATABASE_URL + URL_ADD_CAMPAIGN, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(InsertCampaignActivity.this, "Successfully Inserted New Contact", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };

        jsonObjReq.setTag("LOL");
        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mEditTextRepresentative = findViewById(R.id.textInputEditRepresentative);
        mEditTextDetails = findViewById(R.id.editTextDetails);
        mSearchCompany = findViewById(R.id.searchCompany);
        mSearchContact = findViewById(R.id.searchContact);
        mEditTextRepresentative.setText(getUserId());
        mEditTextRepresentative.setKeyListener(null);


    }

    //  Function to provide current data and time
    private String getDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Get LoggedIn users ID
    private String getUserId() {
        ArrayList<String> newArralist = new ArrayList<>();
        // Creates a shared preferences variable to retrieve the logeed in users IDs and store it in Updated By Section
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        try {
            newArralist = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
