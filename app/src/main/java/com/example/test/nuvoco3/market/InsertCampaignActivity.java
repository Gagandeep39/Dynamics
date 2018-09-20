package com.example.test.nuvoco3.market;

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
import static com.example.test.nuvoco3.helpers.Contract.INSERT_CAMPAIGN;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class InsertCampaignActivity extends AppCompatActivity {
    private static final String TAG = "InsertCampaign Activity";
    String mRepresentative, mCounter, mDate, mCompany, mCampaignDetail, mCreatedOn, mCreatedBy, mUpdatedBy, mUpdatedOn;
    TextInputEditText mEditTextRepresentative, mEditTextDetails, mEditTextCustomerName;
    TextInputLayout mEditTextLayout;
    SearchableSpinner mSearchContact, mSearchCustomer, mSearchBrand;
    RequestQueue queue;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;
    FloatingActionButton fab;
    ArrayAdapter mCustomerAdapter, mContactAdapter, mBrandAdapter;
    LinearLayout mSearchCompanyLayout;
    boolean isChecked = false;
    int mYear, mMonth, mDay;
    TextView mTextViewDate;

    //Spinners
    ArrayList<String> mContactList, mCustomerList;

    //Master helper
    MasterHelper mBrandHelper;
    ArrayList<String> mBrandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_campaign);
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

    private void initializeVariables() {
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        mCustomerList = new ArrayList<>();
        mContactList = new ArrayList<>();
        mBrandList = new ArrayList<>();
        mBrandHelper = new MasterHelper(this, "Brand");
        mBrandList = mBrandHelper.getRecordName();


        if (getIntent().getStringExtra("CustomerDetails") != null) {
            mEditTextLayout.setVisibility(View.VISIBLE);
            mEditTextLayout.setOnKeyListener(null);
            mSearchCompanyLayout.setVisibility(View.GONE);
            mEditTextDetails.requestFocus();

            mEditTextCustomerName.setText(getIntent().getStringExtra("CustomerName"));
            mCompany = getIntent().getStringExtra("CustomerName");
        }



    }

    private void populateSpinners() {
        mBrandAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mBrandList);
        mCustomerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mCustomerList);

        mSearchCustomer.setAdapter(mCustomerAdapter);
        mSearchBrand.setAdapter(mBrandAdapter);

        mSearchCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCounter = mCustomerList.get(position);
                mSearchContact.setAdapter(mContactAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCounter = getString(R.string.default_name);

            }
        });
        mSearchContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mCounter = mContactList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSearchBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCompany = mBrandList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mCompany = getString(R.string.default_name);

            }
        });
    }


    public void populateCustomers() {
        showProgressDialogue();


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
                        if (isChecked) {
                            if (object.getString("createdBy").equals(new UserInfoHelper(InsertCampaignActivity.this).getUserId())) {
                                mCustomerList.add(object.getString("name"));

                            }


                        } else {
                            mCustomerList.add(object.getString("name"));

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
                Toast.makeText(InsertCampaignActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertCampaignActivity.this).getUserToken());
                return headers;
            }


        };
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
                            Toast.makeText(InsertCampaignActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);

    }


    private void validateData() {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        mDate = mTextViewDate.getText().toString() + " " + dateFormat.format(date);
        mRepresentative = mEditTextRepresentative.getText().toString();
        mCampaignDetail = mEditTextDetails.getText().toString();
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();

        if (TextUtils.isEmpty(mRepresentative)) {
            mEditTextRepresentative.setError("Enter LoggedIn User's ID");
        }
        if (TextUtils.isEmpty(mCampaignDetail)) {
            mEditTextDetails.setError("Enter Details");
        }

        if (TextUtils.equals(mCompany, getString(R.string.default_name)))
            Toast.makeText(this, "Select a Brand", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mCounter, getString(R.string.default_name)))
            Toast.makeText(this, "Select a Customer", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(mDate))
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();


        if (!TextUtils.isEmpty(mRepresentative) && !TextUtils.isEmpty(mCampaignDetail) && !TextUtils.equals(mCompany, getString(R.string.default_name)) && !TextUtils.equals(mCounter, getString(R.string.default_name)) && !TextUtils.isEmpty(mDate))
            storeDataOnServer();
    }

    private void storeDataOnServer() {
        showProgressDialogue();


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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_CAMPAIGN, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(InsertCampaignActivity.this, "Successfully Inserted New Contact", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(InsertCampaignActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InsertCampaignActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage());
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
                headers.put("x-access-token", new UserInfoHelper(InsertCampaignActivity.this).getUserToken());
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
        mSearchCustomer = findViewById(R.id.searchCompany);
        mCoordinaterLayout = findViewById(R.id.coordinator);
        mSearchContact = findViewById(R.id.searchContact);
        mEditTextCustomerName = findViewById(R.id.textInputEditCustomerName);
        mSearchCompanyLayout = findViewById(R.id.searchCompanyLayout);
        mEditTextLayout = findViewById(R.id.textInputLayoutCustomerName);
        mTextViewDate = findViewById(R.id.textViewDate);
        mSearchBrand = findViewById(R.id.searchBrand);

        fab = findViewById(R.id.fab);
        mEditTextRepresentative.setText(new UserInfoHelper(this).getUserId());
        mEditTextRepresentative.setKeyListener(null);

        mTextViewDate.setText(getDate());
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
            mCustomerList.clear();
            populateCustomers();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        String dateText = "";
                        if (compareSmallDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())){

                            Toast.makeText(InsertCampaignActivity.this, "Date cannot Exceed Current Date", Toast.LENGTH_SHORT).show();
                        }else {
                            if (monthOfYear + 1 >= 10)
                                dateText = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            else
                                dateText = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;

                            mTextViewDate.setText(dateText);
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date date = new Date();

                            mDate = dateText + " " + dateFormat.format(date);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }



}
