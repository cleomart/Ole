package com.corey.ole;

public class Rent {
    private int baseRent;
    private int water;
    private int trash;
    private int electricity;
    private int internet;
    private String url;
    private int dayOfMonth;

    public Rent() {
        baseRent = 0;
        water = 0;
        trash = 0;
        electricity = 0;
        internet = 0;
        url = "";
        dayOfMonth = 1;
    }

    public Rent(int baseRent, int water, int trash, int electricity, int internet, String url, int dayOfMonth) {
        assert (dayOfMonth > 0 && dayOfMonth <= 28);
        this.baseRent = baseRent;
        this.water = water;
        this.trash = trash;
        this.electricity = electricity;
        this.internet = internet;
        this.url = url;
        this.dayOfMonth = dayOfMonth;
    }

    public int getTotal() {
        return baseRent + water + trash + electricity + internet;
    }

    public int getBaseRent() {
        return baseRent;
    }

    public int getElectricity() {
        return electricity;
    }

    public int getInternet() {
        return internet;
    }

    public int getTrash() {
        return trash;
    }

    public int getWater() {
        return water;
    }

    public String getUrl() {
        return url;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setBaseRent(int baseRent) {
        this.baseRent = baseRent;
    }

    public void setElectricity(int electricity) {
        this.electricity = electricity;
    }

    public void setInternet(int internet) {
        this.internet = internet;
    }

    public void setTrash(int trash) {
        this.trash = trash;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDayOfMonth(int dayOfMonth) {
        assert (dayOfMonth > 0 && dayOfMonth <= 28);
        this.dayOfMonth = dayOfMonth;
    }
}
