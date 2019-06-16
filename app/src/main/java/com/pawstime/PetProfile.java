package com.pawstime;


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

}
