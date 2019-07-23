package com.pawstime.dialogs;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pawstime.AlertReceiver;
import com.pawstime.R;
import com.pawstime.activities.RemindersList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AddReminder extends DialogFragment {

    private EditText reminderText;
    private String reminderString;

    private DatePicker datePicker;
    private TimePicker timePicker;

    public LayoutInflater inflater;
    public View rootView;

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
        int reqCode = getReqCode(context);
        String day = datePicker.getDayOfMonth() + "";
        String month = datePicker.getMonth() + "";
        String year = datePicker.getYear() + "";

        String hour = timePicker.getCurrentHour() + "";
        String minute = timePicker.getCurrentMinute() + "";

        onTimeSet(context, Integer.parseInt(hour), Integer.parseInt(minute), reqCode, reminderString); //Sets time for alarm

        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("message", reminderString);
        map.put("day", day);
        map.put("month", month);
        map.put("year", year);
        map.put("hour", hour);
        map.put("minute", minute);
        map.put("reqCode", String.valueOf(reqCode));

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
    public static void onTimeSet(Context context, int hourOfDay, int minute, int reqCode, String message) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        //Prevents you from setting a reminder alarm for a past date
        if (cal.before(Calendar.getInstance())) {
            Toast.makeText(context, "Invalid Date, Please Try Again.", Toast.LENGTH_LONG).show();
        }
        else{
            startAlarm(context, cal, reqCode, message);
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static void startAlarm(Context context, Calendar cal, int reqCode, String message) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("reqCode", reqCode);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }


    public static int getReqCode(Context context) {
        int highestReq = 0;
        ArrayList<String> remindersList = RemindersList.getRemindersList(context);
        if (remindersList.size() > 0) {
            for (int i = 0; i < remindersList.size(); i++) {
                String reminder = remindersList.get(i);
                try {
                    int reqCode;
                    JSONObject json = (JSONObject) new JSONTokener(reminder).nextValue();
                    reqCode = Integer.parseInt(json.getString("reqCode"));
                    if (reqCode > highestReq) {
                        highestReq = reqCode;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return (highestReq + 1);
    }
}
