package com.example.test.nuvoco3.market;

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
 * Created by gagandeep on 26/12/17.
 */

public class BrandPriceAdapter extends RecyclerView.Adapter<BrandPriceAdapter.ViewHolder> {
    private Context mContext;
    private List<BrandPrice> mBrandPrice;

    public BrandPriceAdapter(Context mContext, List<BrandPrice> mBrandPrice) {
        this.mContext = mContext;
        this.mBrandPrice = mBrandPrice;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_price_info_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BrandPrice mCurrentBrandPrice = mBrandPrice.get(position);
        holder.setIsRecyclable(false);


        holder.mImageViewIcon.setBackgroundResource(chooseImage(mCurrentBrandPrice.getProduct()));
        holder.mBrand.append(mCurrentBrandPrice.getBrand());
        holder.mCounter.append(mCurrentBrandPrice.getCounter());
        holder.mStock.append(mCurrentBrandPrice.getStock());
        holder.mProduct.append(mCurrentBrandPrice.getProduct());
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandOrReduce(holder);
            }
        });
        holder.mImageExpandLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandOrReduce(holder);
            }
        });
        holder.mImageExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandOrReduce(holder);
            }
        });
        holder.mTextViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ViewBrandPriceDetailsActivity.class).putExtra("CustomerName", mCurrentBrandPrice.getCounter()));
            }
        });
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

    @Override
    public int getItemCount() {
        return mBrandPrice.size();
    }

    private int chooseImage(String mCategory) {
        switch (mCategory) {
            case "PPC":
                return R.drawable.ic_ppc;
            case "OPC":
                return R.drawable.ic_opc;
            case "INFRACEM":
                return R.drawable.ic_infracem;
            case "PSC":
                return R.drawable.ic_psc;
        }
        return R.drawable.ic_other_factory;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCounter, mBrand, mProduct, mStock;
        LinearLayout mLinearLayout;
        ImageView mImageExpand, mImageExpandLess, mImageViewIcon;
        TextView mTextViewDetails;
        ConstraintLayout mConstraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mCounter = itemView.findViewById(R.id.textViewCounter);
            mBrand = itemView.findViewById(R.id.textViewBrand);
            mStock = itemView.findViewById(R.id.textViewStock);
            mProduct = itemView.findViewById(R.id.textViewProduct);
            mImageExpand = itemView.findViewById(R.id.imageViewExpand);
            mImageExpandLess = itemView.findViewById(R.id.imageViewExpandLess);
            mTextViewDetails = itemView.findViewById(R.id.textViewDetails);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayout);
            mLinearLayout = itemView.findViewById(R.id.linearLayoutExpanded);
            mImageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        }
    }


}
