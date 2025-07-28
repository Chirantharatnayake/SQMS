package com.example.queuemanagementapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queuemanagmentsystem.R

data class AdminAppointment(
    val customerName: String,
    val service: String,
    val date: String,
    val time: String
)

@Composable
fun AdminScreen() {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)

    val appointments = listOf(
        AdminAppointment("Afzal Sheik", "Account Opening", "July 30", "10:00 AM"),
        AdminAppointment("Binuka Lewke", "Loan Consultation", "August 1", "02:00 PM")
    )

    var branchCode by remember { mutableStateOf("") }
    var staffId by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.bank_logo_rmv),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(60.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Greeting
        Text(
            text = "Hello, Admin",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = red,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Appointments
        Text(
            text = "Ongoing Appointments",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(appointments) { appointment ->
                AdminAppointmentCard(appointment = appointment, red = red)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add Staff Form
        Text(
            text = "Add Staff Member",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = branchCode,
            onValueChange = { branchCode = it },
            label = { Text("Branch Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = staffId,
            onValueChange = { staffId = it },
            label = { Text("Staff ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = staffName,
            onValueChange = { staffName = it },
            label = { Text("Staff Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // TODO: Handle add staff logic (e.g., Firebase)
            },
            colors = ButtonDefaults.buttonColors(containerColor = red),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Add Staff", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AdminAppointmentCard(appointment: AdminAppointment, red: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = appointment.customerName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = red
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Service: ${appointment.service}", fontSize = 14.sp)
            Text(text = "Date: ${appointment.date}", fontSize = 14.sp)
            Text(text = "Time: ${appointment.time}", fontSize = 14.sp)
        }
    }
}
