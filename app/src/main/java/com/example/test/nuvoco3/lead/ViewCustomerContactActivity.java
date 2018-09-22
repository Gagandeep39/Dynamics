package com.example.test.nuvoco3.lead;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.helpers.UserInfoHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_CONTACT;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class ViewCustomerContactActivity extends AppCompatActivity {
    private static final String TAG = "ViewCustomerContact";
    public static ArrayList<CustomerContact> mCustomerContactArrayList, mSearchList;
    CustomerContactAdapter mAdapter;
    RequestQueue queue;
    SearchView mSearchView;
    String mSearchText = "0";
    RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;
    int size = 25;
    int count = 0;
    String mContactId, mCustomerId, mCustomerName, mContactName, mContactPhone, mContactEmail, mContactDOB, mContactDOA, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy;
    private boolean isChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer_contact);
        initializeViews();
        progressDialog = new ProgressDialog(this);
        populateActivity();


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchList.clear();
                Log.e(TAG, "onQueryTextSubmit: BUTTON PRESSED" + query);
                mSearchText = query;
                for (int i = 0; i < mCustomerContactArrayList.size(); i++) {
                    if (mCustomerContactArrayList.get(i).getCustomerName().toLowerCase().contains(mSearchText.toLowerCase()) || mCustomerContactArrayList.get(i).getCustomerId().contains(mSearchText) || mCustomerContactArrayList.get(i).getCustomerContactPhone().contains(mSearchText) || mCustomerContactArrayList.get(i).getCustomerContactEmail().contains(mSearchText) || mCustomerContactArrayList.get(i).getCustomerContactName().contains(mSearchText)) {
                        mSearchList.add(mCustomerContactArrayList.get(i));


                    }
                }
//                readData();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

                if (query.equals("")) {
                    mAdapter = new CustomerContactAdapter(ViewCustomerContactActivity.this, mCustomerContactArrayList);
                } else {
                    mAdapter = new CustomerContactAdapter(ViewCustomerContactActivity.this, mSearchList);
                }

                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setHasStableIds(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                mSearchList.clear();
                mSearchText = query;
                for (int i = 0; i < mCustomerContactArrayList.size(); i++) {
                    if (mCustomerContactArrayList.get(i).getCustomerName().toLowerCase().contains(mSearchText.toLowerCase()) || mCustomerContactArrayList.get(i).getCustomerId().contains(mSearchText) || mCustomerContactArrayList.get(i).getCustomerContactPhone().contains(mSearchText) || mCustomerContactArrayList.get(i).getCustomerContactEmail().contains(mSearchText) || mCustomerContactArrayList.get(i).getCustomerContactName().contains(mSearchText)) {
                        mSearchList.add(mCustomerContactArrayList.get(i));


                    }
                }
                mAdapter = new CustomerContactAdapter(ViewCustomerContactActivity.this, mSearchList);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }
        });
    }

    private void populateActivity() {
        queue = Volley.newRequestQueue(this);
        mCustomerContactArrayList = new ArrayList<>();
        if (getIntent().getStringExtra("customerID") != null) {
            mSearchText = getIntent().getStringExtra("customerID");
            Log.i(TAG, "populateActivity: " + mSearchText);
            readData();
        } else
            readData();
        mAdapter = new CustomerContactAdapter(this, mCustomerContactArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.getItemAnimator();
        mRecyclerView.setAdapter(mAdapter);

    }


    private void readData() {
        showProgress();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_CONTACT, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    JSONArray jsonArray = response.getJSONArray("message");
                    Log.i(TAG, "onResponse: " + response);
                   if (count==0){
                       count=1;
                       if (size>jsonArray.length()){
                           size = jsonArray.length();

                       }else {
                           size = 25;
                       }

                   }
                   else size = jsonArray.length();
                    for (int i = 0; i < size; i++) {

                        JSONObject object = jsonArray.getJSONObject(i);

                        if (isChecked) {
                            if (object.getString("createdBy").equals(new UserInfoHelper(ViewCustomerContactActivity.this).getUserId())) {
                                fetchData(object);

                            }


                        } else {
                            fetchData(object);

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
                Toast.makeText(ViewCustomerContactActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(ViewCustomerContactActivity.this).getUserToken());
                return headers;
            }
        };

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void fetchData(JSONObject object) {
        try {
            if (object.getString("record_id").toLowerCase().contains(mSearchText.toLowerCase())
                    || object.getString("customer_id").toLowerCase().contains(mSearchText.toLowerCase())
                    || object.getString("c_name").toLowerCase().contains(mSearchText.toLowerCase())
                    || object.getString("contactPerson").toLowerCase().contains(mSearchText.toLowerCase())
                    || object.getString("contactPerson_email").toLowerCase().contains(mSearchText.toLowerCase())) {
                mContactId = object.getString("record_id") + "";   //primary key
                mCustomerName = object.getString("c_name") + "";
                mContactName = object.getString("contactPerson") + "";
                mContactPhone = object.getString("contactPerson_phone") + "";
                mContactEmail = object.getString("contactPerson_email") + "";
                mContactDOB = object.getString("contactPerson_DOB") + "";
                mContactDOA = object.getString("contactPerson_DOA") + "";
                mCustomerId = object.getString("customer_id") + ""; //Foreign Key
                mCreatedBy = object.getString("createdBy") + "";
                mCreatedOn = object.getString("createdOn") + "";
                mUpdatedBy = object.getString("updatedBy") + "";
                mUpdatedOn = object.getString("updatedOn") + "";
                Log.i("lol", "onResponse: " + mCustomerId);
                mCustomerContactArrayList.add(new CustomerContact(mContactId, mCustomerId, mCustomerName, mContactName, mContactPhone, mContactEmail, mContactDOB, mContactDOA, mCreatedBy, mCreatedOn, mUpdatedBy, mUpdatedOn));
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initializeViews() {
        mSearchView = findViewById(R.id.searchView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mCoordinaterLayout = findViewById(R.id.coordinator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mSearchList = new ArrayList<>();
        mCustomerContactArrayList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public void enableSearch(View v) {

        mSearchView.setIconified(false);
    }


    private void showProgress() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mCoordinaterLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_customer_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.checkable_menu);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.checkable_menu) {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            isChecked = !item.isChecked();
            item.setChecked(isChecked);
            mCustomerContactArrayList.clear();
            mAdapter.notifyDataSetChanged();
            readData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
