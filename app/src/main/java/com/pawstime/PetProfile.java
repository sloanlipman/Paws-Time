package com.pawstime;


import android.view.Menu;
import android.view.MenuInflater;

public class PetProfile extends BaseActivity {

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

    // TODO give the user the option to add new pet types to the spinner
    // See https://www.mkyong.com/android/android-spinner-drop-down-list-example/ for an example!

    // TODO use an adapter so we can remove hardcoded "[Select One]" from strings.xml
    // See https://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
}
