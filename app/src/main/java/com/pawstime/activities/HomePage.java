package com.pawstime.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.widget.ImageView;
import android.widget.LinearLayout;


import com.pawstime.ImageSaver;
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
        currentActivity = "Profile";
        startProfileActivity(this);
    }

    @Override
    public void onAddPetDialogNegativeClick(DialogFragment dialog) {}

    @Override
    public void onSelectPetDialogPositiveClick(DialogFragment dialog) {
        startProfileActivity(this);
    }
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
        String currentPet = Pet.getCurrentPet();

        // Check if the profile file exists
        File directory = getApplicationContext().getFilesDir();
        File profile = new File(directory, "profile");
        if (currentPet == null) {
            if (profile.exists()) {
                Pet.setCurrentPet(getPetsList(this).get(0));
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
            String imagePath = ImageSaver.directoryName + listOfPets.get(i) + ".png";
            cardView.setPicture(imagePath);
            petList.addView(cardView);
        }
    }
    public static void clickPetCard(String name, Context context) {
        Pet.setCurrentPet(name);
        startProfileActivity(context);
    }
}



