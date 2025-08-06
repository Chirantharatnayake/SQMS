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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val bookings = remember { mutableStateListOf<Map<String, Any>>() }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDocId by remember { mutableStateOf("") }
    var selectedAppt by remember { mutableStateOf<Map<String, Any>?>(null) }

    val notifications = remember { mutableStateListOf<String>() }
    var showNotification by remember { mutableStateOf(false) }

    // ✅ Load bookings + notifications
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        // Load appointments
        firestore.collection("appointments")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { docs ->
                bookings.clear()
                docs.forEach { doc ->
                    bookings.add(doc.data + ("docId" to doc.id))
                }
                isLoading = false
            }

        // Always check for latest notification
        firestore.collection("notifications")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { notifDocs ->
                val latest = notifDocs.firstOrNull()
                val msg = latest?.getString("message")
                if (!msg.isNullOrEmpty()) {
                    notifications.clear()
                    notifications.add(msg)
                    showNotification = true
                }
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
                        IconButton(onClick = {
                            navController.navigate("notifications")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }

                        if (showNotification) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.Red, shape = RoundedCornerShape(50))
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC62828),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (bookings.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Appointments Yet",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (showNotification && notifications.isNotEmpty()) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0E0))
                        ) {
                            Text(
                                text = notifications.first(),
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { appt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Service: ${appt["service"]}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Text("Branch: ${appt["branchName"]}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("Date: ${appt["date"]}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("Time: ${appt["slot"]}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("Staff: ${appt["staffName"]}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("Token: ${appt["token"]}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("OrderID: ${appt["orderId"]}", fontSize = 14.sp, color = Color.DarkGray)
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        selectedDocId = appt["docId"].toString()
                                        selectedAppt = appt
                                        showDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Cancel", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Cancel Appointment") },
                    text = { Text("Are you sure you want to cancel this appointment?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            firestore.collection("appointments").document(selectedDocId).delete()
                                .addOnSuccessListener {
                                    selectedAppt?.let { bookings.remove(it) }
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                                }
                        }) {
                            Text("Yes", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

