package com.divan.imagecompression.Activitys;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.divan.imagecompression.R;

/**
 * Created by Димка on 29.10.2016.
 */

public class PrefActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);
    }
}
