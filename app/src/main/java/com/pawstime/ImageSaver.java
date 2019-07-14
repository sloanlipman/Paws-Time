package com.pawstime;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pawstime.activities.PetProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 Adapted from:
 * Created by Ilya Gazman on 3/6/2016.
 https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
 */
public class ImageSaver {

    public static final String directoryName = "Paws_Time/";
    private String fileName = "image.png";
    private Context context;
    private boolean external;

    //Constants for image resizing
    public static final int PROFILE_SIZE = 700;
    public static final int ICON_SIZE = 100;

    public ImageSaver() {
    }

    ImageSaver(Context context) {
        this.context = context;
    }

    //Create unique image file name
    public ImageSaver setFileName() {
        this.fileName =  Pet.getCurrentPet() + ".png";
        return this;
    }

    public ImageSaver setExternal(boolean isExternal) {
        this.external = isExternal;
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Creates Paws_Time directory on your phone
    @NonNull
    public File createFile() {
        File directory;
        if (external) {
            directory = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!directory.exists())
                directory.mkdirs();
        } else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
            if (!directory.exists())
                directory.mkdirs();
        }

        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("ImageSaver", "Error creating directory " + directory);
        }

        return new File(directory, fileName);
    }

    public static Bitmap load(String path) {
        FileInputStream inputStream = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory(), path);
            inputStream = new FileInputStream(f);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Adapted from https://stackoverflow.com/questions/21085105/get-orientation-of-image-from-mediastore-images-media-data
    private static int getOrientation(String filepath) {// YOUR MEDIA PATH AS STRING
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    /*
        Note:
        Methods getOrientationFromMediaStore and getCorrectlyOrientedImage are adapted from:
        https://stackoverflow.com/questions/3647993/android-bitmaps-loaded-from-gallery-are-rotated-in-imageview/8914291#8914291
    */
    private static int getOrientationFromMediaStore(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri, String path, String source) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = 0;
        switch (source) { //Need to specify whether the picture was from a default picture, which will use a filepath that already exists or if the picture is currently being uploaded from the Android device
            case "Default":
                orientation = getOrientation(path); //default picture
                break;
            case "Uploaded":
                orientation = getOrientationFromMediaStore(context, photoUri); //uploaded from device
                break;
        }
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }
        Bitmap src;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 1000 || rotatedHeight > 1000) {
            float widthRatio = ((float) rotatedWidth) / ((float) 1000);
            float heightRatio = ((float) rotatedHeight) / ((float) 1000);
            float maxRatio = Math.max(widthRatio, heightRatio);
            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            src = BitmapFactory.decodeStream(is, null, options);

        } else {
            src = BitmapFactory.decodeStream(is);
        }
        is.close();
        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            src = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                    src.getHeight(), matrix, true);
        }

        return src;
    }

    // adapted from https://colinyeoh.wordpress.com/2012/05/18/android-getting-image-uri-from-bitmap/
    public static Uri getImageUri(Bitmap inImage, Context inContext) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //adapted from https://developer.android.com/reference/android/graphics/Bitmap.html
    public static Bitmap resizeBitmap(Bitmap src, int imageSize){
        //Get original image dimensions
        int height = src.getHeight();
        int width = src.getWidth();

        int newHeight;
        int newWidth;

        //Adjust dimensions to maintain aspect ratio
        if (width > height) {
            newWidth = imageSize;
            newHeight = (imageSize * height) / width;
        }
        else if (height > width){
            newHeight = imageSize;
            newWidth = (imageSize * width) / height;
        }
        else {
            newHeight = imageSize;
            newWidth = imageSize;
        }

        //Resize Image
        return Bitmap.createScaledBitmap(src, newWidth, newHeight,true);
    }


}
