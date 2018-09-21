package com.example.test.nuvoco3.lead;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.nuvoco3.R;

import java.util.List;

import static com.example.test.nuvoco3.helpers.CalendarHelper.convertJsonDateToSmall;

/**
 * Created by gagandeep on 24/12/17.
 */

public class CustomerContactAdapter extends RecyclerView.Adapter<CustomerContactAdapter.ViewHolder> {
    private static final String TAG = "CustomerContact Adapter Activity";
    private Context mContext;
    private List<CustomerContact> mCustomerContactList;

    public CustomerContactAdapter(Context mContext, List<CustomerContact> mCustomerContactList) {
        this.mContext = mContext;
        this.mCustomerContactList = mCustomerContactList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_contact_list, parent, false);
        return new ViewHolder(v);
    }

    //Holds the item in a Single View
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CustomerContact mCurrentContact = mCustomerContactList.get(position);
        holder.setIsRecyclable(false);


        holder.mImageViewIcon.setBackgroundResource(chooseImage(mCurrentContact.getCustomerName()));
        holder.mTextViewCustomerId.append(mCurrentContact.getCustomerId());
        holder.mTextViewCustomerName.append(mCurrentContact.getCustomerName());
        holder.mTextViewName.append(mCurrentContact.getCustomerContactName());
        holder.mTextViewPhone.append(mCurrentContact.getCustomerContactPhone());
        holder.mTextViewEmail.append(mCurrentContact.getCustomerContactEmail());
        holder.mTextViewDOB.append(convertJsonDateToSmall(mCurrentContact.getCustomerContactDOB()));
        holder.mTextViewDOA.append(convertJsonDateToSmall(mCurrentContact.getCustomerContactDOA()));
    }

    private void expandOrReduce(CustomerAdapter.ViewHolder holder) {

        if (holder.mLinearLayout.getVisibility() == View.GONE && holder.mImageExpand.getVisibility() == View.VISIBLE && holder.mImageExpandLess.getVisibility() == View.GONE) {

            holder.mLinearLayout.setVisibility(View.VISIBLE);
            holder.mImageExpand.setVisibility(View.GONE);
            holder.mImageExpandLess.setVisibility(View.VISIBLE);
        } else if (holder.mLinearLayout.getVisibility() == View.VISIBLE && holder.mImageExpand.getVisibility() == View.GONE && holder.mImageExpandLess.getVisibility() == View.VISIBLE) {

            holder.mLinearLayout.setVisibility(View.GONE);
            holder.mImageExpand.setVisibility(View.VISIBLE);
            holder.mImageExpandLess.setVisibility(View.GONE);
        }
    }

    //  Provides count of Items in The View
    @Override
    public int getItemCount() {
        return mCustomerContactList.size();
    }

    private int chooseImage(String mCategory) {
        switch (mCategory) {
            case "Dealer":
                return R.drawable.ic_dealer;
            case "Sub-dealer":
                return R.drawable.ic_sub_dealer;
            case "Individual":
                return R.drawable.ic_individual;
        }
        return R.drawable.ic_individual;
    }

    //  Initialize Items in a ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewName, mTextViewPhone, mTextViewEmail, mTextViewCustomerId, mTextViewCustomerName, mTextViewDOB, mTextViewDOA;
        ConstraintLayout mConstraintLayout;
        ImageView mImageViewIcon;

        //  Initialize Widgets in a single Item View
        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewCustomerId = itemView.findViewById(R.id.textViewCustomerId);
            mTextViewName = itemView.findViewById(R.id.textViewContactName);
            mTextViewPhone = itemView.findViewById(R.id.textViewContactPhone);
            mTextViewEmail = itemView.findViewById(R.id.textViewContactEmail);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayout);
            mTextViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            mTextViewDOB = itemView.findViewById(R.id.textViewDOB);
            mTextViewDOA = itemView.findViewById(R.id.textViewDOA);
            mImageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        }
    }
}
