package com.app.fitsmile.response.patientlist;

import com.google.gson.annotations.SerializedName;

public class PatientListData {

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

	@SerializedName("fits_reminder_appointment_exist")
	private String fitsReminderAppointmentExist;

	public String getFitsReminderAppointmentExist() {
		return fitsReminderAppointmentExist;
	}

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


	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setMobileCode(String mobileCode) {
		this.mobileCode = mobileCode;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setInsuranceNumber(String insuranceNumber) {
		this.insuranceNumber = insuranceNumber;
	}

	public void setInsuranceBack(String insuranceBack) {
		this.insuranceBack = insuranceBack;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDrivingLicenseBack(String drivingLicenseBack) {
		this.drivingLicenseBack = drivingLicenseBack;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFitsReminderAppointmentExist(String fitsReminderAppointmentExist) {
		this.fitsReminderAppointmentExist = fitsReminderAppointmentExist;
	}
}