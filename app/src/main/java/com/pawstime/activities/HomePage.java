package com.pawstime.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ImageView;

import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;


public class HomePage extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener {
    ImageView homeLogo;

    @Override
    public void onAddPetDialogPositiveClick(DialogFragment dialog) {}

    @Override
    public void onAddPetDialogNegativeClick(DialogFragment dialog) {}

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
}

// TODO we might want to keep some of this code so that we can pre-populate the list of pets on the home page through some fragments

//    FileReader fr;
//    BufferedReader reader;
//    String stream;
//    ArrayList<String> petList = new ArrayList<>();
//    JSONObject json;

//      try {
//                    fr = new FileReader(profile);
//                    reader = new BufferedReader(fr);
//                    while ((stream = reader.readLine()) != null) {
//                        String[] pets = stream.split(",");
//                        for (String pet : pets) {
//                            System.out.println(pet);
//                            petList.add(pet);
//                        }
//                    }
//                    reader.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Files are: " + petList.toString());
//                Pet.setCurrentPetName(petList.get(0));
//                String pet = Pet.loadPetFile(this);
//                try {
//                    json = (JSONObject) new JSONTokener(pet).nextValue();
//                    System.out.println(json);
//                    Pet.setCurrentPetType(json.getString("type"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }