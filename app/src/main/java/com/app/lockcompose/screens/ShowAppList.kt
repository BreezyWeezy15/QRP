import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.app.lockcompose.FirebaseMonitoringService
import com.app.lockcompose.NotificationActionReceiver
import com.app.lockcompose.SharedPreferencesHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAppList() {

    val context = LocalContext.current
    val isLightTheme = !isSystemInDarkTheme()

    val availableApps = remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    val selectedApps = remember { mutableStateListOf<InstalledApp>() }
    val isLoading = remember { mutableStateOf(true) }

    var expanded by remember { mutableStateOf(false) }
    var selectedInterval by remember { mutableStateOf("Select Interval") }
    val timeIntervals = listOf("1 min", "15 min", "30 min", "45 min", "60 min", "75 min", "90 min", "120 min")
    var pinCode by remember { mutableStateOf("") }

    fun parseInterval(interval: String): Int {
        return interval.replace(" min", "").toIntOrNull() ?: 0
    }

    LaunchedEffect(Unit) {
        loadAppsFromFirebase(context) { apps ->
            availableApps.value = apps
            isLoading.value = false
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("》Rules list") },
                actions = {
                    IconButton(onClick = {
                        if (availableApps.value.isEmpty()) {
                            Toast.makeText(context, "No apps to delete", Toast.LENGTH_SHORT).show()
                        } else {
                            deleteAllAppsFromFirebase(context)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Apps"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isLightTheme) Color(0xFFE0E0E0) else Color.DarkGray
                )
            )
        }
    ) { paddingValues ->
        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isLightTheme) Color.White else Color(0xFF303030))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedInterval,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Time Interval") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = if (isLightTheme) Color.White else Color(0xFF424242)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        timeIntervals.forEach { interval ->
                            DropdownMenuItem(
                                text = { Text(interval) },
                                onClick = {
                                    selectedInterval = interval
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(availableApps.value) { app ->
                        AppListItem(
                            app = app,
                            isSelected = selectedApps.contains(app),
                            onClick = {
                                if (selectedApps.contains(app)) {
                                    selectedApps.remove(app)
                                } else {
                                    selectedApps.add(app)
                                }
                            }
                        )
                    }
                }

                TextField(
                    value = pinCode,
                    onValueChange = { pinCode = it },
                    label = { Text("Enter PIN Code") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                Button(
                    onClick = {
                        if (pinCode.isNotEmpty() && selectedApps.isNotEmpty() && selectedInterval != "Select Interval") {
                            val intervalInMinutes = parseInterval(selectedInterval)
                            sendSelectedAppsToFirebase(
                                selectedApps,
                                intervalInMinutes,
                                pinCode,
                                context
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill all required fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(
                            0xFF3F51B5
                        )
                    ), // Indigo color
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "Send Rules",
                        color = Color.White
                    )
                }

            }
        }

    }
}




private fun loadAppsFromFirebase(context: Context, onAppsLoaded: (List<InstalledApp>) -> Unit) {
    if(SharedPreferencesHelper.getSelectedDevice(context) != null){
        val firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("applications")
            .child(SharedPreferencesHelper.getSelectedDevice(context)!!.deviceId)

        firebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appsList = mutableListOf<InstalledApp>()

                for (appSnapshot in snapshot.children) {
                    val appName = appSnapshot.child("name").getValue(String::class.java)
                    val packageName = appSnapshot.child("package_name").getValue(String::class.java)
                    val iconBase64 = appSnapshot.child("icon").getValue(String::class.java)

                    if (appName != null && packageName != null && iconBase64 != null) {
                        val appIcon = base64ToDrawable(context, iconBase64)
                        appsList.add(InstalledApp(appName, packageName, appIcon))
                    }
                }

                onAppsLoaded(appsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load apps: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class DeviceInfo(
    val deviceName: String,
    val deviceId: String,
    var profile: String = "Not Set"
)

private fun deleteAllAppsFromFirebase(context: Context) {
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Apps")
    firebaseDatabase.removeValue().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(context, "All apps deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to delete apps", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AppListItem(app: InstalledApp, isSelected: Boolean, onClick: () -> Unit) {
    val iconPainter = rememberDrawablePainter(app.appIcon)

    val borderModifier = if (isSelected) {
        Modifier.border(2.dp, SolidColor(Color.Blue), RoundedCornerShape(8.dp))
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(borderModifier)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = iconPainter,
                contentDescription = app.appName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun rememberDrawablePainter(drawable: Drawable?): Painter {
    return remember(drawable) {
        val bitmap = drawable?.toBitmap(100, 100) ?: Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        BitmapPainter(bitmap.asImageBitmap())
    }
}

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable
)

@SuppressLint("NewApi")
private fun drawableToByteArray(drawable: Drawable): ByteArray {
    val bitmap = when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        is AdaptiveIconDrawable -> {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        else -> throw IllegalArgumentException("Unsupported drawable type")
    }

    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

private fun base64ToDrawable(context: Context, base64String: String): Drawable {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    return BitmapDrawable(context.resources, bitmap)
}

private fun sendSelectedAppsToFirebase(
    selectedApps: List<InstalledApp>,
    selectedInterval: Int,
    pinCode: String,
    context: Context
) {
    val selectedDevice = SharedPreferencesHelper.getSelectedDevice(context)
    if (selectedDevice == null) {
        Toast.makeText(context, "Please pick a device", Toast.LENGTH_SHORT).show()
    } else {
        val firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Apps")
            .child(selectedDevice.deviceId.toLowerCase(Locale.ROOT))

        firebaseDatabase.child("type").setValue("Custom")
            .addOnSuccessListener {

                selectedApps.forEach { app ->
                    val iconByteArray = drawableToByteArray(app.appIcon)

                    val appData = mapOf(
                        "package_name" to app.packageName,
                        "name" to app.appName,
                        "interval" to selectedInterval.toString(),
                        "pin_code" to pinCode,
                        "icon" to android.util.Base64.encodeToString(iconByteArray, android.util.Base64.DEFAULT)
                    )

                    firebaseDatabase.child(app.appName.lowercase(Locale.ROOT)).setValue(appData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error uploading ${app.appName}: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to set profile type: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}






