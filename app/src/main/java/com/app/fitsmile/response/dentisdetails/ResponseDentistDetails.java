package com.app.fitsmile.response.dentisdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseDentistDetails{

	@SerializedName("licenses")
	private List<LicensesItem> licenses;

	@SerializedName("videocall_services")
	private List<VideocallServicesItem> videocallServices;

	@SerializedName("availableSlots")
	private List<AvailableSlotsItem> availableSlots;

	@SerializedName("message")
	private String message;

	@SerializedName("housecall_services")
	private List<HousecallServicesItem> housecallServices;

	@SerializedName("status")
	private String status;

	@SerializedName("info")
	private Info info;

	public void setLicenses(List<LicensesItem> licenses){
		this.licenses = licenses;
	}

	public List<LicensesItem> getLicenses(){
		return licenses;
	}

	public void setVideocallServices(List<VideocallServicesItem> videocallServices){
		this.videocallServices = videocallServices;
	}

	public List<VideocallServicesItem> getVideocallServices(){
		return videocallServices;
	}

	public void setAvailableSlots(List<AvailableSlotsItem> availableSlots){
		this.availableSlots = availableSlots;
	}

	public List<AvailableSlotsItem> getAvailableSlots(){
		return availableSlots;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setHousecallServices(List<HousecallServicesItem> housecallServices){
		this.housecallServices = housecallServices;
	}

	public List<HousecallServicesItem> getHousecallServices(){
		return housecallServices;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setInfo(Info info){
		this.info = info;
	}

	public Info getInfo(){
		return info;
	}
}