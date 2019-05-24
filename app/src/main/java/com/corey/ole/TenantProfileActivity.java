package com.corey.ole;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class TenantProfileActivity extends NavDrawerActivity {
    protected TenantProfile tenant;

    protected void loadData(){
        TextView name = findViewById(R.id.tenant_name_field);
        TextView room = findViewById(R.id.room_number_field);
        TextView gender = findViewById(R.id.gender_field);
        TextView dob= findViewById(R.id.date_of_birth_field);
        TextView phone = findViewById(R.id.cell_phone_field);
        TextView email = findViewById(R.id.email_field);

        name.setText(tenant.getFirstName() + " " + tenant.getLastName());
        room.setText("Room " + tenant.getRoom());
        gender.setText(tenant.getGender());
        dob.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(tenant.getBirthdate()));
        phone.setText(String.valueOf(tenant.getPhone()).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
        email.setText(tenant.getEmail());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Fetch the property name from the database since it might change!
        database.getReference("property").child(tenant.getPropertyId() + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                TextView property = findViewById(R.id.property_name_field);
                property.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /* Fetch the image from Firebase Storage and sets it to imageButton */
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        if (tenant.getImagePath() != null && tenant.getImagePath().length() != 0) {
            StorageReference islandRef = storageRef.child(tenant.getImagePath());

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

    public void getTenantFromID(String tenantID){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference("users/" + tenantID);
        userRef.orderByKey().addValueEventListener(new ValueEventListener() {
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
}
