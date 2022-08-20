package com.app.fitsmile.response.dentisdetails;

import com.google.gson.annotations.SerializedName;

public class AvailableSlotsItem{

	@SerializedName("active_status")
	private String activeStatus;

	@SerializedName("dayname")
	private String dayname;

	@SerializedName("endtime")
	private String endtime;

	@SerializedName("week_day")
	private String weekDay;

	@SerializedName("starttime")
	private String starttime;

	@SerializedName("id")
	private String id;

	public void setActiveStatus(String activeStatus){
		this.activeStatus = activeStatus;
	}

	public String getActiveStatus(){
		return activeStatus;
	}

	public void setDayname(String dayname){
		this.dayname = dayname;
	}

	public String getDayname(){
		return dayname;
	}

	public void setEndtime(String endtime){
		this.endtime = endtime;
	}

	public String getEndtime(){
		return endtime;
	}

	public void setWeekDay(String weekDay){
		this.weekDay = weekDay;
	}

	public String getWeekDay(){
		return weekDay;
	}

	public void setStarttime(String starttime){
		this.starttime = starttime;
	}

	public String getStarttime(){
		return starttime;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
}