//package com.pawstime.dialogs;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//
//public class DatePickerFragment extends DialogFragment {
//
//    public LayoutInflater inflater;
//    public View rootView;
//
//    public interface DatePickerListener {
//        void onDatePositiveClick(DialogFragment dialog);
//        void onDateNegativeClick(DialogFragment dialog);
//    }
//
//    DatePickerListener listener;
//
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            listener = (DatePickerListener) context;
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
//        return null;
//    }
//}
