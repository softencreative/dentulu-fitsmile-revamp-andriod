package com.app.fitsmile.response;

public class CheckDoctorAvailablePojo {

    private String message;
    private String booking_date;
    private String compare_date;
    private String waiting_duration;

    public String getVideo_id() {
        return video_id;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    private  String alreadyPaid;

    private String video_id;

    public String getAlreadyPaid() {
        return alreadyPaid;
    }

    public void setAlreadyPaid(String alreadyPaid) {
        this.alreadyPaid = alreadyPaid;
    }

    private String status;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getCompare_date() {
        return compare_date;
    }

    public void setCompare_date(String compare_date) {
        this.compare_date = compare_date;
    }

    public String getWaiting_duration() {
        return waiting_duration;
    }

    public void setWaiting_duration(String waiting_duration) {
        this.waiting_duration = waiting_duration;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", status = "+status+"]";
    }
}
