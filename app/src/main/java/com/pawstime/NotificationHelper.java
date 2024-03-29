//adapted from https://codinginflow.com/tutorials/android/alarmmanager

package com.pawstime;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.pawstime.activities.RemindersList;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "pawsTimeReminders";
    public static final String channelName = "Paws Time Reminder";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String message) {
        Intent resultIntent = new Intent(this, RemindersList.class);
        int reqCode = resultIntent.getIntExtra("reqCode", 0);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, reqCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Paws Time Alarm!")
                .setContentText(message)
                .setSmallIcon(R.drawable.baseline_alarm_black_18dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_paws_time))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }
}