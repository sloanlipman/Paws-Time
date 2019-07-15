package com.pawstime.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.activities.BaseActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class AddReminder extends DialogFragment {

    private EditText reminderText;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.new_reminder_title)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    // Save new Reminder
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Cancel
                }).setView(R.layout.add_reminder);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                listener.onDialogPositiveClick(AddReminder.this);
                dismiss();
            });

            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> {
                listener.onDialogNegativeClick(AddReminder.this);
                dismiss();
            });
        });
        return dialog;
    }

    public boolean save(String name, String type, Context context) {
        FileOutputStream outputStream;
        File directory = context.getFilesDir();
        File reminders = new File(directory, "reminders");
        ArrayList<String> remindersList = getRemindersList(reminders);

        if (!reminders.exists()) {
            try {
                outputStream = context.openFileOutput("reminders", Context.MODE_PRIVATE);
                outputStream.write("".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If pet is a new pet
        if (!remindersList.contains(name)) {
            ArrayMap<String, String> map = new ArrayMap<>();
            map.put("name", name);
            map.put("type", type);
            JSONObject json = new JSONObject(map);

            try {
                outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
                outputStream.write(json.toString().getBytes());
                outputStream.close(); // Don't forget to close the stream!
                Toast.makeText(context, "Reminder successfully added!", Toast.LENGTH_SHORT).show();

                // If added, write to list of files
                try {

                    StringBuilder pets = new StringBuilder();
                    for (String reminder: remindersList) {
                        pets.append(reminder);
                        pets.append(",");
                    }

                    String petsToWrite = new String(pets);

                    outputStream = context.openFileOutput("profile", Context.MODE_PRIVATE);
                    outputStream.write(petsToWrite.getBytes()); // Write previous pets
                    outputStream.write((name + ",").getBytes()); // Append new pet's name and a separator (comma)
                    outputStream.close();
                    Pet.setCurrentPetName(name);
                    Pet.setCurrentPetType(type);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "A pet with this name already exists! Please enter a unique name", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static ArrayList<String> getRemindersList(File file) {
        FileReader fr;
        BufferedReader reader;
        String stream;
        ArrayList<String> remindersList = new ArrayList<>();

        try {
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            while ((stream = reader.readLine()) != null) {
                String[] reminders = stream.split(",");
                for (String reminder : reminders) {
                    remindersList.add(reminder);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return remindersList;
    }
}
