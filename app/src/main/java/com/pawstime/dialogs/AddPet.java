package com.pawstime.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.activities.PetProfile;

import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class AddPet extends DialogFragment{
    public interface AddPetDialogListener {
        void onAddPetDialogPositiveClick(DialogFragment dialog);
        void onAddPetDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AddPetDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AddPetDialogListener so we can send events to the host
            listener = (AddPetDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement AddPetDialogListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.add_pet, null);
        EditText name = v.findViewById(R.id.newPetName);
        EditText type = v.findViewById(R.id.newPetType);

        builder.setMessage(R.string.add_new_pet);
        builder.setView(v);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            listener.onAddPetDialogPositiveClick(AddPet.this);
            save(name.getText().toString(), type.getText().toString(), v.getContext());
        });
        if (!v.getContext().toString().contains("HomePage")) {
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                listener.onAddPetDialogNegativeClick(AddPet.this);

            });
        }
        return builder.create();
    }

    public static void save(String name, String type, Context context) {


        FileOutputStream outputStream;
        File directory = context.getFilesDir();
        File profile = new File(directory, "profile");
        ArrayList<String> petList = getPetList(profile);

        if (!profile.exists()) {
            System.out.println("File doesn't exist");
            try {
                outputStream = context.openFileOutput("profile", Context.MODE_PRIVATE);
                outputStream.write("".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    // If pet is a new pet
        if (!petList.contains(name)) {
            ArrayMap<String, String> map = new ArrayMap<>();
            map.put("name", name);
            map.put("type", type);
            JSONObject json = new JSONObject(map);

            try {
                System.out.println("Writing " + json.toString());
                outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
                outputStream.write(json.toString().getBytes());
                outputStream.close(); // Don't forget to close the stream!

                Toast.makeText(context, "Pet successfully added!", Toast.LENGTH_SHORT).show();

            // If added, write to list of files
                try {

                    StringBuilder pets = new StringBuilder();
                    for (String pet: petList) {
                        pets.append(pet);
                        pets.append(",");
                    }

                    String petsToWrite = new String(pets);
                    System.out.println("Writing " + petsToWrite);

                    outputStream = context.openFileOutput("profile", Context.MODE_PRIVATE);
                    outputStream.write(petsToWrite.getBytes()); // Write previous pets
                    outputStream.write((name + ",").getBytes()); // Append new pet's name and a separator (comma)
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
            Pet.setCurrentPetName(name);
            Pet.setCurrentPetType(type);

        } else {
            Toast.makeText(context, "A pet with this name already exists! Please enter a unique name", Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<String> getPetList(File file) {
        FileReader fr;
        BufferedReader reader;
        String stream;
        ArrayList<String> petList = new ArrayList<>();

        try {
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            while ((stream = reader.readLine()) != null) {
                String[] pets = stream.split(",");
                for (String pet : pets) {
                    System.out.println(pet);
                    petList.add(pet);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return petList;
    }
}
