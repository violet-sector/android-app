package com.violetsector.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;

public class PreferencesFragment extends androidx.preference.PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		setPreferencesFromResource(R.xml.preferences, rootKey);

		findPreference("save").setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity(), Main.class);
				startActivity(intent);
				return true;
			}
		});
	}
}