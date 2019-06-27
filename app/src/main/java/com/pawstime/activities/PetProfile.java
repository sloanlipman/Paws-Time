package com.pawstime.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pawstime.R;
import com.pawstime.dialogs.SelectPet;
import com.pawstime.Pet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/*TODO What if the user doesn't have a pet saved?
    Would they just see an empty pet profile, or should they be prompted to make a new one
    in the same fashion as you would be prompted if you were adding one?un
*/
public class PetProfile extends BaseActivity {
    com.github.clans.fab.FloatingActionButton addPet, changePet, export, save;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
        loadPetFromFile();
    }

    void initializeUI() {
        changePet = findViewById(R.id.changePet);
        changePet.setOnClickListener(v -> {
            DialogFragment newReminder = new SelectPet();
            newReminder.show(getSupportFragmentManager(), "selectPet");
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(v -> savePetToFile());

        addPet = findViewById(R.id.addPet);
        export = findViewById(R.id.export);


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

    void savePetToFile() {
    // Read values from the UI
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", Pet.getCurrentPetName());
        map.put("description", description.getText().toString());
        map.put("careInstructions", careInstructions.getText().toString());
        map.put("medicalInfo", medicalInfo.getText().toString());
        map.put("preferredVet", preferredVet.getText().toString());
        map.put("emergencyContact", emergencyContact.getText().toString());
        map.put("petType", Pet.getCurrentPetType());

    // Put the values into a JSON string
        JSONObject json = new JSONObject(map);
        System.out.println(json);

    // Save the JSON into a file
        String filename = Pet.getCurrentPetName(); // TODO
        System.out.println("FILE NAME " + filename);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.close(); // Don't forget to close the stream!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadPetFromFile() {
        FileInputStream input;
        StringBuilder stream = new StringBuilder();
        JSONObject json;
    // Read the File
        try {
            System.out.println("PET NAME " + Pet.getCurrentPetName());
            input = openFileInput(Pet.getCurrentPetName());
            int i;
            while ((i = input.read()) != -1) {
                stream.append((char) i); // Append each byte as a character to the StringBuilder
            }
            input.close(); // Don't forget to close the stream!
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonString = new String(stream); // create a String
        try {
            json = (JSONObject) new JSONTokener(jsonString).nextValue(); // Turn the String into JSON

        // Set the values on the UI
            getJson(json, "description", description);
            getJson(json, "careInstructions", careInstructions);
            getJson(json, "medicalInfo", medicalInfo);
            getJson(json, "preferredVet", preferredVet);
            getJson(json, "emergencyContact", emergencyContact);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getJson(JSONObject json, String string, EditText editText) {
        try {
            editText.setText(json.getString(string));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
