package com.app.fitsmile.firebase_chat;

import java.util.List;

public class ChatListFirebaseResponse {
    private String status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private List<ChatDataFirebase> data;

    public List<ChatDataFirebase> getData() {
        return data;
    }

    public void setData(List<ChatDataFirebase> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
