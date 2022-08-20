package com.app.fitsmile.photoConsultation;

import java.io.Serializable;
import java.util.List;

public class StepData implements Serializable {
    private String id;
    private String name;
    private String user_id;
    private String description;
    private String short_description;
    private String step;
    private String created_date;
    private List<Question> questions;

    public StepData() {
    }

    public StepData(String id, String name, String user_id, String description, String short_description, String step, String created_date, List<Question> questions) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.description = description;
        this.short_description = short_description;
        this.step = step;
        this.created_date = created_date;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "StepData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", description='" + description + '\'' +
                ", short_description='" + short_description + '\'' +
                ", step='" + step + '\'' +
                ", created_date='" + created_date + '\'' +
                ", questions=" + questions +
                '}';
    }
}
