package com.example.android.fwd_thirdproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationsHelper {

    private const val ADMINISTER_REQUEST_CODE = 2022

    /**
     * Sets up the notification channels for API 26+.
     * Note: This uses package name + channel name to create unique channelId's.
     *
     * @param context     application context
     * @param importance  importance level for the notificaiton channel
     * @param showBadge   whether the channel should have a notification badge
     * @param name        name for the notification channel
     * @param description description for the notification channel
     */
    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //TODO: STEP[1] create a new notification channel
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            //TODO: STEP[1] create a new notification channel
        }
    }

    /**
     * Helps issue the default application channels (package name + app name) notifications.
     * Note: this shows the use of [NotificationCompat.BigTextStyle] for expanded notifications.
     *
     * @param context    current application context
     * @param title      title for the notification
     * @param message    content text for the notification when it's not expanded
     * @param bigText    long form text for the expanded notification
     * @param autoCancel `true` or `false` for auto cancelling a notification.
     * if this is true, a [PendingIntent] is attached to the notification to
     * open the application.
     */

    fun NotificationManager.createNotification(
        context: Context, title: String, message: String,
        autoCancel: Boolean, fileName: String, status: String
    ) {

        //TODO: STEP[3] Create a new notification
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {

            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(autoCancel)
            //      setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT

            val pendingIntent: PendingIntent?
            val intent = Intent(context, Detail::class.java)
         //   Toast.makeText(context,fileName+status,Toast.LENGTH_SHORT).show()
            intent.putExtra("fileName", fileName)
            intent.putExtra("status", status)
            pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(context, 0, intent, FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }

            setContentIntent(pendingIntent)
        }

        val detailsActivity = Intent(
            context,
            Detail::class.java
        )

        detailsActivity.action = context.getString(R.string.custom_action)
        detailsActivity.putExtra("notification_id", 123123123)
        detailsActivity.putExtra("fileName", fileName)
        detailsActivity.putExtra("status", status)
        val detailActivityPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, detailsActivity, FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                context,
                0,
                detailsActivity,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        notificationBuilder.addAction(
            R.drawable.ic_launcher_foreground,
            "Go to activity",
            detailActivityPendingIntent
        )


        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())

    }
}