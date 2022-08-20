package com.app.fitsmile.response.trayMinder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeLineResult {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fits_smile_id")
    @Expose
    private String fitsSmileId;
    @SerializedName("aligner_id")
    @Expose
    private String alignerId;
    @SerializedName("aligner_no")
    @Expose
    private String alignerNo;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("in_time")
    @Expose
    private String inTime;
    @SerializedName("out_time")
    @Expose
    private String outTime;
    @SerializedName("total_in_time")
    @Expose
    private String totalInTime;
    @SerializedName("total_out_time")
    @Expose
    private String totalOutTime;
    @SerializedName("goal_reached")
    @Expose
    private String goalReached;
    @SerializedName("updated_date")
    @Expose
    private String updatedDate;


    @SerializedName("isSwitch")
    @Expose
    private Boolean isSwitch;



    @SerializedName("isCurrent")
    @Expose
    private String isCurrent;

    @SerializedName("timming_data")
    @Expose
    private List<TimeLineResult> timingData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFitsSmileId() {
        return fitsSmileId;
    }

    public void setFitsSmileId(String fitsSmileId) {
        this.fitsSmileId = fitsSmileId;
    }

    public String getAlignerId() {
        return alignerId;
    }

    public void setAlignerId(String alignerId) {
        this.alignerId = alignerId;
    }

    public String getAlignerNo() {
        return alignerNo;
    }

    public void setAlignerNo(String alignerNo) {
        this.alignerNo = alignerNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getTotalInTime() {
        return totalInTime;
    }

    public void setTotalInTime(String totalInTime) {
        this.totalInTime = totalInTime;
    }

    public String getTotalOutTime() {
        return totalOutTime;
    }

    public void setTotalOutTime(String totalOutTime) {
        this.totalOutTime = totalOutTime;
    }

    public String getGoalReached() {
        return goalReached;
    }

    public void setGoalReached(String goalReached) {
        this.goalReached = goalReached;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setSwitch(Boolean aSwitch) {
        isSwitch = aSwitch;
    }

    public Boolean getSwitch() {
        return isSwitch;
    }

    public void setCurrent(String current) {
        isCurrent = current;
    }

    public String getCurrent() {
        return isCurrent;
    }

    public void setTimingData(List<TimeLineResult> timingData) {
        this.timingData = timingData;
    }

    public List<TimeLineResult> getTimingData() {
        return timingData;
    }
}
