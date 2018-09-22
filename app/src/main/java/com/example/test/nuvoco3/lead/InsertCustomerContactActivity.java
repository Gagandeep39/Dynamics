package com.example.test.nuvoco3.lead;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.example.test.nuvoco3.helpers.UserInfoHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.CalendarHelper.compareSmallDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDateTime;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_CUSTOMER;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_CONTACT;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;
import static com.example.test.nuvoco3.helpers.UserInfoHelper.isEmailValid;

public class InsertCustomerContactActivity extends AppCompatActivity {
    private static final String TAG = "InsertContacts";
    TextInputEditText editTextContactName, editTextContactPhone, editTextContactEmail, editTextCustomerName, editTextCustomerId;
    TextView textViewDOB, textViewDOA;
    int mYear, mMonth, mDay;
    Bundle bundle;      //Data from another activity
    String mContactName, mCustomerName, mCreatedBy, mCreatedOn, mUpdatedBy, mUpdatedOn, mContactEmail, mContactDOB, mContactDOA, mContactPhone, mCustomerId;  //Customer=>Brand, CustomerContact=>Human
    RequestQueue queue;
    SearchableSpinner mSearchCustomer;
    LinearLayout mLinearLayout;
    ArrayList<String> mCustomerList, mIdList;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;
    FloatingActionButton fab;
    ArrayAdapter<String> mCustomerAdapter;
    boolean isChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_customer_contact);
        getReferences();
        queue = Volley.newRequestQueue(this);
        populateCustomers();
        populatingSpinner();
        populatingDataFields();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });
    }

    private void populatingSpinner() {
        mCustomerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCustomerList);

        mSearchCustomer.setAdapter(mCustomerAdapter);
        mCustomerAdapter.notifyDataSetChanged();
        mSearchCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCustomerName = mCustomerList.get(position);
                mCustomerId = mIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCustomerName = getString(R.string.default_name);
            }
        });
    }

    // Populates the Customer ID and Customer Name when Redirected from New Customer Section
    private void populatingDataFields() {
        bundle = getIntent().getExtras();
        if (getIntent().getStringExtra("customerName") != null) {
            mCustomerId = bundle.getString("customerId");
            mCustomerName = bundle.getString("customerName");

            editTextCustomerName.setText(mCustomerName);
            editTextCustomerName.setKeyListener(null);
            editTextCustomerName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);

        }

        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();
    }

    private void insertDataInFields() {

        mContactName = editTextContactName.getText().toString();
        mContactPhone = editTextContactPhone.getText().toString();
        mContactEmail = editTextContactEmail.getText().toString();
        mContactDOA = "2001-01-01";
//        mContactDOB = textViewDOB.getText().toString();
//        mContactDOA = textViewDOA.getText().toString();
        if (TextUtils.isEmpty(mContactName)) {
            editTextContactName.setError("Enter Contact's Name");
        }
        if (TextUtils.isEmpty(mContactPhone)) {
            editTextContactPhone.setError("Enter Contact's Phone Number");
        } else if(mContactPhone.length()<10)
           editTextContactPhone.setError("Phone number must of 10 digit only");
        if (TextUtils.isEmpty(mContactEmail)) {
            editTextContactEmail.setError("Enter Contact's Email ID");
        } else {
            if (!isEmailValid(mContactEmail))
                editTextContactEmail.setError("Enter a Valid Email ID");
        }
        if (TextUtils.isEmpty(mContactDOB)) {
            textViewDOB.setError("Enter Contact's Date of Birth");
        }
        if (TextUtils.isEmpty(mContactDOA)) {
            textViewDOA.setError("Enter Contact's Date of Anniversary");
        }

        if (TextUtils.equals(mCustomerName, getString(R.string.default_name)))
            Toast.makeText(this, "Select Customer", Toast.LENGTH_SHORT).show();

        if (mContactPhone.length()==10&&!TextUtils.isEmpty(mContactName) && !TextUtils.isEmpty(mContactPhone) && !TextUtils.isEmpty(mContactEmail) && !TextUtils.isEmpty(mContactDOB) && !TextUtils.isEmpty(mContactDOA) && !TextUtils.isEmpty(mCustomerId) && !TextUtils.isEmpty(mCustomerName) && isEmailValid(mContactEmail))
            storeData();
    }

    // initialize Views
    private void getReferences() {
        editTextContactName = findViewById(R.id.textInputEditContactName);
        editTextCustomerName = findViewById(R.id.textInputEditCustomerName);
        editTextContactPhone = findViewById(R.id.textInputEditContactPhone);
        editTextContactEmail = findViewById(R.id.textInputEditContactEmail);
        fab = findViewById(R.id.fabAddData);
        textViewDOA = findViewById(R.id.textViewSelectDOA);
        textViewDOB = findViewById(R.id.textViewSelectDOB);
        mSearchCustomer = findViewById(R.id.searchCustomer);
        mLinearLayout = findViewById(R.id.linearLayout);
        mCoordinaterLayout = findViewById(R.id.coordinator);

        //Set Up Toolbar and Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(this);

        if (getIntent().getStringExtra("needSearch") != null && getIntent().getStringExtra("needSearch").equals("Need")) {
            editTextCustomerName.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    // Allows to select data when clicked on datapicker textView
    public void datePickerFunction(View v) {
        final View buttonClicked = v;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(buttonClicked.getWindowToken(), 0);

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (compareSmallDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())){

                            Toast.makeText(InsertCustomerContactActivity.this, "Date cannot Exceed Current Date", Toast.LENGTH_SHORT).show();
                        }else {
                            if (buttonClicked.getId() == R.id.textViewSelectDOB || buttonClicked.getId() == R.id.imageViewCalendar1) {
                                mContactDOB = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                textViewDOB.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            } else if (buttonClicked.getId() == R.id.textViewSelectDOA || buttonClicked.getId() == R.id.imageViewCalendar2) {
                                mContactDOA = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                textViewDOA.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    // Stores data in strings and calls storeData() to store dta in server
    public void insertData() {
        insertDataInFields();
    }

    // Used to get current date and time


    // Stores data in server
    private void storeData() {
        showProgress();


        final Map<String, String> postParam = new HashMap<>();

//        Toast.makeText(this, "" + mContactDOB, Toast.LENGTH_SHORT).show();
        postParam.put("2", mCustomerName);
        postParam.put("3", mContactName);
        postParam.put("4", mContactPhone);
        postParam.put("5", mContactEmail);
        postParam.put("6", mContactDOB);
        postParam.put("7", mContactDOA);
        postParam.put("8", mCreatedOn);
        postParam.put("9", mCreatedBy);
        postParam.put("10", mUpdatedOn);
        postParam.put("11", mUpdatedBy);
        postParam.put("12", mCustomerId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_CONTACT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(InsertCustomerContactActivity.this, "Successfully Inserted New Contact", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(InsertCustomerContactActivity.this, "" + response, Toast.LENGTH_SHORT).show();
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
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertCustomerContactActivity.this).getUserToken());
                return headers;
            }


        };
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    private void showProgress() {
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
                            storeData();
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);
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
                    Snackbar snackbar = Snackbar.make(mCoordinaterLayout, "Connection Time-out! Reload Customers !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            populateCustomers();
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);


        mCustomerList = new ArrayList<String>();
        mIdList = new ArrayList<>();
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
                            if (object.getString("createdBy").equals(new UserInfoHelper(InsertCustomerContactActivity.this).getUserId())) {
                                mIdList.add(object.getString("record_id"));   //primary key
                                mCustomerList.add(object.getString("name"));
                                populatingSpinner();

                            }


                        } else {
                            mIdList.add(object.getString("record_id"));   //primary key
                            mCustomerList.add(object.getString("name"));
                            populatingSpinner();

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
                headers.put("x-access-token", new UserInfoHelper(InsertCustomerContactActivity.this).getUserToken());
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



}
