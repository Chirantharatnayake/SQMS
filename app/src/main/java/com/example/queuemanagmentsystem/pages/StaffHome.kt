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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queuemanagmentsystem.R //

data class Appointment(
    val name: String,
    val service: String,
    val date: String,
    val time: String
)

@Composable
fun StaffScreen() {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)

    val appointments = listOf(
        Appointment("Afzal Sheik", "Account Opening", "July 30", "10:00 AM"),
        Appointment("Binuka Lewke", "Loan Consultation", "August 1", "02:00 PM"),
        Appointment("Chirantha Ratnayake", "Card Services", "August 2", "11:30 AM")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp)) // ⬅️ Top spacing increased to avoid camera notch

        // Logo
        Image(
            painter = painterResource(id = R.drawable.bank_logo_rmv),
            contentDescription = "People's Bank Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(60.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Welcome Text
        Text(
            text = "Hello, Staff",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = red,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section Title
        Text(
            text = "Customer Appointments",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Appointment List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCardStyled(appointment, yellow, red)
            }
        }
    }
}

@Composable
fun AppointmentCardStyled(appointment: Appointment, yellow: Color, red: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = appointment.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = red
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Service: ${appointment.service}", fontSize = 14.sp)
            Text(text = "Date: ${appointment.date}", fontSize = 14.sp)
            Text(text = "Time: ${appointment.time}", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { /* TODO: Reschedule */ },
                    colors = ButtonDefaults.buttonColors(containerColor = yellow)
                ) {
                    Text("Reschedule")
                }
                OutlinedButton(
                    onClick = { /* TODO: Cancel */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = red)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
