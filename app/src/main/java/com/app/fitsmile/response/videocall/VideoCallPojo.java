package com.app.fitsmile.response.videocall;

import java.util.ArrayList;

public class VideoCallPojo {

    private ArrayList<VideoCallPojoData> datas;
    private String message;
    private String status;
    private String duration;
    private String limited;
    private String old_duration;
    private String uidstr;
    private String call_type;
    private String channel_id;
    private String token;
    private Health_record health_record;
    private String video_call_fee;
    private String health_record_exist;
    private String last_health_record_days;
    private String health_record_fill;
    private String limit_duration;

    public String getLimit_duration() {
        return limit_duration;
    }

    public void setLimit_duration(String limit_duration) {
        this.limit_duration = limit_duration;
    }

    public ArrayList<VideoCallPojoData> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<VideoCallPojoData> datas) {
        this.datas = datas;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    public String getOld_duration() {
        return old_duration;
    }

    public void setOld_duration(String old_duration) {
        this.old_duration = old_duration;
    }

    public String getUidstr() {
        return uidstr;
    }

    public void setUidstr(String uidstr) {
        this.uidstr = uidstr;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Health_record getHealth_record() {
        return health_record;
    }

    public void setHealth_record(Health_record health_record) {
        this.health_record = health_record;
    }

    public String getVideo_call_fee() {
        return video_call_fee;
    }

    public void setVideo_call_fee(String video_call_fee) {
        this.video_call_fee = video_call_fee;
    }

    public String getHealth_record_exist() {
        return health_record_exist;
    }

    public void setHealth_record_exist(String health_record_exist) {
        this.health_record_exist = health_record_exist;
    }

    public String getLast_health_record_days() {
        return last_health_record_days;
    }

    public void setLast_health_record_days(String last_health_record_days) {
        this.last_health_record_days = last_health_record_days;
    }

    public String getHealth_record_fill() {
        return health_record_fill;
    }

    public void setHealth_record_fill(String health_record_fill) {
        this.health_record_fill = health_record_fill;
    }

    public class Health_record {
        private String allergies;
        private String medical_procedures;
        private String user_id;
        private String indicate_other_allergies;
        private String family_member_diagnosed;
        private String medications;
        private String appointment_id;
        private String id;
        private String created_date;
        private String category;
        private String diagnosed;
        private String status;

        public String getAllergies () {
            return allergies;
        }

        public void setAllergies (String allergies) {
            this.allergies = allergies;
        }

        public String getMedical_procedures () {
            return medical_procedures;
        }

        public void setMedical_procedures (String medical_procedures) {
            this.medical_procedures = medical_procedures;
        }

        public String getUser_id () {
            return user_id;
        }

        public void setUser_id (String user_id) {
            this.user_id = user_id;
        }

        public String getIndicate_other_allergies () {
            return indicate_other_allergies;
        }

        public void setIndicate_other_allergies (String indicate_other_allergies) {
            this.indicate_other_allergies = indicate_other_allergies;
        }

        public String getFamily_member_diagnosed () {
            return family_member_diagnosed;
        }

        public void setFamily_member_diagnosed (String family_member_diagnosed) {
            this.family_member_diagnosed = family_member_diagnosed;
        }

        public String getMedications () {
            return medications;
        }

        public void setMedications (String medications) {
            this.medications = medications;
        }

        public String getAppointment_id () {
            return appointment_id;
        }

        public void setAppointment_id (String appointment_id) {
            this.appointment_id = appointment_id;
        }

        public String getId () {
            return id;
        }

        public void setId (String id) {
            this.id = id;
        }

        public String getCreated_date () {
            return created_date;
        }

        public void setCreated_date (String created_date) {
            this.created_date = created_date;
        }

        public String getCategory () {
            return category;
        }

        public void setCategory (String category) {
            this.category = category;
        }

        public String getDiagnosed () {
            return diagnosed;
        }

        public void setDiagnosed (String diagnosed) {
            this.diagnosed = diagnosed;
        }

        public String getStatus () {
            return status;
        }

        public void setStatus (String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "ClassPojo [allergies = "+allergies+", medical_procedures = "+medical_procedures+", user_id = "+user_id+", indicate_other_allergies = "+indicate_other_allergies+", family_member_diagnosed = "+family_member_diagnosed+", medications = "+medications+", appointment_id = "+appointment_id+", id = "+id+", created_date = "+created_date+", category = "+category+", diagnosed = "+diagnosed+", status = "+status+"]";
        }
    }


}
