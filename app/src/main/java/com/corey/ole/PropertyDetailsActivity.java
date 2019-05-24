package com.corey.ole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PropertyDetailsActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView image;
    private TextView name;
    private TextView address;
    private FirebaseDatabase database;
    private String propertyId;
    private String propertyName;
    private Button tenantButton;
    private String landlordId;
    private Button policiesButton;
    private Button announecementsButton;
    private Button documentsButton;
    private Button repairButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);
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

        image = findViewById(R.id.property_details_image);
        name = findViewById(R.id.property_details_name);
        address = findViewById(R.id.property_details_address);
        tenantButton = findViewById(R.id.property_details_tenants_button);
        policiesButton = findViewById(R.id.property_details_policies_button);
        announecementsButton = findViewById(R.id.property_details_announcements_button);
        documentsButton = findViewById(R.id.property_details_documents_button);
        repairButton = findViewById(R.id.property_details_repairs_button);
        Intent intent = getIntent();
        propertyId = intent.getStringExtra("propertyId");
        landlordId = intent.getStringExtra("landlordId");

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("property");
        DatabaseReference property = ref.child(propertyId);

        property.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Property property = dataSnapshot.getValue(Property.class);
                propertyName = property.getName();
                name.setText(propertyName);
                address.setText(property.getStreet() + "\n"+ property.getCityStateZip());

                /* Fetch the image from Firebase Storage and sets it to imageButton */
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                if (property.getImagePath() != null && property.getImagePath().length() != 0) {
                    StorageReference islandRef = storageRef.child(property.getImagePath());

                    final long ONE_MEGABYTE = 1024 * 1024;
                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        tenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTenantList();

            }
        });

        announecementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnnouncementActivity();
            }
        });

        policiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPoliciesActivity();
            }
        });

        repairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRepairsActivity();

            }
        });

    }

    private void startRepairsActivity() {
        Intent intent = new Intent(this, LandlordRepairActivity.class);
        intent.putExtra(LandlordProfile.LANDLORD_ID, landlordId);
        intent.putExtra(Property.PROPERTY_ID, propertyId);
        intent.putExtra(Property.PROPERTY_NAME, propertyName);
        startActivity(intent);
    }

    private void startAnnouncementActivity(){
        Intent intent = new Intent(this, LandlordAnnouncementsActivity.class);
        intent.putExtra(LandlordProfile.LANDLORD_ID, landlordId);
        intent.putExtra(Property.PROPERTY_ID, propertyId);
        intent.putExtra(Property.PROPERTY_NAME, propertyName);
        startActivity(intent);

    }

    private void startPoliciesActivity() {
        Intent intent = new Intent(this, LandlordPoliciesActivity.class);
        intent.putExtra(LandlordProfile.LANDLORD_ID, landlordId);
        intent.putExtra(Property.PROPERTY_ID, propertyId);
        intent.putExtra(Property.PROPERTY_NAME, propertyName);
        startActivity(intent);
    }

    private void startDocumentsActivity() {
        Intent intent = new Intent(this, LandlordPoliciesActivity.class);
        intent.putExtra(LandlordProfile.LANDLORD_ID, landlordId);
        intent.putExtra(Property.PROPERTY_ID, propertyId);
        intent.putExtra(Property.PROPERTY_NAME, propertyName);
        startActivity(intent);

    }

    private void startTenantList(){
        Intent intent = new Intent(this, TenantListActivity.class);
        intent.putExtra("name", propertyName);
        intent.putExtra("PropertyID", propertyId);

        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            Intent intent = new Intent(this, EditAddPropertyActivity.class);
            intent.putExtra("propertyId", propertyId);
            intent.putExtra("landlordId", landlordId);
            intent.putExtra("isAdd", false);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Intent intent = new Intent(this, LandlordProfileActivity.class);
            startActivity(intent);
            return true;
        }  else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
