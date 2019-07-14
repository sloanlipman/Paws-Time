package com.pawstime.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.Button;


public class DeleteProfile extends DialogFragment {
    public LayoutInflater inflater;

    public interface DeleteProfileDialogListener {
        void onDeleteProfileDialogPositiveClick(DialogFragment dialog);
        void onDeleteProfileDialogNegativeClick(DialogFragment dialog);
    }

//    // Use this instance of the interface to deliver action events
    DeleteProfileDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SelectPetDialogListener so we can send events to the host
            listener = (DeleteProfileDialogListener) context;
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

        builder.setMessage("Are you sure you want to delete this profile?");
        builder.setPositiveButton("Delete", (dialog, which) -> {});
        builder.setNegativeButton("Cancel", (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                listener.onDeleteProfileDialogPositiveClick(DeleteProfile.this);
                dismiss();
            });

            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> {
                listener.onDeleteProfileDialogNegativeClick(DeleteProfile.this);
                dismiss();
            });

        });
       return dialog;
    }
}