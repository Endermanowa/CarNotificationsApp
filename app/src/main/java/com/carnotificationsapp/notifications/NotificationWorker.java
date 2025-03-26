package com.carnotificationsapp.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.carnotificationsapp.R;
import com.carnotificationsapp.Vehicle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotificationWorker extends Worker {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY = "cars";
    private SharedPreferences preferences;
    private List<Vehicle> vehicleList; // This should be populated from the database

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Result doWork() {
        checkAndSendNotifications();
        return Result.success();
    }

    private void checkAndSendNotifications() {
        loadVehicleList();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar today = Calendar.getInstance();

        for (Vehicle vehicle : vehicleList) {
            try {
                if (vehicle.ocDate <= 0 || vehicle.maintenanceDate <= 0) continue;

                Date ocDate = new Date(vehicle.ocDate);
                Date maintenanceDate = new Date(vehicle.maintenanceDate);

                // Handle notifications for OC Date
                handleNotificationForDate(ocDate, vehicle.name, "OC");

                // Handle notifications for Maintenance Date
                handleNotificationForDate(maintenanceDate, vehicle.name, "Maintenance");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNotificationForDate(Date targetDate, String vehicleName, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar today = Calendar.getInstance();

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(targetDate);

        long diffInMillis = targetCalendar.getTimeInMillis() - today.getTimeInMillis();
        long daysRemaining = diffInMillis / (1000 * 60 * 60 * 24);

        String message = String.format("%s for car %s is on %s", type, vehicleName, sdf.format(targetDate));

        // Send notifications based on days remaining
        if (daysRemaining <= 30 && daysRemaining > 14) {
            sendNotification(type + " in a month", message);
        } else if (daysRemaining <= 14 && daysRemaining > 7) {
            sendNotification(type + " in 2 weeks", message);
        } else if (daysRemaining <= 7 && daysRemaining > 0) {
            sendNotification(type + " in a week", message);
        } else if (daysRemaining <= 0 && Objects.equals(type, "OC")) {
            sendNotification(type + " very urgent!!!", message);
        } else if (daysRemaining == 0 && Objects.equals(type, "Maintenance")) {
            sendNotification(type + " is outdated", message);
        }
    }

    private void sendNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "notify_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Use a unique notification ID for each vehicle
        int notificationId = (int) System.currentTimeMillis(); // Unique ID based on current time
        if (manager != null) {
            manager.notify(notificationId, notification);
        }
    }

    private void loadVehicleList() {
        String json = preferences.getString(KEY, null);
        Gson gson = new Gson();
        vehicleList = new ArrayList<>();
        vehicleList = json != null ? gson.fromJson(json, new TypeToken<List<Vehicle>>() {}.getType()) : new ArrayList<>();
    }

}
