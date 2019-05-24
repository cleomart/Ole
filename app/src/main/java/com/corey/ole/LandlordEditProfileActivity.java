package com.corey.ole;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LandlordEditProfileActivity extends NavDrawerActivity {

    private String mUid;
    private LandlordProfile landlord;
    private EditText name;
    private EditText room;
    private EditText gender;
    private EditText dob;
    private TextView email;
    private EditText phone;
    private TextView property;
    private FirebaseDatabase mDb;
    private ImageButton imageButton;
    private Bitmap imageBitmap;
    private Button galleryButton;
    private Button cameraButton;
    private AlertDialog imageButtonDialog;

    private static final int REQUEST_TAKE_PHOTO = 2;
    private String currentPhotoPath;
    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tenant_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.tenant_name_edit_field);
        room = findViewById(R.id.room_number_edit_field);
        gender = findViewById(R.id.gender_edit_field);
        dob = findViewById(R.id.date_of_birth_edit_field);
        phone = findViewById(R.id.cell_phone_edit_field);
        email = findViewById(R.id.email_edit_field);
        property = findViewById(R.id.property_name_edit_field);
        imageButton = findViewById(R.id.profile_picture_edit_field);

        mDb = FirebaseDatabase.getInstance();
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference("users").child(mUid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                landlord = dataSnapshot.getValue(LandlordProfile.class);
                loadLandlordData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            saveProfileImageToFirebaseStorage();
            updateLandlordData();
            Intent intent = new Intent(this, LandlordProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageButton.setImageBitmap(imageBitmap);
                imageButtonDialog.cancel();
            } else if (requestCode == PICK_IMAGE) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageButton.setImageBitmap(imageBitmap);
                imageButtonDialog.cancel();
            }
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_photo, null);
        cameraButton = view.findViewById(R.id.add_photo_camera);
        galleryButton = view.findViewById(R.id.add_button_gallery);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }

        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }

        });

        builder.setTitle("Add a photo");
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });

        imageButtonDialog = builder.create();
        imageButtonDialog.getWindow().setLayout(300, 150);
        imageButtonDialog.show();
    }


    protected void updateLandlordData() {
        DatabaseReference userRef = mDb.getReference("users/" + mUid);
        String[] n = name.getText().toString().split(" ");
        if (n.length == 0) {
            // Do nothing
        } else if (n.length == 1) {
            landlord.setFirstName(n[0]);
            landlord.setLastName("");
        } else {
            landlord.setFirstName(n[0]);
            landlord.setLastName(n[1]);
        }

        if (!TextUtils.isEmpty(gender.getText().toString())) {
            landlord.setGender(gender.getText().toString());
        }
        if (!TextUtils.isEmpty(dob.getText().toString())) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
            try {
                Date date = format.parse(dob.getText().toString());
                landlord.setBirthdate(date);
            } catch (ParseException e) {
                // Do nothing
            }
        }

        String num = phone.getText().toString();
        num = num.replaceAll("[^\\d]", "" );
        if (!TextUtils.isEmpty(num)) {
            landlord.setPhone(num);
        }

        userRef.setValue(landlord);
    }

    private void loadLandlordData() {
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
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageButton.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageButton.getWidth(),
                            imageButton.getHeight(), false));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }


    private void saveProfileImageToFirebaseStorage() {
        if (imageBitmap == null) {
            return;
        }
        String path = mUid + "/images/profile.jpg";
        landlord.setImagePath(path);

        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        imageBitmap.recycle();
        imageRef.putBytes(byteArray);
    }
}
