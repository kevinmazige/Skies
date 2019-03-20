package com.kevinmazige.android.skies.paging;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.Satellite;

/*
 * view holder for satellites. This is used to display satellites in add to favourites recyclerview
 */
public class SatelliteDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mSatelliteName;
    private int mSatelliteId;
    private Satellite mSatellite;
    private SatelliteDataAdapter.ItemAction mItemAction;

    SatelliteDataViewHolder(View itemView) {
        super(itemView);
        mSatelliteName = itemView.findViewById(R.id.category_add_to_favourites_satellite_textview);
        itemView.setOnClickListener(this);
    }

    public void setClickAction(SatelliteDataAdapter.ItemAction itemClickAction) {
        mItemAction = itemClickAction;
    }

    public TextView getTextView() {
        return mSatelliteName;
    }


    public void setSatellite(Satellite s) {
        mSatellite = s;
        mSatelliteName.setText(s.getName());
        mSatelliteId = s.getNoradId();
    }

    @Override
    public void onClick(View v) {
        if (mItemAction != null) {
            mItemAction.onClick(mSatelliteName, mSatellite, getLayoutPosition());
        }
    }
}
