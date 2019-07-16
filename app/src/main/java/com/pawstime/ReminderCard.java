package com.pawstime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pawstime.activities.HomePage;

public class ReminderCard extends CardView {
    public TextView reminder;
    public ConstraintLayout layout;
    public CardView reminderCardView;
    public ReminderCard(@NonNull Context context) {
        super(context);
        inflate(getContext(), R.layout.reminder_card_item_layout, this);
        reminder = findViewById(R.id.reminderCardItemName);
        layout = findViewById(R.id.reminderCardLayout);
        reminderCardView = findViewById(R.id.reminderCardItem);


//        reminderCardView.setOnClickListener(v -> click(context));
//        reminder.setOnClickListener(v -> click(context));
    }

    public void setReminder(String newReminder) {
        reminder.setText(newReminder);
    }

    public String getReminder() {
        return reminder.getText().toString();
    }

//    public void click(Context context) {
//        HomePage.clickPetCard(getReminder(), context);
//    }
}
