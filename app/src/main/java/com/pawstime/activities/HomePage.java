package com.pawstime.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.pawstime.Pet;
import com.pawstime.R;

import java.io.File;
import java.io.FileInputStream;


public class HomePage extends BaseActivity {
    ImageView homeLogo;
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

        //Path information for a default picture
        String imagePath = "drawable/ic_paws_time";
        int imageKey = this.getApplicationContext().getResources().getIdentifier(imagePath, "drawable", "com.pawstime"); // Find the image key
        homeLogo.setImageDrawable(this.getApplicationContext().getResources().getDrawable(imageKey)); // Set the image

        checkForPets();
    }

    private void checkForPets() {
        FileInputStream input;
        StringBuilder stream = new StringBuilder();
    // Check if the profile file exists
        File directory = getApplicationContext().getFilesDir();
        File profile = new File(directory, "profile");
        if (profile.exists()) {
            try {
                input = openFileInput("profile");
                int i;
                while ((i = input.read()) != -1) {
                    stream.append((char) i);
                }
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            // Forcibly add new pet
            System.out.println("NO PROFILE. FILE NOT FOUND.");
            // TODO replace with functionality to add new pet
            Pet.setCurrentPetName("Megan");
            Pet.setCurrentPetType("Poodle");
        }

    }
}
