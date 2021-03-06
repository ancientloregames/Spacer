package com.ancientlore.spacer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ActivityMain extends Activity implements View.OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
		buttonPlay.setOnClickListener(this);

		SharedPreferences prefs =
				getSharedPreferences("HiScores", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		((TextView) findViewById(R.id.textViewHiScore)).setText("" + prefs.getLong("HiScore", 1000000) + " mps");
		editor.apply();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.buttonPlay:
				Intent i = new Intent(this, ActivityGame.class);
				startActivity(i);
				finish();
				break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
			return true;
		}
		else
		{
			return false;
		}
	}
}
