package com.pawstime.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.pawstime.ImageSaver;
import com.pawstime.Pet;
import com.pawstime.PetCard;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;


import java.io.File;

import java.util.ArrayList;
import java.util.List;


public class HomePage extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener {
    ImageView homeLogo;
    Button appChanger;

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
        appChanger = findViewById(R.id.appChanger);

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
            PetCard petCard = new PetCard(this);
            petCard.setName(listOfPets.get(i));
            String imagePath = ImageSaver.directoryName + listOfPets.get(i) + ".png";
            petCard.setPicture(imagePath);
            petList.addView(petCard);
        }
    }
    public static void clickPetCard(String name, Context context) {
        Pet.setCurrentPet(name);
        startProfileActivity(context);
    }

    //Order Supplies button allows user to go to Amazon or Chewy
    public void orderSupplies(View v){
        AlertDialog.Builder picBuilder = new AlertDialog.Builder(this);
        picBuilder.setMessage("Would you like to order supplies from Amazon or Chewy?")
                .setCancelable(true)
                .setPositiveButton("Chewy", (dialog, id) -> openApp("com.chewy.android", "https://chewy.com"))
                .setNegativeButton("Amazon", (dialog, id) -> openApp("com.amazon.mShop.android.shopping", "https://amazon.com"));
        AlertDialog pictureAlert = picBuilder.create();
        pictureAlert.setTitle("Order Pet Supplies");
        pictureAlert.show();
    }

    //Checks phone for app if app doesn't exist opens webpage
    public void openApp(String appName, String webName){
        Intent i = getPackageManager().getLaunchIntentForPackage(appName);

        if (i == null){
            Uri webpage = Uri.parse(webName);
            Intent webintent = new Intent(Intent.ACTION_VIEW, webpage);

            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(webintent, 0);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe){
                startActivity(webintent);
            }
            else
                Toast.makeText(getApplicationContext(), "No app available to handle this request.", Toast.LENGTH_LONG).show();
        }
        else
            startActivity(i);
    }

}



