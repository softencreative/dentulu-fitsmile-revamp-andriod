package com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy;

import com.app.fitsmile.photoConsultation.StepData;

import java.io.Serializable;
import java.util.List;

public class DynamicViewResponse implements Serializable {
    private int status;
    private String message;
    private List<StepData> data;

    public DynamicViewResponse() {
    }

    public DynamicViewResponse(int status, List<StepData> data) {
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<StepData> getData() {
        return data;
    }

    public void setData(List<StepData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DynamicViewResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
