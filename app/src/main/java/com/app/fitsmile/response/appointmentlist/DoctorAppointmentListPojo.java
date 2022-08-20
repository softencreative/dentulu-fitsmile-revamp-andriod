package com.app.fitsmile.response.appointmentlist;

import java.util.ArrayList;

public class DoctorAppointmentListPojo {
    private String limit_reach;

    private ArrayList<DoctorAppointmentlistdataPojo> datas;

    private String message;

    private String status;

    public String getLimit_reach ()
    {
        return limit_reach;
    }

    public void setLimit_reach (String limit_reach)
    {
        this.limit_reach = limit_reach;
    }

    public ArrayList<DoctorAppointmentlistdataPojo>getDatas ()
    {
        return datas;
    }

    public void setDatas (ArrayList<DoctorAppointmentlistdataPojo>datas)
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

    @Override
    public String toString()
    {
        return "ClassPojo [limit_reach = "+limit_reach+", datas = "+datas+", message = "+message+", status = "+status+"]";
    }


}
