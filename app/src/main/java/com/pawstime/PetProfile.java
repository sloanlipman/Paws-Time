package com.pawstime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;

public class PetProfile extends BaseActivity {
    com.github.clans.fab.FloatingActionButton changePet;
    @Override
    public int getContentViewId() {
        return R.layout.pet_profile;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_viewPets;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.toolbar_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePet = findViewById(R.id.change_pet);
        changePet.setOnClickListener(v -> {
            DialogFragment newReminder = new SelectPet();
            newReminder.show(getSupportFragmentManager(), "selectPet");
        });
    }

    // TODO give the user the option to add new pet types to the spinner
    // See https://www.mkyong.com/android/android-spinner-drop-down-list-example/ for an example!

    // TODO use an adapter so we can remove hardcoded "[Select One]" from strings.xml
    // See https://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
}
