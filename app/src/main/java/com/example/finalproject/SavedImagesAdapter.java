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

            // Log statements to check if TextView and ImageView are null
            Log.d("Adapter", "customDateTextView is null: " + (customDateTextView == null));
            Log.d("Adapter", "customImageView is null: " + (customImageView == null));

            // Continue with setting text if TextView and ImageView are not null
            if (customDateTextView != null && customImageView != null) {
                customDateTextView.setText(currentImage.getDate());
                // Assuming you have a method to load the image into the ImageView
                // You can replace the following line with your actual implementation
                loadImageIntoImageView(currentImage.getImageUrl(), customImageView);
            }

            ImageButton deleteImageButton = convertView.findViewById(R.id.deleteImageButton);

            // Set a click listener for the delete ImageButton
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Adapter", "Delete button clicked at position: " + position);
                    showDeleteConfirmationDialog(position);
                }
            });

            return convertView;
        }

        return convertView;
    }

    // Method to show the delete confirmation dialog
    public void showDeleteConfirmationDialog(final int position) {
        Log.d("Adapter", "Showing delete confirmation dialog for position: " + position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure you want to delete this image?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Adapter", "Deleting image at position: " + position);
                // Remove the item from the list and notify the adapter
                remove(getItem(position));
                notifyDataSetChanged();
                Log.d("Adapter", "Image deleted. Data set size: " + getCount());
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // You may also need to implement a method to load the image into the ImageView
    private void loadImageIntoImageView(String imageUrl, ImageView imageView) {
        Picasso.get().load(imageUrl).into(imageView);
    }
}
