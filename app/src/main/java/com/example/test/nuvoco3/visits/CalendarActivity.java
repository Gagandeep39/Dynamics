package com.example.test.nuvoco3.visits;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.test.nuvoco3.helpers.CalendarHelper.convertClickedDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.convertSmallDateToJson;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getDate;
import static com.example.test.nuvoco3.helpers.CalendarHelper.getLongDate;
import static com.example.test.nuvoco3.helpers.Contract.BASE_URL;
import static com.example.test.nuvoco3.helpers.Contract.DISPLAY_JCP_VISIT_DETAILS;
import static com.example.test.nuvoco3.helpers.Contract.PROGRESS_DIALOG_DURATION;
import static com.example.test.nuvoco3.helpers.Contract.VISIT_STATUS_COMPLETED;
import static com.example.test.nuvoco3.helpers.Contract.VISIT_STATUS_PENDING;
import static com.example.test.nuvoco3.helpers.Contract.VISIT_STATUS_PLANNED;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "Calendar Activity";
    CompactCalendarView mCalendarView;
    TextView mTextViewMonth;
    ArrayList<String> mEvent;
    boolean isChecked = false;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM  yyyy", Locale.getDefault());
    RecyclerView mRecyclerView;
    JCPDetailsAdapter mJcpAdapter;
    ArrayList<JCPDetails> mJcpArrayList, mDayArrayList;
    RequestQueue queue;
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;
    int arrayListLength = 0;
    TextView mEmptyView;
    int day = 0;
    String mRecordId, mCustomerId, mCustomerName, mDate, mStartTime, mEndTime, mObjective, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy, mJcpId, mOrder, mProduct, mProductQuantity, mVisitStatus, mVisitRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initializeViews();
        initializeVariables();
        readData();
        calendarOperations();


    }


    private void calendarOperations() {
        List<Event> mEventList = new ArrayList<>();
        for (int i = 0; i < mJcpArrayList.size(); i++) {

            mEventList.add(createEvent(mJcpArrayList.get(i)));
        }
        if (day == 0) {
            day = 1;
            Log.i(TAG, "onCreate: " + "herfgjbhfdsdfg");
            for (int i = 0; i < mJcpArrayList.size(); i++) {
                if (mJcpArrayList.get(i).getDate().contains(convertSmallDateToJson(getDate()))) {
                    mDayArrayList.add(mJcpArrayList.get(i));
                }
                mJcpAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mJcpAdapter);
            }
        }
        mCalendarView.addEvents(mEventList);
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Toast.makeText(CalendarActivity.this, "" + dateClicked, Toast.LENGTH_SHORT).show();
                mDayArrayList.clear();
                String clickedDate = convertClickedDate(dateClicked);
                for (int i = 0; i < mJcpArrayList.size(); i++) {
                    if (mJcpArrayList.get(i).getDate().contains(clickedDate)) {
                        mDayArrayList.add(mJcpArrayList.get(i));
//                        checkAdapterIsEmpty();
                    }
                    mJcpAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mJcpAdapter);
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mTextViewMonth.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

    }


    private void initializeVariables() {
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        mJcpArrayList = new ArrayList<>();
        mDayArrayList = new ArrayList<>();
        mJcpAdapter = new JCPDetailsAdapter(mDayArrayList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mJcpAdapter);
//        setupRecyclerView();


    }


    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCalendarView = findViewById(R.id.compactcalendar_view);
        mTextViewMonth = findViewById(R.id.textViewMonth);
        mTextViewMonth.setText(dateFormatForMonth.format(mCalendarView.getFirstDayOfCurrentMonth()));
        mRecyclerView = findViewById(R.id.recyclerView);
        mCoordinatorLayout = findViewById(R.id.coordinator);


    }


    private Event createEvent(JCPDetails jcp) {
        int mColor;
        Event mEvent;
        long mDate = getLongDate(jcp.getDate());
        switch (jcp.getVisitStatus()) {
            case VISIT_STATUS_PLANNED:
                mColor = getResources().getColor(R.color.indicator_blue);
                break;
            case VISIT_STATUS_PENDING:
                mColor = getResources().getColor(R.color.indicator_yellow);
                break;
            case VISIT_STATUS_COMPLETED:
                mColor = getResources().getColor(R.color.indicator_green);
                break;
            default:
                mColor = getResources().getColor(R.color.indicator_purple);
        }

        mEvent = new Event(mColor, mDate);


        return mEvent;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
//        else if (item.getItemId() == R.id.checkable_menu) {
//            isChecked = !item.isChecked();
//            item.setChecked(isChecked);
//            mCustomerList.clear();
//            populateCustomers();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    private void readData() {
        startProgressDialog();

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
                    arrayListLength = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject object = jsonArray.getJSONObject(i);


                            if (object.getString("createdBy").equals(new UserInfoHelper(CalendarActivity.this).getUserId())) {
                        fetchData(object);
                            }


                    }

                    calendarOperations();

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CalendarActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                VolleyLog.d("lol", "Error with Internet : " + error.getMessage());
                // hide the progress dialog
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", new UserInfoHelper(CalendarActivity.this).getUserToken());
                return headers;
            }


        };

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void fetchData(JSONObject object) {
        try {

            mRecordId = object.getString("record_id") + "";
            mJcpId = object.getString("JCP_id");
            mDate = object.getString("Date") + "";
            mCustomerId = object.getString("ccustomer_id") + "";
            mCustomerName = object.getString("customer_name") + "";
            mObjective = object.getString("Objective") + "";
            mVisitRemark = object.getString("Visit_remark") + "";
            mVisitStatus = object.getString("Visit_status") + "";
            mCreatedBy = object.getString("createdBy") + "";
            mCreatedOn = object.getString("createdOn") + "";
            mOrder = object.getString("new_order") + "";
            mProductQuantity = object.getString("order_quantity") + "";
            mProduct = object.getString("product") + "";
            mUpdatedBy = object.getString("updatedby") + "";
            mUpdatedOn = object.getString("updatedOn") + "";
            mJcpArrayList.add(new JCPDetails(mRecordId, mJcpId, mDate, mCustomerId, mCustomerName, mObjective, mOrder, mProduct, mProductQuantity, mVisitRemark, mVisitStatus, mCreatedOn, mCreatedBy, mUpdatedOn, mUpdatedBy));
            mJcpAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startProgressDialog() {

        if (progressDialog != null) {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connection Time-out !", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
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
    }





}
