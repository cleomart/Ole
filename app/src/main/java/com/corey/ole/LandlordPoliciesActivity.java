package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LandlordPoliciesActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private Button addButton;
    private TextView textView;
    private String propertyName;
    private String propertyId;
    private String landlordId;
    private ArrayList<String> policies;
    private DatabaseReference propertyRef;
    private Property thisProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_policies);

        Intent intent = getIntent();
        policies = new ArrayList<>();
        propertyId = intent.getStringExtra(Property.PROPERTY_ID);
        propertyName = intent.getStringExtra(Property.PROPERTY_NAME);
        landlordId = intent.getStringExtra(LandlordProfile.LANDLORD_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        recyclerView = findViewById(R.id.landlord_policies_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.landlord_policies_add_button);
        textView = findViewById(R.id.landlord_policies_text);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        propertyRef = database.getReference("property").child(propertyId);
        propertyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisProperty = dataSnapshot.getValue(Property.class);
                ArrayList<String> n = thisProperty.getPolicies();
                if (n != null) {
                    policies = n;
                    setPoliciesAdapter();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText().toString();

                if (text.length() == 0) {
                    Toast.makeText(LandlordPoliciesActivity.this, "Policies Field Required",
                            Toast.LENGTH_SHORT).show();
                } else {
                   policies.add(text);
                   textView.setText("");
                   setPoliciesAdapter();
                }
            }
        });


    }

    private void setPoliciesAdapter() {
        AddPolicyNoteAdapter adapter = new AddPolicyNoteAdapter(policies);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AddPolicyNoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                policies.remove(position);
                setPoliciesAdapter();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            savePolicies();

        }

        return super.onOptionsItemSelected(item);
    }
    private void savePolicies() {
        thisProperty.setPolicies(policies);
        propertyRef.setValue(thisProperty);
        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra(Property.PROPERTY_ID, propertyId);
        intent.putExtra(LandlordProfile.LANDLORD_ID, landlordId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
