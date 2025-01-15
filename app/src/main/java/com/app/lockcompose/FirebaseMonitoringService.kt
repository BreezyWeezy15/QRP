package com.app.lockcompose


import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.app.lockcompose.Extras.NOTIFICATION_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseMonitoringService : Service() {

    private var randomId = 0L

    override fun onCreate() {
        super.onCreate()
        startForeground(1005,Extras.showNotification(this).build())
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        observeFirebaseChanges()
        return START_STICKY
    }

    private fun observeFirebaseChanges() {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference

        if(SharedPreferencesHelper.getSelectedDevice(this) != null){
            firebaseDatabase
                .child("Permissions")
                .child(SharedPreferencesHelper.getSelectedDevice(this)!!.deviceId!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapShot: DataSnapshot) {
                        if (dataSnapShot.exists()) {
                            val data = dataSnapShot.child("answer").getValue(String::class.java)
                            val id = dataSnapShot.child("id").getValue(Long::class.java)
                            if (!data.isNullOrEmpty() && (id != null && randomId != id)) {
                                randomId = id
                                Extras.showReceiverNotification(this@FirebaseMonitoringService)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", "Database Error: ${error.message}")
                    }
                })
        }



    }


    override fun onBind(intent: Intent?): IBinder? = null
}

