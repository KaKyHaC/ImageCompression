package com.divan.imagecompression.Activitys;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.divan.imagecompression.R;

/**
 * Created by Димка on 12.11.2016.
 */

public class SteganographySetting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.steganography);
    }
}