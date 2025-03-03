package com.carnotificationsapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicles")
public class Vehicle {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String licensePlate;
    public long ocDate; // Stored as timestamp (milliseconds)
    public long maintenanceDate; // Stored as timestamp (milliseconds)

    public Vehicle(String name, String licensePlate, long ocDate, long maintenanceDate) {
        this.name = name;
        this.licensePlate = licensePlate;
        this.ocDate = ocDate;
        this.maintenanceDate = maintenanceDate;
    }

    public void setOcDate(long date) {
        ocDate = date;
    }

    public void setMaintenanceDate(long date) {
        maintenanceDate = date;
    }
}

