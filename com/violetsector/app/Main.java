package com.violetsector.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebViewClientCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.nio.charset.StandardCharsets;

public class Main extends AppCompatActivity
{
	WebView webView;
	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState)
	{
		// Call super method
		super.onCreate(savedInstanceState);

		// Load preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Go to preferences activity if no username / password
		if (prefs.getString("username", null) == null || prefs.getString("password", null) == null)
		{
			Intent intent = new Intent(this, Preferences.class);
			startActivity(intent);
			return;
		}

		// Initialise Firebase Cloud Message if requested by the user
		if (prefs.getBoolean("notifications", false) == true && prefs.getBoolean("tvs_registration", false) == false)
		{
			// Find out what the current token is
			FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
				@Override
				public void onSuccess(InstanceIdResult instanceIdResult) {
					String token = instanceIdResult.getToken();
					// Send the token to TVS if necessary
					FirebaseTVS task = new FirebaseTVS(token, getApplicationContext());
					task.execute();
				}
			});
		}

		// Add progress bar to the activity
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		// Make progress bar visible
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		// Load layout from XML file
		setContentView(R.layout.main);

		// Get a reference to the WebView
		webView = findViewById(R.id.webview);

		// Restore the previous WebView state if exists
		webView.restoreState(savedInstanceState);

		// Customise the WebView
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(false);
		webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

		// Prevent HMTL links opening in browser application
		webView.setWebViewClient(new WebViewClientCompat()
		{
			public boolean shouldOverrideUrlLoading(WebView webView, String url)
			{
				webView.loadUrl(url);
				return true;
			}
		});

		// Display progress bar changes when relevant
		final Activity startClass = this;
		webView.setWebChromeClient(new WebChromeClient()
		{
			public void onProgressChanged(WebView view, int progress)
			{
				// Set progress bar text
				startClass.setTitle("Loading...");

				// Update progress bar
				startClass.setProgress(progress * 100);

				// Reset progress bar text when complete
				if (progress == 100)
				{
					startClass.setTitle(R.string.app_name);
				}
			}
		});
	}

	// Login again if the app restarts
	protected void onStart()
	{
		// Call super method
		super.onStart();

		// Reset attack count if necessary
		if (getIntent().getBooleanExtra("reset_attack_count", false))
		{
			Firebase.resetAttacks();
		}

		// Login the player
		String postData = "username=" + prefs.getString("username", "") + "&password=" + prefs.getString("password", null) + "&nicks=" + prefs.getInt("nickChanges", 0);
		webView.postUrl("https://www.violetsector.com/mobile/login.php", postData.getBytes(StandardCharsets.UTF_8));
	}

	public void shipComputerClicked(MenuItem mi)
	{
		webView.loadUrl("https://www.violetsector.com/mobile/main.php");
	}

	public void scansClicked(MenuItem mi)
	{
		webView.loadUrl("https://www.violetsector.com/mobile/scans.php");
	}

	public void mapClicked(MenuItem mi)
	{
		webView.loadUrl("https://www.violetsector.com/mobile/navcom_map.php");
	}

	public void preferencesClicked(MenuItem mi)
	{
		Intent intent = new Intent(this, Preferences.class);
		startActivity(intent);
	}
}