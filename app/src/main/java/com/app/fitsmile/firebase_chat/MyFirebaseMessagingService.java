package com.app.fitsmile.firebase_chat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.AppController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_ID;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_NAME;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_PROFILE_PIC;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d("Push notification", remoteMessage.toString());
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        if (((AppController) getApplication()).getAppPreference().isLoggedIn()) {
//            // Check if message contains a data payload.
//            if (remoteMessage.getData().size() > 0) {
//                Log.e("FCM", "data in FCM payload");
//                Map<String, String> data = remoteMessage.getData();
//                if (data.containsKey("senderId")) {
//                    Log.d(TAG, "Message Notification Body: " + data.get("message"));
//                    sendNotification(data.get("title"), data.get("body"), data.get("image"), data.get("senderName"), data.get("senderId"));
//                }
//            } else {
//                Log.e("FCM", "notification in FCM payload");
//            }
//        } else {
//            Log.d("FCM", "User not logged in");
//        }
    }
    // [END receive_message]

    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        FirebaseUtils.sendToken(((AppController) getApplication()).getAppPreference(), token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
   /* private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String senderImage, String senderName, String senderFirebaseUId) {
        Activity currentActivity = ((AppController) getApplication()).getCurrentActivity();
        boolean isOurAppInBackground = (currentActivity == null);
        boolean isChatPageOpened;
        boolean isOpponentChatOpened = false;
        if (currentActivity != null) {
            isChatPageOpened = currentActivity.getClass().getName().equals(ChatActivityFirebase.class.getName());
            if (isChatPageOpened) {
                isOpponentChatOpened = ((ChatActivityFirebase) currentActivity).getOpponentId().equals(senderFirebaseUId);
            }
        }
        if (isOurAppInBackground || !isOpponentChatOpened) {
            showNotification(title, messageBody, senderImage, senderName, senderFirebaseUId);
        }
    }

    public void showNotification(String title, String messageBody, String senderImage, String senderName, String senderFirebaseUId) {
        Intent intent = new Intent(this, ChatActivityFirebase.class);
        intent.putExtra(OPPONENT_ID, senderFirebaseUId);
        intent.putExtra(OPPONENT_NAME, senderName);
        intent.putExtra(OPPONENT_PROFILE_PIC, senderImage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1001, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = senderFirebaseUId;
        long[] vibratePattern = new long[]{1000, 1000};
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_logo_white)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setVibrate(vibratePattern)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(vibratePattern);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1001, notificationBuilder.build());
    }

}
