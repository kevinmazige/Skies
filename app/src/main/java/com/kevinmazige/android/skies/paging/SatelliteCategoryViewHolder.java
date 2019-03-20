package com.kevinmazige.android.skies.paging;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kevinmazige.android.skies.R;

/*
 * viewholder for satellite categories
 */
public class SatelliteCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mCategoryName;
    private int mCategoryInt;
    private SatelliteCategoryAdapter.ItemAction mItemAction;

    SatelliteCategoryViewHolder(View itemView) {
        super(itemView);
        mCategoryName = itemView.findViewById(R.id.category_textview);
        itemView.setOnClickListener(this);
    }

    public void setClickAction(SatelliteCategoryAdapter.ItemAction itemClickAction) {
        mItemAction = itemClickAction;
    }

    // store category name
    public void setCategoryName(String name) {
        mCategoryName.setText(name);
    }

    //store category id (the id is used in calls to the database and webApi
    public void setCategoryInt(int category) {
        mCategoryInt = category;
    }

    @Override
    public void onClick(View v) {
        if (mItemAction != null) {
            mItemAction.onClick(mCategoryName.getText().toString(), mCategoryInt);
        }
    }
}
