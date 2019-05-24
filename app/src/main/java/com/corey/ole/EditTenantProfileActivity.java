package com.corey.ole;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditTenantProfileActivity extends NavDrawerActivity {
    protected TenantProfile tenant;
    private EditText name;
    protected EditText room;
    protected TextView roomDisplay;
    private EditText gender;
    private EditText dob;
    private TextView email;
    private EditText phone;
    private TextView property;
    private FirebaseDatabase database;
    private ImageButton imageButton;

    public void loadData(){
        name = findViewById(R.id.tenant_name_edit_field);
        room = findViewById(R.id.room_number_edit_field);
        roomDisplay = findViewById(R.id.room_number_display_field);
        gender = findViewById(R.id.gender_edit_field);
        dob = findViewById(R.id.date_of_birth_edit_field);
        phone = findViewById(R.id.cell_phone_edit_field);
        email = findViewById(R.id.email_edit_field);
        property = findViewById(R.id.property_name_edit_field);
        imageButton = findViewById(R.id.profile_picture_edit_field);

        name.setText(tenant.getFirstName() + " " + tenant.getLastName());
        room.setText(tenant.getRoom());
        roomDisplay.setText("Room " + tenant.getRoom());
        gender.setText(tenant.getGender());
        dob.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(tenant.getBirthdate()));
        phone.setText(String.valueOf(tenant.getPhone()).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
        email.setText(tenant.getEmail());

        // Fetch the property name from the database since it might change!
        database = FirebaseDatabase.getInstance();
        database.getReference("property").child(tenant.getPropertyId() + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                property.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getTenantFromID(String tenantID){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference("users").child(tenantID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tenant = dataSnapshot.getValue(TenantProfile.class);
                loadData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    protected void updateTenantData(String tenantID) {
        DatabaseReference userRef = database.getReference("users/" + tenantID);
        String[] n = name.getText().toString().split(" ");
        if (n.length == 0) {
            // Do nothing
        } else if (n.length == 1) {
            tenant.setFirstName(n[0]);
            tenant.setLastName("");
        } else {
            tenant.setFirstName(n[0]);
            tenant.setLastName(n[1]);
        }

        if (!TextUtils.isEmpty(room.getText().toString()))
            tenant.setRoom(room.getText().toString());

        if (!TextUtils.isEmpty(gender.getText().toString())) {
            tenant.setGender(gender.getText().toString());
        }
        if (!TextUtils.isEmpty(dob.getText().toString())) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
            try {
                Date date = format.parse(dob.getText().toString());
               tenant.setBirthdate(date);
            } catch (ParseException e) {
                // Do nothing
            }
        }

        String num = phone.getText().toString();
        num = num.replaceAll("[^\\d]", "" );
        if (!TextUtils.isEmpty(num)) {
            tenant.setPhone(num);
        }

        userRef.setValue(tenant);
    }

}
