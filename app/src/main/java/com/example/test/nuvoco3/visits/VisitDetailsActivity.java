package com.example.test.nuvoco3.visits;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import static com.example.test.nuvoco3.helpers.CalendarHelper.convertJsonDateToSmall;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDateTime;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_JCP_VISIT_DETAILS;
import static com.example.test.nuvoco3.helpers.Contract.MASTER_PRODUCT;
import static com.example.test.nuvoco3.helpers.Contract.MASTER_VISIT_STATUS;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;
import static com.example.test.nuvoco3.helpers.Contract.UPDATE_JCP_VISIT_DETAILS;

public class VisitDetailsActivity extends AppCompatActivity {
    private static final String TAG = "VisitDetails Activity";
    FloatingActionButton fab;
    boolean isChecked = false;
    TextInputEditText mEditTextRepresentative, mEditTextJcpId, mEditTextCustomerId, mEditTextCustomerName, mEditTextRemark, mEditTextStartTime, mEditTextEndTime, mEditTextDate, mEditTextQuantity;
    TextInputLayout mQuantityLayout;
    SearchableSpinner mStatusSpinner, mProductSpinner;
    CheckBox mCheckBoxOrder;
    TextView mTextViewVisitStatus;

    String mJcpId, mRecordId, mProduct, mDate, mCustomerId, mCustomerName, mObjective, mStartTime, mEndTime, mOrderStatus, mOrderQuantity, mVisitRemark, mVisitStatus, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy;
    MasterHelper mStatusHelper, mProductHelper;
    ArrayList<String> mStatusArrayList, mProductList;
    ArrayAdapter mStatusAdapter, mProductAdapter;

    RequestQueue queue;
    LinearLayout mLinearLayout, mStatusLayout;
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;



    //New Modification
    ArrayList<String> mDetailsArray;
    String mDetailsJcpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_details);
        initializeViews();
        initializeVariables();
        readData();
        populateSpinner();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        mCheckBoxOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCheckBoxOrder.isChecked()) {
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mEditTextQuantity.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInputFromInputMethod(mEditTextQuantity.getWindowToken(), 0);
                } else if (!mCheckBoxOrder.isChecked()) {
                    mLinearLayout.setVisibility(View.GONE);
                }
            }
        });


    }
    private void initializeVariables() {

        mStatusHelper = new MasterHelper(this, MASTER_VISIT_STATUS);
        mProductHelper = new MasterHelper(this, MASTER_PRODUCT);
        mStatusArrayList = mStatusHelper.getRecordName();
        mProductList = mProductHelper.getRecordName();
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);

        if (getIntent().getStringExtra("CustomerName") != null) {
            Log.i("lol", "initializeVariables: " + "here");
            mJcpId = getIntent().getStringExtra("JcpId");
            mDate = getIntent().getStringExtra("Date");
            mCustomerId = getIntent().getStringExtra("CustomerId");
            mCustomerName = getIntent().getStringExtra("CustomerName");
            mObjective = getIntent().getStringExtra("Objective");
            mStartTime = getIntent().getStringExtra("StartTime");
            mEndTime = getIntent().getStringExtra("EndTime");



            mEditTextJcpId.setText(mJcpId);
            mEditTextDate.setText(convertJsonDateToSmall(mDate));
            mEditTextCustomerId.setText(mCustomerId);
            mEditTextCustomerName.setText(mCustomerName);
            mEditTextStartTime.setText(mStartTime);
            mEditTextEndTime.setText(mEndTime);
            mEditTextRepresentative.setText(new UserInfoHelper(this).getUserId());


        }
    }

    private void initializeViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        mEditTextRepresentative = findViewById(R.id.textInputEditName);
        mEditTextJcpId = findViewById(R.id.textInputEditJcpId);
        mEditTextCustomerId = findViewById(R.id.textInputEditCustomerId);
        mEditTextCustomerName = findViewById(R.id.textInputEditCustomerName);
        mEditTextDate = findViewById(R.id.textInputEditDate);
        mEditTextStartTime = findViewById(R.id.textInputEditStartTime);
        mEditTextEndTime = findViewById(R.id.textInputEditEndTime);
        mEditTextQuantity = findViewById(R.id.textInputEditQuantity);
        mEditTextRemark = findViewById(R.id.textInputEditRemark);
        mCheckBoxOrder = findViewById(R.id.checkboxOrder);
        mStatusSpinner = findViewById(R.id.searchStatus);
        mQuantityLayout = findViewById(R.id.textInputLayoutQuantity);
        mLinearLayout = findViewById(R.id.linearLayout);
        mProductSpinner = findViewById(R.id.searchProduct);
        mCoordinatorLayout = findViewById(R.id.coordinator);
        mTextViewVisitStatus = findViewById(R.id.textViewVisitStatus);
        mStatusLayout = findViewById(R.id.statusLayout);
    }


    private void validateData() {
        mVisitRemark = mEditTextRemark.getText().toString();
        mOrderQuantity = mEditTextQuantity.getText().toString();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();

        if (TextUtils.isEmpty(mVisitRemark)) {
            mEditTextRemark.setError("Enter Remark");
        }
        if (mCheckBoxOrder.isChecked() && TextUtils.isEmpty(mOrderQuantity)) {
            mEditTextQuantity.setError("Enter The Order Quantity");
        }
        if (mCheckBoxOrder.isChecked() && mProduct.equals(getString(R.string.default_name)))
            Toast.makeText(this, "Select Product", Toast.LENGTH_SHORT).show();

        if (TextUtils.equals(mVisitStatus, getString(R.string.default_name)))
            Toast.makeText(this, "Select Visit Status", Toast.LENGTH_SHORT).show();

        if (!TextUtils.isEmpty(mVisitRemark) && !mCheckBoxOrder.isChecked() && !TextUtils.equals(mVisitStatus, getString(R.string.default_name))) {
            mProduct = getString(R.string.empty);
            mOrderQuantity = "0";
            mOrderStatus = "0";
            storeDataToServer();
        } else if (!TextUtils.isEmpty(mVisitRemark) && mCheckBoxOrder.isChecked() && !TextUtils.equals(mProduct, getString(R.string.default_name)) && !TextUtils.isEmpty(mOrderQuantity) && !TextUtils.equals(mVisitStatus, getString(R.string.default_name))) {
            mOrderStatus = "1";
            storeDataToServer();
        }

    }


    private void storeDataToServer() {
        showProgressDialogue();
        Map<String, String> postParam = new HashMap<>();

        postParam.put("1", mRecordId);
//        postParam.put("3", convertJsonTimToStandardDateTime(mDate));
//        postParam.put("4", mCustomerId);
//        postParam.put("5", mCustomerName);
//        postParam.put("6", mObjective);
        postParam.put("7", mVisitRemark);
        postParam.put("8", mVisitStatus);
//        postParam.put("9", mCreatedOn);
//        postParam.put("10", mCreatedBy);
        postParam.put("11", mUpdatedOn);
        postParam.put("12", mUpdatedBy);
        postParam.put("13", mOrderStatus);
        postParam.put("14", mOrderQuantity);
        postParam.put("15", mProduct);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + UPDATE_JCP_VISIT_DETAILS, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialogue();
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(VisitDetailsActivity.this, "Successfully Added Data", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(VisitDetailsActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error);
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage() + "lol");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(VisitDetailsActivity.this).getUserToken());
                return headers;
            }


        };

        jsonObjReq.setTag("LOL");
        // Adding request to request queue
        queue.add(jsonObjReq);

    }


    private void populateSpinner() {
        String mStatusString[] = {"Planned", "Yet-to-Meet", "Completed"};
        mStatusAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mStatusString);
        mStatusSpinner.setAdapter(mStatusAdapter);
        mProductAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mProductList);
        mProductSpinner.setAdapter(mProductAdapter);

        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mVisitStatus = mStatusArrayList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mVisitStatus = getString(R.string.default_name);
            }
        });

        mProductSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mProduct = mProductList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mProduct = getString(R.string.default_name);

            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialogue() {
        if (progressDialog != null) {

            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    validateData();
                                }
                            });
                            snackbar.show();
                        }
                    }
                }
            };
            Handler handler = new Handler();
            handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);
        }

    }

    private void readData() {
        showProgressDialogue();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_JCP_VISIT_DETAILS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog != null) {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

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
                Toast.makeText(VisitDetailsActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(VisitDetailsActivity.this).getUserToken());
                return headers;
            }


        };

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void fetchData(JSONObject object) {
        try {
            if (object.getString("JCP_id").toLowerCase().equals(getIntent().getStringExtra("JcpId"))) {
               mRecordId = object.getString("record_id");
                mVisitStatus = object.getString("Visit_status");
                mTextViewVisitStatus.setText("" + mVisitStatus);
                Toast.makeText(this, "" + mVisitStatus, Toast.LENGTH_SHORT).show();
                if (mVisitStatus.contains("Completed")) {
                    fab.hide();
                    mStatusLayout.setVisibility(View.GONE);
                    mTextViewVisitStatus.append(" - Cannot be Changed");
                    mCheckBoxOrder.setVisibility(View.GONE);
                    mEditTextRemark.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialogue() {
        if (progressDialog != null) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    }




