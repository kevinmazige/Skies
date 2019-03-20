package com.kevinmazige.android.skies.paging;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.SatelliteCategory;

public class SatelliteCategoryAdapter extends PagedListAdapter<SatelliteCategory, SatelliteCategoryViewHolder> {

    private ItemAction mItemOnClickAction;
    private Context mContext;

    public SatelliteCategoryAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    public interface ItemAction {
        void onClick(String categoryName, int categoryInt);
    }

    public void setItemOnClickAction(ItemAction itemOnClickAction) {
        mItemOnClickAction = itemOnClickAction;
    }

    @NonNull
    @Override
    public SatelliteCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_recyclerview_main, viewGroup, false);
        return new SatelliteCategoryViewHolder(itemView);
    }

    /*
     * bind satellite category data to viewholder
     */
    @Override
    public void onBindViewHolder(@NonNull SatelliteCategoryViewHolder satelliteCategoryViewHolder, int position) {
        satelliteCategoryViewHolder.setClickAction(mItemOnClickAction);
        SatelliteCategory satelliteCategory = getItem(position);
        if (satelliteCategory != null) {
            satelliteCategoryViewHolder.setCategoryName(satelliteCategory.getName());
            satelliteCategoryViewHolder.setCategoryInt(satelliteCategory.getId());
        }
    }

    private static final DiffUtil.ItemCallback<SatelliteCategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SatelliteCategory>() {
                @Override
                public boolean areItemsTheSame(@NonNull SatelliteCategory oldItem, @NonNull SatelliteCategory newItem) {
                    return oldItem.getName().equals(newItem.getName());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SatelliteCategory oldItem, @NonNull SatelliteCategory newItem) {
                    return oldItem == newItem;
                }
            };

}
