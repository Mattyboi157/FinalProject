package com.example.finalproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener {

    private EditText editTextDate;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView displayDateTextView;
    private ImageView imageView;
    private TextView urlTextView;

    private JSONObject result;


    public static final int MENU_SAVED_IMAGES = R.id.menu_saved_images;
    public static final int MENU_HOME = R.id.menu_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        toggle.setDrawerIndicatorEnabled(true);



        editTextDate = findViewById(R.id.editText);
        Button searchButton = findViewById(R.id.searchButton);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        displayDateTextView = findViewById(R.id.displayDateTextView);
        imageView = findViewById(R.id.imageView);
        urlTextView = findViewById(R.id.urlTextView);
        Button showDatePickerButton = findViewById(R.id.datePickerButton);

        searchButton.setOnClickListener(v -> {
            String selectedDate = editTextDate.getText().toString();
            new FetchAPODTask().execute(selectedDate);
        });

        showDatePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Add navigation item selected listener
        NavigationView navigationView = findViewById(R.id.navigation_view_main);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks here
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
            } else if (itemId == R.id.menu_saved_images) {
                startActivity(new Intent(MainActivity.this, SavedImagesActivity.class));
            }

            // Close the drawer when an item is selected
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        Button savedImagesButton = findViewById(R.id.savedImagesButton);

        savedImagesButton.setOnClickListener(v -> saveCurrentImage());
    }

    private void saveCurrentImage() {
        if (result != null) {
            try {
                String title = result.getString("title");
                String description = result.getString("explanation");
                String imageUrl = result.getString("url");
                String hdUrl = result.getString("hdurl");
                String date = result.getString("date");

                SavedImage savedImage = new SavedImage(title, description, imageUrl, hdUrl, date);
                SharedPreferenceHelper.saveImage(MainActivity.this, savedImage);
                List<SavedImage> savedImages = SharedPreferenceHelper.getSavedImages(MainActivity.this);
                Log.d("SavedImages", "Number of saved images: " + (savedImages != null ? savedImages.size() : 0));

                // Show a toast message
                Toast.makeText(MainActivity.this, R.string.successful_save_message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.failed_save_message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_SAVED_IMAGES) {
            startActivity(new Intent(MainActivity.this, SavedImagesActivity.class));
            return true;
        } else if (item.getItemId() == MENU_HOME) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        @SuppressLint("DefaultLocale") String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
        editTextDate.setText(selectedDate);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchAPODTask extends AsyncTask<String, Void, JSONObject> {
        private final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading_message));
            progressDialog.show();
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String selectedDate = params[0];
            String apiKey = "3VcrHYRLZ6mpdjOEcUTB7oErSXzIiyFBUjHlELgN";
            String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey + "&date=" + selectedDate;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return new JSONObject(result.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressDialog.dismiss();

            if (result != null) {
                try {
                    String title = result.getString("title");
                    String description = result.getString("explanation");
                    String imageUrl = result.getString("url");
                    String hdUrl = result.getString("hdurl");
                    String date = result.getString("date");

                    titleTextView.setText(title);
                    descriptionTextView.setText(description);
                    displayDateTextView.setText(date);

                    Picasso.get().load(imageUrl).into(imageView);

                    int urlText = R.string.hyperlink_message;
                    urlTextView.setText(urlText);
                    urlTextView.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hdUrl));
                        startActivity(browserIntent);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.this.result = result;
            } else {
                Toast.makeText(MainActivity.this, R.string.load_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
