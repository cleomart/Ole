package com.corey.ole;

import java.util.Date;
import java.util.UUID;

public class Repair {

    private Date date;
    private String id;
    private String request;
    private boolean isFixed;
    private String name;
    private String email;
    private String room;
    private String repairer;
    private String tenantId;
    private Date dateSchedule;


    public Repair(String request,String tenantId, String name, String email, String room, Date date) {
        this.id = UUID.randomUUID().toString();
        this.tenantId = tenantId;
        this.request = request;
        this.name = name;
        this.email = email;
        this.room = room;
        this.date = date;
        this.isFixed = false;
    }

    public Repair() {

    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRequest() {
        return request;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRepairer() {
        return repairer;
    }

    public void setRepairer(String repairer) {
        this.repairer = repairer;
    }

    public Date getDateSchedule() {
        return dateSchedule;
    }

    public void setDateSchedule(Date dateSchedule) {
        this.dateSchedule = dateSchedule;
    }
}
