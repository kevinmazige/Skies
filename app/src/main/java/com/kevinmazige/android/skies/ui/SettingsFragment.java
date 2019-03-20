package com.kevinmazige.android.skies.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;


import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.notification.NotificationViewModel;
import com.kevinmazige.android.skies.notification.NotificationViewModelFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationViewModel mNotificationViewModel;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference radiusPreference = findPreference(SettingsActivity.KEY_PREF_RADIUS_EDITTEXT);
        Preference notificationPreference = findPreference(SettingsActivity.KEY_PREF_NOTIFICATION_SWITCH);

        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //dynamically set and keep up-to-date the summary of the user selected radius value
        radiusPreference.setSummary(PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(SettingsActivity.KEY_PREF_RADIUS_EDITTEXT, "66"));


        NotificationViewModelFactory notificationViewModelFactory = NotificationViewModelFactory
                .createFactory(getActivity());

        mNotificationViewModel = ViewModelProviders
                .of(this, notificationViewModelFactory).get(NotificationViewModel.class);

        createNotificationChannel();

        /**
         * Sets radiusPreference change listener to validate user input for radius and to update summary
         */
        radiusPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int input;

                //check that the user entered a valid integer
                try {
                    input = Integer.parseInt((String) newValue);
                } catch (Exception e) {
                    alertUser();
                    return false;
                }

                // check that the user entered an integer between 0 and 90
                if (input >= 0 && input <= 90) {
                    preference.setSummary((String) newValue);
                    return true;
                }
                alertUser();
                return false;
            }
        });

        /**
         * Sets notification change listener to handle notifications
         */

        notificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Boolean switchStatus = (Boolean) newValue;
                if (switchStatus) {
                    //activate visual pass notification worker
                    mNotificationViewModel.getVisualPasses();
                } else {
                    //deactivate visual pass notification worker
                    mNotificationViewModel.cancelWork();
                }
                return true;
            }
        });


    }

    //alerts user that they entered an invalid radius value
    private void alertUser() {
        Toast.makeText(getContext(), "please enter an integer between 0 and 90",
                Toast.LENGTH_LONG).show();
    }

    //create notification channel to be used by visual pass notifications (these are launched by
    //from the VisualPassWorker class
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Visual pass notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("" +
                    "Notifies about visual passes of favourite satellites lasting at least 5 minutes");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
