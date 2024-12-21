import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.app.lockcompose.R
import com.app.lockcompose.SharedPreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(navController: NavController, backStackEntry: NavBackStackEntry) {
    val context = LocalContext.current
    val deviceId = backStackEntry.arguments?.getString("deviceId")
    if (deviceId == null) {
        Toast.makeText(context, "No device selected", Toast.LENGTH_SHORT).show()
        return
    }

    val profiles = listOf(
        Profile("Child", Color(0xFFE57373), R.drawable.child),
        Profile("Pre-K", Color(0xFF81C784), R.drawable.pre),
        Profile("Teen", Color(0xFF64B5F6), R.drawable.teen),
        Profile("Custom", Color(0xFFFFD54F), R.drawable.custom)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Profile Type") },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Profile Type",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(profiles) { profile ->
                        ProfileCard(
                            color = profile.color,
                            imageRes = profile.imageRes,
                            title = profile.title,
                            onClick = {
                                SharedPreferencesHelper.saveSelectedProfile(context, profile.title)
                                SharedPreferencesHelper.saveProfileForDevice(context, deviceId, profile.title)


                                val rulesButtonEnabled = profile.title == "Custom"
                                SharedPreferencesHelper.setRulesButtonEnabled(context, rulesButtonEnabled)


                                val feedback = if (rulesButtonEnabled) {
                                    "Custom Profile Selected"
                                } else {
                                    "${profile.title} Profile Selected"
                                }
                                Toast.makeText(context, feedback, Toast.LENGTH_SHORT).show()

                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(color: Color, imageRes: Int, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

data class Profile(val title: String, val color: Color, val imageRes: Int)