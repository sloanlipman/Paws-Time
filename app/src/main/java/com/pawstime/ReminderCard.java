package com.pawstime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.pawstime.activities.RemindersList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReminderCard extends CardView {

    public TextView reminder;
    public TextView date;
    public ConstraintLayout layout;
    public CardView reminderCardView;
    public int index, reqCode;

    public ReminderCard(@NonNull Context context) {
        super(context);
        inflate(getContext(), R.layout.reminder_card_item_layout, this);
        reminder = findViewById(R.id.reminderTv);
        date = findViewById(R.id.dateTv);
        layout = findViewById(R.id.reminderCardLayout);
        reminderCardView = findViewById(R.id.petCard);

        Activity activity = (Activity) context;

        reminderCardView.setOnLongClickListener(view -> {
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
            deleteBuilder.setMessage("Delete reminder?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Delete reminder
                        RemindersList.deleteReminder(context, index);
                        context.startActivity(new Intent(context, RemindersList.class));
                        activity.finish();

                        //Delete alarm
                        RemindersList.cancelAlarm(context, reqCode);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        // Cancel deletion
                    });
            deleteBuilder.show();
            return true;
        });
    }

    public void setReminder(String newReminder) {
        reminder.setText(newReminder);
    }

    public void setDate(int month, int day, int year, int hour, int minute) {

        Calendar cal = Calendar.getInstance();
        cal.set(year + 1900, month, day, hour, minute);
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
        date.setText((dateFormat.format(cal.getTime())));
    }

    public void setIndex (int i) {
        index = i;
    }

    public void setReqCode(int i) {
        reqCode = i;
    }
}
