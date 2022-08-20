package com.app.fitsmile.response.addpatient;

import com.google.gson.annotations.SerializedName;

public class AddPatientResponse{

	@SerializedName("patient_id")
	private String patientId;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public String getPatientId(){
		return patientId;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}