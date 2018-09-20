package com.example.test.nuvoco3.lead;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.customer.InsertComplaintActivity;
import com.example.test.nuvoco3.market.InsertBrandPriceActivity;
import com.example.test.nuvoco3.market.InsertCampaignActivity;

import static com.example.test.nuvoco3.lead.ViewCustomerActivity.mCustomerArrayList;

public class CustomerDetailsActivity extends AppCompatActivity {
    TextView mName, mType, mCategory, mStatus, mPhone, mEmail, mArea, mDistrict, mState, mAddress, mCreatedBy, mCreatedOn, mUpdatedBy, mUpdatedOn;
    LinearLayout mNewContact, mNewComplaint, mNewCampaign, mNewPrice;
    int position;
    ImageView mImageIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        initializeViews();
        position = getIntent().getIntExtra("position", -1);
        Log.i("ll", "onCreate: " + position);
        if (position != -1)
            displayData(position);
        else Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
        mNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailsActivity.this, InsertCustomerContactActivity.class);
                intent.putExtra("customerId", mCustomerArrayList.get(position).getCustomerId());
                intent.putExtra("customerName", mCustomerArrayList.get(position).getCustomerName());
                startActivity(intent);
            }
        });


        mNewComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerDetailsActivity.this, InsertComplaintActivity.class).putExtra("CustomerName", mCustomerArrayList.get(position).getCustomerName()).putExtra("CustomerId", mCustomerArrayList.get(position).getCustomerId()));
            }
        });

        mNewCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerDetailsActivity.this, InsertCampaignActivity.class).putExtra("CustomerDetails", "true").putExtra("CustomerName", mCustomerArrayList.get(position).getCustomerName()));
            }
        });

        mNewPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerDetailsActivity.this, InsertBrandPriceActivity.class));
            }
        });


    }

    //  Displays details of a particular adapter from the ID received from the recycler view
    private void displayData(int position) {

        mName.append(" " + mCustomerArrayList.get(position).getCustomerName());
        mType.append(" " + mCustomerArrayList.get(position).getCustomerId());
        mCategory.append(" " + mCustomerArrayList.get(position).getCustomerCategory());
        mStatus.append(" " + mCustomerArrayList.get(position).getCustomerStatus());
        mPhone.append(" " + mCustomerArrayList.get(position).getCustomerPhoneno());
        mEmail.append(" " + mCustomerArrayList.get(position).getCustomerEmailId());
        mArea.append(" " + mCustomerArrayList.get(position).getCustomerArea());
        mDistrict.append(" " + mCustomerArrayList.get(position).getCustomerDistrict());
        mState.append(" " + mCustomerArrayList.get(position).getCustomerState());
        mAddress.append(" " + mCustomerArrayList.get(position).getCustomerAddress());
        mCreatedBy.append(" " + mCustomerArrayList.get(position).getCustomerCreatedBy());
        mCreatedOn.append(" " + mCustomerArrayList.get(position).getCustomerCreatedOn());
        mUpdatedBy.append(" " + mCustomerArrayList.get(position).getCustomerUpdatedBy());
        mUpdatedOn.append(" " + mCustomerArrayList.get(position).getCustomerUpdatedOn());
        mImageIcon.setBackgroundResource(chooseImage(mCustomerArrayList.get(position).getCustomerCategory()));



    }

    //  Initialize Views
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Customer Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mName = findViewById(R.id.textViewName);
        mType = findViewById(R.id.textViewType);
        mCategory = findViewById(R.id.textViewCategory);
        mStatus = findViewById(R.id.textViewStatus);
        mPhone = findViewById(R.id.textViewPhone);
        mEmail = findViewById(R.id.textViewEmail);
        mArea = findViewById(R.id.textViewArea);
        mDistrict = findViewById(R.id.textViewDistrict);
        mState = findViewById(R.id.textViewState);
        mAddress = findViewById(R.id.textViewAddress);
        mCreatedBy = findViewById(R.id.textViewCreatedBy);
        mCreatedOn = findViewById(R.id.textViewCreatedOn);
        mUpdatedBy = findViewById(R.id.textViewUpdatedBy);
        mUpdatedOn = findViewById(R.id.textViewUpdatedOn);
        mNewContact = findViewById(R.id.textViewContact);
        mNewComplaint = findViewById(R.id.textViewComplaint);
        mNewCampaign = findViewById(R.id.textViewCampaign);
        mNewPrice = findViewById(R.id.textViewPrice);
        mImageIcon = findViewById(R.id.imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int chooseImage(String mCategory) {
        switch (mCategory) {
            case "Dealer":
                return R.drawable.ic_dealer;
            case "Subdealer":
                return R.drawable.ic_sub_dealer;
            case "Individual":
                return R.drawable.ic_individual;
        }
        return R.drawable.ic_individual;
    }
}
