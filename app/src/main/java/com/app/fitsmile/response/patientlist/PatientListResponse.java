package com.app.fitsmile.response.patientlist;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PatientListResponse{

	@SerializedName("datas")
	private List<PatientListData> datas;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public List<PatientListData> getDatas(){
		return datas;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}