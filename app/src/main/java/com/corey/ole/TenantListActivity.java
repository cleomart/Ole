package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TenantListActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String propertyName = "Property";
    private RecyclerView mRecyclerView;
    private TenantListAdapter mAdapter;
    private String propertyID;
    private FirebaseDatabase mDb;
    private ArrayList<TenantProfile> mTenants = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tenant_list);

        Intent intent = getIntent();
        propertyName = intent.getStringExtra("name");
        propertyID = intent.getStringExtra("PropertyID");

        Toolbar toolbar = findViewById(R.id.tenant_list_toolbar);
        toolbar.setTitle(propertyName + " Tenants");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setDrawerData(navigationView);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDb = FirebaseDatabase.getInstance();
        getTenants();

    }

    private void getTenants() {
        DatabaseReference tenantsRef = mDb.getReference("property/" + propertyID + "/tenants");
        tenantsRef.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTenants.clear();
                if (dataSnapshot.getValue() == null) {
                    return;
                }
                for (String uid : (ArrayList<String>) dataSnapshot.getValue()) {
                    mDb.getReference("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            TenantProfile profile = dataSnapshot.getValue(TenantProfile.class);
                            profile.setId(dataSnapshot.getKey());
                            mTenants.add(profile);
                            setAdapterAndUpdateData();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapterAndUpdateData() {
        // create a new adapter with the updated mComments array
        // this will "refresh" our recycler view
        mAdapter = new TenantListAdapter(this, mTenants);
        mRecyclerView.setAdapter(mAdapter);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
