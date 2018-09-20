package com.example.test.nuvoco3.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.helpers.UserInfoHelper;
import com.example.test.nuvoco3.lead.Customer;
import com.example.test.nuvoco3.lead.CustomerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_CUSTOMER;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class ViewMyCustomerActivity extends AppCompatActivity {
    private static final String TAG = "NUVOCO";
    public static ArrayList<Customer> mCustomerArrayList, mSearchList;
    RecyclerView mRecyclerView;
    String mAddress, mArea, mDistrict, mCategory, mEmail, mPhone, mState, mCreatedBy, mCreatedOn, mUpdatedBy, mUpdatedOn, mId, mName, mStatus;
    CustomerAdapter mAdapter;
    RequestQueue queue;
    SearchView mSearchView;
    String mSearchText = "0";
    String mUserId;
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;
    int size = 0;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);
        initializeViews();
        initializeVariables();

        readData();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchList.clear();
                Log.e(TAG, "onQueryTextSubmit: BUTTON PRESSED" + query);
                mSearchText = query;
                for (int i = 0; i < mCustomerArrayList.size(); i++) {
                    if (mCustomerArrayList.get(i).getCustomerName().toLowerCase().contains(mSearchText.toLowerCase()) || mCustomerArrayList.get(i).getCustomerId().contains(mSearchText) || mCustomerArrayList.get(i).getCustomerPhoneno().contains(mSearchText) || mCustomerArrayList.get(i).getCustomerState().contains(mSearchText) || mCustomerArrayList.get(i).getCustomerEmailId().contains(mSearchText)) {
                        mSearchList.add(mCustomerArrayList.get(i));


                    }
                }
//                readData();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

                if (query.equals("")) {
                    mAdapter = new CustomerAdapter(ViewMyCustomerActivity.this, mCustomerArrayList);
                } else {
                    mAdapter = new CustomerAdapter(ViewMyCustomerActivity.this, mSearchList);
                }

                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                mSearchList.clear();
                mSearchText = query;
                for (int i = 0; i < mCustomerArrayList.size(); i++) {
                    if (mCustomerArrayList.get(i).getCustomerName().toLowerCase().contains(mSearchText.toLowerCase()) || mCustomerArrayList.get(i).getCustomerId().contains(mSearchText) || mCustomerArrayList.get(i).getCustomerPhoneno().contains(mSearchText) || mCustomerArrayList.get(i).getCustomerState().contains(mSearchText) || mCustomerArrayList.get(i).getCustomerEmailId().contains(mSearchText)) {
                        mSearchList.add(mCustomerArrayList.get(i));


                    }
                }
                mAdapter = new CustomerAdapter(ViewMyCustomerActivity.this, mSearchList);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }
        });

    }

    private void initializeVariables() {
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        mCustomerArrayList = new ArrayList<>();
        mSearchList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.getItemAnimator();
        mAdapter = new CustomerAdapter(this, mCustomerArrayList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initializeViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerView);
        mSearchView = findViewById(R.id.searchView);
        mCoordinatorLayout = findViewById(R.id.coordinator);
    }


    private void readData() {
        startProgressDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_CUSTOMER, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    JSONArray jsonArray = response.getJSONArray("message");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);


                        if (object.getString("createdBy").equals(new UserInfoHelper(ViewMyCustomerActivity.this).getUserId())) {
                            if (object.getString("c_email").toLowerCase().contains(mSearchText.toLowerCase())
                                    || object.getString("c_phone").toLowerCase().contains(mSearchText.toLowerCase())
                                    || object.getString("c_state").toLowerCase().contains(mSearchText.toLowerCase())
                                    || object.getString("name").toLowerCase().contains(mSearchText.toLowerCase())
                                    || object.getString("record_id").toLowerCase().contains(mSearchText.toLowerCase())) {

                                mAddress = object.getString("address") + "";

                                mArea = object.getString("c_area") + "";
                                mCategory = object.getString("c_category") + "";
                                mDistrict = object.getString("c_district") + "";
                                mEmail = object.getString("c_email") + "";
                                mPhone = object.getString("c_phone") + "";
                                mState = object.getString("c_state") + "";
                                mName = object.getString("name") + "";
                                mId = object.getString("record_id") + "";
                                mStatus = object.getString("status") + "";
                                mCreatedBy = object.getString("createdBy") + "";
                                mCreatedOn = object.getString("createdOn") + "";
                                mUpdatedBy = object.getString("updatedBy") + "";
                                mUpdatedOn = object.getString("updatedOn") + "";
//                Log.i(TAG, "onResponse: " + mAddress);
                                mCustomerArrayList.add(new Customer(mName, mId, mCategory, mAddress, mArea, mDistrict, mState, mPhone, mEmail, mStatus, mCreatedBy, mCreatedOn, mUpdatedBy, mUpdatedOn));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewMyCustomerActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(ViewMyCustomerActivity.this).getUserToken());
                return headers;
            }


        };

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.checkable_menu) {
            isChecked = !item.isChecked();
            item.setChecked(isChecked);
            mCustomerArrayList.clear();
            mAdapter.notifyDataSetChanged();
            readData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void enableSearch(View v) {

        mSearchView.setIconified(false);
    }

    private void startProgressDialog() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {

                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            readData();

                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);
    }




}