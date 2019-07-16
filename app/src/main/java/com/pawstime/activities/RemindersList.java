package com.pawstime.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;

import com.pawstime.ReminderCard;
import com.pawstime.dialogs.AddReminder;
import com.pawstime.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
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

        populateRemindersList();

        addReminder = findViewById(R.id.addReminder);
        addReminder.setOnClickListener(v -> {
            DialogFragment newReminder = new AddReminder();
            newReminder.show(getSupportFragmentManager(), "newReminder");
        });
    }

    public void populateRemindersList() {
//        LinearLayout reminderList = findViewById(R.id.reminderList);
//
//        ArrayList<String> listOfReminders = getRemindersList(this);
//        for (int i = 0; i < listOfReminders.size(); i++) {
//            ReminderCard cardView = new ReminderCard(this);
//            cardView.setReminder(listOfReminders.get(i));
//            reminderList.addView(cardView);
//        }
    }

    public static ArrayList<JSONObject> getRemindersList(Context context) {
        FileReader fr;
        BufferedReader reader;
        String stream;
        ArrayList<JSONObject> remindersList = new ArrayList<>();
        File directory = context.getFilesDir();
        File reminders = new File(directory, "reminders");

        try {
            fr = new FileReader(reminders);
            reader = new BufferedReader(fr);
            while ((stream = reader.readLine()) != null) {
                String[] reminderStringArray = stream.split(",");
                for (String reminder : reminderStringArray) {
                    JSONObject json = (JSONObject) new JSONTokener(reminder).nextValue();
//                    remindersList.add(reminderToJson(reminder));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return remindersList;
    }

    public void setPetProfileUI(String jsonString) {
        JSONObject json;

        try {
            json = (JSONObject) new JSONTokener(jsonString).nextValue(); // Turn the String into JSON

            // Set the values on the UI
            String nameAndType = json.getString("nameAndType");

//            petNameAndType.setText(nameAndType);
//            reminderToJson(json, "description", description);
//            reminderToJson(json, "careInstructions", careInstructions);
//            reminderToJson(json, "medicalInfo", medicalInfo);
//            reminderToJson(json, "preferredVet", preferredVet);
//            reminderToJson(json, "emergencyContact", emergencyContact);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void reminderToJson(String string) {
//        try {
//            editText.setText(json.getString(string));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



    }
}
