package com.quantizity.quickalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartAlert<currentAlert> extends BroadcastReceiver {

	private static Alert currentAlert = null;
	private static AlertStorage alerts = null;
	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			// After reboot start alert service to catch time tick
			ctx.startService(new Intent(ctx, AlertService.class));
			// on system start, load the alerts from file
			if (alerts==null) {
				try {
					alerts = new AlertStorage();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				alerts.loadAlerts(ctx);				
			}
		}
		else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)) 
		{
			if (alerts==null) {
				try {
					alerts = new AlertStorage();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				alerts.loadAlerts(ctx);				
			}
			boolean fire = false;
			// iterate over list of alerts and start alarm in a different thread if goal reached
			for(Alert currentAlert : alerts.getAlerts()) {
				if (currentAlert.readyToFire()) {
					Thread thread = new Thread(){
						@Override
						public void run() {
							StartAlert.currentAlert.showAlert();
						}
					};
					fire = true;
					thread.start();
				}				
				// once alert thread has started update alert list if recurring or delete alert
				// finally, persist list of alerts, and make QuickAlert app to reload content
				if(fire) {
					alerts.removeAlert(currentAlert);
					alerts.persistAlerts(ctx);
					QuickAlert.reloadContent();
				}		
			}
		}
		else if (intent.getAction().equals(QuickAlert.UPDATE_ACTION)) {
			if(intent!=null && intent.getStringExtra("when")!=null) {
				String texto=intent.getStringExtra("when");
				Toast.makeText(ctx,texto, Toast.LENGTH_SHORT).show();
				if (alerts==null) {
					try {
						alerts = new AlertStorage();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					alerts.loadAlerts(ctx);				
				}
			}
			
		}
	}

}
