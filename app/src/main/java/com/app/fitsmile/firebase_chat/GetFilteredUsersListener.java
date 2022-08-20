package com.app.fitsmile.firebase_chat;

import java.util.List;

public interface GetFilteredUsersListener {
    void onFiltered(List<ChatDataFirebase> allData);
}
