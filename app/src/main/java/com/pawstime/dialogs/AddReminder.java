package com.pawstime.dialogs;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pawstime.AlertReceiver;
import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.activities.BaseActivity;
import com.pawstime.activities.RemindersList;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class AddReminder extends DialogFragment {

    private EditText reminderText;
    private String reminderString;

    private DatePicker datePicker;
    private TimePicker timePicker;

    public LayoutInflater inflater;
    public View rootView;
    public static int REQ_CODE = 5;//Request Code For Alarm

    public interface AddReminderListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AddReminderListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddReminderListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement AddReminderDialogListener");
        }
    }

    @NonNull
    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflater = requireActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.add_reminder, null);

        datePicker = rootView.findViewById(R.id.datePicker);
        timePicker = rootView.findViewById(R.id.timePicker);
        reminderText = rootView.findViewById(R.id.reminderText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.new_reminder_title)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    // Save new Reminder
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Cancel
                }).setView(rootView);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                reminderString = reminderText.getText().toString();
                if (reminderString.length() > 0) {
                    if(save(reminderString, rootView.getContext())) {
                        listener.onDialogPositiveClick(AddReminder.this);
//                      dismiss();
                    }
                } else {
                    Toast.makeText(rootView.getContext(), "Cannot save empty reminder.", Toast.LENGTH_SHORT).show();
                }
            });

            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> {
                listener.onDialogNegativeClick(AddReminder.this);
                dismiss();
            });
        });
        return dialog;
    }

    public boolean save(String reminderString, Context context) {
        FileOutputStream outputStream;
        File directory = context.getFilesDir();
        File reminders = new File(directory, "reminders");
        ArrayList<String> remindersList = RemindersList.getRemindersList(context);

        if (!reminders.exists()) {
            try {
                outputStream = context.openFileOutput("reminders", Context.MODE_PRIVATE);
                outputStream.write("".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String day = datePicker.getDayOfMonth() + "";
        String month = datePicker.getMonth() + "";
        String year = datePicker.getYear() + "";

        String hour = timePicker.getCurrentHour() + "";
        String minute = timePicker.getCurrentMinute() + "";

        onTimeSet(Integer.parseInt(hour), Integer.parseInt(minute)); //Sets time for alarm

        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("message", reminderString);
        map.put("day", day);
        map.put("month", month);
        map.put("year", year);
        map.put("hour", hour);
        map.put("minute", minute);


        JSONObject json = new JSONObject(map);

        try {
            outputStream = context.openFileOutput("reminders", Context.MODE_PRIVATE);
            outputStream.close(); // Don't forget to close the stream!
            Toast.makeText(context, "Reminder successfully added!", Toast.LENGTH_SHORT).show();

            // If added, write to list of files
            try {


                StringBuilder reminderSb = new StringBuilder();
                for (String reminder: remindersList) {
                    reminderSb.append(reminder);
                    reminderSb.append("¿");
                }

                String reminderToWrite = new String(reminderSb);

                outputStream = context.openFileOutput("reminders", Context.MODE_PRIVATE);
                outputStream.write(reminderToWrite.getBytes()); // Write previous reminders
                outputStream.write((json.toString() + "¿").getBytes()); // Append reminder and add separating comma
                outputStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    //adapted from https://codinginflow.com/tutorials/android/alarmmanager
    @TargetApi(Build.VERSION_CODES.N)
    public void onTimeSet(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);


        //Prevents you from setting a reminder alarm for a past date
        if (c.before(Calendar.getInstance())) {
            Toast.makeText(getActivity(), "Invalid Date, Please Try Again.", Toast.LENGTH_LONG).show();
        }
        else{
            startAlarm(c);
        }
        reqCode();//Creates ID for alarm
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), REQ_CODE, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public static void reqCode(){
//        int request = (int)(System.currentTimeMillis());
//        REQ_CODE = request;
    }

    public static void cancelAlarm(Context c) {
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(c, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, REQ_CODE, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(c, "Alarm Cancelled", Toast.LENGTH_LONG).show();
    }

}
