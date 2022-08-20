package com.app.fitsmile.response.getstripe;

import com.google.gson.annotations.SerializedName;

public class StripeCustomerResponse{

	@SerializedName("stripe_customer_id")
	private String stripeCustomerId;

	@SerializedName("email")
	private String email;

	@SerializedName("status")
	private String status;

	public String getStripeCustomerId(){
		return stripeCustomerId;
	}

	public String getEmail(){
		return email;
	}

	public String getStatus(){
		return status;
	}
}