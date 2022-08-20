package com.app.fitsmile.response.date;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AppointmentDateResponse{

	@SerializedName("min_hours")
	private int minHours;

	@SerializedName("video_call_fee")
	private String videoCallFee;

	@SerializedName("min_seconds")
	private int minSeconds;

	@SerializedName("dates")
	private List<DatesItem> dates;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public int getMinHours(){
		return minHours;
	}

	public String getVideoCallFee(){
		return videoCallFee;
	}

	public int getMinSeconds(){
		return minSeconds;
	}

	public List<DatesItem> getDates(){
		return dates;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}