package com.app.fitsmile.response.healthrec;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datas{

	@SerializedName("healthHIstory")
	private List<HealthHIstoryItem> healthHIstory;

	@SerializedName("expire_on")
	private int expire_on;

	public int getExpire_on() {
		return expire_on;
	}

	public void setExpire_on(int expire_on) {
		this.expire_on = expire_on;
	}

	public void setHealthHIstory(List<HealthHIstoryItem> healthHIstory){
		this.healthHIstory = healthHIstory;
	}

	public List<HealthHIstoryItem> getHealthHIstory(){
		return healthHIstory;
	}
}
