package com.app.fitsmile.firebase_chat;

import android.view.View;
import android.widget.Toast;

import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.app.AppPreference;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_FILE;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_IMAGE;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_TEXT;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_VIDEO;
import static com.app.fitsmile.firebase_chat.ChatConstant.DEVICE_TOKENS;
import static com.app.fitsmile.firebase_chat.ChatConstant.IS_TYPING;
import static com.app.fitsmile.firebase_chat.ChatConstant.MESSAGES;
import static com.app.fitsmile.firebase_chat.ChatConstant.OFFLINE;
import static com.app.fitsmile.firebase_chat.ChatConstant.ONLINE;
import static com.app.fitsmile.firebase_chat.ChatConstant.STATUS;

public class FirebaseUtils {

    public static void sendTypingStarted(BaseActivity activity, String opponentId) {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            Typing typing = new Typing();
            typing.setTyping(true);
            typing.setTypingTo(opponentId);
            AppController.getDatabaseReference().child(IS_TYPING).child(activity.appPreference.getFirebaseUID()).setValue(typing, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void sendTypingStopped(BaseActivity activity) {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            AppController.getDatabaseReference().child(IS_TYPING).child(activity.appPreference.getFirebaseUID()).getRef().child("typing").setValue(false, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static DatabaseReference listenToOpponentTyping(BaseActivity activity, String opponentId) {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            return AppController.getDatabaseReference().child(IS_TYPING).child(opponentId);
        }
        return null;
    }

    public static String getRoomName(AppPreference appPreference, String opponentFirebaseUID) {
        String roomName;
        String myFirebaseUID = appPreference.getFirebaseUID();
        if (myFirebaseUID.compareTo(opponentFirebaseUID) > 0) {
            roomName = opponentFirebaseUID + myFirebaseUID;
        } else {
            roomName = myFirebaseUID + opponentFirebaseUID;
        }
        return roomName;
    }

    public static void sendTextMessage(BaseActivity activity, String message, String opponentId/*, Card opponentPublicKey*/) {
        /*if (opponentPublicKey != null) {*/
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(message);
            chatMessage.setContentType(CONTENT_TYPE_TEXT);
            chatMessage.setReciverId(opponentId);
            chatMessage.setSenderId(activity.appPreference.getFirebaseUID());
            chatMessage.setTimestamp(System.currentTimeMillis());

            AppController.getDatabaseReference().child(MESSAGES).child(getRoomName(activity.appPreference, opponentId)).push().setValue(chatMessage, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    sendNotificationForChats(activity,CONTENT_TYPE_TEXT,opponentId);
                }
            });
            /*((AppController) activity.getApplication()).encryptMessage(message, opponentId, new EncryptionListener() {
                @Override
                public void onEncryptionComplete(String encryptedMessage) {

                }

                @Override
                public void onError(String error) {
                    showToast(activity, "Unable to encrypt message");
                }
            });*/
        }
//        }
    }

    public static void sendImageMessage(BaseActivity activity, String opponentId, String imageName/*, Card opponentPublicKey*/) {
//        if (opponentPublicKey != null) {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContentType(CONTENT_TYPE_IMAGE);
            chatMessage.setMessage(imageName);
            chatMessage.setReciverId(opponentId);
            chatMessage.setSenderId(activity.appPreference.getFirebaseUID());
            chatMessage.setTimestamp(System.currentTimeMillis());

            AppController.getDatabaseReference().child(MESSAGES).child(getRoomName(activity.appPreference, opponentId)).push().setValue(chatMessage, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    sendNotificationForChats(activity,CONTENT_TYPE_IMAGE,opponentId);
                }
            });
                /*((AppController) activity.getApplication()).encryptMessage(imageName, opponentId, new EncryptionListener() {
                    @Override
                    public void onEncryptionComplete(String encryptedMessage) {

                    }

                    @Override
                    public void onError(String error) {
                        showToast(activity, "Unable to encrypt message");
                    }
                });*/
        }
//        }
    }

    public static void sendVideoMessage(BaseActivity activity, String opponentId, String videoName/*, Card opponentPublicKey*/) {
//        if (opponentPublicKey != null) {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(videoName);
            chatMessage.setContentType(CONTENT_TYPE_VIDEO);
            chatMessage.setReciverId(opponentId);
            chatMessage.setSenderId(activity.appPreference.getFirebaseUID());
            chatMessage.setTimestamp(System.currentTimeMillis());

            AppController.getDatabaseReference().child(MESSAGES).child(getRoomName(activity.appPreference, opponentId)).push().setValue(chatMessage, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    sendNotificationForChats(activity,CONTENT_TYPE_VIDEO,opponentId);
                }
            });
                /*((AppController) activity.getApplication()).encryptMessage(videoName, opponentId, new EncryptionListener() {
                    @Override
                    public void onEncryptionComplete(String encryptedMessage) {

                    }

                    @Override
                    public void onError(String error) {
                        showToast(activity, "Unable to encrypt message");
                    }
                });*/
        }
//        }
    }

    public static void sendFileMessage(BaseActivity activity, String opponentId, String fileName/*, Card opponentPublicKey*/) {
//        if (opponentPublicKey != null) {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && (activity.appPreference.isLoggedIn())) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(fileName);
            chatMessage.setContentType(CONTENT_TYPE_FILE);
            chatMessage.setReciverId(opponentId);
            chatMessage.setSenderId(activity.appPreference.getFirebaseUID());
            chatMessage.setTimestamp(System.currentTimeMillis());

            AppController.getDatabaseReference().child(MESSAGES).child(getRoomName(activity.appPreference, opponentId)).push().setValue(chatMessage, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    sendNotificationForChats(activity,CONTENT_TYPE_FILE,opponentId);
                }
            });
                /*((AppController) activity.getApplication()).encryptMessage(fileName, opponentId, new EncryptionListener() {
                    @Override
                    public void onEncryptionComplete(String encryptedMessage) {

                    }

                    @Override
                    public void onError(String error) {
                        showToast(activity, "Unable to encrypt message");
                    }
                });*/
        }
//        }
    }

    public static void setMessageRead(AppPreference appPreference, String uniqueKey, String opponentId) {
        if (uniqueKey != null) {
            if (!appPreference.getFirebaseUID().isEmpty() && (appPreference.isLoggedIn())) {
                AppController.getDatabaseReference().child(MESSAGES).child(getRoomName(appPreference, opponentId)).child(uniqueKey).getRef().child("isRead").setValue(1);
            }
        }
    }

    public static void sendToken(AppPreference appPreference, String tokenId) {
        AppController.setCurrentFCMDeviceToken(tokenId);
        if (!appPreference.getFirebaseUID().isEmpty() && (appPreference.isLoggedIn())) {
            AppController.getDatabaseReference().child(DEVICE_TOKENS).child(appPreference.getFirebaseUID()).getRef().child("token_id").setValue(tokenId);
        }
    }

    public static void clearToken(AppPreference appPreference) {
        if (!appPreference.getFirebaseUID().isEmpty() && (appPreference.isLoggedIn())) {
            AppController.getDatabaseReference().child(DEVICE_TOKENS).child(appPreference.getFirebaseUID()).getRef().child("token_id").setValue("");
        }
    }

    public static void setOnline(AppPreference appPreference) {
        if (!appPreference.getFirebaseUID().isEmpty() && (appPreference.isLoggedIn())) {
            AppController.getDatabaseReference().child(STATUS).child(appPreference.getFirebaseUID()).getRef().child("last_changed").setValue(System.currentTimeMillis());
            AppController.getDatabaseReference().child(STATUS).child(appPreference.getFirebaseUID()).getRef().child("state").setValue(ONLINE);
        }
    }

    public static void setOffline(AppPreference appPreference) {
        if (!appPreference.getFirebaseUID().isEmpty() && (appPreference.isLoggedIn())) {
            AppController.getDatabaseReference().child(STATUS).child(appPreference.getFirebaseUID()).getRef().child("last_changed").setValue(System.currentTimeMillis());
            AppController.getDatabaseReference().child(STATUS).child(appPreference.getFirebaseUID()).getRef().child("state").setValue(OFFLINE);
        }
    }

    public static void sendNotificationForChats(BaseActivity activity,String messageType,String opponentId) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("reciverId", opponentId);
        jsonObj.addProperty("senderId",activity.appPreference.getFirebaseUID());
        jsonObj.addProperty("type", messageType);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(activity).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = activity.retrofit.create(ApiInterface.class);
        Call<LegacyChatsResponse> mService;
        mService = mApiService.sendOneSignalNotificationForChat(jsonObj);
        mService.enqueue(new Callback<LegacyChatsResponse>() {
            @Override
            public void onResponse(Call<LegacyChatsResponse> call, Response<LegacyChatsResponse> response) {
                LegacyChatsResponse res = response.body();
                if (res != null) {
                    if (res.getStatus().equals("1")) {
                    } else if (res.getStatus().equals("0")) {
                        //No old chats

                    } else if (res.getStatus().equals("401")) {
                        Utils.showSessionAlert(activity);
                    } else {
                        showToast(activity, "" + res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LegacyChatsResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

}
