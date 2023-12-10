package com.example.finalproject;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.List;

public class SavedImagesAdapter extends ArrayAdapter<SavedImage> {

    public SavedImagesAdapter(@NonNull Context context, @NonNull List<SavedImage> savedImages) {
        super(context, 0, savedImages);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_layout, parent, false);
        }

        // Get the current SavedImage object
        final SavedImage currentImage = getItem(position);

        if (currentImage != null) {
            // Populate the views with the data from the current SavedImage
            TextView customDateTextView = convertView.findViewById(R.id.customDateTextView);
            ImageView customImageView = convertView.findViewById(R.id.customImageView);

            // Continue with setting text if TextView and ImageView are not null
            if (customDateTextView != null && customImageView != null) {
                customDateTextView.setText(currentImage.getDate());
                loadImageIntoImageView(currentImage.getImageUrl(), customImageView);
            }

            ImageButton deleteImageButton = convertView.findViewById(R.id.deleteImageButton);

            // Set a click listener for the delete ImageButton
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Adapter", "Delete button clicked at position: " + position);
                    showDeleteConfirmationDialog(currentImage);
                }
            });


            return convertView;
        }

        return convertView;
    }

    // Method to show the delete confirmation dialog
    public void showDeleteConfirmationDialog(final SavedImage deletedImage) {
        Log.d("Adapter", "Showing delete confirmation dialog for image: " + deletedImage.getImageUrl());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Image");
        builder.setMessage(R.string.delete_confirmation_message);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Adapter", "Deleting image");

                // Remove the item from the list and notify the adapter
                remove(deletedImage);
                notifyDataSetChanged();
                Log.d("Adapter", "Image deleted. Data set size: " + getCount());

                // Delete the image file associated with the deleted item
                deleteImageFile(deletedImage);

                // Remove the saved image from SharedPreferences
                removeFromSharedPreferences(getContext(), deletedImage);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Method to delete the image file associated with the SavedImage
    private void deleteImageFile(SavedImage deletedImage) {
        if (deletedImage != null) {
            // Remove the image from shared preferences
            List<SavedImage> savedImages = SharedPreferenceHelper.getSavedImages(getContext());

            if (savedImages != null && savedImages.contains(deletedImage)) {
                savedImages.remove(deletedImage);
                SharedPreferenceHelper.saveImages(getContext(), savedImages);

                // Notify the adapter that the data set has changed
                notifyDataSetChanged();
            }
        }
    }








    // Method to remove a saved image from SharedPreferences
    private void removeFromSharedPreferences(Context context, SavedImage savedImage) {
        // Get the current list of saved images
        List<SavedImage> savedImages = SharedPreferenceHelper.getSavedImages(context);

        // Remove the deleted image
        if (savedImages != null) {
            savedImages.remove(savedImage);

            // Save the updated list back to SharedPreferences
            SharedPreferenceHelper.saveImages(context, savedImages);
        }
    }

    // Method to load the image into the ImageView using Picasso
    private void loadImageIntoImageView(String imageUrl, ImageView imageView) {
        Picasso.get().load(imageUrl).into(imageView);
    }

    private SavedImage findImageByImageUrl(List<SavedImage> savedImages, String imageUrl) {
        if (savedImages != null) {
            for (SavedImage savedImage : savedImages) {
                if (imageUrl.equals(savedImage.getImageUrl())) {
                    return savedImage;
                }
            }
        }
        return null;
    }

}
