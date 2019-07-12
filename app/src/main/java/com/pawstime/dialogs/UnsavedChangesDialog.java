package com.pawstime.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.Button;

import com.pawstime.activities.BaseActivity;


public class UnsavedChangesDialog extends DialogFragment {
    public LayoutInflater inflater;

    public interface UnsavedChangesDialogListener {
        void onUnsavedChangesDialogPositiveClick(DialogFragment dialog);
        void onUnsavedChangesDialogNegativeClick(DialogFragment dialog);
        void onUnsavedChangesDialogNeutralClick(DialogFragment dialog);
    }

//    // Use this instance of the interface to deliver action events
    UnsavedChangesDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SelectPetDialogListener so we can send events to the host
            listener = (UnsavedChangesDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement AddPetDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = requireActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("You have unsaved changes. Do you want to discard them?");
        builder.setPositiveButton("Save", (dialog, which) -> {});
        builder.setNeutralButton("Cancel", (dialog, which) -> {});
        builder.setNegativeButton("Discard", (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                listener.onUnsavedChangesDialogPositiveClick(UnsavedChangesDialog.this);
                dismiss();
            });

            Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutral.setOnClickListener(v -> {
                listener.onUnsavedChangesDialogNeutralClick(UnsavedChangesDialog.this);
                dismiss();
            });

            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> {
                listener.onUnsavedChangesDialogNegativeClick(UnsavedChangesDialog.this);
                dismiss();
            });

        });
       return dialog;
    }
}