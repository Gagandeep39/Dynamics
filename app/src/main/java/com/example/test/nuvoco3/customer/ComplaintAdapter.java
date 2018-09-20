package com.example.test.nuvoco3.customer;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.nuvoco3.R;

import java.util.List;

/**
 * Created by gagandeep on 27/12/17.
 */

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {
    private Context mContext;
    private List<Complaints> mComplaintList;

    public ComplaintAdapter(Context mContext, List<Complaints> mComplaintList) {
        this.mContext = mContext;
        this.mComplaintList = mComplaintList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Complaints mCurrentComplaint = mComplaintList.get(position);
        holder.setIsRecyclable(false);

        holder.mImageIcon.setBackgroundResource(chooseImage(mCurrentComplaint.getType()));
        holder.mTextViewDate.append(mCurrentComplaint.getmDate());
        holder.mTextViewType.append(mCurrentComplaint.getType());
        holder.mTextViewComplaintId.append(mCurrentComplaint.getComplaintId());
        holder.mTextViewCustomerName.append(mCurrentComplaint.getCustomerName());
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrNot(holder);
            }
        });
        holder.mImageExpandMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrNot(holder);
            }
        });

        holder.mImageExpandLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrNot(holder);
            }
        });

        holder.mTextViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, UpdateStatusActivity.class).putExtra("ComplaintId", mCurrentComplaint.getComplaintId()));
            }
        });

    }

    private void expandOrNot(ViewHolder holder) {
        if (holder.mLinearLayout.getVisibility() == View.GONE) {
            holder.mLinearLayout.setVisibility(View.VISIBLE);
            holder.mImageExpandLess.setVisibility(View.VISIBLE);
            holder.mImageExpandMore.setVisibility(View.GONE);
        } else {
            holder.mLinearLayout.setVisibility(View.GONE);
            holder.mImageExpandLess.setVisibility(View.GONE);
            holder.mImageExpandMore.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {

        return mComplaintList.size();
    }

    private int chooseImage(String mCategory) {
        switch (mCategory) {
            case "Logistics":
                return R.drawable.ic_logistics;
            case "Sales":
                return R.drawable.ic_sales;
            case "Collection":
                return R.drawable.ic_collection;
            case "Finance":
                return R.drawable.ic_finance;
        }
        return R.drawable.ic_technical;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewType, mTextViewComplaintId, mTextViewCustomerName, mTextViewDate, mTextViewDetails;
        ConstraintLayout mConstraintLayout;
        LinearLayout mLinearLayout;
        ImageView mImageExpandLess, mImageExpandMore;
        ImageView mImageIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            mTextViewComplaintId = itemView.findViewById(R.id.textViewComplaintId);
            mTextViewType = itemView.findViewById(R.id.textViewType);
            mTextViewDate = itemView.findViewById(R.id.textViewDate);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayout);
            mTextViewDetails = itemView.findViewById(R.id.textViewDetails);
            mLinearLayout = itemView.findViewById(R.id.linearLayout2);
            mImageExpandLess = itemView.findViewById(R.id.imageViewExpandLess);
            mImageExpandMore = itemView.findViewById(R.id.imageViewExpand);
            mImageIcon = itemView.findViewById(R.id.imageViewIcon);
        }
    }
}
