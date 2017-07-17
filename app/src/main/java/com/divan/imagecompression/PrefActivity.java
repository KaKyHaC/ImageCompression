package com.divan.imagecompression;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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
