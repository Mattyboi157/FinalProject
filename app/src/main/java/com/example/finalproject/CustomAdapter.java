package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private final Context context;
    private final List<SavedImage> savedImages;

    public CustomAdapter(Context context, List<SavedImage> savedImages) {
        this.context = context;
        this.savedImages = savedImages;
    }

    @Override
    public int getCount() {
        return savedImages.size();
    }

    @Override
    public Object getItem(int position) {
        return savedImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            // Inflate the custom item layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_item_layout, null);

            // Create ViewHolder to hold references to your views
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.customImageView);
            viewHolder.dateTextView = view.findViewById(R.id.customDateTextView);

            view.setTag(viewHolder);
        } else {
            // View is recycled, retrieve ViewHolder from tag
            viewHolder = (ViewHolder) view.getTag();
        }

        // Set data to views using Picasso
        SavedImage savedImage = savedImages.get(position);
        Picasso.get().load(savedImage.getImageUrl()).into(viewHolder.imageView);
        viewHolder.dateTextView.setText(savedImage.getDate());

        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView dateTextView;
    }
}