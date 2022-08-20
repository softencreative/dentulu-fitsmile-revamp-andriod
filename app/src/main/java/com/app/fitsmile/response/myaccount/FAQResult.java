package com.app.fitsmile.response.myaccount;

import com.google.gson.annotations.SerializedName;

public class FAQResult  {

    @SerializedName("id")
    private String id;
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private String answer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String qualification) {
        this.question = qualification;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String image) {
        this.answer = image;
    }

}

