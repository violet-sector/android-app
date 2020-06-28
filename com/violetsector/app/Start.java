package com.violetsector.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class Start extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Run super method
		super.onCreate(savedInstanceState);

		// Register the notification channels
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			// Create a notification manager
			NotificationManager notificationManager = getSystemService(NotificationManager.class);

			// Create the attack notification channel
			NotificationChannel attackChannel = new NotificationChannel(getString(R.string.attack_channel_id), getString(R.string.attack_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
			attackChannel.setDescription(getString(R.string.attack_channel_desc));

			// Create the default notification channel
			NotificationChannel defaultChannel = new NotificationChannel(getString(R.string.default_channel_id), getString(R.string.attack_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
			defaultChannel.setDescription(getString(R.string.default_channel_desc));

			// Register the channels with the system
			notificationManager.createNotificationChannel(attackChannel);
			notificationManager.createNotificationChannel(defaultChannel);
		}

		// Load saved preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String username = prefs.getString("username", null);
		String password = prefs.getString("password", null);

		// Check to see if we have a username and password
		if (username == null || password == null)
		{
			// Send the user to the preferences activity so they can provide login details
			Intent intent = new Intent(this, Preferences.class);
			startActivity(intent);
		}

		// Send the user to the main activity
		else
		{
			Intent intent = new Intent(this, Main.class);
			startActivity(intent);
		}

		// Finish with this activity
		finish();
	}
}