package com.app.fitsmile.fitsreminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.AppPreference;
import com.app.fitsmile.appointment.ReminderDetailsActivity;

public  class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();

    // Start and end times in milliseconds
    int seconds;

    // Is the service tracking time?
    private boolean isTimerRunning;

    // Foreground notification id
    private static final int NOTIFICATION_ID = 1;

    // Service binder
    private final IBinder serviceBinder = new RunServiceBinder();
    AppPreference appPreference;

    public class RunServiceBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        appPreference=new AppPreference(this);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Creating service");
        }
        isTimerRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting service");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Binding service");
        }
        seconds=intent.getIntExtra("Seconds",0);
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Destroying service");
        }
    }

    /**
     * Starts the timer
     */
    public void startTimer() {
        isTimerRunning=true;
        if (isTimerRunning) {
            final Handler handler
                    = new Handler();
            handler.post(new Runnable() {
                @Override

                public void run() {


                    // Format the seconds into hours, minutes,
                    // and seconds.


                    if (isTimerRunning) {
                        seconds++;
                    }
                    appPreference.setSeconds(seconds);

                    // Post the code again
                    // with a delay of 1 second.
                    handler.postDelayed(this, 1000);

                }

            });
        }
        else {
            Log.e(TAG, "startTimer request for an already running timer");
        }
    }

    /**
     * Stops the timer
     */
    public void stopTimer() {
        if (isTimerRunning) {
            isTimerRunning = false;
        }
        else {
            Log.e(TAG, "stopTimer request for a timer that isn't running");
        }
    }

    /**
     * @return whether the timer is running
     */
    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    /**
     * Returns the  elapsed time
     *
     * @return the elapsed time in seconds
     */
    public int elapsedTime() {


        return  seconds;
    }

    /**
     * Place the service into the foreground
     */
    public void foreground() {
        startForeground(NOTIFICATION_ID, createNotification());
    }

    /**
     * Return the service to the background
     */
    public void background() {
        stopForeground(true);
    }

    /**
     * Creates a notification for placing the service into the foreground
     *
     * @return a notification for interacting with the service when in the foreground
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.timer_active))
                .setContentText(getResources().getString(R.string.tap_to_return_to_timer))
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, ReminderDetailsActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }
}