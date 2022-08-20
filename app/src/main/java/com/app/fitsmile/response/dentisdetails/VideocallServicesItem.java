package com.app.fitsmile.response.dentisdetails;

import com.google.gson.annotations.SerializedName;

public class VideocallServicesItem{

	@SerializedName("amount")
	private String amount;

	@SerializedName("ADA_code")
	private String aDACode;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	public void setAmount(String amount){
		this.amount = amount;
	}

	public String getAmount(){
		return amount;
	}

	public void setADACode(String aDACode){
		this.aDACode = aDACode;
	}

	public String getADACode(){
		return aDACode;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
}