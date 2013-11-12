package com.quantizity.quickalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;

public class QuickAlert extends Activity {
	EditText txtHours, txtMinutes;
	ListView lista;
	OnFocusChangeListener listener;
	private static AlertStorage alStor = null;
	private static Context ctx = null;
	
	public static final String UPDATE_ACTION = "com.quantizity.quickalert.UPDATE_ALERT_LIST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = getApplicationContext();
		setContentView(R.layout.activity_quick_alert);
		txtHours = (EditText) findViewById(R.id.txtHours);
		txtMinutes = (EditText) findViewById(R.id.txtMinutes);
		lista = (ListView) findViewById(R.id.lstAlerts);
		lista.requestFocus();
		try {
			alStor = new AlertStorage();
		} catch (Exception e) {
			e.printStackTrace();
		}
        lista.setAdapter(new Adaptador(this, alStor.getAlerts()));

		// Comprobación de que se introduce por teclado un número y es válido en el rango definido
		// Se comprueba al cambiar el foco del elemento
		listener = new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					try {
						Integer n = Integer.parseInt(((EditText)v).getText().toString());
						if(n<0) n=0;
						if (v.getId() == txtHours.getId()) {
							if (n>23) n=23;
						}
						else if (v.getId() == txtMinutes.getId()) {
							if (n>59) n=59;
						}
						((EditText)v).setText(n.toString());
					} catch (Exception ex) {
						((EditText)v).setText("0");
					}
				}
			}
			
		};
		txtHours.setOnFocusChangeListener(listener);
		txtMinutes.setOnFocusChangeListener(listener);
		// arrancamos servicio de alertas
		startService(new Intent(this, AlertService.class));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// reload list of alerts from file
		if(alStor!=null) alStor.loadAlerts(this);
	}
	
	public static void reloadContent() {
		alStor.loadAlerts(ctx);
	}
	
	public static AlertStorage getAlertStorage() {
		if (alStor==null) {
			try {
				alStor = new AlertStorage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		alStor.loadAlerts(ctx);
		return alStor;
	}

	public static Context getAppContext() {
		return ctx;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedState){
		super.onSaveInstanceState(savedState);
		savedState.putCharSequence("hours", txtHours.getText());
		savedState.putCharSequence("minutes", txtMinutes.getText());
	} 
	
	@Override
    protected void onRestoreInstanceState(Bundle savedState){
          super.onRestoreInstanceState(savedState);
          if (savedState!=null) {
          	  txtHours.setText(savedState.getCharSequence("hours").toString());
          	  txtMinutes.setText(savedState.getCharSequence("minutes").toString());
            }
	}
	
	
	public void addOneHour(View view)
	{
		Integer num = 0;
		if (txtHours!=null) {
			try {
				num = (Integer)Integer.parseInt(txtHours.getText().toString());
				num = (num+1)%24; // máximo 1 dia
			} catch (Exception ex) {
				// No es numerico
				num=1;
			}
			txtHours.setText(num.toString());
		}
	}
	
	public void addFiveMinutes(View view)
	{
		Integer num = 0;
		if (txtMinutes!=null) {
			try {
				num = (Integer)Integer.parseInt(txtMinutes.getText().toString());
				num = (num+5)%60; // máximo 60 minutos en una hora
			} catch (Exception ex) {
				// No es numérico
				num=5;
			}
			txtMinutes.setText(num.toString());
		}
	}
	
	public void subOneHour(View view)
	{
		Integer num = 0;
		if (txtHours!=null) {
			try {
				num = (Integer)Integer.parseInt(txtHours.getText().toString());
				num--;
				if (num<0) num=24-(-num);
			} catch (Exception ex) {
				// No es numérico
				num=0;
			}
			txtHours.setText(num.toString());
		}
	}
	
	public void subFiveMinutes(View view)
	{
		Integer num = 0;
		if (txtMinutes!=null) {
			try {
				num = (Integer)Integer.parseInt(txtMinutes.getText().toString());
				num=num-5;
				if (num<0) num=60-(-num);
			} catch (Exception ex) {
				// No es numérico
				num=5;
			}
			txtMinutes.setText(num.toString());
		}
	}
	
	public void activate(View view)
	{
		// Setup alarm service
		txtHours.clearFocus();
		txtMinutes.clearFocus();
		String hours = txtHours.getText().toString();
		String minutes = txtMinutes.getText().toString();
		Integer hrs = Integer.parseInt(hours);
		Integer mins = Integer.parseInt(minutes);
		long timeToFire = (hrs*3600+mins*60)*1000;
		boolean pluralHr, pluralMin, setHours, setMinutes;
		
		setHours = hours.equals("") || hours.equals("0") ? false : true;
		setMinutes = minutes.equals("") || minutes.equals("0") ? false : true;
		try {
			pluralHr = Integer.parseInt(hours) > 1 ? true : false;
		} catch (Exception ex) {
			pluralHr = false;
		}
		try {
			pluralMin = Integer.parseInt(minutes) > 1 ? true : false;
		} catch (Exception ex) {
			pluralMin = false;
		}
		
		if (!setHours && !setMinutes) {
			return;
		}
		String textAlert = getResources().getString(R.string.txtAlert_begin) + " " + 
				(!setHours ? "" : txtHours.getText().toString() + (pluralHr ? " " + getResources().getString(R.string.hours) : " " + getResources().getString(R.string.hour))) + 
				(!setHours || !setMinutes? "": " " + getResources().getString(R.string.txtAlert_and) + " ") +
				(!setMinutes? "" :txtMinutes.getText().toString() + (pluralMin ? " " + getResources().getString(R.string.minutes) : " " + getResources().getString(R.string.minute)));
		
		// update list of alerts (check if alert already exists for same goal)
		
		long current = System.currentTimeMillis();
		Alert alert = new Alert();
		alert.setDuration(Long.valueOf(timeToFire));
		Date initTime = new Date();
		initTime.setTime(current);
		alert.setInitTime(initTime);
            Date endTime = new Date();
            endTime.setTime(current+timeToFire);
            alert.setEndTime(endTime);
            if (alStor.hasSameEndTime(alert)) {
                Toast.makeText(this, R.string.alert_exists_text, Toast.LENGTH_SHORT).show();
                return;
		}
		int retCode = alStor.addAlert(alert);
		if (retCode==-1) {
			Toast.makeText(this, R.string.alert_error_text, Toast.LENGTH_SHORT).show();
			return;
		} else if (retCode==-2) {
			Toast.makeText(this, R.string.alert_error_max_elements, Toast.LENGTH_SHORT).show();
			return;
		}
		alStor.persistAlerts(this);
		
		// send parameters to broadcast receiver for new alert

        // TODO : activar alarma para el tiempo solicitado

		Intent i = new Intent();
		i.putExtra("when", textAlert);
		i.setAction(UPDATE_ACTION);
		sendBroadcast(i);
	}
	
	public void reset(View view)
	{
		txtHours.setText("0");
		txtMinutes.setText("5");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.AcercaDe:
			about(null);
			break;
		}
		return true;
	}
	
	public void startPreferences(View view)
	{
		// TODO: create preference activity to change preference such as ring music, snoozing, volume, etc...
	}
	
	public void about(View view)
	{
		Intent i = new Intent(this, AcercaDe.class);
		startActivity(i);
	}
}
