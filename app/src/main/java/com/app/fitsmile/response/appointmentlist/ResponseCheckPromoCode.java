package com.app.fitsmile.response.appointmentlist;

import com.google.gson.annotations.SerializedName;

public class ResponseCheckPromoCode{
	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("specialist_category")
	private String specialist_category;

	@SerializedName("offer_type_str")
	private String offer_type_str;

	@SerializedName("offer_type")
	private String offer_type;

	@SerializedName("offer_value")
	private String offer_value;

	public String getOffer_type() {
		return offer_type;
	}

	public String getOffer_type_str() {
		return offer_type_str;
	}

	public String getOffer_value() {
		return offer_value;
	}

	public String getSpecialist_category() {
		return specialist_category;
	}


	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}