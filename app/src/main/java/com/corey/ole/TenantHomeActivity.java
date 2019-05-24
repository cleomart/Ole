package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class TenantHomeActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mAnnouceRecycler;
    private RecyclerView mUpcomingRecycler;
    private String mUid;
    private FirebaseDatabase mDb;
    private TenantProfile tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setDrawerData(navigationView);

        mDb = FirebaseDatabase.getInstance();

        mAnnouceRecycler = findViewById(R.id.rv_announcements);
        mAnnouceRecycler.setHasFixedSize(true);
        mAnnouceRecycler.setLayoutManager(new LinearLayoutManager(this));

        mUpcomingRecycler = findViewById(R.id.rv_upcoming);
        mUpcomingRecycler.setHasFixedSize(true);
        mUpcomingRecycler.setLayoutManager(new LinearLayoutManager(this));

        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDb.getReference("users").child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tenant = dataSnapshot.getValue(TenantProfile.class);
                setAnnouncements();
                setUpcoming();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAnnouncements() {
        mDb.getReference("property").child(tenant.getPropertyId()).child("announcements").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> data = dataSnapshot.getValue(t);
                if (data != null) {
                    AnnouncementAdapter adapter = new AnnouncementAdapter(data);
                    mAnnouceRecycler.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUpcoming() {

        mDb.getReference("users").child(tenant.getId()).child("repairs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Repair>> t = new GenericTypeIndicator<ArrayList<Repair>>() {};
                ArrayList<Repair> data = dataSnapshot.getValue(t);
                if (data != null) {
                    tenant.setRepairs(data);

                    ArrayList<String> repairString = new ArrayList<>();
                    for (Repair repair : tenant.getRepairs()) {
                        if (!repair.isFixed()) {
                            Date d = repair.getDateSchedule();
                            if (d != null) {
                                String date = new SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault()).format(d);
                                String request = repair.getRequest();
                                if (request.length() > 20) {
                                    repairString.add("Repair Request scheduled on " + date + ": " + request.substring(0, 20) + "...");
                                } else {
                                    repairString.add("Repair Request scheduled on " + date + ": " + request);
                                }

                            }
                        }

                    }

                    AnnouncementAdapter adapter = new AnnouncementAdapter(repairString);
                    mUpcomingRecycler.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        getMenuInflater().inflate(R.menu.tenant_home, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Do nothing
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(this, TenantMessagesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, TenantPropertyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rent) {
            Intent intent = new Intent(this, RentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lease) {

        } else if (id == R.id.nav_repair) {
            Intent intent = new Intent(this, RepairsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, TenantTenantProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}