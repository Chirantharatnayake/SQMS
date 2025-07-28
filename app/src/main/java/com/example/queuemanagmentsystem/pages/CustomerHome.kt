package com.example.queuemanagmentsystem.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(navController: NavController) {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)

    val bannerImages = listOf(
        R.drawable.ai_banking1,
        R.drawable.ai_banking2,
        R.drawable.ai_banking3
    )

    var currentBannerIndex by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf("Home") }
    var username by remember { mutableStateOf("User") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Fetch Username from Firestore
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        username = document.getString("username") ?: "User"
                    }
                }
                .addOnFailureListener {
                    username = "User"
                }
        }
    }

    // Banner animation
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentBannerIndex = (currentBannerIndex + 1) % bannerImages.size
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 72.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(id = R.drawable.bank_logo_rmv),
                    contentDescription = "Bank Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                )

                // Greet the user
                Text(
                    text = "Hello, $username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Crossfade(targetState = currentBannerIndex, label = "BannerSlider") { index ->
                    Image(
                        painter = painterResource(id = bannerImages[index]),
                        contentDescription = "Banner Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Services",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val services = listOf(
                    "Account Opening" to R.drawable.addaccount,
                    "Loan Consultations" to R.drawable.loan,
                    "Bank Card Services" to R.drawable.creditcard,
                    "Locker Access" to R.drawable.locker,
                    "Complaint Resolution" to R.drawable.complain
                )

                Column {
                    services.forEach { (title, iconRes) ->
                        ServiceCard(iconRes = iconRes, title = title) {
                            navController.navigate("appointment/${title}")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    yellow = yellow,
                    red = red
                )
            }
        }
    }
}

@Composable
fun ServiceCard(iconRes: Int, title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(65.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFFF3E0), Color(0xFFFFEBEE))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }
    }
}

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    yellow: Color,
    red: Color
) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, "Home"),
        Triple("Slots", Icons.Default.CalendarToday, "Slots"),
        Triple("Account", Icons.Default.AccountCircle, "Account")
    )

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        color = yellow,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (key, icon, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(key) }
                        .fillMaxHeight()
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selectedTab == key) red else Color.DarkGray,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (selectedTab == key) red else Color.DarkGray,
                        fontWeight = if (selectedTab == key) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
