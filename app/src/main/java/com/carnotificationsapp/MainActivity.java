package com.carnotificationsapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY = "cars";
    private SharedPreferences preferences;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList; // This should be populated from the database
    long ocDate = (long)0;
    String ocString = "";
    long maintenanceDate = (long)0;
    String maintenanceString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Button buttonAddVehicle = findViewById(R.id.buttonAddVehicle);
        Button buttonDeleteVehicle = findViewById(R.id.buttonDeleteVehicle);
        Button buttonChangeOc = findViewById(R.id.buttonChangeOcDate);
        Button buttonChangeMaintenance = findViewById(R.id.buttonChangeMaintenanceDate);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        loadList();

        // Initialize the adapter
        vehicleAdapter = new VehicleAdapter(this, vehicleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(vehicleAdapter);


        buttonAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddVehicleDialog();
            }
        });

        buttonDeleteVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedVehicle();
            }
        });

        buttonChangeOc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeOcDialog();
            }
        });

        buttonChangeMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeMaintenanceDialog();
            }
        });
    }

    private void saveList() {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(vehicleList);
        editor.putString(KEY, json);
        editor.apply();
    }

    private void loadList() {
        String json = preferences.getString(KEY, null);
        Gson gson = new Gson();
        vehicleList = new ArrayList<>();
        vehicleList = json != null ? gson.fromJson(json, new TypeToken<List<Vehicle>>() {}.getType()) : new ArrayList<>();
    }

    private void showAddVehicleDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_vehicle, null);

        EditText editTextName, editTextLicensePlate;
        Button buttonOcDate, buttonMaintenanceDate;

        // Initialize views in the dialog
        editTextName = dialogView.findViewById(R.id.editTextName);
        editTextLicensePlate = dialogView.findViewById(R.id.editTextLicensePlate);
        buttonOcDate = dialogView.findViewById(R.id.buttonOcDate);
        buttonMaintenanceDate = dialogView.findViewById(R.id.buttonMaintenanceDate);

        // Set date pickers for the OC Date and Maintenance Date
        buttonOcDate.setOnClickListener(v -> showDatePickerDialogAdd(true, buttonOcDate));
        buttonMaintenanceDate.setOnClickListener(v -> showDatePickerDialogAdd(false, buttonMaintenanceDate));

        // Create AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Add New Vehicle")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    // Get the data from the input fields
                    String name = editTextName.getText().toString();
                    String licensePlate = editTextLicensePlate.getText().toString();

                    // Validate the input fields
                    if (name.isEmpty() || licensePlate.isEmpty() || ocDate == 0 || maintenanceDate == 0) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create the vehicle object with timestamp dates
                    Vehicle newVehicle = new Vehicle(name, licensePlate, ocDate, maintenanceDate);

                    vehicleList.add(newVehicle);
                    saveList();
                    loadList();
                    vehicleAdapter.updateList(vehicleList);

                    Toast.makeText(this, "Vehicle added successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void removeSelectedVehicle() {
        int selectedPosition = vehicleAdapter.getSelectedPosition();
        if (selectedPosition != RecyclerView.NO_POSITION) {
            vehicleList.remove(selectedPosition);
            saveList();
            loadList();
            vehicleAdapter.updateList(vehicleList);
            vehicleAdapter.notifyItemRemoved(selectedPosition);
            vehicleAdapter.clearSelection(); // Clear selection after removing
        }
    }

    private void showChangeOcDialog() {
        int selectedPosition = vehicleAdapter.getSelectedPosition();
        if (selectedPosition != RecyclerView.NO_POSITION) {
            showDatePickerDialogChange(true);
            vehicleList.get(selectedPosition).setOcDate(ocDate);
            saveList();
            loadList();
            vehicleAdapter.updateList(vehicleList);
            vehicleAdapter.notifyItemChanged(selectedPosition);
            vehicleAdapter.clearSelection(); // Clear selection after removing
        }
    }

    private void showChangeMaintenanceDialog() {
        int selectedPosition = vehicleAdapter.getSelectedPosition();
        if (selectedPosition != RecyclerView.NO_POSITION) {
            showDatePickerDialogChange(false);
            vehicleList.get(selectedPosition).setMaintenanceDate(maintenanceDate);
            saveList();
            loadList();
            vehicleAdapter.updateList(vehicleList);
            vehicleAdapter.notifyItemChanged(selectedPosition);
            vehicleAdapter.clearSelection(); // Clear selection after removing
        }
    }

    private void showDatePickerDialogChange(boolean isOcDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
                    long selectedTimestamp = selectedDate.getTimeInMillis();

                    String formattedDate = String.format("%d/%d/%d", dayOfMonth, month + 1, year);

                    if (isOcDate) {
                        ocDate = selectedTimestamp;
                        ocString = formattedDate;
                    } else {
                        maintenanceDate = selectedTimestamp;
                        maintenanceString = formattedDate;
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showDatePickerDialogAdd(boolean isOcDate, Button button) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
                    long selectedTimestamp = selectedDate.getTimeInMillis();

                    String formattedDate = String.format("%d/%d/%d", dayOfMonth, month + 1, year);

                    if (isOcDate) {
                        ocDate = selectedTimestamp;
                        ocString = formattedDate;
                    } else {
                        maintenanceDate = selectedTimestamp;
                        maintenanceString = formattedDate;
                    }

                    // Update the button text **after** the user picks a date
                    button.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
