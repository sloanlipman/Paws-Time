package com.pawstime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;

public class RemindersList extends BaseActivity {
    FloatingActionButton addReminder;
    @Override
    public int getContentViewId() {
        return R.layout.reminders_list;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_reminders;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.toolbar_reminders;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addReminder = findViewById(R.id.addReminder);
        addReminder.setOnClickListener(v -> {
            DialogFragment newReminder = new AddReminder();
            newReminder.show(getSupportFragmentManager(), "newReminder");
        });
    }

}
