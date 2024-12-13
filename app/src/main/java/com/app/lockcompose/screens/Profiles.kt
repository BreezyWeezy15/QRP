package com.app.lockcompose.screens

import DeviceInfo
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.lockcompose.R
import com.app.lockcompose.SharedPreferencesHelper
import com.google.zxing.integration.android.IntentIntegrator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Profiles(navController: NavController) {
    val context = LocalContext.current

    // Get the list of available devices
    var availableDevices by remember { mutableStateOf(SharedPreferencesHelper.getDeviceInfoList(context)) }

    // Initialize selectedDevice from SharedPreferences if available
    val selectedDevice = remember { mutableStateOf<DeviceInfo?>(SharedPreferencesHelper.getSelectedDevice(context)) }

    val showRenameDialog = remember { mutableStateOf(false) }
    val newDeviceName = remember { mutableStateOf("") }

    // Launcher for scanning the QR code
    val scanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult != null && intentResult.contents != null) {
            val datas = intentResult.contents
            try {
                Toast.makeText(context, "Devices Paired", Toast.LENGTH_LONG).show()
                val (deviceName, deviceId) = datas.split(",")
                Toast.makeText(context, "$deviceName $deviceId", Toast.LENGTH_LONG).show()

                val newDeviceInfo = DeviceInfo(deviceName = deviceName, deviceId = deviceId)

                val deviceInfoList = SharedPreferencesHelper.getDeviceInfoList(context).toMutableList()

                if (deviceInfoList.none { it.deviceId == newDeviceInfo.deviceId }) {
                    deviceInfoList.add(newDeviceInfo)
                    SharedPreferencesHelper.saveDeviceInfoList(context, deviceInfoList)

                    // Update the UI with the new device immediately
                    availableDevices = deviceInfoList
                } else {
                    Toast.makeText(context, "Device already exists: $deviceName\nID: $deviceId", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Invalid QR Code format", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Scan failed or canceled", Toast.LENGTH_SHORT).show()
        }
    }

    // Observe changes to the SharedPreferences and update the state
    LaunchedEffect(Unit) {
        availableDevices = SharedPreferencesHelper.getDeviceInfoList(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Devices") },
                actions = {
                    IconButton(onClick = {
                        val integrator = IntentIntegrator(context as android.app.Activity)
                        integrator.setOrientationLocked(false)
                        integrator.setPrompt("Scan a QR Code")
                        scanLauncher.launch(integrator.createScanIntent())
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.scan),
                            contentDescription = "Scan QR Code",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    selectedDevice.value?.let { device ->
                        SharedPreferencesHelper.saveSelectedDevice(context, device)
                        Toast.makeText(context, "Device saved: ${device.deviceName}", Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(context, "Please select a device", Toast.LENGTH_SHORT).show()

                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Confirm")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Available Devices",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(availableDevices) { device ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedDevice.value = device
                            }
                            .padding(8.dp)
                            .combinedClickable(
                                onClick = {
                                    selectedDevice.value = device
                                },
                                onLongClick = {
                                    newDeviceName.value = device.deviceName
                                    showRenameDialog.value = true
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDevice.value == device,
                            onClick = { selectedDevice.value = device }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = device.deviceName,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = device.deviceId,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    if (showRenameDialog.value) {
        RenameDeviceDialog(
            deviceName = newDeviceName.value,
            onNameChange = { newDeviceName.value = it },
            onDismiss = { showRenameDialog.value = false },
            onConfirm = {
                selectedDevice.value?.let { device ->
                    val updatedDevice = device.copy(deviceName = newDeviceName.value)
                    val updatedDeviceList = availableDevices.toMutableList().apply {
                        remove(device)
                        add(updatedDevice)
                    }
                    SharedPreferencesHelper.saveDeviceInfoList(context, updatedDeviceList)

                    // Update the list immediately in the UI
                    availableDevices = updatedDeviceList
                }
                showRenameDialog.value = false
            }
        )
    }
}

@Composable
fun RenameDeviceDialog(
    deviceName: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Device") },
        text = {
            TextField(
                value = deviceName,
                onValueChange = onNameChange,
                label = { Text("New Device Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
