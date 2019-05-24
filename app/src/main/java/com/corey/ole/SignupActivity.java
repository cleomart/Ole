package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private String gender;

    private String month;
    private String day;
    private String year;
    private Date birthday;

    private String email;
    private String password;
    private String phone;
    private int accountType;
    private String propertyCode;
    private String roomCode = "100A";
    private String uid;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbUsersRef;

    private static final String TAG = "SignupEmailPassword";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_signup_personal_info);

        prepGenderSpiner();

        mAuth = FirebaseAuth.getInstance();
        dbUsersRef = database.getReference("users");

    }

    public void onNextClick(View view) {
        boolean savedDetailsSuccess = savePersonalDetails();
        if (savedDetailsSuccess) {
            setContentView(R.layout.app_bar_signup_account_info);
        } else {
            // TODO: raise dialog
        }

    }

    private void prepGenderSpiner() {
        Spinner genderSpinner = findViewById(R.id.genderSpinner);
        List<String> list = new ArrayList<>();
        list.add("Male");
        list.add("Female");
        list.add("Nonbinary");
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        genderSpinner.setAdapter(dataAdapter);

        if (gender != null) {
            genderSpinner.setSelection(dataAdapter.getPosition(gender));
        }
    }

    private boolean validatePersonalDetails(EditText firstNameField, EditText lastNameField) {
        boolean valid = true;
        if (firstName.length() <= 0) {
            firstNameField.setError("First Name Required.");
            valid = false;
        } else {
            firstNameField.setError(null);
        }

        if (lastName.length() <= 0) {
            lastNameField.setError("Last Name Required.");
            valid = false;
        } else {
            lastNameField.setError(null);
        }

        return valid;
    }

    private boolean savePersonalDetails() {

        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        Spinner genderSpinner = findViewById(R.id.genderSpinner);
        EditText birthMonthField = findViewById(R.id.birthMonthField);
        EditText birthDayField = findViewById(R.id.birthDayField);
        EditText birthYearField = findViewById(R.id.birthYearField);
        TextView dobHeader = findViewById(R.id.dobHeader);

        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        gender = genderSpinner.getSelectedItem().toString();


        month = birthMonthField.getText().toString();
        day = birthDayField.getText().toString();
        year = birthYearField.getText().toString();
        String birthdayString =  month + "/" + day + "/" + year;

        boolean valid = validatePersonalDetails(firstNameField, lastNameField);

        try {
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            format.setLenient(false);
            birthday = format.parse(birthdayString);
            dobHeader.setError(null);
        } catch (ParseException e) {
            e.printStackTrace();
            // TODO: improper date formatting
            dobHeader.setError("Invalid Date of Birth");
            return false;
        }

        return valid;
    }



    private boolean validateAccountDetails(TextView emailField,
                                           TextView passwordField,
                                           TextView phoneField,
                                           RadioGroup accountTypeField,
                                           TextView accountTypeHeader) {
        boolean valid = true;

        String emailRegex = "^(.+)@(.+)$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else if (!emailPattern.matcher(email).matches()) {
            emailField.setError("Please enter a valid email.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        String phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
        Pattern phonePattern = Pattern.compile(phoneRegex);

        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Required.");
            valid = false;
        } else if (!phonePattern.matcher(phone).matches()) {
            phoneField.setError("Please enter a valid 10 digit phone number.");
            valid = false;
        } else {
            phoneField.setError(null);
        }

        if (accountTypeField.getCheckedRadioButtonId() == -1) {
            accountTypeHeader.setError("Account Type is required.");
            valid = false;
        } else {
            accountTypeHeader.setError(null);
        }


        return valid;
    }


    public void onFinishClick(View view) {
        boolean saveAccountDetailsSuccess = saveAccountDetails();

        if (saveAccountDetailsSuccess) {
            createAccount();
        } else {
          // Do nothing
        }
    }

    public void backToPersonalDetails(View view) {
        setContentView(R.layout.app_bar_signup_personal_info);

        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        prepGenderSpiner();
        EditText birthMonthField = findViewById(R.id.birthMonthField);
        EditText birthDayField = findViewById(R.id.birthDayField);
        EditText birthYearField = findViewById(R.id.birthYearField);


        firstNameField.setText(firstName);
        lastNameField.setText(lastName);

        birthMonthField.setText(month);
        birthDayField.setText(day);
        birthYearField.setText(year);
    }


    private boolean saveAccountDetails() {
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        EditText phoneField = findViewById(R.id.phoneField);
        RadioGroup accountTypeField = findViewById(R.id.accountTypeField);
        EditText propertyCodeField = findViewById(R.id.propertyCodeField);
        TextView accountTypeHeader = findViewById(R.id.accountTypeHeader);

        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        phone = phoneField.getText().toString();

        boolean validAccountDetails = validateAccountDetails(emailField, passwordField, phoneField, accountTypeField, accountTypeHeader);

        if (!validAccountDetails) {
            return false;
        }

        if (accountTypeField.getCheckedRadioButtonId() == R.id.tenantRadioButton) {
            accountType = 1;
            propertyCode = propertyCodeField.getText().toString().trim();
        } else if (accountTypeField.getCheckedRadioButtonId() == R.id.landlordRadioButton) {
            accountType = 2;
            propertyCode = null;
        } else {
            return false;
        }

        return true;
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            uid = user.getUid();
                            addUser(accountType);

                            if (accountType == 1) {
                                tenantLogin(user.getUid());
                            } else if (accountType == 2) {
                                landlordLogin(user.getUid());
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            // TODO: launch signup error
                        }

                        // ...
                    }
                });
    }

    private void addUser(int accountType) {
        if (accountType == 1) {
            TenantProfile tenant = new TenantProfile(uid, firstName, lastName,
                    gender, birthday, phone, email, null, propertyCode, roomCode, new ArrayList<Repair>(), new Rent());
            dbUsersRef.child(uid).setValue(tenant);
        } else if (accountType == 2) {
            LandlordProfile landlord = new LandlordProfile(firstName, lastName, email, gender, birthday, phone, null, null);
            dbUsersRef.child(uid).setValue(landlord);
        }
    }

    public void tenantLogin(String uid) {
        Intent intent = new Intent(this, TenantHomeActivity.class);
        intent.putExtra("tenantId", uid);
        startActivity(intent);
    }

    public void landlordLogin(String uid) {
        Intent intent = new Intent(this, LandlordHomeActivity.class);
        intent.putExtra("landlordId", uid);
        startActivity(intent);
    }

    private void addTenantToProperty() {
        database.getReference("property/" + propertyCode + "/tenants").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> tenants = (ArrayList<String>) dataSnapshot.getValue();
                            tenants.add(uid);
                            database.getReference("property/" + propertyCode + "/tenants").setValue(tenants);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
