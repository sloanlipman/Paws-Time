package com.pawstime.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;

import com.pawstime.dialogs.AddReminder;
import com.pawstime.R;

public class RemindersList extends BaseActivity implements AddReminder.AddReminderListener {
    FloatingActionButton addReminder;

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        startActivity(getIntent());
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

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
