package com.app.fitsmile.response.date;

import com.google.gson.annotations.SerializedName;

public class DatesItem{

	@SerializedName("date")
	private String date;

	@SerializedName("available")
	private String available;

	@SerializedName("timestamp")
	private int timestamp;

	public String getDate(){
		return date;
	}

	public String getAvailable(){
		return available;
	}

	public int getTimestamp(){
		return timestamp;
	}
}