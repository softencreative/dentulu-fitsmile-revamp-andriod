package com.app.fitsmile.response.dentisdetails;

import com.google.gson.annotations.SerializedName;

public class LicensesItem{

	@SerializedName("license_no")
	private String licenseNo;

	@SerializedName("country")
	private String country;

	@SerializedName("expire_date")
	private String expireDate;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("is_approved")
	private String isApproved;

	@SerializedName("id")
	private String id;

	@SerializedName("state")
	private String state;

	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("status")
	private String status;

	public void setLicenseNo(String licenseNo){
		this.licenseNo = licenseNo;
	}

	public String getLicenseNo(){
		return licenseNo;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setExpireDate(String expireDate){
		this.expireDate = expireDate;
	}

	public String getExpireDate(){
		return expireDate;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setIsApproved(String isApproved){
		this.isApproved = isApproved;
	}

	public String getIsApproved(){
		return isApproved;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setCreatedDate(String createdDate){
		this.createdDate = createdDate;
	}

	public String getCreatedDate(){
		return createdDate;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}