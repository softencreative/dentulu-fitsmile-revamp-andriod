package com.app.fitsmile.response.profile;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse{

	@SerializedName("practice_location")
	private List<Object> practiceLocation;

	@SerializedName("datas")
	private Datas datas;

	@SerializedName("patients")
	private List<PatientsItem> patients;

	@SerializedName("specialists")
	private List<Object> specialists;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public List<Object> getPracticeLocation(){
		return practiceLocation;
	}

	public Datas getDatas(){
		return datas;
	}

	public List<PatientsItem> getPatients(){
		return patients;
	}

	public List<Object> getSpecialists(){
		return specialists;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}