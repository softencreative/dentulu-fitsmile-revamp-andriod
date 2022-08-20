package com.app.fitsmile.response.appointmentlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HealthHistoryQuestion{

    @SerializedName("datas")
    private List<QuestionItem> datas;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public void setData(List<QuestionItem> data){
        this.datas = data;
    }

    public List<QuestionItem> getData(){
        return datas;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public class QuestionItem{
        @SerializedName("options")
        private List<OptionItem> options;

        @SerializedName("page_option")
        private PageOption page_option;

        @SerializedName("page")
        private String page;

        public String getPage(){
            return page;
        }

        public PageOption getPage_option(){
            return page_option;
        }

        public List<OptionItem> getOptions(){
            return options;
        }
    }

    public class OptionItem{
        @SerializedName("text")
        private String text;

        @SerializedName("selected")
        private String selected;

        @SerializedName("type")
        private String type;

        @SerializedName("category")
        private String category;

        public String getText(){
            return text;
        }

        public String getSelected(){
            return selected;
        }

        public void setSelected(String sel){
            this.selected= sel;
        }

        public void setCategory(String cat){
            this.category= cat;
        }

        public String getType(){
            return type;
        }

        public String getCategory(){
            return category;
        }

    }
    public class PageOption{
        @SerializedName("code")
        private String code;

        @SerializedName("answer")
        private String answer;

        public String getCode(){
            return code;
        }

        public String getAnswer(){
            return answer;
        }
    }
}


