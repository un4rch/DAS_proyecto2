package com.example.das_proyecto2.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyecto2.adapters.ItemAdapter;
import com.example.das_proyecto2.items.Item;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.example.das_proyecto2.R;
import com.example.das_proyecto2.SessionManager;
import com.example.das_proyecto2.services.FetchImagesService;
import com.example.das_proyecto2.services.UploadImageService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements FetchImagesService.OnImagesFetchedListener {
    SessionManager sessionManager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    String userEmail;
    List<Item> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve the value passed via Intent extras
        //String email = getIntent().getStringExtra("email");
        sessionManager = new SessionManager(HomeActivity.this);
        userEmail = sessionManager.getEmail();
        setup(userEmail);
    }

    private void setup(String email) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // Access the header view
        TextView userEmailTextView = headerView.findViewById(R.id.navEmail);
        userEmailTextView.setText(email); // Now set the text
        /*// Find the TextView by its ID
        TextView userEmailTextView = findViewById(R.id.navEmail);
        // Set the text of the TextView to the retrieved email value
        userEmailTextView.setText(email);*/

        Button logOutButton = headerView.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                sessionManager.clearSession();
                Intent authIntent = new Intent(HomeActivity.this, AuthActivity.class);
                startActivity(authIntent);
                finish();
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_dialog_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.nav_item1) {
                            System.out.println(R.id.nav_item1);
                        } else if (menuItem.getItemId() == R.id.nav_item2) {
                            System.out.println(R.id.nav_item2);
                        }
                        // Cerrar el drawer después de manejar la selección de menú
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                }
        );

        // Upload Image Button
        Button uploadImgButton = findViewById(R.id.uploadImgButton);
        uploadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS }, 100);
                } else {
                    openCamera();
                }
            }
        });

        // RecyclerView with images
        new FetchImagesService(this).execute();
    }

    @Override
    public void onImagesFetched(JSONArray images) {
        this.itemList = null;
        if (images != null) {
            this.itemList = new ArrayList<>();
            try {
                for (int i = 0; i < images.length() && i < 10; i++) {
                    JSONObject imageInfo = images.getJSONObject(i);
                    String email = imageInfo.getString("email");
                    String filename = imageInfo.getString("filename");
                    int likes = imageInfo.getInt("likes");
                    this.itemList.add(new Item(filename, likes));
                    //this.itemList.add(new Item(filename, likes));

                    // Here you could update your UI or handle the data as needed
                    Log.i("HomeActivity", "Email: " + email + ", Filename: " + filename + ", Likes: " + likes);
                }
            } catch (Exception e) {
                Log.e("HomeActivity", "Error parsing JSON", e);
            }
        } else {
            Log.e("HomeActivity", "Received empty response");
        }
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Arreglar esta llamada ya que se peta al intentar renderizar las imagenes con ItemAdapter
        //recyclerView.setAdapter(new ItemAdapter(this, this.itemList));
        if (this.itemList != null) {
            recyclerView.setAdapter(new ItemAdapter(this, this.itemList));
            Log.d("HomeActivity", "Size: "+itemList.size());
        }
    }

    @Override
    public void onError(String error) {
        // Handle errors - show message or log
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle ActionBarDrawerToggle clicks
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            new UploadImageService().execute("http://34.136.205.220:8000/upload_img", userEmail, currentPhotoPath);
        }
        Intent intent = getIntent(); // Obtiene el Intent que inició la actividad actual
        finish(); // Finaliza la actividad actual
        startActivity(intent); // Inicia la actividad nuevamente con el mismo Intent
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}