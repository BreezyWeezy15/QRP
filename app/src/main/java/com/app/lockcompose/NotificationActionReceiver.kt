package com.app.lockcompose

import NOTIFICATION_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)

        when (intent.action) {
            "ACTION_YES" -> {
                updatePermission(context, "Yes")
                notificationManager.cancel(NOTIFICATION_ID)
            }
            "ACTION_NO" -> {
                updatePermission(context, "No")
                notificationManager.cancel(NOTIFICATION_ID)
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
            .setValue(map)
            .addOnSuccessListener {
                Log.d("TAG", "Permission updated to $answer")
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to update permission")
            }
    }
}
