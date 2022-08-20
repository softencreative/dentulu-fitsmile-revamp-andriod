package com.app.fitsmile.firebase_chat;

import java.util.List;

public class LegacyChatsResponse {
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
    private List<ChatMessage> chat_history;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ChatMessage> getChat_history() {
        return chat_history;
    }

    public void setChat_history(List<ChatMessage> chat_history) {
        this.chat_history = chat_history;
    }
}
