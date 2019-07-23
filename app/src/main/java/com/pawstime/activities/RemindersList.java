package com.pawstime.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pawstime.AlertReceiver;
import com.pawstime.R;
import com.pawstime.ReminderCard;
import com.pawstime.dialogs.AddReminder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;


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
        getRemindersList(this);
        populateRemindersList();

        addReminder = findViewById(R.id.addReminder);
        addReminder.setOnClickListener(v -> {
            DialogFragment newReminder = new AddReminder();
            newReminder.show(getSupportFragmentManager(), "newReminder");
        });
    }

    public void populateRemindersList() {
        LinearLayout reminderList = findViewById(R.id.reminderList);
        ArrayList<String> listOfReminders = (getRemindersList(this));

        for (int i = 0; i < listOfReminders.size(); i++) {
            String reminder = listOfReminders.get(i);
            ReminderCard cardView = new ReminderCard(this);
            cardView.setIndex(i);
            try {
                JSONObject json = (JSONObject) new JSONTokener(reminder).nextValue();
                cardView.setReminder(json.getString("message"));
                cardView.setReqCode(Integer.parseInt(json.getString("reqCode")));
                int day = Integer.parseInt(json.getString("day"));
                int month = Integer.parseInt(json.getString("month"));
                int year = Integer.parseInt(json.getString("year"));
                int minute = Integer.parseInt(json.getString("minute"));
                int hour = Integer.parseInt(json.getString("hour"));
                cardView.setDate(month, day, year, hour, minute);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            reminderList.addView(cardView);
        }
    }

    public static ArrayList<String> getRemindersList(Context context) {
        FileReader fr;
        BufferedReader reader;
        String stream;
        ArrayList<String> remindersList = new ArrayList<>();
        File directory = context.getFilesDir();
        File reminders = new File(directory, "reminders");

        try {
            fr = new FileReader(reminders);
            reader = new BufferedReader(fr);
            while ((stream = reader.readLine()) != null) {
                String[] reminderStringArray = stream.split("¿");
                for (String reminder : reminderStringArray) {
                    remindersList.add(reminder);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return remindersList;
    }

    public static void deleteReminder(Context context, int index) {
        FileOutputStream outputStream;
        ArrayList<String> remindersList = getRemindersList(context);
        remindersList.remove(remindersList.get(index));

        try {

            StringBuilder reminderSb = new StringBuilder();
            for (String reminder: remindersList) {
                reminderSb.append(reminder);
                reminderSb.append("¿");
            }

            String reminderToWrite = new String(reminderSb);

            outputStream = context.openFileOutput("reminders", Context.MODE_PRIVATE);
            outputStream.write(reminderToWrite.getBytes()); // Write previous reminders
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void cancelAlarm(Context c, int reqCode) {
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(c, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, reqCode, intent, 0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(c, "Alarm Canceled", Toast.LENGTH_LONG).show();
    }

}
