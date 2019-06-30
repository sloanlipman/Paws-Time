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
import android.widget.EditText;

import com.pawstime.Pet;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;

import java.io.FileInputStream;


public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    abstract int getContentViewId();
    abstract int getNavigationMenuItemId();
    abstract int getToolbarTitle();

    BottomNavigationView navView;
    Toolbar toolbar;
    public static String petToAdd;

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
        navView = findViewById(R.id.nav_view);
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
