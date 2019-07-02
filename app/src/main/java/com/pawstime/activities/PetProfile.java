package com.pawstime.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;
import com.pawstime.Pet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileOutputStream;

public class PetProfile extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener {
    com.github.clans.fab.FloatingActionButton addPet, selectPet, export, save;
    com.github.clans.fab.FloatingActionMenu fabMenu;
    ImageView picture;
    Button changePicture;
    EditText description, careInstructions, medicalInfo, preferredVet, emergencyContact;
    TextView petNameAndType;

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

// Add pet listeners
    @Override
    public void onAddPetDialogPositiveClick(DialogFragment dialog) {
       fabMenu.close(true);
        startActivity(getIntent());
        finish();
    }

// Selecting pet listeners
    @Override
    public void onSelectPetDialogPositiveClick(DialogFragment dialog) {
        fabMenu.close(true);
        startActivity(getIntent());
        finish();
    }

    @Override
    public void onSelectPetDialogNeutralClick(DialogFragment dialog) {
        fabMenu.close(true);
        openAddPetDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
        loadPet();
    }

    public void loadPet() {
        String jsonString = BaseActivity.loadPetFile(this);
        setPetProfileUI(jsonString);
    }

    void initializeUI() {
        initializeFabMenu();
        selectPet = findViewById(R.id.selectPet);
        selectPet.setOnClickListener(v -> openSelectPetDialog());

        save = findViewById(R.id.save);
        save.setOnClickListener(v -> savePetToFile());

        addPet = findViewById(R.id.addPet);
        addPet.setOnClickListener(v -> openAddPetDialog());

        export = findViewById(R.id.export);
        export.setOnClickListener(v -> exportPet());

        picture = findViewById(R.id.petPicture);
        changePicture = findViewById(R.id.changePicture);
        description = findViewById(R.id.petDesc);
        careInstructions = findViewById(R.id.careInstructions);
        medicalInfo = findViewById(R.id.medicalInfo);
        preferredVet = findViewById(R.id.preferredVetName);
        emergencyContact = findViewById(R.id.emergencyContactInfo);
        petNameAndType = findViewById(R.id.petNameAndType);
        String pet = Pet.getCurrentPetName() + " the " + Pet.getCurrentPetType();
        petNameAndType.setText(pet);
    }

    public void initializeFabMenu() {
        fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setClosedOnTouchOutside(true); // Dismiss by tapping anywhere

    /* Behavior for when toggling the FAB menu */
        fabMenu.setOnMenuToggleListener(v -> {
            if (getCurrentFocus() != null) {
                int id = getCurrentFocus().getId(); // Finds the ID of the currently focused View
                EditText selected = null;

                // Compare to EditText fields
                switch (id) {
                    case R.id.petDesc: {
                        selected = description;
                        break;
                    }
                    case R.id.careInstructions: {
                        selected = careInstructions;
                        break;
                    }

                    case R.id.medicalInfo: {
                        selected = medicalInfo;
                        break;
                    }

                    case R.id.preferredVetName: {
                        selected = preferredVet;
                        break;
                    }

                    case R.id.emergencyContactInfo: {
                        selected = emergencyContact;
                        break;
                    }
                    default: {
                        Log.e("FAB Menu", "Either nothing is selected, or there EditText isn't specified in this code block"); // If we see this message and there was something selected, we need to add a new case
                    }
                }

                if (!fabMenu.isOpened() && selected != null) {
                    selected.setCursorVisible(true);
                } else if (fabMenu.isOpened() && selected != null) {
                    selected.setCursorVisible(false);
                } else {
                    Log.e("FAB Menu", "The EditText you're looking for isn't specified in this code block"); // Again, we're missing a case if we see this
                }
            }
        });
    }

    public void savePetToFile() {
    // Read values from the UI
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", Pet.getCurrentPetName());
        map.put("type", Pet.getCurrentPetType());
        map.put("description", description.getText().toString());
        map.put("careInstructions", careInstructions.getText().toString());
        map.put("medicalInfo", medicalInfo.getText().toString());
        map.put("preferredVet", preferredVet.getText().toString());
        map.put("emergencyContact", emergencyContact.getText().toString());

    // Put the values into a JSON string
        JSONObject json = new JSONObject(map);

    // Save the JSON into a file
        String filename = Pet.getCurrentPetName();
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.close(); // Don't forget to close the stream!
            Toast.makeText(this, "Pet successfully saved!", Toast.LENGTH_SHORT).show();
            fabMenu.close(true);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setPetProfileUI(String jsonString) {
        JSONObject json;

        try {
            json = (JSONObject) new JSONTokener(jsonString).nextValue(); // Turn the String into JSON

            // Set the values on the UI
            String name = json.getString("name");
            String type = json.getString("type");
            String nameAndType = name + " the " + type;

            petNameAndType.setText(nameAndType);
            editTextToJson(json, "description", description);
            editTextToJson(json, "careInstructions", careInstructions);
            editTextToJson(json, "medicalInfo", medicalInfo);
            editTextToJson(json, "preferredVet", preferredVet);
            editTextToJson(json, "emergencyContact", emergencyContact);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void editTextToJson(JSONObject json, String string, EditText editText) {
        try {
            editText.setText(json.getString(string));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void exportPet() {
        savePetToFile(); // Save the pet first

        String nameAndType;
        String desc = "";
        String care = "";
        String medical = "";
        String vet = "";
        String contact = "";

        if (petNameAndType.length() > 0) {
            nameAndType = petNameAndType.getText().toString(); // Make sure there is a name and description

            // Parse the UI fields
            if (description.getText().length() > 0) {
                desc = description.getText().toString() + "\n\n";
            }

            if (careInstructions.getText().length() > 0) {
                care = "Special care instructions:\n" + careInstructions.getText().toString() + "\n\n";
            }

            if (medicalInfo.getText().length() > 0) {
                medical = "Medical info:\n" + medicalInfo.getText().toString() + "\n\n";
            }

            if (preferredVet.getText().length() > 0) {
                vet = "Our favorite vet:\n" + preferredVet.getText().toString() + "\n\n";
            }

            if (emergencyContact.getText().length() > 0) {
                contact = "In case of emergency, please contact:\n" + emergencyContact.getText().toString();
            }

            String message = "I'm using PawsTime to help keep track of my pets! Here is some information about " + nameAndType + "!\n\n" + desc + care + medical + vet + contact;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Check out my pet!");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getResources().getText(R.string.export_pet_details)));
        } else {
            Toast.makeText(this, "Something went wrong. Please save your changes and then try again", Toast.LENGTH_SHORT).show(); // For whatever reason if there's no pet name and type, show an error
        }
    }
}
