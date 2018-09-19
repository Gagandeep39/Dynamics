package com.example.test.nuvoco3.customer;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.CalendarHelper.compareSmallDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDateTime;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_CUSTOMER;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_COMPLAINT;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_COMPLAINT_DETAILS;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class InsertComplaintActivity extends AppCompatActivity {


    private static final String TAG = "InsertComplaintActivity";
    String mDate, mCustomerId, mCustomerName, mComplaintType, mComplaintDetails, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy, mStatus, mComplaintId, mRepresentative;
    SearchableSpinner mSearchType, mSearchCustomer;
    TextInputEditText mEditTextCustomerId, mEditTextComplaintDetails, mEditTextCustomer;
    TextInputLayout mEditTextLayout;
    LinearLayout mSearchCustomerLayout;
    TextView mTextViewDate;
    private boolean isChecked = false;


    int mYear, mMonth, mDay;
    ImageView imageViewCalendar;
    RequestQueue queue;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;
    FloatingActionButton fab;
    SearchableSpinner mStatusSpinner;


    MasterHelper mTypeHelper, mStatusHelper;
    //Arrray list returned from Helper class
    ArrayList<String> mTypeArrayList;
    ArrayList<String> mCustomerList, mIdList, mStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_complaint);


        initializeViews();
        initializeVariables();
        populateCustomers();
        populateSpinners();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

    }


    private void validateData() {
//        mDate = mEditTextDate.getText().toString();
        mComplaintDetails = mEditTextComplaintDetails.getText().toString();
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();
        mStatus = "Open";

        if (TextUtils.isEmpty(mComplaintDetails))
            mEditTextComplaintDetails.setError("Enter Details");
        if (TextUtils.isEmpty(mCustomerId))
            mEditTextCustomerId.setError("Enter Customer ID");
        if (TextUtils.equals(mCustomerName, getString(R.string.default_name)))
            Toast.makeText(this, "Select Customer", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mComplaintType, getString(R.string.default_name)))
            Toast.makeText(this, "Select Complaint Type", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mStatus, getString(R.string.default_name)))
            Toast.makeText(this, "Select Complaint Type", Toast.LENGTH_SHORT).show();

        if (!TextUtils.isEmpty(mDate) && !TextUtils.isEmpty(mComplaintDetails) && !TextUtils.isEmpty(mCustomerId) && !TextUtils.equals(mCustomerName, getString(R.string.default_name)) && !TextUtils.equals(mComplaintType, getString(R.string.default_name)) && !TextUtils.equals(mStatus, getString(R.string.default_name))) {
            Toast.makeText(this, "Data sent", Toast.LENGTH_SHORT).show();
            storeDataToServer();
        }
    }

    private void storeDataToServer() {

        Map<String, String> postParam = new HashMap<>();

//
        postParam.put("2", mDate);
        postParam.put("3", mCustomerId);
        postParam.put("4", mCustomerName);
        postParam.put("5", mComplaintType);
        postParam.put("6", mComplaintDetails);
        postParam.put("7", mCreatedOn);
        postParam.put("8", mCreatedBy);
        postParam.put("9", mUpdatedOn);
        postParam.put("10", mUpdatedBy);
        postParam.put("11", mStatus);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_COMPLAINT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                mComplaintId = response.getString("complaint_ID");
                                mRepresentative = new UserInfoHelper(InsertComplaintActivity.this).getUserId();
//                                startActivity(intent);
                                saveDetailsDataToServer();
                            } else {
                                Toast.makeText(InsertComplaintActivity.this, "" + response, Toast.LENGTH_SHORT).show();
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
                headers.put("x-access-token", new UserInfoHelper(InsertComplaintActivity.this).getUserToken());
                return headers;
            }


        };

        jsonObjReq.setTag("LOL");
        // Adding request to request queue
        queue.add(jsonObjReq);

    }


    private void populateSpinners() {
        ArrayAdapter<String> mCustomerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCustomerList);
        ArrayAdapter<String> mTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mTypeArrayList);

        mSearchCustomer.setAdapter(mCustomerAdapter);
        mSearchType.setAdapter(mTypeAdapter);
        mCustomerAdapter.notifyDataSetChanged();
        mSearchCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCustomerName = mCustomerList.get(position);
                mCustomerId = mIdList.get(position);
                Log.i("lol", "onItemSelected: " + mCustomerName + " " + mCustomerId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCustomerName = getString(R.string.default_name);
            }
        });
        mSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mComplaintType = mTypeArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mComplaintType = getString(R.string.default_name);
            }
        });


    }



    public void datePickerFunction(View v) {
        final View buttonClicked = v;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(buttonClicked.getWindowToken(), 0);

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (compareSmallDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())) {

                            Toast.makeText(InsertComplaintActivity.this, "Date cannot Exceed Current Date", Toast.LENGTH_SHORT).show();
                        } else {

                            mTextViewDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();

                            mDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + dateFormat.format(date);
                            Log.i(TAG, "onDateSet: " + mDate);

                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }




    public void populateCustomers() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mCoordinaterLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(InsertComplaintActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 10000);


        Map<String, String> postParam = new HashMap<>();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_CUSTOMER, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);


                        if (isChecked) {
                            if (object.getString("createdBy").equals(new UserInfoHelper(InsertComplaintActivity.this).getUserId())) {

                                mIdList.add(object.getString("record_id"));   //primary key
                                mCustomerList.add(object.getString("name"));

                            }


                        } else {
                            mIdList.add(object.getString("record_id"));   //primary key
                            mCustomerList.add(object.getString("name"));

                        }
                    }
                    progressDialog.dismiss();


                } catch (JSONException e1) {
                    e1.printStackTrace();
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InsertComplaintActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertComplaintActivity.this).getUserToken());
                return headers;
            }


        };

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void initializeVariables() {
        if (getIntent().getStringExtra("CustomerName") != null) {
            mSearchCustomerLayout.setVisibility(View.GONE);
            mEditTextLayout.setVisibility(View.VISIBLE);
            mEditTextCustomer.setText(getIntent().getStringExtra("CustomerName"));
            mEditTextCustomer.setKeyListener(null);
            mEditTextComplaintDetails.requestFocus();

            mCustomerName = getIntent().getStringExtra("CustomerName");
            mCustomerId = getIntent().getStringExtra("CustomerId");
        }

        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        //Initializing Helper class and its Array list
        mTypeHelper = new MasterHelper(this, "ComplaintType");
//        mStatusHelper = new MasterHelper(this, "ComplaintStatus");
        mTypeArrayList = new ArrayList<>();
        //Storing return Value in Array List
        mTypeArrayList = mTypeHelper.getRecordName();
//        mStatusList = mStatusHelper.getRecordName();

        mIdList = new ArrayList<>();
        mCustomerList = new ArrayList<>();

    }

    private void initializeViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        imageViewCalendar = findViewById(R.id.imageViewCalendar);
        mEditTextCustomerId = findViewById(R.id.textInputEditCustomerId);
        mEditTextComplaintDetails = findViewById(R.id.editTextDetails);
        mCoordinaterLayout = findViewById(R.id.coordinator);
        mSearchCustomer = findViewById(R.id.searchCustomer);
        mEditTextCustomer = findViewById(R.id.textInputEditName);
        mEditTextLayout = findViewById(R.id.editTextLayoutName);
        mTextViewDate = findViewById(R.id.textViewDate);
        mSearchCustomerLayout = findViewById(R.id.searchCustomerLayout);
        mSearchType = findViewById(R.id.searchType);
        mStatusSpinner = findViewById(R.id.searchStatus);
    }

    private void saveDetailsDataToServer() {
        showProgressDialogue();
        Map<String, String> postParam = new HashMap<>();

//
        postParam.put("2", mComplaintId);
        postParam.put("3", mDate);
        postParam.put("4", mRepresentative);
        postParam.put("5", mCustomerId);
        postParam.put("6", mCustomerName);
        postParam.put("7", mStatus);
        postParam.put("8", mComplaintDetails);
        postParam.put("9", "");
        postParam.put("10", mCreatedOn);
        postParam.put("11", mCreatedBy);
        postParam.put("12", mUpdatedOn);
        postParam.put("13", mUpdatedBy);
        postParam.put("14", "2017-01-01");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_COMPLAINT_DETAILS, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(InsertComplaintActivity.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(InsertComplaintActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InsertComplaintActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage() + "lol");
            }
        })  {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertComplaintActivity.this).getUserToken());
                return headers;
            }


        };
        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void showProgressDialogue() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mCoordinaterLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveDetailsDataToServer();
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
            isChecked = !item.isChecked();
            item.setChecked(isChecked);
            mCustomerList.clear();
            populateCustomers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
