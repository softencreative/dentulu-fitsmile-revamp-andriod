package com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail;

import java.util.List;

public class PhotoConsultancyDetailResult {
    private String patient_mobile;

    private List<QuestionData> question_data;

    private List<Doctor_options> doctor_options;

    private String widget_video;

    private String widget_files;



    private String user_id;

    private String patient_id;

    private String patient_email;

    private String id;

    private String created_date;

    private String patient_lastname;

    private String patient_firstname;

    private List<QuestionImages> question_images;

    public String getPatient_mobile ()
    {
        return patient_mobile;
    }

    public void setPatient_mobile (String patient_mobile)
    {
        this.patient_mobile = patient_mobile;
    }

    public List<QuestionData> getQuestion_data ()
    {
        return question_data;
    }

    public void setQuestion_data (List<QuestionData> question_data)
    {
        this.question_data = question_data;
    }

    public List<Doctor_options> getDoctor_options ()
    {
        return doctor_options;
    }

    public void setDoctor_options (List<Doctor_options> doctor_options)
    {
        this.doctor_options = doctor_options;
    }

    public String getWidget_video ()
    {
        return widget_video;
    }

    public void setWidget_video (String widget_video)
    {
        this.widget_video = widget_video;
    }

    public String getWidget_files ()
    {
        return widget_files;
    }

    public void setWidget_files (String widget_files)
    {
        this.widget_files = widget_files;
    }


    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getPatient_id ()
    {
        return patient_id;
    }

    public void setPatient_id (String patient_id)
    {
        this.patient_id = patient_id;
    }

    public String getPatient_email ()
    {
        return patient_email;
    }

    public void setPatient_email (String patient_email)
    {
        this.patient_email = patient_email;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getPatient_lastname ()
    {
        return patient_lastname;
    }

    public void setPatient_lastname (String patient_lastname)
    {
        this.patient_lastname = patient_lastname;
    }

    public String getPatient_firstname ()
    {
        return patient_firstname;
    }

    public void setPatient_firstname (String patient_firstname)
    {
        this.patient_firstname = patient_firstname;
    }

    public List<QuestionImages> getQuestion_images ()
    {
        return question_images;
    }

    public void setQuestion_images (List<QuestionImages> question_images)
    {
        this.question_images = question_images;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [patient_mobile = "+patient_mobile+", question_data = "+question_data+", doctor_options = "+doctor_options+", widget_video = "+widget_video+", widget_files = "+widget_files+", user_id = "+user_id+", patient_id = "+patient_id+", patient_email = "+patient_email+", id = "+id+", created_date = "+created_date+", patient_lastname = "+patient_lastname+", patient_firstname = "+patient_firstname+", question_images = "+question_images+"]";
    }
}
