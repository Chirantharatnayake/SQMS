// File: app/src/main/java/com/example/queuemanagmentsystem/pages/StaffScreen.kt
package com.example.queuemanagmentsystem.pages

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

// --------------------------
// Data models
// --------------------------
data class Appointment(
    val id: String = "",
    val service: String = "",
    val date: String = "",
    val time: String = "",
    val token: String = "",
    val customerUid: String = "",
    val branch: String = "",
    val staffName: String = "",
    val branchName: String = "",
    val orderId: String = "",
    val uid: String = "",
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

data class PriorityEntry(
    val id: String = "",
    val uid: String = "",
    val priorityId: String = "",
    val staffName: String = "",
    val branch: String = "",
    val branchName: String = "",
    val service: String = "",
    val originalAppointmentDate: String = "",
    val originalAppointmentSlot: String = "",
    val createdAt: Timestamp? = null,
    val status: String = "active"
)

// --------------------------
// Crash-safe logo
// --------------------------
@Composable
private fun BankLogo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val resId = remember {
        context.resources.getIdentifier("bank_logo_rmv", "drawable", context.packageName)
    }
    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Logo",
            modifier = modifier
        )
    } else {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Logo",
            modifier = modifier,
            tint = Color(0xFFC62828)
        )
    }
}

// --------------------------
// Appointment card
// --------------------------
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
        modifier = Modifier
            .fillMaxWidth()
            .testTag("appointmentCard_${appointment.token}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = red,
                        modifier = Modifier.testTag("customerName_${appointment.token}")
                    )
                    Text(
                        text = "Phone: $phone",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.testTag("customerPhone_${appointment.token}")
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = green),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("tokenCard_${appointment.token}")
                ) {
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
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Service", fontSize = 12.sp, color = Color.Gray); Text(appointment.service, fontSize = 14.sp) }
                Column { Text("Date", fontSize = 12.sp, color = Color.Gray); Text(appointment.date, fontSize = 14.sp) }
                Column { Text("Time", fontSize = 12.sp, color = Color.Gray); Text(appointment.time, fontSize = 14.sp) }
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
                    ) { Text("Complete", color = Color.White) }

                    OutlinedButton(
                        onClick = { onCancel(appointment) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel", color = red) }
                }
            } else {
                Button(
                    onClick = { onDeleteCompleted(appointment) },
                    colors = ButtonDefaults.buttonColors(containerColor = red),
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Delete", color = Color.White) }
            }
        }
    }
}

// --------------------------
// Priority card
// --------------------------
@Composable
private fun PriorityCard(
    entry: PriorityEntry,
    customerName: String,
    customerPhone: String,
    onClear: (PriorityEntry) -> Unit
) {
    val created = entry.createdAt?.toDate()
    val dateStr = created?.let {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
    } ?: "—"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("priorityCard_${entry.priorityId}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(customerName.ifBlank { "Customer" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    if (customerPhone.isNotBlank()) {
                        Text("Phone: $customerPhone", fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Priority: ${entry.priorityId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Service", fontSize = 12.sp, color = Color.Gray); Text(entry.service, fontSize = 14.sp) }
                Column { Text("Branch", fontSize = 12.sp, color = Color.Gray); Text(entry.branchName, fontSize = 14.sp) }
                Column { Text("Created", fontSize = 12.sp, color = Color.Gray); Text(dateStr, fontSize = 14.sp) }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onClear(entry) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                modifier = Modifier.align(Alignment.End)
            ) { Text("Remove from Priority") }
        }
    }
}

// --------------------------
// Staff bottom nav
// --------------------------
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
            .testTag("staffBottomNavBar")
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
                        .testTag("navItem_$key")
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

// --------------------------
// Staff Home screen
// --------------------------
@Composable
fun StaffScreen(staffName: String, navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val green = Color(0xFF2E7D32)

    var currentStaff by remember { mutableStateOf<StaffMember?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val staffAppointments = remember { mutableStateListOf<Appointment>() }
    val completedAppointments = remember { mutableStateListOf<Appointment>() }

    // uid -> (name, phone)
    val customerDetailsMap = remember { mutableStateMapOf<String, Pair<String, String>>() }

    // Priority
    val priorityEntries = remember { mutableStateListOf<PriorityEntry>() }
    var loadingPriority by remember { mutableStateOf(true) }

    // Dialogs
    var showCancelDialog by remember { mutableStateOf(false) }
    var apptToCancel by remember { mutableStateOf<Appointment?>(null) }

    var showClearPriorityDialog by remember { mutableStateOf(false) }
    var priorityToClear by remember { mutableStateOf<PriorityEntry?>(null) }

    fun ensureCustomerDetails(uid: String) {
        if (uid.isBlank() || customerDetailsMap.containsKey(uid)) return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("username") ?: ""
                val phone = doc.getString("phone") ?: ""
                if (name.isNotBlank() || phone.isNotBlank()) {
                    customerDetailsMap[uid] = name to phone
                }
            }
    }

    fun cancelAppointment(appt: Appointment) {
        firestore.collection("appointments").document(appt.id).get().addOnSuccessListener { document ->
            val uidFromDoc = document.getString("uid")
            val uidFromDocAlt = document.getString("customerUid")
            val uidFinal = uidFromDoc ?: uidFromDocAlt ?: appt.customerUid.ifEmpty { appt.uid }

            firestore.collection("appointments").document(appt.id).delete().addOnSuccessListener {
                staffAppointments.remove(appt)

                // cancellation notification
                if (uidFinal.isNotEmpty()) {
                    val cancellationNotif = hashMapOf<String, Any>(
                        "uid" to uidFinal,
                        "title" to "Appointment Cancelled",
                        "message" to "We sincerely apologize for any inconvenience caused. Your appointment scheduled for ${appt.date} at ${appt.time} has been cancelled by our staff due to unforeseen circumstances.",
                        "timestamp" to Timestamp.now(),
                        "type" to "cancellation"
                    )
                    firestore.collection("notifications").add(cancellationNotif)
                }

                // create priority entry + priority notification
                if (uidFinal.isNotEmpty()) {
                    val priorityId = "P-" + (100000..999999).random().toString()
                    val data = hashMapOf<String, Any>(
                        "uid" to uidFinal,
                        "priorityId" to priorityId,
                        "staffName" to appt.staffName,
                        "branch" to appt.branch,
                        "branchName" to appt.branchName,
                        "service" to appt.service,
                        "originalAppointmentDate" to appt.date,
                        "originalAppointmentSlot" to appt.time,
                        "createdAt" to Timestamp.now(),
                        "status" to "active"
                    )
                    firestore.collection("priority_list").add(data)
                        .addOnSuccessListener { ref ->
                            // show immediately in UI
                            priorityEntries.add(
                                PriorityEntry(
                                    id = ref.id,
                                    uid = uidFinal,
                                    priorityId = priorityId,
                                    staffName = appt.staffName,
                                    branch = appt.branch,
                                    branchName = appt.branchName,
                                    service = appt.service,
                                    originalAppointmentDate = appt.date,
                                    originalAppointmentSlot = appt.time,
                                    createdAt = Timestamp.now(),
                                    status = "active"
                                )
                            )
                            ensureCustomerDetails(uidFinal)

                            val priorityNotif = hashMapOf<String, Any>(
                                "uid" to uidFinal,
                                "title" to "Priority Access Granted",
                                "message" to "You’ve been added to our priority list. Show this Priority ID at the branch to meet ${appt.staffName} on any business day. Priority ID: $priorityId",
                                "timestamp" to Timestamp.now(),
                                "type" to "priority",
                                "priorityId" to priorityId,
                                "staffName" to appt.staffName,
                                "branchName" to appt.branchName,
                                "service" to appt.service
                            )
                            firestore.collection("notifications").add(priorityNotif)
                            Toast.makeText(context, "Cancelled & priority created.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to create priority entry", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to cancel appointment: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun completeAppointment(appt: Appointment) {
        val completedData = hashMapOf<String, Any>(
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

        firestore.collection("completed_appointments").add(completedData).addOnSuccessListener { ref ->
            firestore.collection("appointments").document(appt.id).delete().addOnSuccessListener {
                val completedAppt = appt.copy(id = ref.id)
                staffAppointments.remove(appt)
                completedAppointments.add(completedAppt)
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
        isLoading = true

        firestore.collection("appointments")
            .whereEqualTo("staffName", staff.staffName)
            .get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    val appt = doc.toObject<Appointment>().copy(
                        id = doc.id,
                        time = doc.getString("slot") ?: (doc.getString("time") ?: ""),
                        customerName = doc.getString("customerName") ?: "",
                        customerPhone = doc.getString("customerPhone") ?: "",
                        customerUid = doc.getString("customerUid") ?: (doc.getString("uid") ?: ""),
                        uid = doc.getString("uid") ?: ""
                    )
                    staffAppointments.add(appt)

                    val name = appt.customerName.ifEmpty { "Unknown" }
                    val phone = appt.customerPhone.ifEmpty { "N/A" }
                    if (appt.customerUid.isNotEmpty()) customerDetailsMap.putIfAbsent(appt.customerUid, name to phone)
                    if (appt.uid.isNotEmpty()) customerDetailsMap.putIfAbsent(appt.uid, name to phone)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load appointments: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        firestore.collection("completed_appointments")
            .whereEqualTo("staffName", staff.staffName)
            .get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    val appt = doc.toObject<Appointment>().copy(
                        id = doc.id,
                        time = doc.getString("slot") ?: (doc.getString("time") ?: ""),
                        customerName = doc.getString("customerName") ?: "",
                        customerPhone = doc.getString("customerPhone") ?: "",
                        customerUid = doc.getString("customerUid") ?: (doc.getString("uid") ?: ""),
                        uid = doc.getString("uid") ?: ""
                    )
                    completedAppointments.add(appt)

                    val name = appt.customerName.ifEmpty { "Completed" }
                    val phone = appt.customerPhone.ifEmpty { "N/A" }
                    if (appt.customerUid.isNotEmpty()) customerDetailsMap.putIfAbsent(appt.customerUid, name to phone)
                    if (appt.uid.isNotEmpty()) customerDetailsMap.putIfAbsent(appt.uid, name to phone)
                }
            }
    }

    fun loadPriority(staff: StaffMember) {
        loadingPriority = true
        priorityEntries.clear()
        firestore.collection("priority_list")
            .whereEqualTo("staffName", staff.staffName)
            .whereEqualTo("status", "active")
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    val e = doc.toObject<PriorityEntry>().copy(id = doc.id)
                    priorityEntries.add(e)
                    ensureCustomerDetails(e.uid)
                }
                loadingPriority = false
            }
            .addOnFailureListener {
                loadingPriority = false
                Toast.makeText(context, "Failed to load priority list: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun clearPriority(entry: PriorityEntry) {
        firestore.collection("priority_list")
            .document(entry.id)
            .update(mapOf("status" to "cleared", "clearedAt" to Timestamp.now()))
            .addOnSuccessListener {
                priorityEntries.remove(entry)
                Toast.makeText(context, "Removed from priority list", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to remove: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun clearAllPriority() {
        if (priorityEntries.isEmpty()) return
        val batch = firestore.batch()
        priorityEntries.forEach { e ->
            val ref = firestore.collection("priority_list").document(e.id)
            batch.update(ref, mapOf("status" to "cleared", "clearedAt" to Timestamp.now()))
        }
        batch.commit()
            .addOnSuccessListener {
                priorityEntries.clear()
                Toast.makeText(context, "All priority entries cleared", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to clear all: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Load staff + lists
    LaunchedEffect(staffName) {
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
                loadPriority(staff)
            } else {
                Toast.makeText(context, "Staff not found", Toast.LENGTH_SHORT).show()
                isLoading = false
                loadingPriority = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            isLoading = false
            loadingPriority = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().testTag("staffHomeScreen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .testTag("staffHomeContent")
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            BankLogo(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(60.dp)
                    .testTag("staffHomeLogo")
            )
            Spacer(modifier = Modifier.height(12.dp))

            currentStaff?.let { staff ->
                Text(
                    "Hello, ${staff.staffName}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(staff.serviceType, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Branch: ${staff.branchCode}", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Priority Customers ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Priority Customers", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
                if (priorityEntries.isNotEmpty()) {
                    TextButton(onClick = { clearAllPriority() }, modifier = Modifier.testTag("clearAllPriorityBtn")) {
                        Text("Clear All", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            when {
                loadingPriority -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                priorityEntries.isEmpty() -> {
                    Text("No priority customers.", color = Color.Gray, modifier = Modifier.testTag("noPriorityMessage"))
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        modifier = Modifier.testTag("priorityList")
                    ) {
                        items(priorityEntries, key = { it.id }) { e ->
                            val (name, phone) = customerDetailsMap[e.uid] ?: ("Loading..." to "")
                            PriorityCard(
                                entry = e,
                                customerName = name,
                                customerPhone = phone,
                                onClear = {
                                    priorityToClear = e
                                    showClearPriorityDialog = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Appointments ----------
            Text("Your Appointments", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0xFFC62828)) }
            } else if (staffAppointments.isEmpty()) {
                Text("No upcoming appointments.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.testTag("appointmentsList")
                ) {
                    items(staffAppointments) { appt ->
                        val key = appt.customerUid.ifEmpty { appt.uid }
                        val customer = if (key.isNotEmpty()) customerDetailsMap[key] else null
                        AppointmentCardStyled(
                            name = customer?.first ?: appt.customerName.ifEmpty { "Loading..." },
                            phone = customer?.second ?: appt.customerPhone.ifEmpty { "Loading..." },
                            appointment = appt,
                            yellow = yellow,
                            red = Color(0xFFC62828),
                            green = green,
                            onCancel = {
                                apptToCancel = appt
                                showCancelDialog = true
                            },
                            onComplete = { completeAppointment(appt) }
                        )
                    }
                }
            }

            if (completedAppointments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Completed Bookings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.testTag("completedAppointmentsList")
                ) {
                    items(completedAppointments) { appt ->
                        val key = appt.customerUid.ifEmpty { appt.uid }
                        val customer = if (key.isNotEmpty()) customerDetailsMap[key] else null
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

        // Bottom nav
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            StaffBottomNavBar(
                selectedTab = "Home",
                onTabSelected = { tab ->
                    val encoded = Uri.encode(staffName)
                    when (tab) {
                        "Home" -> navController.navigate("staff_home/$encoded") { launchSingleTop = true }
                        "Account" -> navController.navigate("staff_account/$encoded") { launchSingleTop = true }
                    }
                }
            )
        }

        // Cancel confirmation
        if (showCancelDialog && apptToCancel != null) {
            val a = apptToCancel!!
            AlertDialog(
                onDismissRequest = { showCancelDialog = false; apptToCancel = null },
                title = { Text("Cancel appointment?") },
                text = {
                    Text(
                        "Are you sure you want to cancel this appointment?\n\n" +
                                "Service: ${a.service}\nDate: ${a.date}\nTime: ${a.time}\nToken: ${a.token}"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            cancelAppointment(a)
                            showCancelDialog = false
                            apptToCancel = null
                        }
                    ) { Text("Yes") }
                },
                dismissButton = { TextButton(onClick = { showCancelDialog = false; apptToCancel = null }) { Text("No") } }
            )
        }

        // Clear priority confirmation
        if (showClearPriorityDialog && priorityToClear != null) {
            val e = priorityToClear!!
            AlertDialog(
                onDismissRequest = { showClearPriorityDialog = false; priorityToClear = null },
                title = { Text("Remove priority entry?") },
                text = { Text("This will remove ${customerDetailsMap[e.uid]?.first ?: "this customer"} from the priority list.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clearPriority(e)
                            showClearPriorityDialog = false
                            priorityToClear = null
                        },
                        modifier = Modifier.testTag("confirmClearPriority")
                    ) { Text("Remove") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearPriorityDialog = false; priorityToClear = null },
                        modifier = Modifier.testTag("dismissClearPriority")
                    ) { Text("Cancel") }
                }
            )
        }
    }
}
