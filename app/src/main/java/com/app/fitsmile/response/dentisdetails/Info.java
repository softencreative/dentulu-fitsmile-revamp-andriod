package com.app.fitsmile.response.dentisdetails;

import com.google.gson.annotations.SerializedName;

public class Info{

	@SerializedName("info_description")
	private String infoDescription;

	@SerializedName("image")
	private String image;

	@SerializedName("firstname")
	private String firstname;

	@SerializedName("star")
	private String star;

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("language")
	private String language;

	@SerializedName("experience")
	private String experience;

	@SerializedName("lastname")
	private String lastname;

	@SerializedName("rating_count")
	private String ratingCount;

	@SerializedName("qualification")
	private String qualification;

	@SerializedName("specialist")
	private String specialist;

	@SerializedName("rate")
	private String rate;

	@SerializedName("medical_school")
	private String medicalSchool;

	@SerializedName("member")
	private String member;

	@SerializedName("fav")
	private String fav;

	@SerializedName("id")
	private String id;

	@SerializedName("info")
	private String info;

	@SerializedName("image_url")
	private String image_url;

	public void setInfoDescription(String infoDescription){
		this.infoDescription = infoDescription;
	}

	public String getInfoDescription(){
		return infoDescription;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setFirstname(String firstname){
		this.firstname = firstname;
	}

	public String getFirstname(){
		return firstname;
	}

	public void setStar(String star){
		this.star = star;
	}

	public String getStar(){
		return star;
	}

	public void setTimezone(String timezone){
		this.timezone = timezone;
	}

	public String getTimezone(){
		return timezone;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public String getLanguage(){
		return language;
	}

	public void setExperience(String experience){
		this.experience = experience;
	}

	public String getExperience(){
		return experience;
	}

	public void setLastname(String lastname){
		this.lastname = lastname;
	}

	public String getLastname(){
		return lastname;
	}

	public void setRatingCount(String ratingCount){
		this.ratingCount = ratingCount;
	}

	public String getRatingCount(){
		return ratingCount;
	}

	public void setQualification(String qualification){
		this.qualification = qualification;
	}

	public String getQualification(){
		return qualification;
	}

	public void setSpecialist(String specialist){
		this.specialist = specialist;
	}

	public String getSpecialist(){
		return specialist;
	}

	public void setRate(String rate){
		this.rate = rate;
	}

	public String getRate(){
		return rate;
	}

	public void setMedicalSchool(String medicalSchool){
		this.medicalSchool = medicalSchool;
	}

	public String getMedicalSchool(){
		return medicalSchool;
	}

	public void setMember(String member){
		this.member = member;
	}

	public String getMember(){
		return member;
	}

	public void setFav(String fav){
		this.fav = fav;
	}

	public String getFav(){
		return fav;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setInfo(String info){
		this.info = info;
	}

	public String getInfo(){
		return info;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getImage_url() {
		return image_url;
	}
}