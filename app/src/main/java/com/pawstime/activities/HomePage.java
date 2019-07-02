package com.pawstime.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.pawstime.Pet;
import com.pawstime.PetCardView;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;


import java.io.File;

import java.util.ArrayList;


public class HomePage extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener {
    ImageView homeLogo;

    @Override
    public void onAddPetDialogPositiveClick(DialogFragment dialog) {
        Intent i = new Intent(getBaseContext(), PetProfile.class);
        startActivity(i);
        System.out.println("After populating");
//        startActivity(getIntent());
        finish(); // TODO see if adding this closes the app or not

    }

    @Override
    public void onAddPetDialogNegativeClick(DialogFragment dialog) {
        System.out.println("Clicked negative");
    }

    @Override
    public void onSelectPetDialogPositiveClick(DialogFragment dialog) {}

    @Override
    public void onSelectPetDialogNegativeClick(DialogFragment dialog) {}

    @Override
    public void onSelectPetDialogNeutralClick(DialogFragment dialog) {}

    @Override
    public int getContentViewId() {
        return R.layout.home_page;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.toolbar_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeLogo = findViewById(R.id.homeLogo);

        setLogo();
        checkForPets();
        populatePetList();
    }

    private void checkForPets() {
        String currentPetName = Pet.getCurrentPetName();
        String currentPetType = Pet.getCurrentPetType();

        // Check if the profile file exists
        File directory = getApplicationContext().getFilesDir();
        File profile = new File(directory, "profile");
        if (currentPetName == null && currentPetType == null) {
            if (profile.exists()) {
                openSelectPetDialog();
            } else {
                openAddPetDialog();
            }
        }
    }

    private void setLogo() {
        String imagePath = "drawable/ic_paws_time"; //Path information for a default picture
        int imageKey = this.getApplicationContext().getResources().getIdentifier(imagePath, "drawable", "com.pawstime"); // Find the image key
        homeLogo.setImageDrawable(this.getApplicationContext().getResources().getDrawable(imageKey)); // Set the image
    }

    public void populatePetList() {
        LinearLayout petList = findViewById(R.id.petList);

        System.out.println("Populating pet list");
        ArrayList<String> listOfPets = getPetsList(this);

        for (int i = 0; i < listOfPets.size(); i++) {
            PetCardView cardView = new PetCardView(this);
            cardView.setName(listOfPets.get(i));
//            cardView.setPicture();
            System.out.println("Inflated " + cardView.name.getText().toString());
            petList.addView(cardView);
        }

    }
}



