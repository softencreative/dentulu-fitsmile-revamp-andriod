package com.app.fitsmile.response.myaccount;

import com.google.gson.annotations.SerializedName;

public class UserDatas{

	@SerializedName("image")
	private String image;

	@SerializedName("avail")
	private String avail;

	@SerializedName("firstname")
	private String firstname;

	@SerializedName("joined_date")
	private String joinedDate;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("about")
	private String about;

	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("email")
	private String email;

	@SerializedName("username")
	private String username;

	@SerializedName("lastname")
	private String lastname;

	public String getImage(){
		return image;
	}

	public String getAvail(){
		return avail;
	}

	public String getFirstname(){
		return firstname;
	}

	public String getJoinedDate(){
		return joinedDate;
	}

	public String getMobile(){
		return mobile;
	}

	public String getAbout(){
		return about;
	}

	public String getCreatedDate(){
		return createdDate;
	}

	public String getEmail(){
		return email;
	}

	public String getUsername(){
		return username;
	}

	public String getLastname(){
		return lastname;
	}
}