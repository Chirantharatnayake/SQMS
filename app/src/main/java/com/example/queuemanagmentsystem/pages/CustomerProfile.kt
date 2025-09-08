package com.example.queuemanagmentsystem.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Enhanced color palette for People's Bank theme
private val PrimaryYellow = Color(0xFFF9A825)
private val SecondaryYellow = Color(0xFFFDD835)
private val PrimaryRed = Color(0xFFC62828)
private val SecondaryRed = Color(0xFFD32F2F)
private val White = Color.White
private val LightGray = Color(0xFFF5F5F5)
private val DarkGray = Color(0xFF424242)
private val SoftShadow = Color(0x1A000000)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CustomerProfileScreen(navController: NavController, darkMode: Boolean, onToggleDark: (Boolean) -> Unit) {
    val context = LocalContext.current

    // memoize Firebase instances
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val user = auth.currentUser

    // state
    var name by remember { mutableStateOf("Loading...") }
    val email = user?.email ?: "unknown@email.com"
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedTab by remember { mutableStateOf("Account") }
    var isVisible by remember { mutableStateOf(false) }

    // image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
        }
    }

    // load username
    LaunchedEffect(user?.uid) {
        isVisible = true
        user?.uid?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("username") ?: "User"
                }
        }
    }

    val backgroundColor = if (darkMode) Color(0xFF1A1A1A) else LightGray.copy(alpha = 0.3f)
    val surfaceColor = if (darkMode) Color(0xFF2D2D2D) else White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (darkMode) {
                        listOf(
                            Color(0xFF1A1A1A),
                            Color(0xFF2D2D2D),
                            Color(0xFF1A1A1A)
                        )
                    } else {
                        listOf(
                            PrimaryYellow.copy(alpha = 0.1f),
                            White,
                            backgroundColor
                        )
                    }
                )
            )
            .testTag("customerProfileScreen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
                .padding(horizontal = 24.dp)
                .testTag("customerProfileContent"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Enhanced Header Card with Profile Info
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -100 },
                    animationSpec = tween(800, easing = EaseOutBounce)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("profileInfoCard"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Enhanced profile avatar with glow effect
                        EnhancedProfileAvatar(
                            imageUri = profileImageUri,
                            onClick = { imagePickerLauncher.launch("image/*") },
                            darkMode = darkMode
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (darkMode) White else DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("customerNameText")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (darkMode) Color(0xFF3A3A3A) else PrimaryYellow.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("customerEmailCard")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (darkMode) PrimaryYellow else PrimaryRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = email,
                                    fontSize = 14.sp,
                                    color = if (darkMode) Color.LightGray else DarkGray.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.testTag("customerEmailText")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced Settings Section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(1000, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .testTag("preferencesCard"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("preferencesHeader")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = if (darkMode) PrimaryYellow else PrimaryRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Preferences",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (darkMode) White else DarkGray
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced Dark mode toggle
                        EnhancedDarkModeToggleRow(
                            enabled = darkMode,
                            onChange = onToggleDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced Account Actions Section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(1000, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 400))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .testTag("accountActionsCard"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("accountActionsHeader")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = if (darkMode) PrimaryYellow else PrimaryRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Account Actions",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (darkMode) White else DarkGray
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced logout button
                        Button(
                            onClick = {
                                auth.signOut()
                                navController.navigate("register") {
                                    popUpTo("landing") { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("customerLogoutButton"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryRed
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sign Out",
                                    fontSize = 18.sp,
                                    color = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Keep existing bottom navigation exactly as is
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .testTag("customerProfileBottomNav")
        ) {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        "Home" -> navController.navigate("landing")
                        "Slots" -> navController.navigate("slots")
                        "Account" -> navController.navigate("account")
                    }
                },
                yellow = PrimaryYellow,
                red = PrimaryRed
            )
        }
    }
}

@Composable
private fun EnhancedProfileAvatar(
    imageUri: Uri?,
    onClick: () -> Unit,
    darkMode: Boolean
) {
    Card(
        modifier = Modifier
            .size(140.dp)
            .shadow(16.dp, CircleShape)
            .clickable(onClick = onClick)
            .testTag("customerProfileAvatar"),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = if (darkMode) {
                            listOf(
                                Color(0xFF444444),
                                Color(0xFF2A2A2A)
                            )
                        } else {
                            listOf(
                                PrimaryYellow.copy(alpha = 0.1f),
                                White
                            )
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            val painter = imageUri?.let { rememberAsyncImagePainter(it) }
                ?: painterResource(id = R.drawable.profilepic)

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .testTag("customerProfileImage")
            )

            // Camera overlay
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .testTag("customerProfileCameraButton"),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (darkMode) PrimaryYellow else PrimaryRed
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedDarkModeToggleRow(enabled: Boolean, onChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("darkModeToggleCard"),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFF3A3A3A) else PrimaryYellow.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Card(
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("darkModeToggleIcon"),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (enabled) PrimaryYellow.copy(alpha = 0.2f) else PrimaryRed.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (enabled) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (enabled) PrimaryYellow else PrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Dark Mode",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) PrimaryYellow else PrimaryRed,
                        modifier = Modifier.testTag("darkModeToggleTitle")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (enabled) "Comfortable for low light" else "Tap to reduce eye strain",
                        fontSize = 13.sp,
                        color = if (enabled) Color(0xFFBBBBBB) else Color(0xFF6D6D6D),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.testTag("darkModeToggleDescription")
                    )
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = { onChange(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryYellow,
                    checkedTrackColor = PrimaryYellow.copy(alpha = 0.3f),
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = PrimaryRed.copy(alpha = 0.3f)
                ),
                modifier = Modifier.testTag("darkModeToggleSwitch")
            )
        }
    }
}
