package com.app.fitsmile.book;

public class BookingSuccessPojo
{
    private String payment_updated;

    private String payment;

    private String message;

    private String payment_date;

    private String status;

    private String video_id;

    public String getPayment_updated ()
    {
        return payment_updated;
    }

    public void setPayment_updated (String payment_updated)
    {
        this.payment_updated = payment_updated;
    }

    public String getPayment ()
    {
        return payment;
    }

    public void setPayment (String payment)
    {
        this.payment = payment;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getPayment_date ()
    {
        return payment_date;
    }

    public void setPayment_date (String payment_date)
    {
        this.payment_date = payment_date;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getVideo_id ()
    {
        return video_id;
    }

    public void setVideo_id (String video_id)
    {
        this.video_id = video_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [payment_updated = "+payment_updated+", payment = "+payment+", message = "+message+", payment_date = "+payment_date+", status = "+status+", video_id = "+video_id+"]";
    }
}

