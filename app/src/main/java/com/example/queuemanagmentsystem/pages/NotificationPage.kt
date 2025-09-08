package com.example.queuemanagmentsystem.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import com.example.queuemanagmentsystem.SharedPrefernces.AppPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    var isLoading by remember { mutableStateOf(true) }
    val notifications = remember { mutableStateListOf<Map<String, Any>>() }
    var showConfirmClear by remember { mutableStateOf(false) }

    // Fetch notifications for the logged-in user
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            firestore.collection("notifications")
                .whereEqualTo("uid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { docs ->
                    notifications.clear()
                    for (doc in docs) {
                        println("✅ Notification fetched: ${doc.data}")
                        notifications.add(doc.data)
                    }

                    // ✅ Save latest notification timestamp
                    val latestTimestamp = docs.maxOfOrNull {
                        (it["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: 0L
                    }
                    if (latestTimestamp != null) {
                        AppPreferences.saveLastSeenNotificationTimestamp(context, latestTimestamp)
                    }

                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load notifications: ${e.message}", Toast.LENGTH_LONG).show()
                    println("❌ Notification load failed: ${e.message}")
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    fun clearAllNotifications(uid: String) {
        isLoading = true
        firestore.collection("notifications")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { docs ->
                val batch = firestore.batch()
                for (doc in docs) {
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        notifications.clear()
                        AppPreferences.saveLastSeenNotificationTimestamp(context, System.currentTimeMillis())
                        Toast.makeText(context, "All notifications cleared", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to clear: ${e.message}", Toast.LENGTH_LONG).show()
                        isLoading = false
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch: ${e.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("notificationBackButton")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Professional "Clear" action on the right
                    if (notifications.isNotEmpty()) {
                        TextButton(
                            onClick = { showConfirmClear = true },
                            modifier = Modifier.testTag("notificationClearButton")
                        ) {
                            Text("Clear", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC62828),
                    titleContentColor = Color.White
                ),
                modifier = Modifier.testTag("notificationTopBar")
            )
        },
        modifier = Modifier.testTag("notificationScreen")
    ) { innerPadding ->

        if (showConfirmClear) {
            AlertDialog(
                onDismissRequest = { showConfirmClear = false },
                title = { Text("Clear all notifications?") },
                text = { Text("This will remove all notifications for this account. This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmClear = false
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                clearAllNotifications(uid)
                            }
                        },
                        modifier = Modifier.testTag("confirmClearDialog")
                    ) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmClear = false },
                        modifier = Modifier.testTag("cancelClearDialog")
                    ) {
                        Text("Cancel")
                    }
                },
                modifier = Modifier.testTag("clearConfirmationDialog")
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
                .testTag("notificationContent")
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("notificationLoadingIndicator")
                    )
                }

                notifications.isEmpty() -> {
                    Text(
                        text = "No notifications yet",
                        fontSize = 18.sp,
                        color = colors.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("noNotificationsMessage"),
                        fontWeight = FontWeight.Medium
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("notificationsList"),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notifications) { notif ->
                            val message = notif["message"]?.toString() ?: "No message"
                            val timestamp = notif["timestamp"]
                            val dateFormatted = try {
                                val ts = timestamp as? com.google.firebase.Timestamp
                                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                ts?.let { sdf.format(it.toDate()) } ?: "Received"
                            } catch (e: Exception) {
                                "Received"
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("notificationCard_${notif.hashCode()}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.surface),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = message,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onSurface,
                                        modifier = Modifier.testTag("notificationMessage_${notif.hashCode()}")
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = dateFormatted,
                                        fontSize = 12.sp,
                                        color = colors.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.testTag("notificationTimestamp_${notif.hashCode()}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
