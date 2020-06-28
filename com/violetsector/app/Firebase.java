package com.violetsector.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Firebase extends FirebaseMessagingService
{
	// Variable to track the number of times the user has been attacked
	private static int attackCount = 0;

	// Keep track of last notification with sound
	private static long last_noise = 0;

	// Unique id reference for notifications
	static final int TVS_ID = 0x5;

	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage)
	{
		String channelID, title, text, subtext = null;

		// Determine if it's an attack notification
		String hp = remoteMessage.getData().get("hp");
		if (hp != null)
		{
			// Send the notification on the attack channel
			channelID = getString(R.string.attack_channel_id);

			// Notification title for attacks
			title = "You have been attacked!";

			// Notification message for attacks
			text = "Your hitpoints are " + remoteMessage.getData().get("hp");

			// Advise the user of how many times he's been attacked
			String attacks = getResources().getQuantityString(R.plurals.numberOfAttacks, ++attackCount);
			subtext = attackCount + " " + attacks;
		}

		else
		{
			channelID = getString(R.string.default_channel_id);
			title = remoteMessage.getData().get("title");
			text = remoteMessage.getData().get("message");
		}

		// Create a builder for the notification
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID);

		// Create a pending intent to broadcast when the notification is clicked
		Intent notificationIntent = new Intent(this, Main.class);
		notificationIntent.putExtra("reset_attack_count", true);
		//notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				0);

		// Bind the notification to the intent
		notificationBuilder.setContentIntent(pendingIntent);

		// Set notification title
		notificationBuilder.setContentTitle(title);

		// Set notification message
		notificationBuilder.setContentText(text);

		// Set the notification subtext
		notificationBuilder.setSubText(subtext);

		// Set the notification icon
		notificationBuilder.setSmallIcon(R.drawable.ic_computer);

		// Clear the notification when it's selected by the user
		notificationBuilder.setAutoCancel(true);

		// Report when the last attack occurred
		notificationBuilder.setWhen(System.currentTimeMillis());

		// Vibrate phone, play notification sound and blink LED
		if (System.currentTimeMillis() - last_noise > 5000)
		{
			notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
			// Set last time noises were sounded
			last_noise = System.currentTimeMillis();
		}

		// Create a manager for the notification
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

		// Send the notification to the manager for display
		notificationManager.notify(TVS_ID, notificationBuilder.build());
	}

	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. Note that this is called when the InstanceID token
	 * is initially generated so this is where you would retrieve the token.
	 */
	@Override
	public void onNewToken(String token)
	{
		// Flag that a new token needs to be sent to TVS
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		prefs.edit().putBoolean("tvs_registration", false).commit();

		// Send the new token to TVS
		FirebaseTVS task = new FirebaseTVS(token, this);
		task.execute();
	}

	// Reset the attack counter when the user clicks a notification
	static void resetAttacks()
	{
		attackCount = 0;
	}
}