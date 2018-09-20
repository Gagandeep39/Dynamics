package com.example.test.nuvoco3.lead;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.nuvoco3.R;

import java.util.List;

import static com.example.test.nuvoco3.lead.ViewCustomerActivity.mCustomerArrayList;

/**
 * Created by gagandeep on 23/12/17.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    private static final String TAG = "Customer Adapter";
    private Context mContext;
    private List<Customer> mCustomer;

    public CustomerAdapter(Context mContext, List<Customer> mCustomer) {
        this.mContext = mContext;
        this.mCustomer = mCustomer;
    }

    //Creates view Holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list, parent, false);
        return new ViewHolder(v);
    }

    //Use to Populate individual Items
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Customer currentCustomer = mCustomer.get(position);
        int id = Integer.parseInt(currentCustomer.getCustomerId());
        holder.setIsRecyclable(false);

        String mCategory = currentCustomer.getCustomerCategory();
        holder.mImageIcon.setBackgroundResource(chooseImage(mCategory));
        holder.mCustomerName.append(currentCustomer.getCustomerName());
        holder.mCustomerArea.append(currentCustomer.getCustomerArea());
        holder.mCustomerType.append(currentCustomer.getCustomerId());
        holder.mCustomerCategory.append(currentCustomer.getCustomerCategory());
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrReduce(holder);

            }
        });
        holder.mImageExpandLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrReduce(holder);

            }
        });
        holder.mImageExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandOrReduce(holder);
            }
        });
        holder.mImageCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(new Intent(mContext, CustomerDetailsActivity.class).putExtra("position", position));
            }
        });
        holder.mImageContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ViewCustomerContactActivity.class).putExtra("customerID", mCustomerArrayList.get(position).getCustomerId()));
            }
        });
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

    private void expandOrReduce(ViewHolder holder) {

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

    //Expands or Compress the Items in recycler view

    //Gives the Size of to Create a Recycler view
    @Override
    public int getItemCount() {
        return mCustomer.size();
    }

    //Initialize Views in ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCustomerName, mCustomerType, mCustomerArea, mCustomerCategory;
        TextView mTextViewCustomer, mTextViewContacts;
        CardView mCardView;
        ConstraintLayout mConstraintLayout;
        LinearLayout mLinearLayout;
        ImageView mImageExpand, mImageExpandLess;
        TextView mImageCustomer, mImageContacts;
        ImageView mImageIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            mCustomerType = itemView.findViewById(R.id.textViewCustomerType);
            mCustomerCategory = itemView.findViewById(R.id.textViewCustomerCategory);
            mCustomerArea = itemView.findViewById(R.id.textViewCustomerArea);
            mCardView = itemView.findViewById(R.id.cardView);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayout);
            mImageExpand = itemView.findViewById(R.id.imageViewExpand);
            mImageExpandLess = itemView.findViewById(R.id.imageViewExpandLess);
            mLinearLayout = itemView.findViewById(R.id.gridLayoutExpanded);
            mImageCustomer = itemView.findViewById(R.id.textViewCustomer);
            mImageContacts = itemView.findViewById(R.id.textViewContact);
            mImageIcon = itemView.findViewById(R.id.imageViewIcon);
        }
    }


}
