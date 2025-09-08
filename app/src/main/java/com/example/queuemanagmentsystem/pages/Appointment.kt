package com.example.queuemanagmentsystem.pages

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.queuemanagmentsystem.R
import com.example.queuemanagmentsystem.ui.theme.LocalAppDarkMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavController, selectedService: String = "Account Opening") {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val context = LocalContext.current
    val dark = LocalAppDarkMode.current

    val surface = MaterialTheme.colorScheme.surface
    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground

    // Enhanced color scheme for professional look
    val lightGray = Color(0xFFF5F5F5)
    val cardBackground = if (dark) Color(0xFF2A2A2A) else Color.White
    val subtleBg = if (dark) Color(0xFF3A3A3A) else Color(0xFFFFF8E1)
    val subtleBgAlt = if (dark) Color(0xFF404040) else Color(0xFFFFFBF0)
    val bookedBg = if (dark) Color(0xFF4A1C1C) else Color(0xFFFFEBEE)
    val bookedBorder = if (dark) Color(0xFFEF5350) else red
    val slotBorderBase = if (dark) Color(0xFF777777) else yellow
    val dropdownContainer = cardBackground
    val dropdownText = if (dark) Color.White else Color.Black
    val sectionTextColor = if (dark) Color.White else red

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Branches exactly as in the first screenshot (same order)
    val branchCodeMap = linkedMapOf(
        "Colombo" to "BR001",
        "Kandy" to "BR002",
        "Galle" to "BR003",
        "Jaffna" to "BR004",
        "Batticaloa" to "BR005",
        "Badulla" to "BR006",
        "Anuradhapura" to "BR007",
        "Kalutara" to "BR008",
        "Gampaha" to "BR009",
        "Kurunegala" to "BR010",
        "Matara" to "BR011",
        "Trincomalee" to "BR012",
        "Polonnaruwa" to "BR013",
        "Kegalle" to "BR014",
        "Matale" to "BR015",
        "Ampara" to "BR016",
        "Moneragala" to "BR017",
        "Nuwara Eliya" to "BR018"
    )

    val today = Calendar.getInstance()
    val selectedDate = remember { mutableStateOf(today.time) }

    val timeSlots = listOf(
        "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
        "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM",
        "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM",
        "12:00 PM", "12:15 PM", "12:30 PM", "12:45 PM",
        "01:00 PM", "01:15 PM", "01:30 PM", "01:45 PM",
        "02:00 PM", "02:15 PM", "02:30 PM", "02:45 PM",
        "03:00 PM", "03:15 PM", "03:30 PM", "03:45 PM"
    )

    val branchList = branchCodeMap.keys.toList()
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf("Slots") }
    var expanded by remember { mutableStateOf(false) }
    var selectedBranch by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(selectedDate.value)
    val bookedSlots = remember { mutableStateListOf<String>() }

    LaunchedEffect(selectedBranch, selectedDate.value) {
        if (selectedBranch.isNotEmpty()) {
            val branchCode = branchCodeMap[selectedBranch] ?: "UNKNOWN"
            firestore.collection("appointments")
                .whereEqualTo("branch", branchCode)
                .whereEqualTo("date", formattedDate)
                .get()
                .addOnSuccessListener { docs ->
                    bookedSlots.clear()
                    docs.forEach { doc -> bookedSlots.add(doc.getString("slot") ?: "") }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Fix: apply Color or Brush via .then to avoid mixed-type argument in single background()
            .then(
                if (dark) {
                    Modifier.background(background)
                } else {
                    Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFDE7),
                                Color(0xFFFFF8E1),
                                Color.White
                            )
                        )
                    )
                }
            )
            .testTag("appointmentScreen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
                .testTag("appointmentContent")
        ) {
            // Enhanced Header with gradient background
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .testTag("appointmentHeader"),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController.navigate("landing") }
                        .padding(20.dp)
                        .testTag("backButton")
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = red.copy(alpha = 0.1f)),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = red,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Book Appointment",
                            color = sectionTextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Schedule your banking service",
                            color = onBackground.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enhanced Banner Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bannerImage"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.appointmentimg),
                        contentDescription = "Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    // Gradient overlay for better text readability
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Service Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("serviceSelectionCard"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            tint = red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Selected Service",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = sectionTextColor,
                            modifier = Modifier.testTag("selectedServiceTitle")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selectedServiceDisplay"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dark) Color(0xFF4A4A4A) else yellow.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = selectedService,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (dark) Color.White else Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Branch Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("branchSelectionCard"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Select Branch",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = sectionTextColor,
                            modifier = Modifier.testTag("selectBranchTitle")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .testTag("branchDropdown"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dark) Color(0xFF4A4A4A) else subtleBg
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                if (selectedBranch.isEmpty()) "Choose your branch" else selectedBranch,
                                color = if (selectedBranch.isEmpty())
                                    onBackground.copy(alpha = 0.6f) else
                                    if (dark) Color.White else Color.Black,
                                fontWeight = if (selectedBranch.isEmpty()) FontWeight.Normal else FontWeight.Medium,
                                modifier = Modifier.testTag("selectedBranchText")
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Arrow",
                                tint = if (dark) Color.White else Color.Black
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(dropdownContainer, RoundedCornerShape(12.dp))
                                .border(1.dp, yellow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .testTag("branchDropdownMenu")
                        ) {
                            branchList.forEach { branch ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            branch,
                                            color = if (dark) Color.White else dropdownText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = {
                                        selectedBranch = branch
                                        expanded = false
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .testTag("branchOption_$branch")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dateSelectionCard"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Select Date",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = sectionTextColor,
                            modifier = Modifier.testTag("selectDateTitle")
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val datePickerDialog = remember {
                        DatePickerDialog(context, { _, year, month, day ->
                            val cal = Calendar.getInstance().apply { set(year, month, day) }
                            if (cal.get(Calendar.DAY_OF_WEEK) in listOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
                                Toast.makeText(context, "Weekends not allowed", Toast.LENGTH_LONG).show()
                            } else selectedDate.value = cal.time
                        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                            .testTag("datePickerButton"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = yellow),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = red,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Pick a Date",
                                color = red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selectedDateDisplay"),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dark) Color(0xFF4A4A4A) else Color(0xFFF0F0F0)
                        )
                    ) {
                        Text(
                            "Selected: ${displayDateFormat.format(selectedDate.value)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (dark) Color.White else onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time Slots Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timeSlotsCard"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Available Time Slots",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = sectionTextColor,
                            modifier = Modifier.testTag("timeSlotsTitle")
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Time slots grid with enhanced styling
                    timeSlots.chunked(2).forEach { rowSlots ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val isBooked = bookedSlots.contains(slot)
                                val isSelected = selectedSlot == slot

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(
                                            elevation = if (isSelected) 8.dp else 2.dp,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = !isBooked) {
                                            selectedSlot = slot
                                        }
                                        .testTag("timeSlot_$slot"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isBooked -> bookedBg
                                            isSelected -> yellow
                                            else -> if (dark) Color(0xFF4A4A4A) else subtleBgAlt
                                        }
                                    ),
                                    border = BorderStroke(
                                        2.dp,
                                        when {
                                            isBooked -> bookedBorder
                                            isSelected -> red
                                            else -> slotBorderBase.copy(alpha = 0.3f)
                                        }
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            slot,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = when {
                                                isBooked -> bookedBorder
                                                isSelected -> red
                                                else -> if (dark) Color.White else Color.Black
                                            }
                                        )
                                    }
                                }
                            }
                            if (rowSlots.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced Book Appointment Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (selectedSlot != null && selectedBranch.isNotEmpty()) {
                            val user = auth.currentUser ?: return@clickable Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show()
                            val uid = user.uid
                            val branchCode = branchCodeMap[selectedBranch] ?: "UNKNOWN"
                            val token = (100000..999999).random().toString()
                            val orderId = UUID.randomUUID().toString().take(10).uppercase()

                            firestore.collection("appointments")
                                .whereEqualTo("branch", branchCode)
                                .whereEqualTo("date", formattedDate)
                                .whereEqualTo("slot", selectedSlot)
                                .get()
                                .addOnSuccessListener { docs ->
                                    if (!docs.isEmpty) {
                                        Toast.makeText(context, "Slot already booked!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        firestore.collection("staff")
                                            .whereEqualTo("branchCode", branchCode)
                                            .get()
                                            .addOnSuccessListener { staffDocs ->
                                                val matchingStaff = staffDocs.firstOrNull {
                                                    val staffBranch = it.getString("branchCode")?.trim() ?: ""
                                                    val staffService = it.getString("serviceType")?.trim() ?: ""
                                                    staffBranch == branchCode &&
                                                            staffService.equals(selectedService, ignoreCase = true)
                                                }

                                                if (matchingStaff != null) {
                                                    val staffName = matchingStaff.getString("staffName") ?: "Not Assigned"

                                                    firestore.collection("users").document(uid).get()
                                                        .addOnSuccessListener { userDoc ->
                                                            val username = userDoc.getString("username") ?: "Unknown"
                                                            val phone = userDoc.getString("phone") ?: "N/A"

                                                            val appointment = hashMapOf(
                                                                "uid" to uid,
                                                                "orderId" to orderId,
                                                                "token" to token,
                                                                "branch" to branchCode,
                                                                "branchName" to selectedBranch,
                                                                "date" to formattedDate,
                                                                "slot" to selectedSlot,
                                                                "service" to selectedService,
                                                                "staffName" to staffName,
                                                                "customerName" to username,
                                                                "customerPhone" to phone,
                                                                "timestamp" to System.currentTimeMillis()
                                                            )

                                                            firestore.collection("appointments").add(appointment)
                                                                .addOnSuccessListener {
                                                                    Toast.makeText(context, "Appointment Confirmed!", Toast.LENGTH_SHORT).show()
                                                                    navController.navigate("slots")
                                                                }
                                                                .addOnFailureListener {
                                                                    Toast.makeText(context, "Booking failed!", Toast.LENGTH_SHORT).show()
                                                                }
                                                        }
                                                } else {
                                                    Toast.makeText(context, "Matching staff not found", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Error fetching staff", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Select branch and time slot", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .testTag("confirmAppointmentButton"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = red),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Confirm Appointment",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .testTag("bottomNavigation")
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
