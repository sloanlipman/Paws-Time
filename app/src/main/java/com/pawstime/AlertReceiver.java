//adapted from https://codinginflow.com/tutorials/android/alarmmanager

package com.pawstime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        int reqCode = intent.getIntExtra("reqCode", 0);
        String message = intent.getStringExtra("message");
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(message);
        notificationHelper.getManager().notify(reqCode, nb.build());
    }
}