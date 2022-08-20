package com.app.fitsmile.noti;

import java.util.ArrayList;

public class NotificationPojo {



    private String limit_reach;

    private String message;

    private ArrayList<NotificationResponse> notifications;

    private String status;

    public String getLimit_reach ()
    {
        return limit_reach;
    }

    public void setLimit_reach (String limit_reach)
    {
        this.limit_reach = limit_reach;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public ArrayList<NotificationResponse> getNotifications ()
    {
        return notifications;
    }

    public void setNotifications (ArrayList<NotificationResponse> notifications)
    {
        this.notifications = notifications;
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
        return "ClassPojo [limit_reach = "+limit_reach+", message = "+message+", notifications = "+notifications+", status = "+status+"]";
    }
}
