package com.carnotificationsapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;
    private Context context;
    private int selectedPosition = RecyclerView.NO_POSITION; // Tracks selected item

    public VehicleAdapter(Context context, List<Vehicle> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        // Format the dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date ocDate = new Date(vehicle.ocDate);
        Date maintenanceDate = new Date(vehicle.maintenanceDate);
        Date currentDate = new Date();
        long ocFromNow = TimeUnit.MILLISECONDS.toDays(vehicle.ocDate - currentDate.getTime());
        long maintenanceFromNow = TimeUnit.MILLISECONDS.toDays(vehicle.maintenanceDate - currentDate.getTime());


        // Set the vehicle data into the views
        holder.textViewName.setText(vehicle.name);
        holder.textViewLicensePlate.setText("License Plate: " + vehicle.licensePlate);
        holder.textViewOcDate.setText("OC Date: " + dateFormat.format(vehicle.ocDate));
        holder.textViewMaintenanceDate.setText("Maintenance Date: " + dateFormat.format(vehicle.maintenanceDate));

        if (ocDate.before(currentDate)) {
            holder.textViewOcDate.setTextColor(Color.MAGENTA); // Date has passed
        } else if (ocFromNow <= 7) {
            holder.textViewOcDate.setTextColor(Color.RED); // Red (within 1 week)
        } else if (ocFromNow <= 14) {
            holder.textViewOcDate.setTextColor(Color.parseColor("#FFA500")); // Orange (within 2 weeks)
        } else if (ocFromNow <= 30) {
            holder.textViewOcDate.setTextColor(Color.YELLOW); // Yellow (within a month)
        } else {
            holder.textViewOcDate.setTextColor(Color.GREEN); // Green (more than a month away)
        }

        if (maintenanceDate.before(currentDate)) {
            holder.textViewMaintenanceDate.setTextColor(Color.MAGENTA); // Date has passed
        } else if (maintenanceFromNow <= 7) {
            holder.textViewMaintenanceDate.setTextColor(Color.RED); // Red (within 1 week)
        } else if (maintenanceFromNow <= 14) {
            holder.textViewMaintenanceDate.setTextColor(Color.parseColor("#FFA500")); // Orange (within 2 weeks)
        } else if (maintenanceFromNow <= 30) {
            holder.textViewMaintenanceDate.setTextColor(Color.YELLOW); // Yellow (within a month)
        } else {
            holder.textViewMaintenanceDate.setTextColor(Color.GREEN); // Green (more than a month away)
        }

        // Change background color and text color based on selection
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(0xFFE0E0E0); // Light gray for selected
            holder.textViewName.setTextColor(0xFF000000); // Black text color for selected item
            holder.textViewLicensePlate.setTextColor(0xFF000000); // Black text color for selected item
        } else {
            holder.itemView.setBackgroundColor(0x00000000); // Transparent for non-selected
            holder.textViewName.setTextColor(0xFF808080); // Default text color (gray) for non-selected items
            holder.textViewLicensePlate.setTextColor(0xFF808080); // Default text color (gray) for non-selected items
        }

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the selected position
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Notify adapter to refresh the views
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewLicensePlate, textViewOcDate, textViewMaintenanceDate;

        public VehicleViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLicensePlate = itemView.findViewById(R.id.textViewLicensePlate);
            textViewOcDate = itemView.findViewById(R.id.textViewOcDate);
            textViewMaintenanceDate = itemView.findViewById(R.id.textViewMaintenanceDate);
        }
    }

    // Method to get the selected position
    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Method to clear the selection
    public void clearSelection() {
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    // Method to update the list
    public void updateList(List<Vehicle> newList) {
        this.vehicleList = newList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

}
