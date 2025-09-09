package com.example.queuemanagementapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun StaffAccountScreen(staffName: String, navController: NavController) {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)

    var staff by remember { mutableStateOf<StaffMember?>(null) }

    // Firestore fetch
    LaunchedEffect(Unit) {
        val query = FirebaseFirestore.getInstance()
            .collection("staff")
            .whereEqualTo("staffName", staffName)
            .get().await()

        if (!query.isEmpty) {
            val doc = query.documents[0]
            staff = StaffMember(
                id = doc.getString("id") ?: "",
                staffName = doc.getString("staffName") ?: "",
                email = doc.getString("email") ?: "",
                branchCode = doc.getString("branchCode") ?: "",
                serviceType = doc.getString("serviceType") ?: ""
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("staffAccountScreen")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 72.dp)
                .testTag("staffAccountContent")
        ) {
            // Profile image
            Image(
                painter = painterResource(id = R.drawable.profilepic),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .testTag("staffProfileImage")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Staff name
            Text(
                text = staff?.staffName ?: "Loading...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("staffAccountName")
            )

            // Branch code
            Text(
                text = "Branch Code: ${staff?.branchCode ?: "Loading..."}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.testTag("staffAccountBranchCode")
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Button(
                onClick = {
                    navController.navigate("admin_login") {
                        popUpTo("staff_account/$staffName") { inclusive = true } // clear backstack
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = red),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .testTag("staffLogoutButton")
            ) {
                Text("Logout", color = Color.White, fontSize = 16.sp)
            }
        }

        // Bottom Nav Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .testTag("staffAccountBottomNav")
        ) {
            StaffBottomNavBar(
                selectedTab = "Account",
                onTabSelected = { tab ->
                    when (tab) {
                        "Home" -> navController.navigate("staff_home/$staffName")
                        "Account" -> navController.navigate("staff_account/$staffName")
                    }
                }
            )
        }
    }
}
