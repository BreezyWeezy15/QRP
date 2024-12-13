package com.app.lockcompose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Button
            Button(
                onClick = {
                    navController.navigate("profile")
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Button width
                    .height(55.dp), // Button height
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE) // Purple color for the button
                ),
                shape = RoundedCornerShape(12.dp), // Rounded corners
                elevation = ButtonDefaults.elevatedButtonElevation(8.dp) // Elevation for shadow effect
            ) {
                Text(
                    text = "Profiles",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White), // Text styling
                    modifier = Modifier.padding(horizontal = 16.dp) // Padding for text inside button
                )
            }

            // Rules Button
            Button(
                onClick = {
                    navController.navigate("showAppList")
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Button width
                    .height(55.dp), // Button height
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03DAC5) // Teal color for the button
                ),
                shape = RoundedCornerShape(12.dp), // Rounded corners
                elevation = ButtonDefaults.elevatedButtonElevation(8.dp) // Elevation for shadow effect
            ) {
                Text(
                    text = "Rules",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White), // Text styling
                    modifier = Modifier.padding(horizontal = 16.dp) // Padding for text inside button
                )
            }
        }
    }
}

