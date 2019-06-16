package com.pawstime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


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
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getToolbarTitle());
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
//        navView.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, HomePage.class));
            } else if (itemId == R.id.navigation_reminders) {
                startActivity(new Intent(this, RemindersList.class));
            } else if (itemId == R.id.navigation_viewPets) {
                startActivity(new Intent(this, PetProfile.class));
            }
            finish();
//        }, 300);
        return true;
    }
}
