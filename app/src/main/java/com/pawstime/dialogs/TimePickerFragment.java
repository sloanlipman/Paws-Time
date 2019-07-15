//package com.pawstime.dialogs;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.pawstime.R;
//
//public class TimePickerFragment extends DialogFragment {
//
//    public LayoutInflater inflater;
//    public View rootView;
//
//    public interface TimePickerListener {
//        void onTimePositiveClick(DialogFragment dialog);
//        void onTimeNegativeClick(DialogFragment dialog);
//    }
//
//    TimePickerListener listener;
//
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            listener = (TimePickerListener) context;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException("Activity must implement AddReminderDialogListener");
//        }
//    }
//
//    @NonNull
//    @Override
//
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        inflater = requireActivity().getLayoutInflater();
//        rootView = inflater.inflate(R.layout.add_pet, null);
//
//        builder.setMessage(R.string.add_new_pet);
//        builder.setView(rootView);
//
//        builder.setPositiveButton(R.string.save, (dialog, which) -> {});
//        if (!rootView.getContext().toString().contains("HomePage")) {
//            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {});
//        }
//
//        AlertDialog dialog = builder.create(); // Create the dialog
////        dialog.show(); // Show the dialog
//
//        return dialog;
//    }
//}
