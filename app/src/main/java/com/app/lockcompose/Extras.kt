package com.app.lockcompose

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object Extras {

    const val CHANNEL_ID = "parent_permission_channel"
     const val NOTIFICATION_ID = 1


    @SuppressLint("MissingPermission")
    fun showReceiverNotification(context: Context) {

        val yesIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_YES
        }
        val yesPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val noIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_NO
        }
        val noPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            noIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Parent Permission")
            .setContentText("Allow the child to use the phone?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(NotificationCompat.Action(0, "Yes", yesPendingIntent))
            .addAction(NotificationCompat.Action(0, "No", noPendingIntent))

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
     fun showNotification(context: Context): NotificationCompat.Builder {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Listening to parent permission")
            .setContentText("Allow the child to use the phone?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

       return builder
    }
}