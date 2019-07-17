package com.pawstime.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.pawstime.ImageSaver;
import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;
import com.pawstime.dialogs.UnsavedChangesDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    abstract int getContentViewId();
    abstract int getNavigationMenuItemId();
    abstract int getToolbarTitle();

    BottomNavigationView navView;
    Toolbar toolbar;
    static String currentActivity = "Home";
    static MenuItem requestedMenuItem;
    boolean areChangesUnsaved = false;
    public static String path;

    private void updateNavigationBarState() {
        int actionId = this.getNavigationMenuItemId();
        this.selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        navView = findViewById(R.id.bottomNav);
        navView.setOnNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getToolbarTitle());
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {} // Disable hardware back button

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        requestedMenuItem = item;
        if (currentActivity.equals("Profile") && requestedMenuItem.getItemId() != R.id.navigation_profile && areChangesUnsaved) { // && !someCheckForUnsavedChangesMethod()
            openUnsavedDialog();
        } else {
            performNavigate(requestedMenuItem);
        }
        return true;
    }

    public void performNavigate(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.navigation_home: {
                startActivity(new Intent(this, HomePage.class));
                currentActivity = "Home";
                break;
            }

            case R.id.navigation_reminders: {
                startActivity(new Intent(this, RemindersList.class));
                currentActivity = "Reminders";
                break;
            }

            case R.id.navigation_profile: {
                startActivity(new Intent(this, PetProfile.class));
                currentActivity = "Profile";
                break;
            }
        }
        finish();
    }

    void openAddPetDialog() {
        DialogFragment newPet = new AddPet();
        if (this.toString().contains("HomePage")) {
            newPet.setCancelable(false);
        }
        newPet.show(getSupportFragmentManager(), "newPet");
    }

    void openSelectPetDialog() {
        DialogFragment selectPet = new SelectPet();
        if (this.toString().contains("HomePage")) {
            selectPet.setCancelable(false);
        }
        selectPet.show(getSupportFragmentManager(), "selectPet");
    }

    void openUnsavedDialog() {
        DialogFragment unsavedChangesDialog = new UnsavedChangesDialog();
        unsavedChangesDialog.show(getSupportFragmentManager(), "unsavedChangesDialog");
    }

    public static ArrayList<String> getPetsList(Context context) {
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
                petList.addAll(Arrays.asList(pets));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return petList;
    }

    public static String loadPetFile(Context context) {
        FileInputStream input;
        StringBuilder stream = new StringBuilder();

        // Read the File
        try {
            input = context.openFileInput(Pet.getCurrentPet());
            int i;
            while ((i = input.read()) != -1) {
                stream.append((char) i); // Append each byte as a character to the StringBuilder
            }
            input.close(); // Don't forget to close the stream!
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(stream);
    }

    public static void startProfileActivity(Context context) {
        Intent i = new Intent(context, PetProfile.class);
        context.startActivity(i);
        ((Activity)context).finish();
    }

    //Loads and resizes existing pet profile picture into ImageView
    public static void loadPic(int picSize, ImageView pic, String path) {
        if ((path != null) && (ImageSaver.load(path) != null)){
            Bitmap picResize = ImageSaver.resizeBitmap(Objects.requireNonNull(ImageSaver.load(path)), picSize);
            pic.setImageBitmap(picResize);
        }
    }


    //Loads image path using current pet name
    public void loadPath(){
        path = ImageSaver.directoryName + Pet.getCurrentPet() + ".png";
    }


}
