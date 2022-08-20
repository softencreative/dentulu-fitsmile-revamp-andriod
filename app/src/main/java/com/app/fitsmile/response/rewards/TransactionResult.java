package com.app.fitsmile.response.rewards;

public class TransactionResult
{
    private String created_date;

    private String id;

    private String discount_reward_amt;

    private String booking_date;

    private String name;

    private String booking_time;

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getDiscount_reward_amt ()
    {
        return discount_reward_amt;
    }

    public void setDiscount_reward_amt (String discount_reward_amt)
    {
        this.discount_reward_amt = discount_reward_amt;
    }

    public String getBooking_date ()
    {
        return booking_date;
    }

    public void setBooking_date (String booking_date)
    {
        this.booking_date = booking_date;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getBooking_time ()
    {
        return booking_time;
    }

    public void setBooking_time (String booking_time)
    {
        this.booking_time = booking_time;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [created_date = "+created_date+", id = "+id+", discount_reward_amt = "+discount_reward_amt+", booking_date = "+booking_date+", name = "+name+", booking_time = "+booking_time+"]";
    }
}

