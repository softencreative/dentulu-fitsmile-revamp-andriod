package com.app.fitsmile.response.profile;

import com.google.gson.annotations.SerializedName;

public class PatientsItem{

	@SerializedName("insurance")
	private String insurance;

	@SerializedName("image")
	private String image;

	@SerializedName("mobile_code")
	private String mobileCode;

	@SerializedName("reference_id")
	private String referenceId;

	@SerializedName("is_primary")
	private String isPrimary;

	@SerializedName("sex")
	private String sex;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("insurance_number")
	private String insuranceNumber;

	@SerializedName("insurance_back")
	private String insuranceBack;

	@SerializedName("insurance_type")
	private String insuranceType;

	@SerializedName("is_deleted")
	private String isDeleted;

	@SerializedName("relationship_type")
	private String relationshipType;

	@SerializedName("driving_license")
	private String drivingLicense;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("dob")
	private String dob;

	@SerializedName("id")
	private String id;

	@SerializedName("driving_license_back")
	private String drivingLicenseBack;

	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("email")
	private String email;

	@SerializedName("emergency_contact")
	private Object emergencyContact;

	@SerializedName("status")
	private String status;

	public String getInsurance(){
		return insurance;
	}

	public String getImage(){
		return image;
	}

	public String getMobileCode(){
		return mobileCode;
	}

	public String getReferenceId(){
		return referenceId;
	}

	public String getIsPrimary(){
		return isPrimary;
	}

	public String getSex(){
		return sex;
	}

	public String getMobile(){
		return mobile;
	}

	public String getLastName(){
		return lastName;
	}

	public String getInsuranceNumber(){
		return insuranceNumber;
	}

	public String getInsuranceBack(){
		return insuranceBack;
	}

	public String getInsuranceType(){
		return insuranceType;
	}

	public String getIsDeleted(){
		return isDeleted;
	}

	public String getRelationshipType(){
		return relationshipType;
	}

	public String getDrivingLicense(){
		return drivingLicense;
	}

	public String getUserId(){
		return userId;
	}

	public String getDob(){
		return dob;
	}

	public String getId(){
		return id;
	}

	public String getDrivingLicenseBack(){
		return drivingLicenseBack;
	}

	public String getCreatedDate(){
		return createdDate;
	}

	public String getFirstName(){
		return firstName;
	}

	public String getEmail(){
		return email;
	}

	public Object getEmergencyContact(){
		return emergencyContact;
	}

	public String getStatus(){
		return status;
	}
}