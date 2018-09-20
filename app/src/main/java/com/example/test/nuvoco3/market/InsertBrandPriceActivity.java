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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_BRANDS;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_CUSTOMER;
import static com.example.test.nuvoco3.helpers.Contract.INSERT_PRICES;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class InsertBrandPriceActivity extends AppCompatActivity {
    private static final String TAG = "BrandPrice Activity";
    String mRepresentative, mCounter, mDate, mBrand, mProduct, mWSP, mRSP, mStock, mRemarks, mCreatedBy, mCreatedOn, mUpdatedBy, mUpdatedOn;
    SearchableSpinner mSearchProduct, mSearchBrand, mSearchCustomers;
    TextInputEditText mEditTextRepresentative, mEditTextCounter, mEditTextWSP, mEditTextRSP, mEditTextStocks, mUserName;
    EditText mEditTextRemarks;
    RequestQueue queue;
    ArrayList<String> mBrandArrayList, mProductArrayList, mCustomerArrayList;
    CoordinatorLayout mCoordinaterLayout;
    ProgressDialog progressDialog;
    MasterHelper mBrandHelper;
    boolean isChecked = false;
    TextView mTextViewMessage;

    ArrayAdapter<String> mBrandAdapter;
    LinearLayout mDynamicRootLayout;

    //Date picker
    TextView mTextViewDate;
    //Displaying recently Added items
    RecyclerView mRecyclerView;
    BrandPriceDetailsAdapter mDetailsAdapter;
    ArrayList<BrandPrice> mBrandPricePriceList, mProducatArrayList;
    int mDay, mYear, mMonth;

    //Dynamic Layout
    ArrayList<DynamicPrice> mDynamicList;

    //Product Helper
    int count = 0;
    // Populates Spinner with Data and allows Selection of Items
    String tempProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_price);
        initializeViews();
        initializeVariables();
        populateCustomers();
        initializeBottomViews();
        populateSpinners();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initializeBottomViews() {
        mDetailsAdapter = new BrandPriceDetailsAdapter(this, mBrandPricePriceList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDetailsAdapter);
    }

    private void initializeVariables() {
        queue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        mBrandArrayList = new ArrayList<>();
        mCustomerArrayList = new ArrayList<>();
        mBrandPricePriceList = new ArrayList<>();

        mDynamicList = new ArrayList<>();
        mBrandHelper = new MasterHelper(this, "Brand");
        mBrandArrayList = mBrandHelper.getRecordName();

        mTextViewDate.setText(getDate());
        mDate = getDateTime();


    }

    //Stores data and variables and checks if Something is left empty or not
    private void validateDate() {
        mRepresentative = mEditTextRepresentative.getText().toString();
        mWSP = mEditTextWSP.getText().toString();
        mRSP = mEditTextRSP.getText().toString();
        mStock = mEditTextStocks.getText().toString();
        mRemarks = mEditTextRemarks.getText().toString();
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedOn = getDateTime();

        if (TextUtils.isEmpty(mRepresentative))
            Toast.makeText(this, "Enter Representative's ID", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mCounter, getString(R.string.default_name)))
            Toast.makeText(this, "Select Customer", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(mWSP))
            mEditTextWSP.setError("Enter WSP");
        if (TextUtils.isEmpty(mRSP))
            mEditTextRSP.setError("Enter RSP");
        if (TextUtils.isEmpty(mStock))
            mEditTextStocks.setError("Stocks cannot be Empty");
        if (TextUtils.isEmpty(mRemarks))
            mEditTextRemarks.setError("Remarks Field cannot be Empty");
        if (TextUtils.isEmpty(mDate))
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();


        if (TextUtils.equals(mProduct, "default"))
            Toast.makeText(this, "Select a Product", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mBrand, "default"))
            Toast.makeText(this, "Select a Brand", Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(mRepresentative) && !TextUtils.isEmpty(mCounter) && !TextUtils.isEmpty(mWSP) && !TextUtils.isEmpty(mRSP) && !TextUtils.isEmpty(mStock) && !TextUtils.isEmpty(mRemarks) && !TextUtils.equals(mProduct, "default") && !TextUtils.equals(mBrand, "default") && !TextUtils.isEmpty(mDate))
            sendDataToServer();

    }

    // All fields are Validated and now the data will be saved into the server
    private void sendDataToServer() {
        showProgressDialog();


        Map<String, String> postParam = new HashMap<>();

        postParam.put("2", mRepresentative);
        postParam.put("3", mCounter);
        postParam.put("4", mDate);
        postParam.put("5", mBrand);
        postParam.put("6", mWSP);
        postParam.put("7", mRSP);
        postParam.put("8", mStock);
        postParam.put("9", mRemarks);
        postParam.put("10", mCreatedOn);
        postParam.put("11", mCreatedBy);
        postParam.put("12", mUpdatedOn);
        postParam.put("13", mUpdatedBy);
        postParam.put("14", mProduct);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + INSERT_PRICES, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(InsertBrandPriceActivity.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                                mBrandPricePriceList.add(new BrandPrice("1", mRepresentative, mCounter, mDate, mBrand, mProduct, mWSP, mRSP, mStock, mRemarks, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy));
                                mDetailsAdapter.notifyDataSetChanged();


                            } else {
                                Toast.makeText(InsertBrandPriceActivity.this, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InsertBrandPriceActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error with Connection: " + error.getMessage() + "lol");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertBrandPriceActivity.this).getUserToken());
                return headers;
            }
        };
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    private void showProgressDialog() {
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
                            validateDate();
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, PROGRESS_DIALOG_DURATION);
    }

    private void populateSpinners() {

        ArrayAdapter<String> mProductAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mProductArrayList);
        mBrandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mBrandArrayList);
        final ArrayAdapter<String> mCustomerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCustomerArrayList);

        progressDialog.dismiss();
        mSearchProduct.setAdapter(mProductAdapter);
        mSearchBrand.setAdapter(mBrandAdapter);
        mSearchCustomers.setAdapter(mCustomerAdapter);
        mSearchBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBrand = mBrandArrayList.get(position);
                populateProducts();
                mTextViewMessage.setVisibility(View.GONE);
                mDynamicRootLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBrand = getString(R.string.default_name);

            }
        });
        mSearchProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProduct = mProductArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProduct = getString(R.string.default_name);

            }
        });
        mSearchCustomers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCounter = mCustomerArrayList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mCounter = getString(R.string.default_name);
            }
        });

    }

    //Initialize Widgets on the Screen
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mEditTextRepresentative = findViewById(R.id.textInputEditRepresentative);
        mEditTextWSP = findViewById(R.id.textInputEditWSP);
        mEditTextRSP = findViewById(R.id.textInputEditRSP);
        mEditTextStocks = findViewById(R.id.textInputEditStocks);
        mEditTextRemarks = findViewById(R.id.editTextRemark);
        mSearchBrand = findViewById(R.id.searchCategoryBrand);
        mCoordinaterLayout = findViewById(R.id.coordinator);
        mSearchProduct = findViewById(R.id.searchCategoryProduct);
        mUserName = findViewById(R.id.textInputEditName);
        mSearchCustomers = findViewById(R.id.searchCounter);
        mTextViewDate = findViewById(R.id.textViewDate);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDynamicRootLayout = findViewById(R.id.dynamicLayout);
        mTextViewMessage = findViewById(R.id.textViewMessage);

        mEditTextRepresentative.setText(new UserInfoHelper(this).getUserId());
        mEditTextRepresentative.setKeyListener(null);
        mUserName.setText(new UserInfoHelper(this).getUserName());
        mUserName.setKeyListener(null);
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
                        String dateText = "";
                        if (compareSmallDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, getDate())) {

                            Toast.makeText(InsertBrandPriceActivity.this, "Date cannot Exceed Current Date", Toast.LENGTH_SHORT).show();
                        } else {
                            if (monthOfYear + 1 >= 10)
                                dateText = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            else
                                dateText = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;

                            mTextViewDate.setText(dateText);
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date date = new Date();
                            mTextViewDate.setText(dateText);

                            mDate = dateText + " " + dateFormat.format(date);

                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }


    public void populateCustomers() {
        showProgressDialog();


        Map<String, String> postParam = new HashMap<>();


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
                            if (object.getString("createdBy").equals(new UserInfoHelper(InsertBrandPriceActivity.this).getUserId())) {
                                mCustomerArrayList.add(object.getString("name"));

                            }


                        } else {
                            mCustomerArrayList.add(object.getString("name"));

                        }
                    }


                } catch (JSONException e1) {
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
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertBrandPriceActivity.this).getUserToken());
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
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            isChecked = !item.isChecked();
            item.setChecked(isChecked);
            mCustomerArrayList.clear();
            populateCustomers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void addNewDataSet() {
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(8, 8, 8, 8);
        CardView cardView = new CardView(this);
        cardView.setCardElevation(8);
        cardView.setRadius(8);
        int pad = getResources().getDimensionPixelOffset(R.dimen.padding_standard);
        cardView.setLayoutParams(cardParams);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(pad, pad, pad, pad);
        cardView.addView(rootLayout);
        TextView textView = new TextView(this);
        textView.setPadding(pad, pad, pad, pad);
        textView.setTextSize(12);
        textView.setText(mBrand);
        rootLayout.addView(textView);

        for (int i = 0; i < mProductArrayList.size(); i++) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
            );
            LinearLayout mChildLayout = new LinearLayout(this);
            mChildLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.addView(mChildLayout);
            TextView mDynamicProduct = new TextView(this);
            mDynamicProduct.setText(mProductArrayList.get(i));

            TextInputLayout mTextLayoutWSP = new TextInputLayout(this);
            TextInputLayout mTextLayoutRSP = new TextInputLayout(this);
            TextInputLayout mTextLayoutStock = new TextInputLayout(this);

            EditText mDynamicEditTextRSP = new EditText(this);
            EditText mDynamicEditTextWSP = new EditText(this);
            EditText mDynamicEditTextStock = new EditText(this);

            mTextLayoutRSP.addView(mDynamicEditTextRSP);
            mTextLayoutWSP.addView(mDynamicEditTextWSP);
            mTextLayoutStock.addView(mDynamicEditTextStock);

            mDynamicEditTextRSP.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            mDynamicEditTextWSP.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            mDynamicEditTextStock.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            mTextLayoutRSP.setHint("RSP");
            mTextLayoutWSP.setHint("WSP");
            mTextLayoutStock.setHint("Stocks");

            mDynamicProduct.setLayoutParams(param);
            mTextLayoutRSP.setLayoutParams(param);
            mTextLayoutWSP.setLayoutParams(param);
            mTextLayoutStock.setLayoutParams(param);
            mDynamicProduct.setGravity(Gravity.CENTER_VERTICAL);

            mChildLayout.addView(mDynamicProduct);
            mChildLayout.addView(mTextLayoutRSP);
            mChildLayout.addView(mTextLayoutWSP);
            mChildLayout.addView(mTextLayoutStock);

            mDynamicList.add(new DynamicPrice(mDynamicProduct, mDynamicEditTextRSP, mDynamicEditTextWSP, mDynamicEditTextStock));
        }
        final Button button = new Button(this);
        button.setText("Save Entries");
        button.setBackgroundResource(R.drawable.my_button_bg);
        button.setTextColor(getResources().getColor(R.color.colorAccent));
        button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                for (int i = 0; i < mDynamicList.size(); i++) {
                    if (!TextUtils.isEmpty(mDynamicList.get(i).getEditTextStock().getText().toString()) && !TextUtils.isEmpty(mDynamicList.get(i).getEditTextRSP().getText().toString()) && !TextUtils.isEmpty(mDynamicList.get(i).getEditTextWSP().getText().toString()) || (TextUtils.isEmpty(mDynamicList.get(i).getEditTextRSP().getText().toString()) && TextUtils.isEmpty(mDynamicList.get(i).getEditTextWSP().getText().toString()))) {
                        count++;
                    } else {
                        count = -1;
                        break;
                    }
                }
                if (count == -1) {
                    count = 0;
                    Toast.makeText(InsertBrandPriceActivity.this, "Fill all details of a particular product", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < mDynamicList.size(); i++) {
                        if (!TextUtils.isEmpty(mDynamicList.get(i).getEditTextRSP().getText().toString())) {
                            mProduct = mDynamicList.get(i).getTextView().getText().toString();
                            mWSP = mDynamicList.get(i).getEditTextWSP().getText().toString();
                            mRSP = mDynamicList.get(i).getEditTextRSP().getText().toString();
                            mStock = mDynamicList.get(i).getEditTextStock().getText().toString();
                            mRemarks = mEditTextRemarks.getText().toString();
                            mCreatedBy = new UserInfoHelper(InsertBrandPriceActivity.this).getUserId();
                            mCreatedOn = getDateTime();
                            mUpdatedBy = new UserInfoHelper(InsertBrandPriceActivity.this).getUserId();
                            mUpdatedOn = getDateTime();
                            mRepresentative = mEditTextRepresentative.getText().toString();


                            if (TextUtils.isEmpty(mRemarks))
                                mEditTextRemarks.setError("Enter Remarks");
                            else {

                                mDynamicList.get(i).getEditTextWSP().setKeyListener(null);
                                mDynamicList.get(i).getEditTextRSP().setKeyListener(null);
                                mDynamicList.get(i).getEditTextStock().setKeyListener(null);
                                sendDataToServer();
//                                validateDate();

                            }
                        }
                    }
                    if (!TextUtils.isEmpty(mRemarks))
                        button.setVisibility(View.GONE);
//                    addNewDataSet();
                }
            }
        });


        rootLayout.addView(button);
        mDynamicRootLayout.addView(cardView);

    }


    public void populateProducts() {
        showProgressDialog();
        mProductArrayList = new ArrayList<>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + DISPLAY_BRANDS + "/" + mBrand, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("lol", "onResponse:  " + response);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        tempProduct = object.getString("product");
                        mProductArrayList.add(tempProduct);
//                        Log.i(TAG, "onResponse: " + mRecordName);

//                        }
                    }
                    addNewDataSet();

                } catch (JSONException e1) {
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
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(InsertBrandPriceActivity.this).getUserToken());
                return headers;
            }


        };

        // Adding request to request queue
        queue.add(jsonObjReq);

//            progressDialog.dismiss();
        Log.d(TAG, "getRecordName: i" + "deftg;");
    }


}
