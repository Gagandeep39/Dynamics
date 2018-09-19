package com.example.test.nuvoco3.customer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.test.nuvoco3.signup.LoginActivity.DATABASE_URL;

public class ComplaintStatusActivity extends AppCompatActivity {
    SearchableSpinner mSearchCustomer;
    ArrayList<String> mCustomerArrayList;
    RequestQueue queue;
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;
    ArrayList<Complaints> mComplaintArrayList;

    ArrayAdapter mComplaintAdapter;
    String mCustomer;
    String mComplaintId, mCustomerId, mCustomerName, mCreatedOn, mCreatedBy, mUpdatedBy, mUpdatedOn, mDetails, mType, mDate;
    RecyclerView mRecyclerView;
    ComplaintAdapter mComplaintRecyclerAdapter;
    TextView mTextViewHelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_status);
        initializeViews();
        initializeVariables();
        populateCustomers();
        populateSpinner();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.getItemAnimator();




    }

    private void initializeVariables() {

        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        mCustomerArrayList = new ArrayList<>();
        mComplaintArrayList = new ArrayList<>();
        mComplaintAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mComplaintArrayList);
    }

    private void populateSpinner() {
        ArrayAdapter mCustomerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mCustomerArrayList);
        mSearchCustomer.setAdapter(mCustomerAdapter);
        mSearchCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mCustomer = mCustomerArrayList.get(position);
                readData();
//                mRecyclerView.setLayoutManager(new LinearLayoutManager(ComplaintStatusActivity.this));
//                mRecyclerView.setHasFixedSize(true);
//                mComplaintRecyclerAdapter = new ComplaintAdapter(ComplaintStatusActivity.this, mComplaintArrayList);
//                mRecyclerView.setAdapter(mComplaintRecyclerAdapter);
//                mComplaintRecyclerAdapter.notifyDataSetChanged();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mSearchCustomer = findViewById(R.id.searchCustomer);
        mCoordinatorLayout = findViewById(R.id.coordinator);
        mRecyclerView = findViewById(R.id.recyclerView);
        mTextViewHelp = findViewById(R.id.textViewHelp);

    }

    public void populateCustomers() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        populateCustomers();
                    }
                });
                snackbar.show();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 20000);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                DATABASE_URL + "/dispCust", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        mCustomerArrayList.add(object.getString("name"));
                    }


                } catch (JSONException e1) {
                    e1.printStackTrace();
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void readData() {
        startProgressDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                DATABASE_URL + "/dispComplaint", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("lol", "onResponse:  " + response);
                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("Customer_name").toLowerCase().contains(mCustomer)) {
                            mComplaintId = object.getString("record_id") + "";
                            mCustomerId = object.getString("Customer_id") + "";
                            mCustomerName = object.getString("Customer_name") + "";
                            mDate = object.getString("Date") + "";
                            mType = object.getString("Type_Ofcomplaint") + "";
                            mDetails = object.getString("complaint_details") + "";

                            mCreatedOn = object.getString("createdOn") + "";
                            mCreatedBy = object.getString("createdBy") + "";
                            mUpdatedBy = object.getString("upatedBy") + "";
                            mUpdatedOn = object.getString("upatedOn") + "";
                            mComplaintArrayList.add(new Complaints(mCustomerId, mCustomerName, mType, mDetails, mComplaintId, mDate, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy));

                        }

                    }
                    mComplaintRecyclerAdapter = new ComplaintAdapter(ComplaintStatusActivity.this, mComplaintArrayList);
                    mTextViewHelp.setVisibility(View.GONE);
                    mRecyclerView.setAdapter(mComplaintRecyclerAdapter);
                    Log.i("test", "onResponse: " + "sdfrgthj");



                } catch (JSONException e1) {
                    e1.printStackTrace();
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);

    }


    private void startProgressDialog() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        readData();
                    }
                });
                snackbar.show();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 20000);
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
