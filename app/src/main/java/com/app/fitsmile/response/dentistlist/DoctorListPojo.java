package com.app.fitsmile.response.dentistlist;

import java.util.ArrayList;

public class DoctorListPojo {


    private ArrayList<DoctorListDatas> datas;

    private String message;

    private String status;
    private String limit_reach;

    private String base_url;

    public ArrayList<DoctorListDatas> getDatas ()
    {
        return datas;
    }

    public void setDatas (ArrayList<DoctorListDatas>datas)
    {
        this.datas = datas;
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

    public String getLimit_reach() {
        return limit_reach;
    }

    public void setLimit_reach(String limit_reach) {
        this.limit_reach = limit_reach;
    }
    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }
    @Override
    public String toString()
    {
        return "ClassPojo [datas = "+datas+", message = "+message+",base_url = "+base_url+", status = "+status+"]";
    }

}
