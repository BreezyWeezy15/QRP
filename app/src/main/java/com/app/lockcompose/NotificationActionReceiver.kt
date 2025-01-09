package com.app.lockcompose


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationActionReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "parent_permission_channel"
    private val NOTIFICATION_ID = 1

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)
        getPermission(context)
        when (intent.action) {
            "ACTION_YES" -> {
                updatePermission("Yes")
                notificationManager.cancel(NOTIFICATION_ID)
            }
            "ACTION_NO" -> {
                updatePermission( "No")
                notificationManager.cancel(NOTIFICATION_ID)
            }
        }
    }

    private fun updatePermission(answer: String) {
        val map = hashMapOf<String, Any>()
        map["answer"] = answer
        map["type"] = "Custom"

        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        firebaseDatabase
            .child("Permissions")
            .setValue(map)
            .addOnSuccessListener {
                Log.d("TAG", "Permission updated to $answer")
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to update permission")
            }
    }
    private fun getPermission(context: Context) {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        firebaseDatabase
            .child("Permissions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        val data = dataSnapShot.child("answer").getValue(String::class.java)
                        if (!data.isNullOrEmpty()) {
                            showNotification(context)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Parent Permission Channel"
            val descriptionText = "Channel for parent permission notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    @SuppressLint("MissingPermission")
    fun showNotification(context: Context) {


        createNotificationChannel(context)

        val yesIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_YES"
        }
        val yesPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val noIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_NO"
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



}
