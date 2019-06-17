package com.pawstime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

public class AddReminder extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.new_reminder_title)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    // Save new Reminder
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Cancel
                }).setView(R.layout.add_reminder);
    // On passing events back to where the dialog was called from, see https://developer.android.com/guide/topics/ui/dialogs#PassingEvents
        return builder.create();
    }
}
