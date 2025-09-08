package com.example.queuemanagmentsystem.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.queuemanagmentsystem.SharedPrefernces.AppPreferences
import com.example.queuemanagmentsystem.ui.theme.LocalAppDarkMode
import kotlinx.coroutines.tasks.await

// --------- constants ---------
private val Red = Color(0xFFC62828)
private val RedLight = Color(0xFFFFE0E0)
// private val Bg = Color(0xFFF9F9F9) // (legacy) now replaced by theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotScreen(navController: NavController) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    // memoize Firebase instances
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    // state
    val bookings = remember { mutableStateListOf<Map<String, Any>>() }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDocId by remember { mutableStateOf("") }
    var selectedAppt by remember { mutableStateOf<Map<String, Any>?>(null) }

    val notifications = remember { mutableStateListOf<String>() }
    var showNotification by remember { mutableStateOf(false) }

    // Load bookings + notification badge (same behavior, cleaner with await)
    val uid = auth.currentUser?.uid
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect

        // load appointments
        try {
            val docs = firestore.collection("appointments")
                .whereEqualTo("uid", uid)
                .get()
                .await()
            bookings.clear()
            docs.forEach { doc -> bookings.add(doc.data + ("docId" to doc.id)) }
        } finally {
            isLoading = false
        }

        // red dot logic using AppPreferences
        val latest = firestore.collection("notifications")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            .firstOrNull()

        val msg = latest?.getString("message")
        val latestTs = (latest?.get("timestamp") as? com.google.firebase.Timestamp)
            ?.toDate()?.time ?: 0L
        val lastSeenTs = AppPreferences.getLastSeenNotificationTimestamp(context)

        if (!msg.isNullOrEmpty() && latestTs > lastSeenTs) {
            notifications.clear()
            notifications.add(msg)
            showNotification = true
        } else {
            showNotification = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Appointments", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("landing") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        IconButton(onClick = { navController.navigate("notifications") }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                        // alignment moved to the call site (inside BoxScope)
                        NotificationDot(
                            visible = showNotification,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Red,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                bookings.isEmpty() -> EmptyState(
                    showNotification = showNotification,
                    notifications = notifications,
                    modifier = Modifier.align(Alignment.Center) // alignment applied here
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { appt ->
                        BookingCard(
                            appt = appt,
                            onCancel = {
                                selectedDocId = appt["docId"].toString()
                                selectedAppt = appt
                                showDialog = true
                            }
                        )
                    }
                }
            }

            if (showDialog) {
                CancelDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        showDialog = false
                        firestore.collection("appointments").document(selectedDocId).delete()
                            .addOnSuccessListener {
                                selectedAppt?.let { bookings.remove(it) }
                                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
            }
        }
    }
}

// --------- small composables (same UI, just extracted) ---------
@Composable
private fun NotificationDot(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return
    Box(
        modifier = modifier
            .size(10.dp)
            .background(Color.Red, shape = RoundedCornerShape(50))
    )
}

@Composable
private fun EmptyState(
    showNotification: Boolean,
    notifications: List<String>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val dark = LocalAppDarkMode.current
    val noteBg = if (dark) Color(0xFF4A1C1C) else RedLight
    val noteText = if (dark) Color(0xFFFFB3B3) else Color.Red
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Appointments Yet",
            color = colors.onBackground.copy(alpha = 0.7f),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (showNotification && notifications.isNotEmpty()) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = noteBg)
            ) {
                Text(
                    text = notifications.first(),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = noteText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BookingCard(
    appt: Map<String, Any>,
    onCancel: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val surface = colors.surface
    val onSurface = colors.onSurface
    val muted = onSurface.copy(alpha = 0.75f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Service: ${appt["service"]}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
            Text("Branch: ${appt["branchName"]}", fontSize = 14.sp, color = muted)
            Text("Date: ${appt["date"]}", fontSize = 14.sp, color = muted)
            Text("Time: ${appt["slot"]}", fontSize = 14.sp, color = muted)
            Text("Staff: ${appt["staffName"]}", fontSize = 14.sp, color = muted)
            Text("Token: ${appt["token"]}", fontSize = 14.sp, color = muted)
            Text("OrderID: ${appt["orderId"]}", fontSize = 14.sp, color = muted)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}

@Composable
private fun CancelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Appointment") },
        text = { Text("Are you sure you want to cancel this appointment?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Yes", color = Color.Red) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("No") }
        }
    )
}