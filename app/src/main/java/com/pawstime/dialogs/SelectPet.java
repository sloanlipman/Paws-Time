package com.pawstime.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.activities.BaseActivity;

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
        ArrayList<String> listOfPets = BaseActivity.getPetsList(rootView.getContext());
        addPets(listOfPets, rootView.getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);

        builder.setMessage(R.string.select_pet)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {});
        builder.setNeutralButton(R.string.add_new_pet, (dialog, which) -> listener.onSelectPetDialogNeutralClick(SelectPet.this));

        if (!rootView.getContext().toString().contains("HomePage")) { // Only show the Add Pet button on Home Page
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {});
        }
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                listener.onSelectPetDialogPositiveClick(SelectPet.this);
                if (selectPet()) {
                    dismiss();
                } else {
                    Toast.makeText(rootView.getContext(), "Please select a pet or add a new one!", Toast.LENGTH_SHORT).show();
                }
            });
        });
       return dialog;
    }

    void addPets(ArrayList<String> listOfPets, Context context) {
        String currentPet = Pet.getCurrentPet();
        for (int i = 0; i < listOfPets.size(); i++) {
            RadioButton rb = new RadioButton(context);
            rb.setId(i);
            rb.setText(listOfPets.get(i));
            radioGroup.addView(rb);
            if (rb.getText().toString().equals(currentPet)) {
                rb.toggle();
            }
        }
    }

   public boolean selectPet() {
        if(radioGroup.getCheckedRadioButtonId() != -1) {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedPet = rootView.findViewById(selectedId);
            Pet.setCurrentPet(selectedPet.getText().toString());
            return true;
        }
        return false;
    }
}