package com.app.fitsmile.photoConsultation;

import java.io.Serializable;

public class OptionValue implements Serializable {
    private String title;
    private String value;
    private boolean isChecked;

    public OptionValue() {
    }

    public OptionValue(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "OptionValue{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
