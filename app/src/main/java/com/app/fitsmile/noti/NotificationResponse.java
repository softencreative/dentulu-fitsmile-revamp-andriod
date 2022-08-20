package com.app.fitsmile.noti;

public class NotificationResponse  {

    private String is_read;

    private String ref_id;

    private String sub_name;

    private String user_id;

    private String sub_id;

    private String id;

    private String notification_id;

    private String created_date;

    private String type;

    private String message;

    private String status;

    public String getIs_read ()
    {
        return is_read;
    }

    public void setIs_read (String is_read)
    {
        this.is_read = is_read;
    }

    public String getRef_id ()
    {
        return ref_id;
    }

    public void setRef_id (String ref_id)
    {
        this.ref_id = ref_id;
    }

    public String getSub_name ()
    {
        return sub_name;
    }

    public void setSub_name (String sub_name)
    {
        this.sub_name = sub_name;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getSub_id ()
    {
        return sub_id;
    }

    public void setSub_id (String sub_id)
    {
        this.sub_id = sub_id;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getNotification_id ()
    {
        return notification_id;
    }

    public void setNotification_id (String notification_id)
    {
        this.notification_id = notification_id;
    }

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
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
        return "ClassPojo [is_read = "+is_read+", ref_id = "+ref_id+", sub_name = "+sub_name+", user_id = "+user_id+", sub_id = "+sub_id+", id = "+id+", notification_id = "+notification_id+", created_date = "+created_date+", type = "+type+", message = "+message+", status = "+status+"]";
    }
}
