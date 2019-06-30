package com.pawstime.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pawstime.Pet;
import com.pawstime.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class SelectPet extends DialogFragment {
    public LayoutInflater inflater;
    private RadioGroup radioGroup;
    public View rootView;

    public interface SelectPetDialogListener {
        void onSelectPetDialogPositiveClick(DialogFragment dialog);
        void onSelectPetDialogNegativeClick(DialogFragment dialog);
        void onSelectPetDialogNeutralClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    SelectPetDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SelectPetDialogListener so we can send events to the host
            listener = (SelectPetDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement AddPetDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = requireActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.select_pet, null);
        radioGroup = rootView.findViewById(R.id.selectPetsRadioGroup);
        ArrayList<String> listOfPets = getPets(rootView.getContext());
        addPets(listOfPets, rootView.getContext());
        inflater.inflate(R.layout.select_pet, radioGroup, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);

        builder.setMessage(R.string.select_pet)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        selectPet();
                        listener.onSelectPetDialogPositiveClick(SelectPet.this);

                    });
        builder.setNeutralButton(R.string.add_new_pet, (dialog, which) -> listener.onSelectPetDialogNeutralClick(SelectPet.this));
        if (!rootView.getContext().toString().contains("HomePage")) {
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                // Cancel
                listener.onSelectPetDialogNegativeClick(SelectPet.this);
            });
        }
        return builder.create();
    }

    ArrayList<String> getPets(Context context) {
        FileReader fr;
        BufferedReader reader;
        String stream;
        ArrayList<String> petList = new ArrayList<>();
        File directory = context.getFilesDir();
        File profile = new File(directory, "profile");

        try {
            fr = new FileReader(profile);
            reader = new BufferedReader(fr);
            while((stream = reader.readLine()) != null) {
                String[] pets = stream.split(",");
                for (String pet: pets) {
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

    void addPets(ArrayList<String> listOfPets, Context context) {
        String currentPet = Pet.getCurrentPetName();
        for (int i = 0; i < listOfPets.size(); i++) {
            RadioButton rb = new RadioButton(context);
            rb.setId(i);
            rb.setText(listOfPets.get(i));
            Log.d("Profile", rb.getText().toString());
            System.out.println("Profile " + rb.getText().toString().equals(currentPet));
            radioGroup.addView(rb);
            if (rb.getText().toString().equals(currentPet)) {
                rb.toggle();
            }
        }
    }

   void selectPet() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedPet = rootView.findViewById(selectedId);
        Pet.setCurrentPetName(selectedPet.getText().toString());
    }
}