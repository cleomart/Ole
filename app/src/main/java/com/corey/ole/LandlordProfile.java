package com.corey.ole;

import java.util.ArrayList;
import java.util.Date;

public class LandlordProfile {

    public static final String LANDLORD_ID = "landlordId";
    private int accountType = 2;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private Date birthdate;
    private String imagePath;
    private String phone;
    private ArrayList<String> properties;

    public LandlordProfile(String firstName, String lastName, String email, String gender, Date birthdate, String phone, String imagePath, ArrayList<String> properties) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.phone = phone;

        if (imagePath == null) {
            this.imagePath = "default/images/default_profile_picture.jpg";
        } else {
            this.imagePath = imagePath;
        }

        this.birthdate = birthdate;
        if (properties == null) {
            this.properties = new ArrayList<>();
        } else {
            this.properties = properties;
        }
    }

    public LandlordProfile() {
    }

    public int getAccountType() {
        return accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }
}
