package com.app.fitsmile.response.doctime;

import com.google.gson.annotations.SerializedName;

public class TimingsItem{

	@SerializedName("available")
	private String available;

	@SerializedName("time")
	private String time;

	@SerializedName("timestamp")
	private int timestamp;

	public void setAvailable(String available){
		this.available = available;
	}

	public String getAvailable(){
		return available;
	}

	public void setTime(String time){
		this.time = time;
	}

	public String getTime(){
		return time;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}
}