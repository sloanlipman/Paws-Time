package com.pawstime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReminderCard extends CardView {

    public TextView reminder;
    public TextView date;
    public ConstraintLayout layout;
    public CardView reminderCardView;

    public ReminderCard(@NonNull Context context) {
        super(context);
        inflate(getContext(), R.layout.reminder_card_item_layout, this);
        reminder = findViewById(R.id.reminderTv);
        date = findViewById(R.id.dateTv);
        layout = findViewById(R.id.reminderCardLayout);
        reminderCardView = findViewById(R.id.reminderCardItem);
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
}
