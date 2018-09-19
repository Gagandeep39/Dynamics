package com.example.test.nuvoco3.visits;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.helpers.MasterHelper;
import com.example.test.nuvoco3.helpers.UserInfoHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.CalendarHelper.getDateTime;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_JCP_VISIT;
import static com.example.test.nuvoco3.helpers.Contract.MASTER_VISIT_STATUS;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;
import static com.example.test.nuvoco3.helpers.Contract.UPDATE_JCP_VISIT_DETAILS;
import static com.example.test.nuvoco3.helpers.Contract.VISIT_STATUS_COMPLETED;

public class ViewVisitDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ViewVisitDetails";
    CoordinatorLayout mCoordinatorLayout;
    TextInputEditText mEditTextCustomerId, mEditTextJcpId, mEditTextCustomerName, mEditTextDate, mEditTextStartTime, mEditTextEndTime, mEditTextOrderTaken, mEditTextProduct, mEditTextOrderQuantity, mEditTextVisitRemark, mEditTextVisitStatus, mEditTextObjective;
    SearchableSpinner mVisitStatusSpinner;
    LinearLayout mOrderTakenLayout, mVisitStatusLayout;
    String mRecordId, mJcpId, mCustomerId, mCustomerName, mVisitStatus, mDate, mStartTime, mEndTime, mQuantity, mOrderTaken, mProduct, mRemark, mObjective, mUpdatedOn, mUpdatedBy;
ProgressDialog progressDialog;
RequestQueue queue;

//helper
    MasterHelper mVisitStatusHelper;
    ArrayList<String> mVisitStatusList;
    ArrayAdapter mVisitStatusAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visit_details);
        initializeViews();
        initializeVariables();

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        mUpdatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        if (mVisitStatusLayout.getVisibility()==View.VISIBLE){
            if (mVisitStatus.equals(getString(R.string.default_name)))
                Toast.makeText(this, "Update Visit Status", Toast.LENGTH_SHORT).show();
            else
                sendDataToServer();
        }else if(mVisitStatusLayout.getVisibility()==View.GONE){

            Toast.makeText(this, "The Visit is already Completed" , Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void sendDataToServer() {
        startProgressDialog();
        Map<String, String> postParam = new HashMap<>();

        postParam.put("1", mRecordId);
        postParam.put("7", mRemark);
        postParam.put("8", mVisitStatus);
        postParam.put("11", mUpdatedOn);
        postParam.put("12", mUpdatedBy);
        postParam.put("13", mOrderTaken);
        postParam.put("14", mQuantity);
        postParam.put("15", mProduct);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + UPDATE_JCP_VISIT_DETAILS, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialogue();
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(ViewVisitDetailsActivity.this, "Successfully Updated Visit Status", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (response.getString("status").equals(getString(R.string.something_went_wrong))) {
                                Toast.makeText(ViewVisitDetailsActivity.this, "" + getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ViewVisitDetailsActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewVisitDetailsActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(ViewVisitDetailsActivity.this).getUserToken());
                return headers;
            }
        };
        // Adding request to request queue
        queue.add(jsonObjReq);
    }


    private void initializeVariables(){
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        if (getIntent().getStringExtra("JcpId")!=null){
            mVisitStatus = getIntent().getStringExtra("VisitStatus");
            mRemark = getIntent().getStringExtra("VisitRemark");
            mOrderTaken =getIntent().getStringExtra("OrderTaken") ;
            mJcpId =getIntent().getStringExtra("JcpId") ;
            mRecordId = getIntent().getStringExtra("RecordId");
            mEditTextJcpId.setText(mJcpId);
            mEditTextVisitStatus.setText(mVisitStatus);
            mEditTextVisitRemark.setText(mRemark);
            mEditTextOrderTaken.setText(mOrderTaken);
            Log.i(TAG, "initializeVariables: " + getIntent().getStringExtra("OrderTaken"));
            if (!getIntent().getStringExtra("OrderTaken").equals("0")){
                mOrderTakenLayout.setVisibility(View.VISIBLE);
                mEditTextOrderQuantity.setText(getIntent().getStringExtra("OrderQuantity"));
                mEditTextProduct.setText(getIntent().getStringExtra("OrderedProduct"));
            }
            if (!mVisitStatus.equals(VISIT_STATUS_COMPLETED)){
                mVisitStatusLayout.setVisibility(View.VISIBLE);
                mVisitStatus = getString(R.string.default_name);
                populateSpinners();
            }
        }

        readData();
    }

    private void populateSpinners()   {
    mVisitStatusAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mVisitStatusList);

        mVisitStatusSpinner.setAdapter(mVisitStatusAdapter);
        mVisitStatusAdapter.notifyDataSetChanged();
        mVisitStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mVisitStatus = mVisitStatusList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mVisitStatus = getString(R.string.default_name);

            }
        });
    }

    private void initializeViews(){


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mEditTextCustomerId = findViewById(R.id.editTextCustomerId);
        mEditTextJcpId = findViewById(R.id.editTextJcpId);
        mEditTextCustomerName = findViewById(R.id.editTextCustomerName);
        mEditTextDate = findViewById(R.id.editTextDate);
        mEditTextEndTime = findViewById(R.id.editTextEndTime);
        mEditTextStartTime = findViewById(R.id.editTextStartTime);
        mEditTextVisitStatus = findViewById(R.id.editTextStatus);
        mEditTextVisitRemark = findViewById(R.id.editTextRemark);
        mEditTextOrderTaken = findViewById(R.id.editTextOrderTaken);
        mEditTextProduct = findViewById(R.id.editTextProduct);
        mEditTextOrderQuantity = findViewById(R.id.editTextQuantity);
        mVisitStatusSpinner = findViewById(R.id.searchStatus);
        mEditTextObjective = findViewById(R.id.editTextObjective);
        mOrderTakenLayout = findViewById(R.id.orderTakenLayout);
        mVisitStatusLayout = findViewById(R.id.visitStatusLayout);
        mCoordinatorLayout = findViewById(R.id.coordinator);

        mVisitStatusHelper = new MasterHelper(this, MASTER_VISIT_STATUS);
        mVisitStatusList = mVisitStatusHelper.getRecordName();
      }

    private void readData() {
        startProgressDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_JCP_VISIT, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialogue();
                try {
                    Log.i(TAG, "onResponse: " + response);

                    JSONArray jsonArray = response.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject object = jsonArray.getJSONObject(i);
                        fetchData(object);


                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewVisitDetailsActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(ViewVisitDetailsActivity.this).getUserToken());
                return headers;
            }


        };

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void fetchData(JSONObject object) {
        try {
            if (object.getString("record_id").toLowerCase().equals(getIntent().getStringExtra("JcpId"))) {
                mCustomerId = object.getString("Customer_id") + "";

                mDate = object.getString("Date") + "";
                mObjective = object.getString("Objective") + "";
                mCustomerName = object.getString("customer_name") + "";
                mEndTime = object.getString("end_Time") + "";
                mStartTime = object.getString("start_Time") + "";
            }
            mEditTextCustomerId.setText(mCustomerId);
            mEditTextDate.setText(mDate);
            mEditTextObjective.setText(mObjective);
            mEditTextCustomerName.setText(mCustomerName);
            mEditTextStartTime.setText(mStartTime);
            mEditTextEndTime.setText(mEndTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startProgressDialog() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dismissProgressDialogue();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void dismissProgressDialogue() {
        if (progressDialog != null) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}
