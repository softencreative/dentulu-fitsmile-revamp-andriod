package com.app.fitsmile.response.login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{

	@SerializedName("image")
	private String image;

	@SerializedName("firstname")
	private String firstname;

	@SerializedName("name")
	private String name;

	@SerializedName("reward_amount")
	private String rewardAmount;

	@SerializedName("dental_office_firebase_uid")
	private String dentalOfficeFirebaseUid;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("firebase_uid")
	private String firebaseUid;

	@SerializedName("message")
	private String message;

	@SerializedName("token")
	private String token;

	@SerializedName("lastname")
	private String lastname;

	@SerializedName("session_token")
	private String sessionToken;

	@SerializedName("access_level")
	private String accessLevel;

	@SerializedName("firebase_custom_token")
	private String firebaseCustomToken;

	@SerializedName("by_code")
	private String byCode;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("workplace_approved")
	private String workplaceApproved;

	@SerializedName("first_booking")
	private String firstBooking;

	@SerializedName("provider_id")
	private String providerId;

	@SerializedName("logo")
	private String logo;

	@SerializedName("otp_status")
	private String otpStatus;

	@SerializedName("workplace_id")
	private String workplaceId;

	@SerializedName("provider_level")
	private String providerLevel;

	@SerializedName("status")
	private String status;

	@SerializedName("refer_code")
	private String referCode;

	@SerializedName("fits_minder_id")
	private String fitMinderId;

	@SerializedName("login_count")
	private int loginCount;

	public String getImage(){
		return image;
	}

	public String getFirstname(){
		return firstname;
	}

	public String getName() {
		return name;
	}

	public String getRewardAmount(){
		return rewardAmount;
	}

	public String getDentalOfficeFirebaseUid(){
		return dentalOfficeFirebaseUid;
	}

	public String getMobile(){
		return mobile;
	}

	public String getFirebaseUid(){
		return firebaseUid;
	}

	public String getMessage(){
		return message;
	}

	public String getToken(){
		return token;
	}

	public String getLastname(){
		return lastname;
	}

	public String getSessionToken(){
		return sessionToken;
	}

	public String getAccessLevel(){
		return accessLevel;
	}

	public String getFirebaseCustomToken(){
		return firebaseCustomToken;
	}

	public String getByCode(){
		return byCode;
	}

	public String getUserId(){
		return userId;
	}

	public String getWorkplaceApproved(){
		return workplaceApproved;
	}

	public String getFirstBooking(){
		return firstBooking;
	}

	public String getProviderId(){
		return providerId;
	}

	public String getLogo(){
		return logo;
	}

	public String getOtpStatus(){
		return otpStatus;
	}

	public String getWorkplaceId(){
		return workplaceId;
	}

	public String getProviderLevel(){
		return providerLevel;
	}

	public String getStatus(){
		return status;
	}

	public String getReferCode(){
		return referCode;
	}

	public void setFitMinderId(String fitMinderId) {
		this.fitMinderId = fitMinderId;
	}

	public String getFitMinderId() {
	return fitMinderId;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public int getLoginCount() {
		return loginCount;
	}
}