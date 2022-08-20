package com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail;

public class PhotoConsultancyDetailResponse {
    private PhotoConsultancyDetailResult data;

    private String message;

    private int status;

    public PhotoConsultancyDetailResult getData ()
    {
        return data;
    }

    public void setData (PhotoConsultancyDetailResult data)
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
