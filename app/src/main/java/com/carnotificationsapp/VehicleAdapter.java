package com.carnotificationsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        String ocDate = dateFormat.format(vehicle.ocDate);
        String maintenanceDate = dateFormat.format(vehicle.maintenanceDate);

        // Set the vehicle data into the views
        holder.textViewName.setText(vehicle.name);
        holder.textViewLicensePlate.setText("License Plate: " + vehicle.licensePlate);
        holder.textViewOcDate.setText("OC Date: " + ocDate);
        holder.textViewMaintenanceDate.setText("Maintenance Date: " + maintenanceDate);

        // Change background color and text color based on selection
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(0xFFE0E0E0); // Light gray for selected
            holder.textViewName.setTextColor(0xFF000000); // Black text color for selected item
            holder.textViewLicensePlate.setTextColor(0xFF000000); // Black text color for selected item
            holder.textViewOcDate.setTextColor(0xFF000000); // Black text color for selected item
            holder.textViewMaintenanceDate.setTextColor(0xFF000000); // Black text color for selected item
        } else {
            holder.itemView.setBackgroundColor(0x00000000); // Transparent for non-selected
            holder.textViewName.setTextColor(0xFF808080); // Default text color (gray) for non-selected items
            holder.textViewLicensePlate.setTextColor(0xFF808080); // Default text color (gray) for non-selected items
            holder.textViewOcDate.setTextColor(0xFF808080); // Default text color (gray) for non-selected items
            holder.textViewMaintenanceDate.setTextColor(0xFF808080); // Default text color (gray) for non-selected items
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
