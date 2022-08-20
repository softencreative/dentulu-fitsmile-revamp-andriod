package com.app.fitsmile.response.register;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponse{

	@SerializedName("session_token")
	private String sessionToken;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("otp_message")
	private String otpMessage;

	@SerializedName("mail_status")
	private String mailStatus;

	@SerializedName("message")
	private String message;

	@SerializedName("otp_status")
	private String otpStatus;

	@SerializedName("status")
	private String status;

	@SerializedName("token")
	private String token;

	@SerializedName("refer_code")
	private String referCode;

	public String getSessionToken(){
		return sessionToken;
	}

	public String getUserId(){
		return userId;
	}

	public String getOtpMessage(){
		return otpMessage;
	}

	public String getMailStatus(){
		return mailStatus;
	}

	public String getMessage(){
		return message;
	}

	public String getOtpStatus(){
		return otpStatus;
	}

	public String getStatus(){
		return status;
	}

	public String getToken(){
		return token;
	}

	public String getReferCode(){
		return referCode;
	}
}