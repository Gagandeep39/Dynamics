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
import static com.example.test.nuvoco3.helpers.Contract.INSERT_GENERAL_MARKET_INFO;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class InsertGeneralMarketActivity extends AppCompatActivity {
    private static final String TAG = "GeneralMarket Activity";
    String mRepresentative, mBrand, mCounter, mDate, mMarketDetail, mMSP, mPrice, mDemand, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy;
    TextInputEditText mEditTextRepresentative, mEditTextCounter, mEditTextMSP, mEditTextPrice;
    TextInputEditText mEditTextMarketDetails, mEditTextDemands;
    SearchableSpinner mSearchCustomer, mSearchProduct, mSearchBrand;
    RequestQueue queue;
    ArrayList<String> mCustomerList, mIdList;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;
    MasterHelper mBrandHelper, mProductHelper;
    ArrayList<String> mBrandList, mProductList;
    ArrayAdapter mBrandAdapter, mProductAdapter;
    TextInputEditText mTextViewDate;
    int mYear, mMonth, mDay;
    boolean isChecked = false;
    ArrayAdapter<String> mCustomerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_general_market);
        initializeViews();
        initializeVariables();
        populateCustomers();
        populateSpinner();
        FloatingActionButton fab = findViewById(R.id.fab);
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

        mBrandHelper = new MasterHelper(this, "Brand");
        mProductHelper = new MasterHelper(this, "Product");

        mBrandList = mBrandHelper.getRecordName();
        mProductList = mProductHelper.getRecordName();
        mCustomerList = new ArrayList<>();
    }

    private void validateData() {
        mRepresentative = mEditTextRepresentative.getText().toString();
        mPrice = mEditTextPrice.getText().toString();
        mMarketDetail = mEditTextMarketDetails.getText().toString();
        mDemand = mEditTextDemands.getText().toString();
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();
        if (TextUtils.isEmpty(mRepresentative)) {
            mEditTextRepresentative.setError("Enter Representative's Name");
        }
        if (TextUtils.isEmpty(mPrice)) {
            mEditTextPrice.setError("Enter Price");
        }
        if (TextUtils.isEmpty(mMarketDetail)) {
            mEditTextMarketDetails.setError("Enter Market Details");
        }
        if (mMSP.equals(getString(R.string.default_name))) {
            Toast.makeText(this, "Select MajorSelling Product", Toast.LENGTH_SHORT).show();
        }
        if (mDate == null) {
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
        }


        if (TextUtils.equals(mCounter, "default"))
            Toast.makeText(this, "Select a Customer", Toast.LENGTH_SHORT).show();

        if (TextUtils.equals(mCounter, "default"))
            Toast.makeText(this, "Select a Brand", Toast.LENGTH_SHORT).show();


        if (!TextUtils.isEmpty(mRepresentative) && !TextUtils.isEmpty(mCounter) && !TextUtils.equals(mMSP, getString(R.string.default_name)) && !TextUtils.isEmpty(mPrice) && !TextUtils.isEmpty(mMarketDetail) && !TextUtils.isEmpty(mDemand) && !TextUtils.equals(mCounter, getString(R.string.default_name)) && !TextUtils.equals(mBrand, getString(R.string.default_name)))
            sendDataToServer();
    }

    //Saves Data to Server
    private void sendDataToServer() {
        showValidationDialogue();

        Map<String, String> postParam = new HashMap<>();

//
        postParam.put("2", mRepresentative);
        postParam.put("3", mCounter);
        postParam.put("4", mDate);
        postParam.put("5", mBrand);
        postParam.put("6", mMarketDetail);
        postParam.put("7", mMSP);
        postParam.put("8", mPrice);
        postParam.put("9", mDemand);
        postParam.put("10", mCreatedOn);
        postParam.put("11", mCreatedBy);
        postParam.put("12", mUpdatedOn);
        postParam.put("13", mUpdatedBy);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_GENERAL_MARKET_INFO, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(InsertGeneralMarketActivity.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(InsertGeneralMarketActivity.this, "" + response, Toast.LENGTH_SHORT).show();
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
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertGeneralMarketActivity.this).getUserToken());
                return headers;
            }


        };
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    //Shows Dialogue to Populate Customer


    //Shows dialogue to Send Data to server
    private void showValidationDialogue() {

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
                            validateData();
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 20000);
    }

    //Adds data to Spinner
    private void populateSpinner() {
        mCustomerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mCustomerList);
        mSearchCustomer.setAdapter(mCustomerAdapter);
        mCustomerAdapter.notifyDataSetChanged();
        mSearchCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCounter = mCustomerList.get(position);
//                mCustomerId = mIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCounter = getString(R.string.default_name);
            }
        });

        //Master helper Spinner
        mBrandAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mBrandList);
        mProductAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mProductList);

        mSearchBrand.setAdapter(mBrandAdapter);
        mSearchProduct.setAdapter(mProductAdapter);
        mSearchBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBrand = mBrandList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBrand = getString(R.string.default_name);

            }
        });

        mSearchProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMSP = mProductList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mMSP = getString(R.string.default_name);

            }
        });
    }

    //Get References for Views
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mEditTextRepresentative = findViewById(R.id.textInputEditRepresentative);
        mEditTextPrice = findViewById(R.id.textInputEditPrice);
        mEditTextMarketDetails = findViewById(R.id.editTextDetails);
        mCoordinaterLayout = findViewById(R.id.coordinator);
        mEditTextDemands = findViewById(R.id.editTextDemand);
        mSearchCustomer = findViewById(R.id.searchCategoryCustomer);
        mSearchProduct = findViewById(R.id.searchCategoryProduct);
        mSearchBrand = findViewById(R.id.searchCategoryBrand);
        mTextViewDate = findViewById(R.id.editTextDate);


        mTextViewDate.setText(getDate());
        mEditTextRepresentative.setText(new UserInfoHelper(this).getUserId());
        mEditTextRepresentative.setKeyListener(null);
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
                            if (object.getString("createdBy").equals(new UserInfoHelper(InsertGeneralMarketActivity.this).getUserId())) {
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
                Toast.makeText(InsertGeneralMarketActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertGeneralMarketActivity.this).getUserToken());
                return headers;
            }


        };
        queue.add(jsonObjReq);
    }


    public void datePickerFunction(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
                        if (compareSmallDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())){

                            Toast.makeText(InsertGeneralMarketActivity.this, "Date cannot Exceed Current Date", Toast.LENGTH_SHORT).show();
                        }else {

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
                            Toast.makeText(InsertGeneralMarketActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
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
