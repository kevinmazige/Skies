package com.kevinmazige.android.skies.paging;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.Satellite;

import java.util.ArrayList;

public class SatelliteDataAdapter extends PagedListAdapter<Satellite, SatelliteDataViewHolder> {

    private ItemAction mItemOnClickAction;
    private Context mContext;
    private ArrayList<TextView> mSelectedSatellitesTv = new ArrayList<>();
    private ArrayList<Satellite> mSelectedSatellites = new ArrayList<>();

    public SatelliteDataAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    public interface ItemAction {
        void onClick(TextView v, Satellite s, int position);
    }

    public void setItemOnClickAction(ItemAction itemOnClickAction) {
        mItemOnClickAction = itemOnClickAction;
    }


    @NonNull
    @Override
    public SatelliteDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_recyclerview_add_to_favourites_satellite, viewGroup, false);
        return new SatelliteDataViewHolder(itemView);
    }


    /*
     * Bind satellite data to viewholder
     */
    @Override
    public void onBindViewHolder(@NonNull SatelliteDataViewHolder satelliteDataViewHolder, int position) {
        satelliteDataViewHolder.setClickAction(mItemOnClickAction);
        TextView textView = satelliteDataViewHolder.getTextView();
        Satellite satellite = getItem(position);
        if (satellite != null) {
            satelliteDataViewHolder.setSatellite(satellite);
        }

        /*
         * if the satellite has been clicked on, change the textview colour to show it highlighted.
         * Remember this colour change even when user has scrolled across pages.
         */
        if (isSelected(textView.getText().toString())) {
            textView.setBackgroundColor(ContextCompat.getColor(textView.getContext(),
                    R.color.colorHighlight));
        } else {
            textView.setBackgroundColor(ContextCompat.getColor(textView.getContext(),
                    R.color.colorPrimaryLight));
        }
    }

    /**
     * checks if two satellites are the exact same item or if they are the same object.
     */
    private static final DiffUtil.ItemCallback<Satellite> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Satellite>() {
                @Override
                public boolean areItemsTheSame(@NonNull Satellite oldItem, @NonNull Satellite newItem) {
                    return oldItem.getNoradId().equals(newItem.getNoradId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Satellite oldItem, @NonNull Satellite newItem) {
                    return oldItem == newItem;
                }
            };


    /***********************************************************************************
     * These functions keep track of satellites selected by the user
     ***********************************************************************************/

    /*
     * return a list of user clicked on satellites
     */
    public ArrayList<Satellite> getSelectedSatellites() {
        return mSelectedSatellites;
    }

    /*
     * adds user clicked on satellites to arraylist in case they need their data modified.
     */

    public void addSelectedSatellite(TextView tv, Satellite s, int p) {
        mSelectedSatellitesTv.add(tv);
        mSelectedSatellites.add(s);
    }

    /*
     * removes selected satellites from the arraylist used to keep track of them in case they need
     * modifying e.g. adding to favourites
     */
    public void removeSelectedSatellite(TextView tv, Satellite s, int p) {
        mSelectedSatellitesTv.remove(tv);
        mSelectedSatellites.remove(s);
    }

    /*
     * clears all user selected satellites, changing their colour to the unhighlighted state
     */
    public void clearAllSelectedSatellites() {

        mSelectedSatellites.clear();

        for (TextView tv : mSelectedSatellitesTv) {
            tv.setBackgroundColor(ContextCompat.getColor(tv.getContext(), R.color.colorPrimaryLight));
        }
        mSelectedSatellitesTv.clear();
    }

    /***********************************************************************************
     *
     ***********************************************************************************/

    public Satellite getSatelliteAtPosition(int position) {
        return getItem(position);
    }

    /*
     * checks if the satellite with the given name has been clicked on by the user
     */
    public Boolean isSelected(String name) {

        for (Satellite s : mSelectedSatellites) {
            if (s.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
