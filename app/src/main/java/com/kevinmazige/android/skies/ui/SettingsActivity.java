package com.kevinmazige.android.skies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_NOTIFICATION_SWITCH = "notification_switch";
    public static final String KEY_PREF_RADIUS_EDITTEXT = "radius_edittext";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
