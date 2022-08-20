package com.app.fitsmile.response.rewards;

import java.util.ArrayList;

public class EarningResponse
    {
        private ArrayList<EarningResult> earnings;

        private String status;

        private String limit_reach;

        public ArrayList<EarningResult> getEarnings ()
        {
            return earnings;
        }

        public void setEarnings ( ArrayList<EarningResult> earnings)
        {
            this.earnings = earnings;
        }

        public String getStatus ()
        {
            return status;
        }

        public void setStatus (String status)
        {
            this.status = status;
        }

        public String getLimit_reach ()
        {
            return limit_reach;
        }

        public void setLimit_reach (String limit_reach)
        {
            this.limit_reach = limit_reach;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [earnings = "+earnings+", status = "+status+", limit_reach = "+limit_reach+"]";
        }
    }





