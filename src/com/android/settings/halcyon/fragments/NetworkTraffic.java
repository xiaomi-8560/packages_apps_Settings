/*
 * Copyright (C) 2020 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.halcyon.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.halcyon.preference.SystemSettingListPreference;
import com.android.settings.halcyon.preference.SystemSettingMainSwitchPreference;

public class NetworkTraffic extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private SystemSettingListPreference mLocation;
    private SystemSettingListPreference mIndicatorMode;

    private static final String KEY_NETWORK_TRAFFIC = "network_traffic_state";
    private static final String KEY_NETWORK_TRAFFIC_LOCATION = "network_traffic_location";
    private static final String KEY_NETWORK_TRAFFIC_MODE = "network_traffic_mode";
    private SystemSettingMainSwitchPreference mNetworkTraffic;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.network_traffic);
        final ContentResolver resolver = getActivity().getContentResolver();

        mLocation = (SystemSettingListPreference) findPreference(KEY_NETWORK_TRAFFIC_LOCATION);
        mIndicatorMode = (SystemSettingListPreference) findPreference(KEY_NETWORK_TRAFFIC_MODE);

        mNetworkTraffic = (SystemSettingMainSwitchPreference)
                findPreference(KEY_NETWORK_TRAFFIC);
        boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                KEY_NETWORK_TRAFFIC, 0, UserHandle.USER_CURRENT) == 1;
        mNetworkTraffic.setChecked(isNetMonitorEnabled);
        mNetworkTraffic.setOnPreferenceChangeListener(this);

        // Set initial summaries
        updateLocationSummary(mLocation.getValue());
        updateIndicatorModeSummary(mIndicatorMode.getValue());

        mLocation.setOnPreferenceChangeListener(this);
        mIndicatorMode.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetworkTraffic) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver, KEY_NETWORK_TRAFFIC,
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mLocation) {
            updateLocationSummary((String) newValue);
            return true;
        } else if (preference == mIndicatorMode) {
            updateIndicatorModeSummary((String) newValue);
            return true;
        }
        return false;
    }

    private void updateLocationSummary(String value) {
        int index = mLocation.findIndexOfValue(value);
        if (index >= 0) {
            mLocation.setSummary(mLocation.getEntries()[index]);
        }
    }

    private void updateIndicatorModeSummary(String value) {
        int index = mIndicatorMode.findIndexOfValue(value);
        if (index >= 0) {
            mIndicatorMode.setSummary(mIndicatorMode.getEntries()[index]);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HALCYON;
    }
}