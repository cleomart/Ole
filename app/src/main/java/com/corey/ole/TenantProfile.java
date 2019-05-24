package com.corey.ole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

// custom class made for storing a tenant object.
public class TenantProfile {

    public static final String EXTRA_TENANT_ID = "tenantID";

    private final int accountType = 1;
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthdate;
    private String phone;
    private String email;
    private String imagePath;
    private String propertyId;
    private String room;
    private ArrayList<Repair> repairs;
    private Rent rent;


    public TenantProfile(String id, String firstName, String lastName, String gender, Date birthdate, String phone, String email, String imagePath,
                         String propertyID, String room, ArrayList<Repair> repairs, Rent rent) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthdate = birthdate;
        this.phone = phone;
        this.email = email;

        if (imagePath == null) {
            this.imagePath = "default/images/default_profile_picture.jpg";
        } else {
            this.imagePath = imagePath;
        }
        this.propertyId = propertyID;
        this.room = room;
        this.repairs = repairs;
        this.rent = rent;
    }

    public TenantProfile() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Repair> getRepairs() {
        if (repairs != null) {
            Collections.sort(repairs, new Comparator<Repair>() {
                @Override
                public int compare(Repair repair, Repair t1) {
                    return t1.getDate().compareTo(repair.getDate());
                }
            });
        }
        return repairs;
    }

    public void setRepairs(ArrayList<Repair> repairs) {
        this.repairs = repairs;
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

    public String getGender() {
        return gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }


    public String getRoom() {
        return room;
    }

    public Rent getRent() {
        return rent;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setRoom(String room) {
        this.room = room;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    public void addRepair(Repair repair) {
        if (this.repairs == null)
           this.repairs = new ArrayList<>();

        this.repairs.add(repair);
    }

//
//    public static TenantProfile makeTestTenants(int propertyID) {
//        TenantProfile tenant = new TenantProfile("1", "John Smith", "Male",
//                new Date(System.currentTimeMillis() - (25 * 60 * 60 * 1000)),
//                1234567890,
//                "email@email.com",
//                null,
//                propertyID,
//                "212");
//        return tenant;
//    }

}


