package com.app.fitsmile.response.appointmentdetails;

public class VideoDetailPojo {


        private VideoDetailPojoData datas;

        private String message;

        private String status;

        public VideoDetailPojoData getDatas () {
            return datas;
        }

        public void setDatas (VideoDetailPojoData datas)
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
            return "ClassPojo [datas = "+datas+", message = "+message+", status = "+status+"]";
        }
    }



