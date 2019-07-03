package com.pawstime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.widget.ImageView;
import android.widget.LinearLayout;


import com.pawstime.Pet;
import com.pawstime.PetCard;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;


import java.io.File;

import java.util.ArrayList;


public class HomePage extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener {
    ImageView homeLogo;

    @Override
    public void onAddPetDialogPositiveClick(DialogFragment dialog) {
            Intent i = new Intent(this, PetProfile.class);
            startActivity(i);
            finish();
    }

    @Override
    public void onAddPetDialogNegativeClick(DialogFragment dialog) {}

    @Override
    public void onSelectPetDialogPositiveClick(DialogFragment dialog) {}
    @Override
    public void onSelectPetDialogNegativeClick(DialogFragment dialog) {}
    @Override
    public void onSelectPetDialogNeutralClick(DialogFragment dialog) {
        openAddPetDialog();
    }

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

        ArrayList<String> listOfPets = getPetsList(this);

        for (int i = 0; i < listOfPets.size(); i++) {
            PetCard cardView = new PetCard(this);
            cardView.setName(listOfPets.get(i));
//            cardView.setPicture();
            petList.addView(cardView);
        }

    }
}



