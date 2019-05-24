package com.corey.ole;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;

public class LandlordRepairActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String propertyId;
    private String propertyName;
    private String landlordId;
    private RecyclerView recyclerView;
    private ArrayList<Repair> repairs;
    private HashSet<String> repairIds;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_repair);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        propertyId = intent.getStringExtra(Property.PROPERTY_ID);
        propertyName = intent.getStringExtra(Property.PROPERTY_NAME);
        landlordId = intent.getStringExtra(LandlordProfile.LANDLORD_ID);
        toolbar.setTitle(propertyName);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setDrawerData(navigationView);

        context = this;


        recyclerView = findViewById(R.id.landlord_repair_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repairs = new ArrayList<>();
        repairIds = new HashSet<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("property");
        DatabaseReference tenants = ref.child(propertyId).child("tenants");

        if (tenants != null) {
            tenants.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<String>> g  = new GenericTypeIndicator<ArrayList<String>>() {};
                    ArrayList<String> tenants = dataSnapshot.getValue(g);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("users");
                    if (tenants == null)
                        return;

                    repairs = new ArrayList<>();
                    for (String id : tenants) {
                        DatabaseReference userRepair = ref.child(id).child("repairs");

                        if (userRepair != null) {
                            userRepair.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Repair>> g  = new GenericTypeIndicator<ArrayList<Repair>>() {};
                                    ArrayList<Repair> rep = dataSnapshot.getValue(g);
                                    if (rep == null)
                                        return;
                                    for (Repair r: rep) {
                                        if (!repairIds.contains(r.getId())) {
                                            repairs.add(r);
                                            repairIds.add(r.getId());
                                        } else {
                                            for (int i = 0; i < repairs.size(); i ++){
                                                if (repairs.get(i).getId() == r.getId()) {
                                                    repairs.set(i, r);
                                                }
                                            }
                                        }
                                    }
                                    LandlordRepairAdapter adapter = new LandlordRepairAdapter(repairs, context);
                                    recyclerView.setAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }
    private void setUpRepairAdapter() {


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
          //  savePolicies();

        }

        return super.onOptionsItemSelected(item);
    }



}
