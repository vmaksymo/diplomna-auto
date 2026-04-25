package com.example.auto;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notifications {

    public void show_notification(Context context, String title, String message) {


        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification =
                new NotificationCompat.Builder(context, "myChannelID")
                        .setSmallIcon(R.drawable.logo_app)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .build();

        manager.notify((int) System.currentTimeMillis(), notification);
    }
}
