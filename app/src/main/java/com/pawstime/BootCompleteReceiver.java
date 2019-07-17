package com.pawstime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pawstime.activities.RemindersList;

public class BootCompleteReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        //TODO: Do something here, get list of reminders/alarms and start them
        new RemindersList();
    }
}
