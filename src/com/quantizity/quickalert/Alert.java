package com.quantizity.quickalert;

import java.util.Date;

import android.content.Context;
import android.content.Intent;

public class Alert {

	private String description;
	private Long duration;
	private Date initTime;
	private Date endTime;
	private boolean recurring = false;
	private boolean fired = false;
	private boolean notification = false;
	private boolean snooze = false;
	private boolean ringTone;
	private String ringTonePath;
	private boolean vibrate;
	
	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public boolean isRingTone() {
		return ringTone;
	}

	public void setRingTone(boolean ringTone) {
		this.ringTone = ringTone;
	}

	public String getRingTonePath() {
		return ringTonePath;
	}

	public void setRingTonePath(String ringTonePath) {
		this.ringTonePath = ringTonePath;
	}

	private int timesSnoozed;
	
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
	
	public void showAlert(Context ctx) {
		Intent i = new Intent(ctx, AlertActivity.class);
		i.putExtra("notification", notification);
		i.putExtra("snooze", snooze);
		i.putExtra("timesSnooze", timesSnoozed);
		i.putExtra("description", description);
		i.putExtra("vibrate", vibrate);
		ctx.startActivity(i);
	}
	public boolean isOld() {
		if (this.endTime.getTime() < System.currentTimeMillis()) return true;
		return false;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public boolean isSnooze() {
		return snooze;
	}

	public void setSnooze(boolean snooze) {
		this.snooze = snooze;
	}

	public int getTimesSnoozed() {
		return timesSnoozed;
	}

	public void setTimesSnoozed(int timesSnoozed) {
		this.timesSnoozed = timesSnoozed;
	}
	
}
