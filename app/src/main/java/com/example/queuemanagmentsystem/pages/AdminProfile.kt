// File: AdminAccountScreen.kt
package com.example.queuemanagmentsystem.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminAccountScreen(navController: NavController) {
    val red = Color(0xFFC62828)
    val adminId = "admin001" // hardcoded for now

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Admin Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = red
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Profile picture placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profilepic),
                contentDescription = "Profile Pic",
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Admin ID: $adminId", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Welcome back, Admin!", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Sign out (safe even if not signed in)
                try { FirebaseAuth.getInstance().signOut() } catch (_: Exception) {}

                // Go back to Admin/Staff login and clear admin_home from the back stack
                navController.navigate("admin_login") {
                    popUpTo("admin_home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log out", color = Color.White)
        }
    }
}
