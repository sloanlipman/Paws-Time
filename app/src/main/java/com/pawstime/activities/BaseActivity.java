package com.pawstime.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;


public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    abstract int getContentViewId();
    abstract int getNavigationMenuItemId();
    abstract int getToolbarTitle();

    BottomNavigationView navView;
    Toolbar toolbar;

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
    public void onBackPressed() {
        System.out.println("Can't do that, chief");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, HomePage.class));
            } else if (itemId == R.id.navigation_reminders) {
                startActivity(new Intent(this, RemindersList.class));
            } else if (itemId == R.id.navigation_viewPets) {
                startActivity(new Intent(this, PetProfile.class));
            }
            finish();
        return true;
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
            input = context.openFileInput(Pet.getCurrentPetName());
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
}
