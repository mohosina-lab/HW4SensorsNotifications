package com.example.hw4sensorsnotifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // When the notification is tapped, open the app (MainActivity)
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = inputData.getString("title") ?: "HW4 Notification"
        val text = inputData.getString("text") ?: "Background notification triggered"

        val notification = NotificationCompat.Builder(applicationContext, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text)) // helps show it clearly as I was facing struggle to see the notification
            .setPriority(NotificationCompat.PRIORITY_HIGH)             // pre-O behavior
            .setDefaults(NotificationCompat.DEFAULT_ALL)               // sound
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val nm = NotificationManagerCompat.from(applicationContext)
        if (nm.areNotificationsEnabled()) {
            nm.notify(1001, notification)
        }
        return Result.success()
    }
}
