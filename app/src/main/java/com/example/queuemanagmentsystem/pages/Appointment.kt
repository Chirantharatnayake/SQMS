package com.example.queuemanagmentsystem.pages

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    navController: NavController,
    selectedService: String = "Account Opening"
) {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val context = LocalContext.current

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

    val branchList = listOf(
        "Colombo", "Kandy", "Galle", "Jaffna", "Batticaloa", "Badulla", "Anuradhapura", "Kalutara",
        "Gampaha", "Kurunegala", "Matara", "Trincomalee", "Polonnaruwa", "Kegalle", "Matale",
        "Ampara", "Moneragala", "Nuwara Eliya", "Puttalam", "Hambantota", "Rathnapura", "Mawanella"
    )

    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf("Slots") }
    var expanded by remember { mutableStateOf(false) }
    var selectedBranch by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            //  Back
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .clickable { navController.navigate("landing") }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = red,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Back to Home",
                    color = red,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //  Banner (adjusted height)
            Image(
                painter = painterResource(id = R.drawable.appointmentimg),
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp) // increased from 180.dp
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            //  Selected Service
            Text("Selected Service", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)
            Text(
                text = selectedService,
                fontSize = 16.sp,
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )

            //  Select Branch
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Branch", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedBranch.isEmpty()) "Select Branch" else selectedBranch,
                        color = Color.DarkGray
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        tint = Color.DarkGray
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    branchList.forEach { branch ->
                        DropdownMenuItem(
                            text = { Text(branch) },
                            onClick = {
                                selectedBranch = branch
                                expanded = false
                            }
                        )
                    }
                }
            }

            //  Date Picker
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Date", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)

            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)

                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                            Toast.makeText(
                                context,
                                "Selected date falls on a weekend. Bank is open on weekdays only.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            selectedDate.value = calendar.time
                        }
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                )
            }

            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = yellow)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pick a Date", color = red)
            }

            Text(
                text = "Selected: ${dateFormat.format(selectedDate.value)}",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            //  Time Slots
            Text("Available Time Slots", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = red)

            Column {
                timeSlots.chunked(2).forEach { rowSlots ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSlots.forEach { slot ->
                            val isSelected = selectedSlot == slot
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) red else yellow,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(
                                        color = if (isSelected) yellow else Color(0xFFFFF8E1),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedSlot = slot }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = slot,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) red else Color.Black
                                )
                            }
                        }
                        if (rowSlots.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            //  Confirm Button
            Button(
                onClick = {
                    if (selectedSlot != null && selectedBranch.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            "Appointment at $selectedBranch on ${dateFormat.format(selectedDate.value)} at $selectedSlot",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "Please select a branch and time slot.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = red)
            ) {
                Text("Confirm Appointment", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        //  Bottom Navigation (optional)
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
