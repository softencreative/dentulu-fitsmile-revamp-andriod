package com.app.fitsmile.response.photoConsultancy.appointmentList;

public class PhotoConsultancyListResult {
    private String reference_id;

    private String user_id;

    private String patient_id;

    private String patient_name;

    private String mobile;

    private String id;

    private String created_date;

    private String doctor_name;

    private String email;

    private String consult_status;

    public String getReference_id ()
    {
        return reference_id;
    }

    public void setReference_id (String reference_id)
    {
        this.reference_id = reference_id;
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

    public String getPatient_name ()
    {
        return patient_name;
    }

    public void setPatient_name (String patient_name)
    {
        this.patient_name = patient_name;
    }

    public String getMobile ()
    {
        return mobile;
    }

    public void setMobile (String mobile)
    {
        this.mobile = mobile;
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

    public String getDoctor_name ()
    {
        return doctor_name;
    }

    public void setDoctor_name (String doctor_name)
    {
        this.doctor_name = doctor_name;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getConsult_status ()
    {
        return consult_status;
    }

    public void setConsult_status (String consult_status)
    {
        this.consult_status = consult_status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [reference_id = "+reference_id+", user_id = "+user_id+", patient_id = "+patient_id+", patient_name = "+patient_name+", mobile = "+mobile+", id = "+id+", created_date = "+created_date+", doctor_name = "+doctor_name+", email = "+email+", consult_status = "+consult_status+"]";
    }
}
