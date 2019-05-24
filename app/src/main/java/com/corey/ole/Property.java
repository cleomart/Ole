package com.corey.ole;

import java.util.ArrayList;
import java.util.UUID;

public class Property {

    public static final String PROPERTY_ID = "propertyId";
    public static final String PROPERTY_NAME = "propertyName";
    private String id;
    private String name;
    private String street;
    private String cityStateZip;
    private String imagePath;     // image path in Firebase Storage
    private ArrayList<String> tenants;
    private ArrayList<String> policies;
    private ArrayList<String> notes;
    private ArrayList<String> announcements;
    private ArrayList<String> updates;


    Property(String name, String street, String cityStateZip, String imagePath,
             ArrayList<String> tenants, ArrayList<String> policies, ArrayList<String> notes,
             ArrayList<String> announcements, ArrayList<String> updates) {

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.street = street;
        this.cityStateZip = cityStateZip;
        this.imagePath = imagePath;
        this.tenants = tenants;
        this.policies = policies;
        this.notes = notes;
        this.announcements = announcements;
        this.updates = updates;
    }

    Property() {
        this.id = UUID.randomUUID().toString();
        this.name = null;
        this.street = null;
        this.cityStateZip = null;
        this.imagePath = "";
        this.tenants = null;
        this.policies = null;
        this.notes = null;
        this.announcements = null;
        this.updates = null;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCityStateZip() {
        return cityStateZip;
    }

    public void setCityStateZip(String cityStateZip) {
        this.cityStateZip = cityStateZip;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public int getNumTenants() {
        if (tenants != null) {
            return tenants.size();
        } else {
            return 0;
        }
    }

    public ArrayList<String> getTenants() {
        return tenants;
    }

    public void setTenants(ArrayList<String> tenants) {
        this.tenants = tenants;
    }

    public ArrayList<String> getPolicies() {
        return policies;
    }

    public void setPolicies(ArrayList<String> policies) {
        this.policies = policies;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public ArrayList<String> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(ArrayList<String> announcements) {
        this.announcements = announcements;
    }

    public ArrayList<String> getUpdates() {
        return updates;
    }

    public void setUpdates(ArrayList<String> updates) {
        this.updates = updates;
    }

    public void addAnnouncement(String a) {
        announcements.add(a);
    }

    public void removeAnnouncement(String a) {
        announcements.remove(a);
    }

    public void addPolicy(String p) {
        policies.add(p);
    }

    public void removePolicy(String p) {
        policies.remove(p);
    }

    public void addNote(String n) {
        notes.add(n);
    }

    public void removeNote(String n) {
        notes.remove(n);
    }

    public void addTenant(String id) {
        tenants.add(id);
    }

    public void removeTenant(String id) {
        tenants.remove(id);
    }

}
