package com.app.fitsmile.photoConsultation;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String id;
    private String user_id;
    private String step_form_id;
    private String type;
    private String title;
    private String name;
    private List<OptionValue> value;
    private String description;
    private String required;
    private String sort;


    private Bitmap bitmap;
    private String base64Encoded;
    private boolean isSelected;
    private String inputData;
    private boolean isErrorVisible;

    public Question() {
    }

    public Question(String id, String user_id, String step_form_id, String type, String title, String name, List<OptionValue> value, String description, String required, String sort) {
        this.id = id;
        this.user_id = user_id;
        this.step_form_id = step_form_id;
        this.type = type;
        this.title = title;
        this.name = name;
        this.value = value;
        this.description = description;
        this.required = required;
        this.sort = sort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStep_form_id() {
        return step_form_id;
    }

    public void setStep_form_id(String step_form_id) {
        this.step_form_id = step_form_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OptionValue> getValue() {
        return value;
    }

    public void setValue(List<OptionValue> value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public boolean isErrorVisible() {
        return isErrorVisible;
    }

    public void setErrorVisible(boolean errorVisible) {
        isErrorVisible = errorVisible;
    }

    public String getBase64Encoded() {
        return base64Encoded;
    }

    public void setBase64Encoded(String base64Encoded) {
        this.base64Encoded = base64Encoded;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", step_form_id='" + step_form_id + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", description='" + description + '\'' +
                ", required='" + required + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
