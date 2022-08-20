package com.app.fitsmile.response.photoConsultancy.appointmentList;

import java.util.List;

public class PhotoConsultancyListResponse {
    private List<PhotoConsultancyListResult> data;

    private String message;

    private int status;

    public List<PhotoConsultancyListResult> getData ()
    {
        return data;
    }

    public void setData (List<PhotoConsultancyListResult> data)
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

    public int getStatus ()
    {
        return status;
    }

    public void setStatus (int status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", message = "+message+", status = "+status+"]";
    }
}
