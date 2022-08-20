package com.app.fitsmile.response;

public class SettingsResult {
    private String user_id;

    private String sms;

    private String id;

    private String updated_date;

    private String created_date;

    private String touch_id;

    private String aligner;

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getSms ()
    {
        return sms;
    }

    public void setSms (String sms)
    {
        this.sms = sms;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUpdated_date ()
    {
        return updated_date;
    }

    public void setUpdated_date (String updated_date)
    {
        this.updated_date = updated_date;
    }

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getTouch_id ()
    {
        return touch_id;
    }

    public void setTouch_id (String touch_id)
    {
        this.touch_id = touch_id;
    }

    public String getAligner ()
    {
        return aligner;
    }

    public void setAligner (String aligner)
    {
        this.aligner = aligner;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [user_id = "+user_id+", sms = "+sms+", id = "+id+", updated_date = "+updated_date+", created_date = "+created_date+", touch_id = "+touch_id+", aligner = "+aligner+"]";
    }
}
