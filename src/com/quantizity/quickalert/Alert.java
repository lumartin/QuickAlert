package com.quantizity.quickalert;

import java.util.Date;

public class Alert {

	private String description;
	private Long duration;
	private Date initTime;
	private Date endTime;
	private boolean recurring;
	private boolean fired;
	
	public Alert() {
		description = "";
		duration = Long.valueOf(0);
		initTime = null;
		endTime = null;
		recurring = false;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public Date getInitTime() {
		return initTime;
	}
	public void setInitTime(Date initTime) {
		this.initTime = initTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public boolean isRecurring() {
		return recurring;
	}
	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}
	
	public boolean readyToFire() {
		if (System.currentTimeMillis() < this.endTime.getTime() && 
				this.endTime.getTime() < (System.currentTimeMillis()+60000)) return true;
		return false;
	}

	public boolean isFired() {
		return fired;
	}

	public void setFired(boolean fired) {
		this.fired = fired;
	}
	
	public void showAlert() {
		// TODO: create a new activity for alert to show a dialog with a button (or 2 if snooze available) and a message (optional)
		// Code for vibration
		// get vibrator
		//Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// create a vibration pattern
		//long[] pattern = { 0, 200, 500 };
		// repeat pattern until canceled
		//v.vibrate(pattern, 0);
		// cancel vibration
		//v.cancel();
	}
	public boolean isOld() {
		if (this.endTime.getTime() < System.currentTimeMillis()) return true;
		return false;
	}
}
