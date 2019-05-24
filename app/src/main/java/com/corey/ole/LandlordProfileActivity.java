package com.corey.ole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LandlordProfileActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String mUid;
    private LandlordProfile landlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_tenant_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setDrawerData(navigationView);

        findViewById(R.id.rent_button).setVisibility(View.INVISIBLE);

        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getLandlordFromID(mUid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit) {
            // do something
            Intent intent = new Intent(this, LandlordEditProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, LandlordHomeActivity.class);
            intent.putExtra("landlordId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(this, LandlordMessagesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            // Do nothing
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void getLandlordFromID(String landlordID){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference("users/" + landlordID);
        userRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                landlord = dataSnapshot.getValue(LandlordProfile.class);
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void loadData(){
        TextView name = findViewById(R.id.tenant_name_field);
        TextView room = findViewById(R.id.room_number_field);
        TextView gender = findViewById(R.id.gender_field);
        TextView dob= findViewById(R.id.date_of_birth_field);
        TextView phone = findViewById(R.id.cell_phone_field);
        TextView email = findViewById(R.id.email_field);
        TextView property = findViewById(R.id.property_name_field);

        name.setText(landlord.getFirstName() + " " + landlord.getLastName());
        room.setVisibility(View.INVISIBLE);
        gender.setText(landlord.getGender());
        dob.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(landlord.getBirthdate()));
        phone.setText(String.valueOf(landlord.getPhone()).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
        email.setText(landlord.getEmail());
        property.setVisibility(View.INVISIBLE);

        /* Fetch the image from Firebase Storage and sets it to imageButton */
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        if (landlord.getImagePath() != null && landlord.getImagePath().length() != 0) {
            StorageReference islandRef = storageRef.child(landlord.getImagePath());

            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    ImageView image = findViewById(R.id.profile_picture_field);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                            image.getHeight(), false));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }
}
