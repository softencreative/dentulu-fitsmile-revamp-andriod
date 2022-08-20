package com.app.fitsmile.response.healthrec;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HealthHIstoryItem{

	@SerializedName("values")
	private List<String> values;

	@SerializedName("name")
	private String name;

	public void setValues(List<String> values){
		this.values = values;
	}

	public List<String> getValues(){
		return values;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}