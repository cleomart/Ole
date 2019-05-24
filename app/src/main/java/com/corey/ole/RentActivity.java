package com.corey.ole;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class RentActivity extends TenantProfileActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView baseRent;
    private TextView water;
    private TextView trash;
    private TextView electricity;
    private TextView internet;
    private TextView total;
    private TextView youPay;
    private Button paymentButton;
    private String url;
    private TextView due;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);
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

        baseRent = findViewById(R.id.baseRentValue);
        water = findViewById(R.id.waterValue);
        trash = findViewById(R.id.trashValue);
        electricity = findViewById(R.id.electricityValue);
        internet = findViewById(R.id.internetValue);
        total = findViewById(R.id.totalValue);
        youPay = findViewById(R.id.youPayValue);
        paymentButton = findViewById(R.id.payNowButton);
        due = findViewById(R.id.dueHeader);

        getTenantFromID(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    protected void loadData() {
        Rent rent = tenant.getRent();
        if (rent == null) {
            rent = new Rent();
            tenant.setRent(rent);
        }

        baseRent.setText("$" + rent.getBaseRent());
        water.setText("$" + rent.getWater());
        trash.setText("$" + rent.getTrash());
        electricity.setText("$" + rent.getElectricity());
        internet.setText("$" + rent.getInternet());
        total.setText("$" + rent.getTotal());
        youPay.setText("$" + rent.getTotal());
        url = rent.getUrl();
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth > rent.getDayOfMonth()) {
            int month = cal.get(Calendar.MONTH);
            if (month < Calendar.DECEMBER) {
                cal.set(Calendar.MONTH, month + 1);
            } else {
                int year = cal.get(Calendar.YEAR);
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.YEAR, year + 1);
            }
        }
        cal.set(Calendar.DAY_OF_MONTH, rent.getDayOfMonth());
        SimpleDateFormat df = new SimpleDateFormat("MMMM d yyyy");
        due.setText("Due " + df.format(cal.getTime()));
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
        getMenuInflater().inflate(R.menu.rent, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, TenantHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(this, TenantMessagesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, TenantPropertyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rent) {
            // Do nothing
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
