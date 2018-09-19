package com.example.test.nuvoco3;

import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.nuvoco3.helpers.UserInfoHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.CalendarHelper.getDateTime;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.MASTER_VISIT_STATUS;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;

public class RecordManagementActivity extends AppCompatActivity {
    public static final String URL_INSERT_PRODUCT = "/insertMaster";
    private static final String TAG = "RecordManagement";
    TextInputEditText mEditTextCategory, mEditTextName, mEditTextStatus;
    FloatingActionButton fab;
    String mCategory, mType, mStatus, mName, mUpdatedOn, mCreatedOn, mCreatedBy, mUpdatedBy;
    RequestQueue queue;
    SearchableSpinner mTypeSearch, mStatusSearch;
    String mTypeArray[] = {"District", "Area", "State", "CustomerType", "Category", "ComplaintType", "Brand", "Product", "Department", MASTER_VISIT_STATUS, "Objective", "ComplaintStatus"};
    String mStatusArray[] = {"True", "False"};
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_management);
        initializeViews();
        populateSpinner();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void populateSpinner() {
        ArrayAdapter mTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mTypeArray);
        mTypeSearch.setAdapter(mTypeAdapter);
        mTypeSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mType = mTypeArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mType = getString(R.string.default_name);

            }
        });

    }


    private void validateData() {
        mCategory = mEditTextCategory.getText().toString();
        mName = mEditTextName.getText().toString();
        mStatus = "1";
        mCreatedBy = new UserInfoHelper(this).getUserId();
        mUpdatedBy = new UserInfoHelper(this).getUserId();
        mCreatedOn = getDateTime();
        mUpdatedOn = getDateTime();


        if (TextUtils.isEmpty(mCategory))
            mEditTextCategory.setError("Category field Cannot Be Empty");
        if (TextUtils.isEmpty(mName))
            mEditTextName.setError("name field Cannot Be Empty");
        if (TextUtils.equals(mType, getString(R.string.default_name)))
            Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
        if (TextUtils.equals(mStatus, getString(R.string.default_name)))
            Toast.makeText(this, "Select Status", Toast.LENGTH_SHORT).show();

        if (!TextUtils.isEmpty(mCategory) && !TextUtils.isEmpty(mName) && !TextUtils.equals(mType, getString(R.string.default_name)) && !TextUtils.equals(mStatus, getString(R.string.default_name))) {
            sendDataToServer();
        }
    }

    private void sendDataToServer() {
        showProgressDialogue();
        Map<String, String> postParam = new HashMap<>();

        postParam.put("1", "101");
        postParam.put("2", mType);
        postParam.put("3", mCategory);
        postParam.put("4", mName);
        postParam.put("5", mStatus);
        postParam.put("6", mCreatedOn);
        postParam.put("7", mCreatedBy);
        postParam.put("8", mUpdatedOn);
        postParam.put("9", mUpdatedBy);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL + URL_INSERT_PRODUCT, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.i(TAG, response.toString());
                        try {
                            if (response.getString("status").equals("updated")) {
                                Toast.makeText(RecordManagementActivity.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(RecordManagementActivity.this, "" + response, Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        // Adding request to request queue
        queue.add(jsonObjReq);


    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        mEditTextCategory = findViewById(R.id.textInputEditCategory);
        mEditTextName = findViewById(R.id.textInputEditName);
        mTypeSearch = findViewById(R.id.searchType);
        mStatusSearch = findViewById(R.id.searchStatus);
        queue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
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
                            Toast.makeText(RecordManagementActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
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
