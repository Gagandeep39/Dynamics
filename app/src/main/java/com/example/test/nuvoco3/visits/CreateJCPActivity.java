package com.example.test.nuvoco3.visits;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.CalendarHelper.compareSmallDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.compareSmallDateEquality;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDateTime;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_CUSTOMER;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_JCP_VISIT_DETAILS;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_JCP_VIST;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;
import static com.example.test.nuvoco3.helpers.Contract.VISIT_STATUS_PLANNED;

public class CreateJCPActivity extends AppCompatActivity {
    private static final String TAG = "CreateJCP Activity";
    int mYear, mMonth, mDay, mHour, mMinute;
    TextView mTextViewStartTime, mTextViewEndTime, mTextViewDate;
    FloatingActionButton fab;
    TextInputEditText mEditTextRepresentative, mEditTextObjective;
    String mCustomerId, mCustomerName, mDate, mStartTime, mEndTime, mObjective, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy;
    SearchableSpinner mCustomerSpinner, mObjectiveSpinner;
    ArrayList<String> mCustomerList, mIdList, mObjectiveList;
    ArrayAdapter mCustomerAdapter, mObjectiveAdapter;
    RequestQueue queue;
    boolean isChecked = false;
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;

    MasterHelper mObjectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_jcp);
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

    private void validateData() {
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();
        Log.e(TAG, "validateData1: " + getDateTime());
        Log.e(TAG, "validateData2: " + mTextViewDate.getText().toString());
        mDate = convertDate(mTextViewDate.getText().toString());

        if (TextUtils.isEmpty(mDate))
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(mStartTime))
            Toast.makeText(this, "Select Start Time", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(mEndTime))
            Toast.makeText(this, "Select End Time", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(mObjective))
            mEditTextObjective.setError("Enter Objective");
        if (TextUtils.equals(mCustomerName, getString(R.string.default_name)))
            Toast.makeText(this, "Select Customer", Toast.LENGTH_SHORT).show();

        if (!TextUtils.isEmpty(mCustomerId) && !TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime) && !TextUtils.isEmpty(mDate) && !TextUtils.equals(mCustomerName, getString(R.string.default_name)) && !TextUtils.isEmpty(mObjective))
            sendDataToServer();

    }

    private void sendDataToServer() {
        showProgressDialogue();
        Map<String, String> postParam = new HashMap<>();

        postParam.put("2", mCustomerId);
        postParam.put("3", mCustomerName);
        postParam.put("4", mDate);
        postParam.put("5", mStartTime);
        postParam.put("6", mEndTime);
        postParam.put("7", mObjective);
        postParam.put("8", mCreatedOn);
        postParam.put("9", mCreatedBy);
        postParam.put("10", mUpdatedOn);
        postParam.put("11", mUpdatedBy);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_JCP_VIST, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                               storeDetailsToServer(response.getString("id"));
                            } else if (response.getString("status").equals(getString(R.string.something_went_wrong))) {
                                Toast.makeText(CreateJCPActivity.this, "" + getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CreateJCPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateJCPActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(CreateJCPActivity.this).getUserToken());
                return headers;
            }
        };
        // Adding request to request queue
        queue.add(jsonObjReq);
    }


    private void populateSpinners() {
        mCustomerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCustomerList);
        mObjectiveAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mObjectiveList);

        mCustomerSpinner.setAdapter(mCustomerAdapter);
        mCustomerAdapter.notifyDataSetChanged();

        mObjectiveSpinner.setAdapter(mObjectiveAdapter);
        mObjectiveAdapter.notifyDataSetChanged();

        mCustomerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCustomerName = mCustomerList.get(i);
                mCustomerId = mIdList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mCustomerName = getString(R.string.default_name);

            }
        });
        mObjectiveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mObjective = mObjectiveList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mObjective = getString(R.string.default_name);

            }
        });
    }

    private void initializeVariables() {
        progressDialog = new ProgressDialog(this);
        mCustomerList = new ArrayList<>();
        mIdList = new ArrayList<>();
        mObjectHelper = new MasterHelper(this, "Objective");
        mObjectiveList = mObjectHelper.getRecordName();

        queue = Volley.newRequestQueue(this);
        mEditTextRepresentative.setText(new UserInfoHelper(this).getUserId());
        populateCustomers();

    }

    private void initializeViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        mTextViewDate = findViewById(R.id.textViewDate);
        mTextViewEndTime = findViewById(R.id.textViewEndTime);
        mTextViewStartTime = findViewById(R.id.textViewStartTime);
        mEditTextRepresentative = findViewById(R.id.textInputEditTextRepresentative);
//        mEditTextObjective = findViewById(R.id.textInputEditTextObjective);
        mCustomerSpinner = findViewById(R.id.searchCustomer);
        mObjectiveSpinner = findViewById(R.id.searchObjective);
        mCoordinatorLayout = findViewById(R.id.coordinator);

        mTextViewDate.setText(getDate());
    }

    public void timePickerFunction(final View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextViewStartTime.getWindowToken(), 0);

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (v.getId() == R.id.textViewStartTime) {
                            mStartTime = hourOfDay + ":" + minute + ":00";
                            mTextViewStartTime.setText(hourOfDay + ":" + minute);
                        }
                        if (v.getId() == R.id.textViewEndTime) {
                            mEndTime = hourOfDay + ":" + minute + ":00";
                            mTextViewEndTime.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public void datePickerFunction(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextViewDate.getWindowToken(), 0);

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
                        if (compareSmallDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())||compareSmallDateEquality(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())){
                            String dateText = "";
                            if (monthOfYear + 1 >= 10)
                                dateText = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            else
                                dateText = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;

                            mTextViewDate.setText(dateText);
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date date = new Date();

                            mDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + dateFormat.format(date);

                        }else {
                            Toast.makeText(CreateJCPActivity.this, "Cannot plan a visit for a Past date", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public void populateCustomers() {
        if (progressDialog != null) {

            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_CUSTOMER, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismissProgressDialogue();
                    JSONArray jsonArray = response.getJSONArray("message");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        if (isChecked) {
                            if (object.getString("createdBy").equals(new UserInfoHelper(CreateJCPActivity.this).getUserId())) {
                                mCustomerList.add(object.getString("name"));
                                mIdList.add(object.getString("record_id"));

                            }
                        } else {
                            mCustomerList.add(object.getString("name"));
                            mIdList.add(object.getString("record_id"));

                        }
                    }
                    Log.i(TAG, "onResponse: " + mCustomerList.size());

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                dismissProgressDialogue();
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(CreateJCPActivity.this).getUserToken());
                return headers;
            }


        };
        // Adding request to request queue
        queue.add(jsonObjReq);
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


    private void showProgressDialogue() {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
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
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);

    }


    private void storeDetailsToServer(String mJcpId) {
        showProgressDialogue();
        Map<String, String> postParam = new HashMap<>();

        postParam.put("2", mJcpId);
        postParam.put("3", mDate);
        postParam.put("4", mCustomerId);
        postParam.put("5", mCustomerName);
        postParam.put("6", mObjective);
        postParam.put("7", "Non");
        postParam.put("8", VISIT_STATUS_PLANNED);
        postParam.put("9", mCreatedOn);
        postParam.put("10", mCreatedBy);
        postParam.put("11", mUpdatedOn);
        postParam.put("12", mUpdatedBy);
        postParam.put("13", "0");
        postParam.put("14", "0");
        postParam.put("15", "none");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_JCP_VISIT_DETAILS, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                dismissProgressDialogue();
                                Toast.makeText(CreateJCPActivity.this, "Successfully Added Data", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CreateJCPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
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
                headers.put("x-access-token", new UserInfoHelper(CreateJCPActivity.this).getUserToken());
                return headers;
            }


        };

        jsonObjReq.setTag("LOL");
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    public void dismissProgressDialogue() {
        if (progressDialog != null) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public String convertDate(String start_dt) {
        Log.e(TAG, "convertJsonDateToSmall: " + start_dt);
        String pattern = "yyyy-MM-dd";
        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date = null;
        //Wed, 19 Sep 2018 23:11:42 GMT
        try {
            date = formatter.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String finalString = newFormat.format(date);
        return finalString;


    }




}
