package com.app.fitsmile.response.myaccount;

import com.google.gson.annotations.SerializedName;

public class LocationDatasItem{

	@SerializedName("pincode")
	private String pincode;

	@SerializedName("address")
	private String address;

	@SerializedName("reference_id")
	private String referenceId;

	@SerializedName("city")
	private String city;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("building_info")
	private String buildingInfo;

	@SerializedName("is_deleted")
	private String isDeleted;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("category")
	private String category;

	@SerializedName("short_address")
	private String shortAddress;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("status")
	private String status;

	public String getPincode(){
		return pincode;
	}

	public String getAddress(){
		return address;
	}

	public String getReferenceId(){
		return referenceId;
	}

	public String getCity(){
		return city;
	}

	public String getLatitude(){
		return latitude;
	}

	public String getBuildingInfo(){
		return buildingInfo;
	}

	public String getIsDeleted(){
		return isDeleted;
	}

	public String getUserId(){
		return userId;
	}

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}

	public String getCreatedDate(){
		return createdDate;
	}

	public String getCategory(){
		return category;
	}

	public String getShortAddress(){
		return shortAddress;
	}

	public String getLongitude(){
		return longitude;
	}

	public String getStatus(){
		return status;
	}
}