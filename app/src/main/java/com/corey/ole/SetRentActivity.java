package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

public class SetRentActivity extends TenantProfileActivity {

    private String tenantID;
    private EditText baseRent;
    private EditText water;
    private EditText trash;
    private EditText electricity;
    private EditText internet;
    private TextView total;
    private EditText url;
    private Spinner dayOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_rent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent tenantIntent = getIntent();
        Bundle intentExtras = tenantIntent.getExtras();
        tenantID = intentExtras.getString("tenantID");

        baseRent = findViewById(R.id.baseRentValue);
        water = findViewById(R.id.waterValue);
        trash = findViewById(R.id.trashValue);
        electricity = findViewById(R.id.electricityValue);
        internet = findViewById(R.id.internetValue);
        total = findViewById(R.id.totalValue);
        url = findViewById(R.id.urlValue);
        dayOfMonth = findViewById(R.id.daySpinner);

        getTenantFromID(tenantID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {
            Rent rent = getRent();
            tenant.setRent(rent);
            FirebaseDatabase.getInstance().getReference("users/" + tenantID).setValue(tenant);
            Intent intent = new Intent(this, LandlordTenantProfileActivity.class);
            intent.putExtra(TenantProfile.EXTRA_TENANT_ID, tenantID);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadData() {
        Rent rent = tenant.getRent();
        if (rent == null) {
            rent = new Rent();
            tenant.setRent(rent);
        }

        baseRent.setText(String.valueOf(rent.getBaseRent()));
        water.setText(String.valueOf(rent.getWater()));
        trash.setText(String.valueOf(rent.getTrash()));
        electricity.setText(String.valueOf(rent.getElectricity()));
        internet.setText(String.valueOf(rent.getInternet()));
        total.setText(String.valueOf(rent.getTotal()));
        url.setText(rent.getUrl());
        dayOfMonth.setSelection(rent.getDayOfMonth() - 1);

        addTextChangeListener(baseRent);
        addTextChangeListener(water);
        addTextChangeListener(trash);
        addTextChangeListener(electricity);
        addTextChangeListener(internet);
    }

    private Rent getRent() {
        String baseRentVal = baseRent.getText().toString();
        String waterVal = water.getText().toString();
        String trashVal = trash.getText().toString();
        String electricityVal = electricity.getText().toString();
        String internetVal = internet.getText().toString();
        String urlVal = url.getText().toString();
        int day = dayOfMonth.getSelectedItemPosition() + 1;

        int baseRent;
        int water;
        int trash;
        int electricity;
        int internet;

        if (TextUtils.isEmpty(baseRentVal)) {
            baseRent = tenant.getRent().getBaseRent();
        } else {
            baseRent = Integer.parseInt(baseRentVal);
        }
        if (TextUtils.isEmpty(waterVal)) {
            water = tenant.getRent().getWater();
        } else {
            water = Integer.parseInt(waterVal);
        }
        if (TextUtils.isEmpty(trashVal)) {
            trash = tenant.getRent().getTrash();
        } else {
            trash = Integer.parseInt(trashVal);
        }
        if (TextUtils.isEmpty(electricityVal)) {
            electricity = tenant.getRent().getElectricity();
        } else {
            electricity = Integer.parseInt(electricityVal);
        }
        if (TextUtils.isEmpty(internetVal)) {
            internet = tenant.getRent().getInternet();
        } else {
            internet = Integer.parseInt(internetVal);
        }
        if (TextUtils.isEmpty(urlVal)) {
            urlVal = tenant.getRent().getUrl();
        }

        return new Rent(baseRent, water, trash, electricity, internet, urlVal, day);
    }

    private void addTextChangeListener(EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Rent rent = getRent();
                total.setText(String.valueOf(rent.getTotal()));
            }
        });
    }
}
