package com.example.queuemanagmentsystem.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.queuemanagmentsystem.R
import com.example.queuemanagmentsystem.pages.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CustomerProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("Loading...") }
    val email = user?.email ?: "unknown@email.com"
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedTab by remember { mutableStateOf("Account") }

    val red = Color(0xFFC62828)
    val yellow = Color(0xFFF9A825)

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("username") ?: "User"
                }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(60.dp)) // Moved image lower for notch

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val painter = profileImageUri?.let { rememberAsyncImagePainter(it) }
                        ?: painterResource(id = R.drawable.profilepic)

                    Image(
                        painter = painter,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(email, fontSize = 16.sp, color = Color.Gray)

                Spacer(Modifier.height(30.dp))

                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                Spacer(Modifier.height(30.dp))

                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate("register") {
                            popUpTo("landing") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Logout", fontSize = 16.sp, color = Color.White)
                }
            }

            // Bottom Navigation Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        when (it) {
                            "Home" -> navController.navigate("landing")
                            "Slots" -> navController.navigate("slots")
                            "Account" -> navController.navigate("account")
                        }
                    },
                    yellow = yellow,
                    red = red
                )
            }
        }
    }
}
