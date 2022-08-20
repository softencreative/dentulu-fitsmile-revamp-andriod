package com.app.fitsmile.response.dentisdetails;

import com.google.gson.annotations.SerializedName;

public class HousecallServicesItem{

	@SerializedName("is_category")
	private String isCategory;

	@SerializedName("parent")
	private String parent;

	@SerializedName("amount")
	private String amount;

	@SerializedName("covered_insurance")
	private String coveredInsurance;

	@SerializedName("ADA_code")
	private String aDACode;

	@SerializedName("description")
	private String description;

	@SerializedName("amount_with_insurance")
	private String amountWithInsurance;

	@SerializedName("duration")
	private String duration;

	@SerializedName("is_deleted")
	private String isDeleted;

	@SerializedName("is_WP")
	private String isWP;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("visit_fee")
	private String visitFee;

	@SerializedName("status")
	private String status;

	public void setIsCategory(String isCategory){
		this.isCategory = isCategory;
	}

	public String getIsCategory(){
		return isCategory;
	}

	public void setParent(String parent){
		this.parent = parent;
	}

	public String getParent(){
		return parent;
	}

	public void setAmount(String amount){
		this.amount = amount;
	}

	public String getAmount(){
		return amount;
	}

	public void setCoveredInsurance(String coveredInsurance){
		this.coveredInsurance = coveredInsurance;
	}

	public String getCoveredInsurance(){
		return coveredInsurance;
	}

	public void setADACode(String aDACode){
		this.aDACode = aDACode;
	}

	public String getADACode(){
		return aDACode;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setAmountWithInsurance(String amountWithInsurance){
		this.amountWithInsurance = amountWithInsurance;
	}

	public String getAmountWithInsurance(){
		return amountWithInsurance;
	}

	public void setDuration(String duration){
		this.duration = duration;
	}

	public String getDuration(){
		return duration;
	}

	public void setIsDeleted(String isDeleted){
		this.isDeleted = isDeleted;
	}

	public String getIsDeleted(){
		return isDeleted;
	}

	public void setIsWP(String isWP){
		this.isWP = isWP;
	}

	public String getIsWP(){
		return isWP;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
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

	public void setCreatedDate(String createdDate){
		this.createdDate = createdDate;
	}

	public String getCreatedDate(){
		return createdDate;
	}

	public void setVisitFee(String visitFee){
		this.visitFee = visitFee;
	}

	public String getVisitFee(){
		return visitFee;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}