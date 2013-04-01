package com.quantizity.quickalert;

import java.util.Date;

public class Alert {

	private String description;
	private Long duration;
	private Date initTime;
	private Date endTime;
	private boolean recurring;
	
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
	
}
