package com.violetsector.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class Preferences extends AppCompatActivity
{
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.preferences_container, new PreferencesFragment())
				.commit();
	}
}