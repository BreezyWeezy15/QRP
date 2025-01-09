package com.app.lockcompose


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
                updatePermission("Yes")
                notificationManager.cancel(1)
            }
            "ACTION_NO" -> {
                updatePermission( "No")
                notificationManager.cancel(1)
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



}
