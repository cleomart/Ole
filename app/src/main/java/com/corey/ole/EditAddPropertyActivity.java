package com.corey.ole;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditAddPropertyActivity extends NavDrawerActivity {

    private TextView nameView;
    private TextView streetView;
    private TextView cityStateZipView;
    private ImageButton imageButton;
    private Button cameraButton;
    private Button galleryButton;
    private AlertDialog imageButtonDialog;
    private Property thisProperty;
    private String propertyId;
    private String landlordId;
    private ArrayList<String> policies;
    private ArrayList<String> notes;
    private boolean isAddProperty;
    private FirebaseDatabase database;
    private DatabaseReference propertyRef;
    private Bitmap imageBitmap;
    private ArrayList<String> propertyIds;
    private StorageReference storageRef;


    private static final int REQUEST_TAKE_PHOTO = 2;
    private String currentPhotoPath;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_property);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        isAddProperty = intent.getBooleanExtra("isAdd", false);
        propertyId = intent.getStringExtra("propertyId");
        landlordId = intent.getStringExtra("landlordId");

        if (isAddProperty) {
            toolbar.setTitle("Add Property");
            thisProperty = new Property();
        } else {
            toolbar.setTitle("Edit Property");
        }
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        policies = new ArrayList<>();
        notes = new ArrayList<>();
        imageBitmap = null;

        imageButton = findViewById(R.id.edit_property_image);
        nameView = findViewById(R.id.edit_property_name);
        streetView = findViewById(R.id.edit_property_street);
        cityStateZipView = findViewById(R.id.edit_property_csz);
        database = FirebaseDatabase.getInstance();
        propertyRef = database.getReference("property");
        storageRef = FirebaseStorage.getInstance().getReference();

        if (!isAddProperty) {
            DatabaseReference property = propertyRef.child(propertyId);
            property.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    thisProperty = dataSnapshot.getValue(Property.class);
                    nameView.setText(thisProperty.getName());
                    streetView.setText(thisProperty.getStreet());
                    cityStateZipView.setText(thisProperty.getCityStateZip());
                    /* Fetch the image from Firebase Storage and sets it to imageButton */
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    if (thisProperty.getImagePath() != null && thisProperty.getImagePath().length() != 0) {
                        StorageReference islandRef = storageRef.child(thisProperty.getImagePath());

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
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } else {
            DatabaseReference landlordRef = database.getReference("users").child(landlordId);
            DatabaseReference propertiesRef = landlordRef.child("properties");
            propertiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    propertyIds = dataSnapshot.getValue(t);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        }



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }

        });


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

    private void saveProperty() {
        String name = nameView.getText().toString();
        if (name.length() == 0) {
            Toast.makeText(EditAddPropertyActivity.this, "Name Field Required",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        thisProperty.setName(name);

        String street = streetView.getText().toString();
        if (street.length() == 0) {
            Toast.makeText(EditAddPropertyActivity.this, "Street Field Required",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        thisProperty.setStreet(street);

        String city = cityStateZipView.getText().toString();
        if (city.length() == 0) {
            Toast.makeText(EditAddPropertyActivity.this, "Street Field Required",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        thisProperty.setCityStateZip(city);
        thisProperty.setPolicies(policies);
        thisProperty.setNotes(notes);

        String path = thisProperty.getId() + "/images/profile.jpg";
        thisProperty.setImagePath(path);
        saveImageToFirebaseStorage();

        DatabaseReference property = propertyRef.child(thisProperty.getId());
        property.setValue(thisProperty);

        if (isAddProperty) {
            DatabaseReference landlordRef = database.getReference("users").child(landlordId);
            DatabaseReference propertiesRef = landlordRef.child("properties");
            propertyIds.add(thisProperty.getId());
            propertiesRef.setValue(propertyIds);

        }

        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra("propertyId", thisProperty.getId());
        intent.putExtra("landlordId", landlordId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void saveImageToFirebaseStorage() {
        if (imageBitmap == null) {
            return;

        }
        String path = thisProperty.getId() + "/images/profile.jpg";
        thisProperty.setImagePath(path);

        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        imageBitmap.recycle();
        imageRef.putBytes(byteArray)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
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
            saveProperty();
        }

        return super.onOptionsItemSelected(item);
    }

}
