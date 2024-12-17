package com.app.lockcompose

import AddProfileScreen
import ShowAppList
import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.lockcompose.screens.MainScreen
import com.app.lockcompose.screens.Profiles
import com.app.lockcompose.ui.theme.LockComposeTheme


class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission()

        SharedPreferencesHelper.deleteDeviceInfoList(this)
        setContent {
            LockComposeTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("showAppList") { ShowAppList() }
                    composable("main") { MainScreen(navController) }
                    composable("profile") { Profiles(navController) }
                    composable("addProfile/{deviceId}") { backStackEntry ->
                        AddProfileScreen(navController, backStackEntry)
                    }
                }
            }
        }
    }


    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

}

