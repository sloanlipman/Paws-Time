package com.pawstime.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pawstime.ImageSaver;
import com.pawstime.R;
import com.pawstime.dialogs.AddPet;
import com.pawstime.dialogs.SelectPet;
import com.pawstime.Pet;
import com.pawstime.dialogs.UnsavedChangesDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileOutputStream;
import java.io.IOException;

public class PetProfile extends BaseActivity implements AddPet.AddPetDialogListener, SelectPet.SelectPetDialogListener, UnsavedChangesDialog.UnsavedChangesDialogListener {
    com.github.clans.fab.FloatingActionButton addPet, selectPet, export, save;
    com.github.clans.fab.FloatingActionMenu fabMenu;
    ImageView picture;
    Button changePicture;
    EditText description, careInstructions, medicalInfo, preferredVet, emergencyContact;
    TextView petNameAndType;

    private static final int CAMERA_PIC_REQUEST = 1337; // Request Code for returning picture from camera
    private static final int GALLERY_PIC_REQUEST = 7331; // Request Code for returning picture from gallery
    private static final int CAMERA_PERMISSION_REQUEST = 1234; // Request Code for camera permission
    private static final int GAL_STORAGE_PERMISSION_REQUEST = 4321; // Request Code for external storage permission
    private static final int CAM_STORAGE_PERMISSION_REQUEST = 5432; // Request Code for external storage permission
    private static String path;
    Bitmap petBitPicResize;

    
    LocalBroadcastManager lbm;
    boolean openSelectPetAfterUnsavedDialog = false;
    boolean openAddPetAfterUnsavedDialog = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Pet", "Received");
            openUnsavedDialog();
        }
    };

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

   @Override
   public void onStart() {
       super.onStart();
       // Register the receiver to the activity using a LocalBroadcastManager
       IntentFilter intentFilter = new IntentFilter("com.pawstime.open_unsaved_dialog");
       lbm =  LocalBroadcastManager.getInstance(getApplicationContext());
       LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
   }

   @Override
   public void onPause() {
       super.onPause();
       LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
        initializeFabMenu();
        initializePictureButton();
        loadPet();
        loadPath();
        loadPic(ImageSaver.PROFILE_SIZE, picture);
    }

    public void loadPet() {
        String jsonString = BaseActivity.loadPetFile(this);
        setPetProfileUI(jsonString);
    }

    //Loads image path using current pet name
    public void loadPath(){
        path = ImageSaver.directoryName + Pet.getCurrentPet() + ".png";
    }

    //Loads and resizes existing pet profile picture into ImageView
    public void loadPic(int picSize, ImageView pic) {
        if ((path != null) && (ImageSaver.load(path) != null)){
            Bitmap picResize = ImageSaver.resizeBitmap(ImageSaver.load(path), picSize);
            pic.setImageBitmap(picResize);
//            picture.setImageBitmap(ImageSaver.load(path));
        }
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
    }

    public void setAddTextChangedListener(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                String currentText = "";
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    currentText = s.toString();
                    areChangesUnsaved = false;
                    System.out.println("Before Text changed " + s.toString());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!currentText.equals(s.toString())) {
                        areChangesUnsaved = true;
                    }
                    System.out.println("On Text changed " + s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
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
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.setType("image/*");
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

    //Take picture using camera
    private void takePicture(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        //Picture import gets handled by onActivityResult method
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
    }

    public void setPetProfileUI(String jsonString) {
        JSONObject json;

        try {
            json = (JSONObject) new JSONTokener(jsonString).nextValue(); // Turn the String into JSON

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

    void openUnsavedDialog() {
        DialogFragment unsavedChangesDialog = new UnsavedChangesDialog();
        unsavedChangesDialog.show(getSupportFragmentManager(), "unsavedChangesDialog");
    }

    @Override
    // Pull selected image into app and set pet profile picture
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        picture = findViewById(R.id.petPicture);
        final String path = "/Paws_Time/img" + Pet.getCurrentPet() + ".png";
        if ((resultCode == Activity.RESULT_OK) && (data != null)){
            AlertDialog.Builder requestBuilder = new AlertDialog.Builder(PetProfile.this);
            if (requestCode == CAMERA_PIC_REQUEST){ //Request code 1337
                try{
                    Bitmap petCameraBitPic = (Bitmap) data.getExtras().get("data");
                    petBitPicResize = ImageSaver.resizeBitmap(petCameraBitPic, ImageSaver.PROFILE_SIZE);
                    if(ContextCompat.checkSelfPermission(PetProfile.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //permission is not granted
                        requestBuilder.setMessage("Paws Time needs your permission to store the profile picture.")
                                .setCancelable(true)
                                .setPositiveButton("Ok", (dialog, which) -> {
                                    ActivityCompat.requestPermissions(PetProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAM_STORAGE_PERMISSION_REQUEST);
                                })
                                .setNegativeButton("Cancel", null);
                        AlertDialog requestAlert = requestBuilder.create();
                        requestAlert.setTitle("File Permissions Request");
                        requestAlert.show();
                    }
                    else{
                        Context inContext = getApplicationContext();
                        petBitPicResize = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), ImageSaver.getImageUri(petBitPicResize, inContext), path, "Uploaded");
                        picture.setImageBitmap(petBitPicResize);
                        new ImageSaver().setExternal(true).setFileName().save(petBitPicResize);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if ((requestCode == GALLERY_PIC_REQUEST) && (data.getData() != null)){//Request code 7331
                Uri selectedPictureURI = data.getData();
                if (selectedPictureURI.toString().contains("image")){
                    try{
                        Bitmap petGalleryBitPic = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), selectedPictureURI, path, "Uploaded");
                        petGalleryBitPic = ImageSaver.resizeBitmap(petGalleryBitPic, ImageSaver.PROFILE_SIZE);
                        petBitPicResize = petGalleryBitPic;
                        if(ContextCompat.checkSelfPermission(PetProfile.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            //permission is not granted
                            requestBuilder.setMessage("Paws Time needs your permission to store the profile picture.")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", (dialog, which) -> {
                                        ActivityCompat.requestPermissions(PetProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GAL_STORAGE_PERMISSION_REQUEST);
                                    })
                                    .setNegativeButton("Cancel", null);
                            AlertDialog requestAlert = requestBuilder.create();
                            requestAlert.setTitle("File Permissions Request");
                            requestAlert.show();
                        }
                        else{
                            picture.setImageBitmap(petBitPicResize);
                            new ImageSaver().setExternal(true).setFileName().save(petBitPicResize);
                        }

                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this, "Pet Profile Picture Selection Canceled", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    // Process permission requests
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        // If user gives permission, open camera and request a picture
        if (requestCode == CAMERA_PERMISSION_REQUEST) { // Request code 1234
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        }
        if (requestCode == GAL_STORAGE_PERMISSION_REQUEST) { // Request code 4321
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                picture.setImageBitmap(petBitPicResize);
                new ImageSaver().setExternal(true).setFileName().save(petBitPicResize);
            }
        }
        if (requestCode == CAM_STORAGE_PERMISSION_REQUEST) { // Request code 5432
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try{
                    Context inContext = getApplicationContext();
                    petBitPicResize = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), ImageSaver.getImageUri(petBitPicResize, inContext), path, "Uploaded");
                    picture.setImageBitmap(petBitPicResize);
                    new ImageSaver().setExternal(true).setFileName().save(petBitPicResize);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
