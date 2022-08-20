package com.app.fitsmile.response.healthrec;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseViewHealthRecord{

	/*@SerializedName("datas")
	private Datas datas;*/

	@SerializedName("data")
	private List<HealthHIstoryItem> data;

	@SerializedName("expire_on")
	private int expire_on;

	public int getExpire_on() {
		return expire_on;
	}

	public void setExpire_on(int expire_on) {
		this.expire_on = expire_on;
	}


	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;


	public void setData(List<HealthHIstoryItem> data) {
		this.data = data;
	}

	public List<HealthHIstoryItem> getData() {
		return data;
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