package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreferenceHelper {

    private static final String PREF_NAME = "SavedImagesPref";
    private static final String KEY_SAVED_IMAGES = "savedImages";

    public static void showToast(Context context, int messageResourceId) {
        String message = context.getString(messageResourceId);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Save a single image
    public static void saveImage(Context context, SavedImage savedImage) {
        List<SavedImage> savedImages = getSavedImages(context);

        if (savedImages == null) {
            savedImages = new ArrayList<>();
        } else {
            savedImages = new ArrayList<>(savedImages);
        }

        savedImages.add(savedImage);

        saveImages(context, savedImages);

        showToast(context, R.string.successful_save_message);
    }


    // Get the list of saved images
    public static List<SavedImage> getSavedImages(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedImagesJson = preferences.getString(KEY_SAVED_IMAGES, null);

        if (!TextUtils.isEmpty(savedImagesJson)) {
            Gson gson = new Gson();
            SavedImage[] savedImagesArray = gson.fromJson(savedImagesJson, SavedImage[].class);
            return Arrays.asList(savedImagesArray);
        }

        return null;
    }

    // Save the list of saved images
    public static void saveImages(Context context, List<SavedImage> savedImages) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savedImages);
        editor.putString(KEY_SAVED_IMAGES, json);
        editor.apply();
    }

    // Clear all saved images
    public static void clearSavedImages(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_SAVED_IMAGES).apply();
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
