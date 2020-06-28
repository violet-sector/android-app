package com.violetsector.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class FirebaseTVS extends AsyncTask<Void, Void, String>
{
	private Boolean register;
	private Context context;
	HttpURLConnection conn;
	SharedPreferences prefs;
	OutputStream out;
	BufferedReader in;
	String reply = "";
	String token = null;

	// Constructor
	public FirebaseTVS(String token, Context context)
	{
		this.token = token;
		this.context = context;
	}

	@Override
	protected String doInBackground(Void... arg0)
	{
		// Get the saved settings
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String username = prefs.getString("username", null);
		String password = prefs.getString("password", null);

		// Abort if we don't have a username or password
		if (username == null || password == null)
		{
			return null;
		}

		try
		{
			// Create a connection object
			conn = (HttpURLConnection) new URL("https://www.violetsector.com/android/register.php").openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Make sure a POST request is used
			conn.setDoOutput(true);

			// Stream the POST request as length will be variable
			conn.setChunkedStreamingMode(0);

			// Determine the registration variable to be sent
			String registration = (token != null) ? token : "false";

			// Encode the username, password and registration
			String data = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password) + "&register=" + URLEncoder.encode(registration);

			// Get output stream
			OutputStream outputStream = conn.getOutputStream();

			// Send the POST data
			outputStream.write(data.getBytes());

			// Close the output stream
			outputStream.close();

			// Get a buffer for the server reply
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while((line = in.readLine()) != null)
			{
				// Update preferences
				if (line.equals("registeredWithTVS=true"))
				{
					prefs.edit().putBoolean("tvs_registration", true).commit();
				}
				else if (line.equals("registeredWithTVS=false"))
				{
					// Reset tvs registration preference
					prefs.edit().putBoolean("tvs_registration", false).commit();
				}
				reply += line;
			}
		}
		catch (MalformedURLException e)
		{
			// this will never happen - hardcoded url
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			// TODO retry later
			e.printStackTrace();
		}
		finally
		{
			conn.disconnect();
		}
		return reply;
	}
}