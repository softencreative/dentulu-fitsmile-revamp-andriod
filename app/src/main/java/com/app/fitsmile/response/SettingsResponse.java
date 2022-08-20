package com.app.fitsmile.response;

public class SettingsResponse {
    private SettingsResult data;

    private String message;

    private String status;

    private String id;

    public String getId() {
        return id;
    }

    public SettingsResult getData ()
    {
        return data;
    }

    public void setData (SettingsResult data)
    {
        this.data = data;
    }

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

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", message = "+message+",id = "+id+", status = "+status+"]";
    }

    public void setId(String id) {
        this.id = id;
    }
}
