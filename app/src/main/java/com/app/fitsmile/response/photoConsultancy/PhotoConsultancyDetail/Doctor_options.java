package com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail;

public class Doctor_options
{
    private String patient_comment;

    private String patient_detail_id;

    private String description;

    private String case_type;

    private String before_after_name;

    private String[] videos;

    private String dropdown;

    private String special_offer;

    private String insurance_coverage;

    private String estimated_cost;

    private String before_after_image;

    private String after_image;

    private String id;

    private String created_date;

    private String no_of_appointment;

    public String getPatient_comment ()
    {
        return patient_comment;
    }

    public void setPatient_comment (String patient_comment)
    {
        this.patient_comment = patient_comment;
    }

    public String getPatient_detail_id ()
    {
        return patient_detail_id;
    }

    public void setPatient_detail_id (String patient_detail_id)
    {
        this.patient_detail_id = patient_detail_id;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getCase_type ()
    {
        return case_type;
    }

    public void setCase_type (String case_type)
    {
        this.case_type = case_type;
    }

    public String getBefore_after_name ()
    {
        return before_after_name;
    }

    public void setBefore_after_name (String before_after_name)
    {
        this.before_after_name = before_after_name;
    }

    public String[] getVideos ()
    {
        return videos;
    }

    public void setVideos (String[] videos)
    {
        this.videos = videos;
    }

    public String getDropdown ()
    {
        return dropdown;
    }

    public void setDropdown (String dropdown)
    {
        this.dropdown = dropdown;
    }

    public String getSpecial_offer ()
    {
        return special_offer;
    }

    public void setSpecial_offer (String special_offer)
    {
        this.special_offer = special_offer;
    }

    public String getInsurance_coverage ()
    {
        return insurance_coverage;
    }

    public void setInsurance_coverage (String insurance_coverage)
    {
        this.insurance_coverage = insurance_coverage;
    }

    public String getEstimated_cost ()
    {
        return estimated_cost;
    }

    public void setEstimated_cost (String estimated_cost)
    {
        this.estimated_cost = estimated_cost;
    }

    public String getBefore_after_image ()
    {
        return before_after_image;
    }

    public void setBefore_after_image (String before_after_image)
    {
        this.before_after_image = before_after_image;
    }

    public String getAfter_image ()
    {
        return after_image;
    }

    public void setAfter_image (String after_image)
    {
        this.after_image = after_image;
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

    public String getNo_of_appointment ()
    {
        return no_of_appointment;
    }

    public void setNo_of_appointment (String no_of_appointment)
    {
        this.no_of_appointment = no_of_appointment;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [patient_comment = "+patient_comment+", patient_detail_id = "+patient_detail_id+", description = "+description+", case_type = "+case_type+", before_after_name = "+before_after_name+", videos = "+videos+", dropdown = "+dropdown+", special_offer = "+special_offer+", insurance_coverage = "+insurance_coverage+", estimated_cost = "+estimated_cost+", before_after_image = "+before_after_image+", after_image = "+after_image+", id = "+id+", created_date = "+created_date+", no_of_appointment = "+no_of_appointment+"]";
    }
}