package com.app.fitsmile.firebase_chat;

import com.app.fitsmile.app.AppPreference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatMessage {
    @Exclude
    public String uniqueKey;
    @Exclude
    public boolean isLegacyChatMessage = false;

    private String contentType;
    private int isRead = 0;
    private String message;
    private String reciverId;
    private String senderId;
    private long timestamp;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMessage() {
        if (message != null) {
            return message;
        } else {
            return "";
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMyMessage(AppPreference appPreference) {
        String myFirebaseUId = appPreference.getFirebaseUID();
        return senderId.equals(myFirebaseUId);
    }

    public boolean isOpponentMessage(AppPreference appPreference) {
        String myFirebaseUId = appPreference.getFirebaseUID();
        if (myFirebaseUId != null && reciverId != null) {
            return reciverId.equals(myFirebaseUId);
        } else {
            return false;
        }
    }
}
