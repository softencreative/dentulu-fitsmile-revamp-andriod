package com.app.fitsmile.response.videofee;

import com.google.gson.annotations.SerializedName;

public class VideoCallFeeResponse{

	@SerializedName("video_call_fee")
	private String videoCallFee;

	@SerializedName("health_record_exist")
	private String healthRecordExist;

	@SerializedName("health_record_fill")
	private String healthRecordFill;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public String getVideoCallFee(){
		return videoCallFee;
	}

	public String getHealthRecordExist(){
		return healthRecordExist;
	}

	public String getHealthRecordFill(){
		return healthRecordFill;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}