package com.quantizity.quickalert;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlertActivity extends Activity {
	private final int MAX_TIMES_SNOOZED = 3;
	private Button okButton = null;
	private TextView txtAlert = null;
	private Intent intent = null;
	AlertStorage alStor = null;
	Alert myAlert = null;
	Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	long[] pattern = { 0, 200, 500 };
	public static final String UPDATE_ACTION = "com.quantizity.quickalert.UPDATE_ALERT_LIST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		intent = getIntent();
		myAlert = new Alert();
		readIntentParameters();
		alStor = QuickAlert.getAlertStorage();
		txtAlert = (TextView) findViewById(R.id.txtAlert);
		okButton = (Button) findViewById(R.id.ok_button);
		if (myAlert.isSnooze() && myAlert.getTimesSnoozed()<MAX_TIMES_SNOOZED) okButton.setText(R.string.snooze);
		else okButton.setText(R.string.accept);
		if (!myAlert.getDescription().equals("")) txtAlert.setText(myAlert.getDescription());
		if (myAlert.isVibrate()) vibrator.vibrate(pattern, 60000);
		
	}
	
	private void readIntentParameters() {
		myAlert.setNotification(intent.getBooleanExtra("notification", false));
		myAlert.setSnooze(intent.getBooleanExtra("snooze", false));
		myAlert.setRingTone(intent.getBooleanExtra("ringTone", false));
		myAlert.setRingTonePath(intent.getStringExtra("ringTonePath"));
		myAlert.setDescription(intent.getStringExtra("description"));
		myAlert.setTimesSnoozed(intent.getIntExtra("timesSnoozed", 0));
		myAlert.setVibrate(intent.getBooleanExtra("vibrate", false));
	}
	
	public void accept(View view) {
		if (myAlert.isVibrate()) vibrator.cancel();
		if (myAlert.isSnooze() && myAlert.getTimesSnoozed()<MAX_TIMES_SNOOZED) {
			// TODO: set new alert 5 minutes later
			long current = System.currentTimeMillis();
			myAlert.setDuration(Long.valueOf(current+50000));
			Date initTime = new Date();
			initTime.setTime(current);
			myAlert.setInitTime(initTime);
			Date endTime = new Date();
			endTime.setTime(current+50000);
			myAlert.setEndTime(endTime);
			int j=0;
			while (alStor.hasSameEndTime(myAlert) && j<10) {
				myAlert.setDuration(Long.valueOf(myAlert.getDuration()+10000));
				endTime = new Date();
				endTime.setTime(myAlert.getEndTime().getTime()+10000);
				myAlert.setEndTime(endTime);
				j++;
			}
			if (j<10) {
				// After 10 tries if it's possible create alert
				int retCode = alStor.addAlert(myAlert);
				alStor.persistAlerts(this);
				
				// send parameters to broadcast receiver for new alert
				Intent i = new Intent();
				i.putExtra("when", "");
				i.setAction(UPDATE_ACTION);
				sendBroadcast(i);
			}
			
		}
		// TODO: clear notification, stop tone, destroy view
		if(myAlert.isVibrate()) vibrator.cancel();
		this.finish();
	}
	
	@Override
	protected void onStop() {
		if(myAlert.isVibrate()) vibrator.cancel();
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		if(myAlert.isVibrate()) vibrator.cancel();
		super.onPause();
	}

}
