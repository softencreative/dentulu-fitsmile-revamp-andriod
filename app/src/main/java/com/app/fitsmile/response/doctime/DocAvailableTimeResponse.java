package com.app.fitsmile.response.doctime;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DocAvailableTimeResponse{

	@SerializedName("timings")
	private List<TimingsItem> timings;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setTimings(List<TimingsItem> timings){
		this.timings = timings;
	}

	public List<TimingsItem> getTimings(){
		return timings;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}