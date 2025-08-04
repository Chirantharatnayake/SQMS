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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavController, selectedService: String = "Account Opening") {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val context = LocalContext.current

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val branchCodeMap = mapOf(
        "Colombo" to "BR001", "Kandy" to "BR002", "Galle" to "BR003", "Jaffna" to "BR004",
        "Batticaloa" to "BR005", "Badulla" to "BR006", "Anuradhapura" to "BR007", "Kalutara" to "BR008",
        "Gampaha" to "BR009", "Kurunegala" to "BR010", "Matara" to "BR011", "Trincomalee" to "BR012",
        "Polonnaruwa" to "BR013", "Kegalle" to "BR014", "Matale" to "BR015", "Ampara" to "BR016",
        "Moneragala" to "BR017", "Nuwara Eliya" to "BR018", "Puttalam" to "BR019",
        "Hambantota" to "BR020", "Rathnapura" to "BR021", "Mawanella" to "BR022"
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

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
                    .clickable { navController.navigate("landing") }
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = red, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to Home", color = red, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.appointmentimg),
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Selected Service", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)
            Text(
                text = selectedService,
                fontSize = 16.sp,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp)).padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Branch", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp)).clickable { expanded = true }.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedBranch.isEmpty()) "Select Branch" else selectedBranch, color = Color.DarkGray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow", tint = Color.DarkGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)) {
                    branchList.forEach { branch ->
                        DropdownMenuItem(text = { Text(branch) }, onClick = {
                            selectedBranch = branch
                            expanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Date", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)
            val datePickerDialog = remember {
                DatePickerDialog(context, { _, year, month, day ->
                    val cal = Calendar.getInstance().apply { set(year, month, day) }
                    if (cal.get(Calendar.DAY_OF_WEEK) in listOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
                        Toast.makeText(context, "Weekends not allowed", Toast.LENGTH_LONG).show()
                    } else selectedDate.value = cal.time
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
            }
            Button(onClick = { datePickerDialog.show() }, modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = yellow)) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pick a Date", color = red)
            }
            Text("Selected: ${dateFormat.format(selectedDate.value)}", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 16.dp))

            Text("Available Time Slots", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)
            Column {
                timeSlots.chunked(2).forEach { rowSlots ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowSlots.forEach { slot ->
                            val isBooked = bookedSlots.contains(slot)
                            val isSelected = selectedSlot == slot
                            Box(
                                modifier = Modifier.weight(1f).border(2.dp, when {
                                    isBooked -> Color.Red
                                    isSelected -> red
                                    else -> yellow
                                }, RoundedCornerShape(12.dp)).background(when {
                                    isBooked -> Color(0xFFFFCDD2)
                                    isSelected -> yellow
                                    else -> Color(0xFFFFF8E1)
                                }, RoundedCornerShape(12.dp)).clickable(enabled = !isBooked) {
                                    selectedSlot = slot
                                }.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(slot, fontWeight = FontWeight.Medium, color = when {
                                    isBooked -> Color.Red
                                    isSelected -> red
                                    else -> Color.Black
                                })
                            }
                        }
                        if (rowSlots.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = {
                    if (selectedSlot != null && selectedBranch.isNotEmpty()) {
                        val user = auth.currentUser ?: return@Button Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show()
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
                                    val appointment = hashMapOf(
                                        "uid" to uid,
                                        "orderId" to orderId,
                                        "token" to token,
                                        "branch" to branchCode,
                                        "branchName" to selectedBranch,
                                        "date" to formattedDate,
                                        "slot" to selectedSlot,
                                        "service" to selectedService,
                                        "staff" to "To Be Assigned",
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
                            }
                    } else {
                        Toast.makeText(context, "Select branch and time slot", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = red)
            ) {
                Text("Confirm Appointment", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
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