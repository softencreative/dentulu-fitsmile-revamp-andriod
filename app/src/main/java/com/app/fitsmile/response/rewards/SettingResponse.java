package com.app.fitsmile.response.rewards;

import java.util.ArrayList;

public class SettingResponse
{
    private ArrayList<SettingResult> settings;

    private String message;

    private String status;

    public ArrayList<SettingResult> getSettings ()
    {
        return settings;
    }

    public void setSettings (ArrayList<SettingResult> settings)
    {
        this.settings = settings;
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
        return "ClassPojo [settings = "+settings+", message = "+message+", status = "+status+"]";
    }
}