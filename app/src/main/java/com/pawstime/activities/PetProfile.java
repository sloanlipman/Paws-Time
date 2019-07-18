package com.pawstime.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pawstime.ImageSaver;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.DeleteProfile;
import com.pawstime.dialogs.SelectPet;
import com.pawstime.Pet;
import com.pawstime.dialogs.UnsavedChangesDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PetProfile extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener,
        UnsavedChangesDialog.UnsavedChangesDialogListener, DeleteProfile.DeleteProfileDialogListener {

    com.github.clans.fab.FloatingActionButton addPet, selectPet, export, save, delete;
    com.github.clans.fab.FloatingActionMenu fabMenu;
    de.hdodenhof.circleimageview.CircleImageView picture;
    Button changePicture;
    EditText description, careInstructions, medicalInfo, preferredVet, emergencyContact;
    TextView petNameAndType;
    ConstraintLayout profileLayout;
    ProgressBar progressBar;

    private static final int CAMERA_PIC_REQUEST = 1337; // Request Code for returning picture from camera
    private static final int GALLERY_PIC_REQUEST = 7331; // Request Code for returning picture from gallery
    private static final int CAMERA_PERMISSION_REQUEST = 1234; // Request Code for camera permission
    private static final int GAL_STORAGE_PERMISSION_REQUEST = 4321; // Request Code for external storage permission
    private static final int CAM_STORAGE_PERMISSION_REQUEST = 5432; // Request Code for external storage permission
    File tempFile;
    String currentPhotoPath;
    Bitmap petBitPicResize;

    boolean openSelectPetAfterUnsavedDialog = false;
    boolean openAddPetAfterUnsavedDialog = false;

    @Override
    public int getContentViewId() {
        return R.layout.pet_profile;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_profile;
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

    @Override
    public void onAddPetDialogNegativeClick(DialogFragment dialog) {
        fabMenu.close(true);
    }

// Selecting pet listeners
    @Override
    public void onSelectPetDialogPositiveClick(DialogFragment dialog) {
        fabMenu.close(true);
        startActivity(getIntent());
        finish();
    }

    @Override
    public void onSelectPetDialogNegativeClick(DialogFragment dialog) {
        fabMenu.close(true);
    }


    @Override
    public void onSelectPetDialogNeutralClick(DialogFragment dialog) {
        fabMenu.close(true);
        openAddPetDialog();
    }

    public void onUnsavedChangesDialogPositiveClick(DialogFragment dialog) {
        savePetToFile();
        if (openSelectPetAfterUnsavedDialog) {
            openSelectPetDialog();
        } else if (openAddPetAfterUnsavedDialog) {
            openAddPetDialog();
        } else {
            performNavigate(requestedMenuItem);
        }
        fabMenu.close(true);
    }

    public void onUnsavedChangesDialogNegativeClick(DialogFragment dialog) {
        if (openSelectPetAfterUnsavedDialog) {
            openSelectPetDialog();
        } else if (openAddPetAfterUnsavedDialog) {
            openAddPetDialog();
        } else {
            performNavigate(requestedMenuItem);
        }
        fabMenu.close(true);
    }

    public void onUnsavedChangesDialogNeutralClick(DialogFragment dialog) {
        fabMenu.close(true);
        selectBottomNavigationBarItem(R.id.navigation_profile);
    }

    public void onDeleteProfileDialogPositiveClick(DialogFragment dialog) {
        fabMenu.close(true);
        deleteProfile();
    }
    public void onDeleteProfileDialogNegativeClick(DialogFragment dialog) {
        fabMenu.close(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
        initializeFabMenu();
        initializePictureButton();
        loadPet();
        loadPath();
        loadPic(ImageSaver.PROFILE_SIZE, picture, path);
    }

    public void loadPet() {
        String jsonString = loadPetFile(this);
        setPetProfileUI(jsonString);
    }

    void initializeUI() {
        picture = findViewById(R.id.petPicture);
        changePicture = findViewById(R.id.changePicture);
        description = findViewById(R.id.petDesc);
        careInstructions = findViewById(R.id.careInstructions);
        medicalInfo = findViewById(R.id.medicalInfo);
        preferredVet = findViewById(R.id.preferredVetName);
        emergencyContact = findViewById(R.id.emergencyContactInfo);
        petNameAndType = findViewById(R.id.petNameAndType);
        petNameAndType.setText(Pet.getCurrentPet());
        EditText[] editTexts = {description, careInstructions, medicalInfo, preferredVet, emergencyContact};
        setAddTextChangedListener(editTexts);

        progressBar = findViewById(R.id.indeterminateBar);
        profileLayout = findViewById(R.id.profileLayout);
    }

    public void setAddTextChangedListener(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                String currentText = "";
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    currentText = s.toString();
                    areChangesUnsaved = false;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!currentText.equals(s.toString())) {
                        areChangesUnsaved = true;
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (!currentText.equals(s.toString())) {
                        areChangesUnsaved = true;
                    }
                }
            });
        }
    }

    public void initializeFabMenu() {
        selectPet = findViewById(R.id.selectPet);
        selectPet.setOnClickListener(v -> {
            openAddPetAfterUnsavedDialog = false;
            openSelectPetAfterUnsavedDialog = false;
            if (areChangesUnsaved) {
                openSelectPetAfterUnsavedDialog = true;
                openUnsavedDialog();
            } else {
                openSelectPetDialog();
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(v -> savePetToFile());

        addPet = findViewById(R.id.addPet);
        addPet.setOnClickListener(v -> {
            openAddPetAfterUnsavedDialog = false;
            openSelectPetAfterUnsavedDialog = false;
            if (areChangesUnsaved) {
                openAddPetAfterUnsavedDialog = true;
                openUnsavedDialog();
            } else {
                openAddPetDialog();
            }
        });

        export = findViewById(R.id.export);
        export.setOnClickListener(v -> exportPet());

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(v -> {
            DialogFragment deleteProfileDialog = new DeleteProfile();
            deleteProfileDialog.show(getSupportFragmentManager(), "deleteProfile");
        });

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

    // Button click opens AlertDialog to prompt user to select a pet profile picture
    public void initializePictureButton(){
        changePicture.setOnClickListener(v -> {
            AlertDialog.Builder picBuilder = new AlertDialog.Builder(PetProfile.this);
            //Send user to gallery to select pet profile picture
            //If user permits use of camera, send user to camera to make pet profile picture
            picBuilder.setMessage("Would you like to take a new picture or select one from the gallery?")
                    .setCancelable(true)
                    .setPositiveButton("Gallery", (dialog, which) -> {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(galleryIntent,GALLERY_PIC_REQUEST);
                    })
                    .setNegativeButton("Camera", (dialog, which) -> {
                        if(ContextCompat.checkSelfPermission(PetProfile.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            //permission is not granted
                            ActivityCompat.requestPermissions(PetProfile.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                        }
                        else{
                            takePicture();
                        }
                    });
            AlertDialog pictureAlert = picBuilder.create();
            pictureAlert.setTitle("Select A Picture");
            pictureAlert.show();
        });
    }
    private void createImageFile() {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tempFile = new File(storageDir + "/temp.png");

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = tempFile.getAbsolutePath();
    }
    //Take picture using camera
    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            createImageFile();
            // Continue only if the File was successfully created
            if (tempFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.pawstime.fileprovider",
                        tempFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
            }
        }
}

    public void savePetToFile() {
    // Read values from the UI

        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("nameAndType", Pet.getCurrentPet());
        map.put("description", description.getText().toString());
        map.put("careInstructions", careInstructions.getText().toString());
        map.put("medicalInfo", medicalInfo.getText().toString());
        map.put("preferredVet", preferredVet.getText().toString());
        map.put("emergencyContact", emergencyContact.getText().toString());

    // Put the values into a JSON string
        JSONObject json = new JSONObject(map);

    // Save the JSON into a file
        String filename = Pet.getCurrentPet();
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.close(); // Don't forget to close the stream!
            Toast.makeText(this, "Pet successfully saved!", Toast.LENGTH_SHORT).show();
            fabMenu.close(true);
            areChangesUnsaved = false;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
        Bitmap petBit = ((BitmapDrawable)picture.getDrawable()).getBitmap();

        new ImageSaver().setExternal(true).setFileName().save(petBit);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tempFile = new File(storageDir + "/temp.png");
        if (tempFile.exists()) {
            tempFile.delete();
        }

    }

    public void setPetProfileUI(String jsonString) {
        JSONObject json;

        try {
            json = (JSONObject) new JSONTokener(jsonString).nextValue(); // Turn the String into JSON\
            // Set the values on the UI
            String nameAndType = json.getString("nameAndType");

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

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            String message = "I'm using PawsTime to help keep track of my pets! Here is some information about " + nameAndType + "!\n\n" + desc + care + medical + vet + contact;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Check out my pet!");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("image/gif");
            File directory = new File(Environment.getExternalStorageDirectory(), ImageSaver.directoryName);
            File picture = new File(directory, Pet.getCurrentPet() + ".png");
            if (picture.exists()) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picture));
            }

            startActivity(Intent.createChooser(intent, getResources().getText(R.string.export_pet_details)));
        } else {
            Toast.makeText(this, "Something went wrong. Please save your changes and then try again", Toast.LENGTH_SHORT).show(); // For whatever reason if there's no pet name and type, show an error
        }
    }

    private void deleteProfile() {
        FileOutputStream outputStream;
        File dir = getFilesDir();

    // Delete the pet's profile file
        File petInformation = new File(dir, Pet.getCurrentPet());
        petInformation.delete();

    // Delete the picture
        File currentPetPicture = new File(Environment.getExternalStorageDirectory(), path);
        currentPetPicture.delete();

    // Rewrite the output String
        ArrayList<String> currentListOfPets = getPetsList(this);
        currentListOfPets.remove(Pet.getCurrentPet()); // Remove pet
        try {

            StringBuilder pets = new StringBuilder();
            for (String pet: currentListOfPets) {
                    pets.append(pet);
                    pets.append("¿");
            }

            outputStream = openFileOutput("profile", Context.MODE_PRIVATE);
            outputStream.write(new String(pets).getBytes()); // Write previous pets
            outputStream.close();

            if (currentListOfPets.size() != 0) {
                Pet.setCurrentPet(currentListOfPets.get(0)); // Set the first pet as the current pet
            } else {
                File profile = new File(dir, "profile");
                profile.delete(); // Delete the entire profile file so we can start from scratch
                Pet.setCurrentPet(null); // Set null so the home page will prompt to add a new one
            }

            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Profile deleted. Navigating home.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    // Pull selected image into app and set pet profile picture
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        picture = findViewById(R.id.petPicture);
        progressBar.setVisibility(View.VISIBLE);
        profileLayout.setAlpha(0.5f);
        if ((resultCode == Activity.RESULT_OK) && (data != null)){
            if (requestCode == CAMERA_PIC_REQUEST) { //Request code 1337
                if (ContextCompat.checkSelfPermission(PetProfile.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    showFilePermissionsRequest(CAM_STORAGE_PERMISSION_REQUEST).show(); // Request permissions if not yet granted
                } else{
                    setCameraBitmap();
                }
            }
            if ((requestCode == GALLERY_PIC_REQUEST) && (data.getData() != null)){//Request code 7331
                Uri selectedPictureURI = data.getData();
                if (selectedPictureURI.toString().contains("image")){
                    try{
                        Bitmap petGalleryBitPic = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), selectedPictureURI);
                        petGalleryBitPic = ImageSaver.resizeBitmap(petGalleryBitPic, ImageSaver.PROFILE_SIZE);
                        petBitPicResize = petGalleryBitPic;
                        if (ContextCompat.checkSelfPermission(PetProfile.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            showFilePermissionsRequest(GAL_STORAGE_PERMISSION_REQUEST).show(); // Request permissions if not yet granted
                        } else{
                            picture.setImageBitmap(petBitPicResize);
                            areChangesUnsaved = true;
                            progressBar.setVisibility(View.GONE);
                            profileLayout.setAlpha(1);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this, "Pet Profile Picture Selection Canceled", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            profileLayout.setAlpha(1);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private AlertDialog showFilePermissionsRequest(int requestCode) {
        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(PetProfile.this);
        requestBuilder.setMessage("Paws Time needs your permission to store the profile picture.")
                .setCancelable(true)
                .setPositiveButton("Ok", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    profileLayout.setAlpha(0.5f);
                    ActivityCompat.requestPermissions(PetProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    progressBar.setVisibility(View.GONE);
                    profileLayout.setAlpha(1);
                });
        AlertDialog requestAlert = requestBuilder.create();
        requestAlert.setOnCancelListener(dialog -> {
            progressBar.setVisibility(View.GONE);
            profileLayout.setAlpha(1);
        });
        requestAlert.setTitle("File Permissions Request");
        return requestAlert;
    }

    private void setCameraBitmap() {
        Bitmap cameraBit = BitmapFactory.decodeFile(currentPhotoPath, null);
        Context inContext = getApplicationContext();
        try {
            petBitPicResize = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), ImageSaver.getImageUri(cameraBit, inContext));
        } catch (IOException e) {
            e.printStackTrace();
        }
        petBitPicResize = ImageSaver.resizeBitmap(petBitPicResize, ImageSaver.PROFILE_SIZE);
        picture.setImageBitmap(petBitPicResize);
        areChangesUnsaved = true;
        progressBar.setVisibility(View.GONE);
        profileLayout.setAlpha(1);
    }

    @Override
    // Process permission requests
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If user gives permission, open camera and request a picture
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CAMERA_PERMISSION_REQUEST: {
                    progressBar.setVisibility(View.GONE);
                    profileLayout.setAlpha(1);
                    takePicture();
                    break;
                } case GAL_STORAGE_PERMISSION_REQUEST: {
                    progressBar.setVisibility(View.GONE);
                    profileLayout.setAlpha(1);
                    picture.setImageBitmap(petBitPicResize);
                    areChangesUnsaved = true;
                    break;
                } case CAM_STORAGE_PERMISSION_REQUEST: {
                    progressBar.setVisibility(View.VISIBLE);
                    profileLayout.setAlpha(0.5f);
                    setCameraBitmap();
                    break;
                }
            }
        }
        else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
            progressBar.setVisibility(View.GONE);
            profileLayout.setAlpha(1);
        }
    }
}
