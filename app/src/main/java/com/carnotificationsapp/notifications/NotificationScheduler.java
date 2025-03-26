package com.carnotificationsapp.notifications;

import android.content.Context;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    private Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
    }

    // This method schedules the periodic notification worker
    public void scheduleNotification() {
        // Create a PeriodicWorkRequest to check notifications every day
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                .build();

        // Enqueue the work to be executed by WorkManager
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
