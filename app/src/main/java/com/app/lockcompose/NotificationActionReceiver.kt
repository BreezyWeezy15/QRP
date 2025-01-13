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

    companion object {

        const val ACTION_YES = "ACTION_YES"
        const val ACTION_NO = "ACTION_NO"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)

        Extras.showReceiverNotification(context)

        when (intent.action) {
            ACTION_YES -> {
                updatePermission(context, "Yes")
                notificationManager.cancel(Extras.NOTIFICATION_ID)
            }
            ACTION_NO -> {
                updatePermission(context, "No")
                notificationManager.cancel(Extras.NOTIFICATION_ID)
            }
            "TRIGGER_NOTIFICATION" -> {

            }
        }
    }

    private fun updatePermission(context: Context, answer: String) {
        val map = hashMapOf<String, Any>()
        map["answer"] = answer
        map["type"] = "Custom"

        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        firebaseDatabase
            .child("Permissions")
            .child(SharedPreferencesHelper.getSelectedDevice(context)!!.deviceId!!)
            .setValue(map)
            .addOnSuccessListener {
                Log.d("TAG", "Permission updated to $answer")
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to update permission")
            }
    }


}

