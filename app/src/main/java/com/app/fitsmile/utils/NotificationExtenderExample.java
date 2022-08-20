package com.app.fitsmile.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.fitsmile.app.AppController;
import com.app.fitsmile.appointment.VideoCallActivity;
import com.app.fitsmile.appointment.VideoCallStartActivity;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationExtenderExample extends NotificationExtenderService {
   @Override
   protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
      // Read Properties from result
      OverrideSettings overrideSettings = new OverrideSettings();
//      overrideSettings.extender = new NotificationCompat.Extender() {
//         @Override
//         public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
//            if (!receivedResult.restoring) {
//               // Only set custom channel if notification is not being restored
//               // Note: this would override any channels set through the OneSignal dashboard
//               return builder.setChannelId("News");
//            }
//            return builder.setChannelId("VideoCall");
//         }
//
//
//      };

      OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
      Log.e("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);
      Log.e("OneSignalExample", "Notification displayed with id: " + receivedResult);
      try {
         Log.d("Notification", "Patient App " + receivedResult.payload);
         JSONObject data = receivedResult.payload.additionalData;
         String type = data.optString("type");
         if (type.equals("videoCallRequest")) {
            Log.d(">>>Raman>>>", ">>>>>>>>>>>>>>>>>>>>>>>>videoCallRequest>>");
            String strToken = null;
            try {
               strToken = receivedResult.payload.additionalData.getString("token");
               String strChannel = receivedResult.payload.additionalData.getString("channel_id");
               String strSender = receivedResult.payload.additionalData.getString("caller_id");
               String strReceiver = receivedResult.payload.additionalData.getString("uidstr");
               String strFname = receivedResult.payload.additionalData.getString("first_name");
               String strLname = receivedResult.payload.additionalData.getString("last_name");
               String is_emergency = receivedResult.payload.additionalData.getString("is_emergency");
               String limitedDuration = receivedResult.payload.additionalData.getString("limitedDuration");
               String strVideoId = receivedResult.payload.additionalData.getString("appointment_id");
               AppController.Name = strFname + " " + strLname;
               AppController.getInstance().getAppPreference().setAgoraCallDetails(strToken, strChannel, strSender, strReceiver, strFname, strLname, strVideoId, is_emergency, limitedDuration);
            } catch (JSONException e) {
               Log.e("Raman....2","ntoficatation_catch");

               e.printStackTrace();
            }
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
           // notifManager.cancelAll();
            Log.e("Raman....2","receive");
            Intent intent = new Intent(this, VideoCallStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

         } else if (type.equals("videoCallReject")) {
            if (VideoCallStartActivity.activity != null) {
               Log.d(">>>Raman>>>", ">>>>>>>>>>>>>>>>>>>>>>>>videoCallReject_if>>");
               VideoCallStartActivity.activity.finish();
            }
            if (VideoCallActivity.activity != null) {
               Log.d(">>>Raman>>>", ">>>>>>>>>>>>>>>>>>>>>>>>videoCallReject_if_>>");
               VideoCallActivity.activity.finish();
               VideoCallActivity.decline = true;
               VideoCallActivity.testing = false;
            }
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
         }
        // OneSignal.cancelNotification(displayedResult.androidNotificationId);

      } catch (Exception e) {
         e.printStackTrace();
         Log.e("Raman","ntoficatation_catch");
      }
      // Return true to stop the notification from displaying
      return false;
   }
}