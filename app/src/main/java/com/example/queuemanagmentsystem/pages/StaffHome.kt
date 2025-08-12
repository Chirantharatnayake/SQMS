package com.example.queuemanagementapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Appointment(
    val id: String = "",
    val service: String = "",
    val date: String = "",
    // We show "time" in UI. Firestore stores "slot", so we will set time = slot when loading.
    val time: String = "",
    val token: String = "",
    val customerUid: String = "",   // some docs may store this as "uid"; we handle both
    val branch: String = "",
    val staffName: String = "",
    val branchName: String = "",
    val orderId: String = "",
    val uid: String = "",           // keep original uid field too (for compatibility)
    // >>> Pulled straight from Firestore so the card shows immediately
    val customerName: String = "",
    val customerPhone: String = ""
)

data class StaffMember(
    val id: String = "",
    val staffName: String = "",
    val email: String = "",
    val branchCode: String = "",
    val serviceType: String = ""
)

@Composable
fun AppointmentCardStyled(
    name: String,
    phone: String,
    appointment: Appointment,
    yellow: Color,
    red: Color,
    green: Color,
    isCompleted: Boolean = false,
    onCancel: (Appointment) -> Unit = {},
    onComplete: (Appointment) -> Unit = {},
    onDeleteCompleted: (Appointment) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)
                    Text(text = "Phone: $phone", fontSize = 14.sp, color = Color.DarkGray)
                }
                Card(colors = CardDefaults.cardColors(containerColor = green), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "Token: ${appointment.token}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Service", fontSize = 12.sp, color = Color.Gray)
                    Text(appointment.service, fontSize = 14.sp)
                }
                Column {
                    Text("Date", fontSize = 12.sp, color = Color.Gray)
                    Text(appointment.date, fontSize = 14.sp)
                }
                Column {
                    Text("Time", fontSize = 12.sp, color = Color.Gray)
                    Text(appointment.time, fontSize = 14.sp)
                }
            }

            if (appointment.orderId.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Order ID: ${appointment.orderId}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isCompleted) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onComplete(appointment) },
                        colors = ButtonDefaults.buttonColors(containerColor = green),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = { onCancel(appointment) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            } else {
                Button(
                    onClick = { onDeleteCompleted(appointment) },
                    colors = ButtonDefaults.buttonColors(containerColor = red),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StaffBottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)

    val items = listOf(
        Triple("Home", Icons.Default.Home, "Home"),
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

@Composable
fun StaffScreen(staffName: String, navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val green = Color(0xFF2E7D32)

    var currentStaff by remember { mutableStateOf<StaffMember?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var pendingLookups by remember { mutableStateOf(0) }
    val staffAppointments = remember { mutableStateListOf<Appointment>() }
    val completedAppointments = remember { mutableStateListOf<Appointment>() }
    val customerDetailsMap = remember { mutableStateMapOf<String, Pair<String, String>>() }

    fun cancelAppointment(appt: Appointment) {
        firestore.collection("appointments").document(appt.id).get().addOnSuccessListener { document ->
            val uidFromDoc = document.getString("uid")
            val uidFromDocAlt = document.getString("customerUid")
            val uidFinal = uidFromDoc ?: uidFromDocAlt ?: appt.customerUid.ifEmpty { appt.uid }

            firestore.collection("appointments").document(appt.id).delete().addOnSuccessListener {
                staffAppointments.remove(appt)

                if (uidFinal.isNotEmpty()) {
                    val notificationData = hashMapOf(
                        "uid" to uidFinal,
                        "message" to "Your appointment on ${appt.date} at ${appt.time} has been canceled by staff.",
                        "timestamp" to Timestamp.now()
                    )
                    firestore.collection("notifications").add(notificationData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Appointment canceled and notification sent", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Appointment canceled but failed to send notification", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Appointment canceled but UID missing", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch appointment: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun completeAppointment(appt: Appointment) {
        val completedData = hashMapOf(
            "uid" to appt.customerUid.ifEmpty { appt.uid },
            "orderId" to appt.orderId,
            "token" to appt.token,
            "branch" to appt.branch,
            "branchName" to appt.branchName,
            "date" to appt.date,
            "slot" to appt.time,
            "service" to appt.service,
            "staffName" to appt.staffName,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("completed_appointments").add(completedData).addOnSuccessListener {
            firestore.collection("appointments").document(appt.id).delete().addOnSuccessListener {
                staffAppointments.remove(appt)
                completedAppointments.add(appt)
                Toast.makeText(context, "Marked as complete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteCompleted(appt: Appointment) {
        firestore.collection("completed_appointments").document(appt.id).delete().addOnSuccessListener {
            completedAppointments.remove(appt)
            Toast.makeText(context, "Deleted from completed bookings", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadAppointments(staff: StaffMember) {
        staffAppointments.clear()
        completedAppointments.clear()
        customerDetailsMap.clear()
        isLoading = true

        firestore.collection("appointments")
            .whereEqualTo("staffName", staff.staffName)
            .get().addOnSuccessListener { docs ->
                pendingLookups = 0 // no extra lookups now
                for (doc in docs) {
                    // Build with explicit field fixes (slot -> time, uid variants, name/phone)
                    val appt = doc.toObject(Appointment::class.java).copy(
                        id = doc.id,
                        time = doc.getString("slot") ?: (doc.getString("time") ?: ""),
                        customerName = doc.getString("customerName") ?: "",
                        customerPhone = doc.getString("customerPhone") ?: "",
                        customerUid = doc.getString("customerUid") ?: (doc.getString("uid") ?: ""),
                        uid = doc.getString("uid") ?: ""
                    )
                    staffAppointments.add(appt)

                    val pair = Pair(
                        appt.customerName.ifEmpty { "Unknown" },
                        appt.customerPhone.ifEmpty { "N/A" }
                    )
                    if (appt.customerUid.isNotEmpty()) customerDetailsMap[appt.customerUid] = pair
                    if (appt.uid.isNotEmpty()) customerDetailsMap[appt.uid] = pair
                }
                isLoading = false
            }

        firestore.collection("completed_appointments")
            .whereEqualTo("staffName", staff.staffName)
            .get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    val appt = doc.toObject(Appointment::class.java).copy(
                        id = doc.id,
                        time = doc.getString("slot") ?: (doc.getString("time") ?: ""),
                        customerName = doc.getString("customerName") ?: "",
                        customerPhone = doc.getString("customerPhone") ?: "",
                        customerUid = doc.getString("customerUid") ?: (doc.getString("uid") ?: ""),
                        uid = doc.getString("uid") ?: ""
                    )
                    completedAppointments.add(appt)

                    val pair = Pair(
                        appt.customerName.ifEmpty { "Completed" },
                        appt.customerPhone.ifEmpty { "N/A" }
                    )
                    if (appt.customerUid.isNotEmpty()) customerDetailsMap[appt.customerUid] = pair
                    if (appt.uid.isNotEmpty()) customerDetailsMap[appt.uid] = pair
                }
            }
    }

    LaunchedEffect(Unit) {
        try {
            val staffQuery = firestore.collection("staff").whereEqualTo("staffName", staffName).get().await()
            if (!staffQuery.isEmpty) {
                val staffDoc = staffQuery.documents.first()
                val staff = StaffMember(
                    id = staffDoc.getString("id") ?: "",
                    staffName = staffDoc.getString("staffName") ?: "",
                    email = staffDoc.getString("email") ?: "",
                    branchCode = staffDoc.getString("branchCode") ?: "",
                    serviceType = staffDoc.getString("serviceType") ?: ""
                )
                currentStaff = staff
                loadAppointments(staff)
            } else {
                Toast.makeText(context, "Staff not found", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.bank_logo_rmv),
                contentDescription = "Logo",
                modifier = Modifier.align(Alignment.CenterHorizontally).height(60.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            currentStaff?.let { staff ->
                Text("Hello, ${staff.staffName}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828), modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(staff.serviceType, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Branch: ${staff.branchCode}", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Your Appointments", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFC62828))
                }
            } else if (staffAppointments.isEmpty()) {
                Text("No upcoming appointments.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(staffAppointments) { appt ->
                        val customer = customerDetailsMap[appt.customerUid.ifEmpty { appt.uid }]
                        AppointmentCardStyled(
                            name = customer?.first ?: appt.customerName.ifEmpty { "Loading..." },
                            phone = customer?.second ?: appt.customerPhone.ifEmpty { "Loading..." },
                            appointment = appt,
                            yellow = yellow,
                            red = Color(0xFFC62828),
                            green = green,
                            onCancel = { cancelAppointment(appt) },
                            onComplete = { completeAppointment(appt) }
                        )
                    }
                }
            }

            if (completedAppointments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Completed Bookings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(completedAppointments) { appt ->
                        val customer = customerDetailsMap[appt.customerUid.ifEmpty { appt.uid }]
                        AppointmentCardStyled(
                            name = customer?.first ?: appt.customerName.ifEmpty { "Completed" },
                            phone = customer?.second ?: appt.customerPhone.ifEmpty { "N/A" },
                            appointment = appt,
                            yellow = yellow,
                            red = Color(0xFFC62828),
                            green = green,
                            isCompleted = true,
                            onDeleteCompleted = { deleteCompleted(appt) }
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            StaffBottomNavBar(
                selectedTab = "Home",
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
