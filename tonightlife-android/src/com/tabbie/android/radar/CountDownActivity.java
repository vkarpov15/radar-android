package com.tabbie.android.radar;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;

public class CountDownActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		
	 final Calendar c = Calendar.getInstance();
	 final long currentMillis = c.getTimeInMillis();
	 final long noonMillis = 12*60*60*1000;
	 final long deltaMillis = noonMillis - currentMillis;
	 
	 
	 
	 new CountDownTimer(deltaMillis, 1000) {

	     public void onTick(long millisUntilFinished) {
	         // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
	     }

	     public void onFinish() {
	         // mTextField.setText("done!");
	     }
	  }.start();
		
	}

}
