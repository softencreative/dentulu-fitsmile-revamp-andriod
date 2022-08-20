package com.app.fitsmile.response.videocall;

public class VideoCallPojoData {


    private String is_connected;

    private String amount;

    private String is_ended;

    private String firstname;

    private String comments;

    private String doctor_fee;

    private String payment_status;

    private String rating;

    private String last_message;

    private String lastname;

    private String duration;

    private String doctor_id;

    private String user_id;

    private String recorded_id;

    private String id;

    private String created_date;

    private String status;

    public String getIs_connected ()
    {
        return is_connected;
    }

    public void setIs_connected (String is_connected)
    {
        this.is_connected = is_connected;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getIs_ended ()
    {
        return is_ended;
    }

    public void setIs_ended (String is_ended)
    {
        this.is_ended = is_ended;
    }

    public String getFirstname ()
    {
        return firstname;
    }

    public void setFirstname (String firstname)
    {
        this.firstname = firstname;
    }

    public String getComments ()
    {
        return comments;
    }

    public void setComments (String comments)
    {
        this.comments = comments;
    }

    public String getDoctor_fee ()
    {
        return doctor_fee;
    }

    public void setDoctor_fee (String doctor_fee)
    {
        this.doctor_fee = doctor_fee;
    }

    public String getPayment_status ()
    {
        return payment_status;
    }

    public void setPayment_status (String payment_status)
    {
        this.payment_status = payment_status;
    }

    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public String getLast_message ()
    {
        return last_message;
    }

    public void setLast_message (String last_message)
    {
        this.last_message = last_message;
    }

    public String getLastname ()
    {
        return lastname;
    }

    public void setLastname (String lastname)
    {
        this.lastname = lastname;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getDoctor_id ()
    {
        return doctor_id;
    }

    public void setDoctor_id (String doctor_id)
    {
        this.doctor_id = doctor_id;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getRecorded_id ()
    {
        return recorded_id;
    }

    public void setRecorded_id (String recorded_id)
    {
        this.recorded_id = recorded_id;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [is_connected = "+is_connected+", amount = "+amount+", is_ended = "+is_ended+", firstname = "+firstname+", comments = "+comments+", doctor_fee = "+doctor_fee+", payment_status = "+payment_status+", rating = "+rating+", last_message = "+last_message+", lastname = "+lastname+", duration = "+duration+", doctor_id = "+doctor_id+", user_id = "+user_id+", recorded_id = "+recorded_id+", id = "+id+", created_date = "+created_date+", status = "+status+"]";
    }
}
