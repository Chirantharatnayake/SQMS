// File: app/src/main/java/com/example/queuemanagmentsystem/pages/StaffAccountScreen.kt
package com.example.queuemanagmentsystem.pages

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Crash-safe avatar: remember only the resource ID (plain Int),
 * then call painterResource inside the composable branch.
 */
@Composable
private fun ProfileAvatar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // ✅ This is safe: not a composable call
    val resId = remember {
        context.resources.getIdentifier("profilepic", "drawable", context.packageName)
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId), // ✅ composable call in composable context
            contentDescription = "Profile Image",
            modifier = modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Image",
            modifier = modifier.size(100.dp),
            tint = Color(0xFFC62828)
        )
    }
}

@Composable
fun StaffAccountScreen(staffName: String, navController: NavController) {
    val red = Color(0xFFC62828)
    var staff by remember { mutableStateOf<StaffMember?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Load staff doc by name
    LaunchedEffect(staffName) {
        try {
            val query = FirebaseFirestore.getInstance()
                .collection("staff")
                .whereEqualTo("staffName", staffName)
                .get()
                .await()

            staff = if (!query.isEmpty) {
                val doc = query.documents.first()
                StaffMember(
                    id = doc.getString("id") ?: "",
                    staffName = doc.getString("staffName") ?: "",
                    email = doc.getString("email") ?: "",
                    branchCode = doc.getString("branchCode") ?: "",
                    serviceType = doc.getString("serviceType") ?: ""
                )
            } else null
        } finally {
            loading = false
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
                .padding(top = 64.dp, bottom = 88.dp) // leave space for bottom nav
                .testTag("staffAccountContent")
        ) {
            ProfileAvatar(
                modifier = Modifier
                    .testTag("staffProfileImage")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when {
                    loading -> "Loading..."
                    staff?.staffName.isNullOrBlank() -> staffName
                    else -> staff!!.staffName
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = red,
                modifier = Modifier.testTag("staffAccountName")
            )

            Text(
                text = when {
                    loading -> "Loading..."
                    staff?.email.isNullOrBlank() -> "email not set"
                    else -> staff!!.email
                },
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.testTag("staffAccountEmail")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Branch Code: " + when {
                    loading -> "Loading..."
                    staff?.branchCode.isNullOrBlank() -> "N/A"
                    else -> staff!!.branchCode
                },
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.testTag("staffAccountBranchCode")
            )

            Text(
                text = "Service: " + when {
                    loading -> "Loading..."
                    staff?.serviceType.isNullOrBlank() -> "N/A"
                    else -> staff!!.serviceType
                },
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.testTag("staffAccountServiceType")
            )

            Spacer(modifier = Modifier.height(32.dp))

            // "Logout" simply routes to your admin_login (adjust if you have a dedicated staff login)
            Button(
                onClick = {
                    navController.navigate("admin_login") {
                        popUpTo("staff_account/${Uri.encode(staffName)}") { inclusive = true }
                        launchSingleTop = true
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

        // Bottom nav
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .testTag("staffAccountBottomNav")
        ) {
            StaffBottomNavBar(
                selectedTab = "Account",
                onTabSelected = { tab ->
                    val encoded = Uri.encode(staffName)
                    when (tab) {
                        "Home" -> navController.navigate("staff_home/$encoded") { launchSingleTop = true }
                        "Account" -> navController.navigate("staff_account/$encoded") { launchSingleTop = true }
                    }
                }
            )
        }
    }
}
