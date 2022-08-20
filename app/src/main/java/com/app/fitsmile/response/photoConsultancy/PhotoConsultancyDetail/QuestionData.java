package com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail;

import java.util.List;

public class QuestionData {
    private String id;

    private String type;

    private String title;

    private List<String> value;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public List<String> getValue ()
    {
        return value;
    }

    public void setValue (List<String> value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", type = "+type+", title = "+title+", value = "+value+"]";
    }
}
