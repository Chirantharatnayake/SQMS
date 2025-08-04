package com.example.queuemanagementapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminScreen() {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var branchCode by remember { mutableStateOf("") }
    var staffId by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.bank_logo_rmv),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(60.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hello, Admin",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = red,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            value = serviceType,
            onValueChange = { serviceType = it },
            label = { Text("Service Type") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (branchCode.isBlank() || staffId.isBlank() || staffName.isBlank() || serviceType.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                } else {
                    val email = "${staffId}@peoplebank.lk"
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid
                            if (uid != null) {
                                val staffData = hashMapOf(
                                    "branchCode" to branchCode,
                                    "staffId" to staffId,
                                    "staffName" to staffName,
                                    "serviceType" to serviceType,
                                    "email" to email
                                )
                                firestore.collection("staff").document(uid)
                                    .set(staffData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Staff added successfully", Toast.LENGTH_SHORT).show()
                                        branchCode = ""
                                        staffId = ""
                                        staffName = ""
                                        serviceType = ""
                                        password = ""
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to save staff data", Toast.LENGTH_SHORT).show()
                                        Log.e("FIRESTORE", "Error: ${it.message}")
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Firebase Auth failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            Log.e("AUTH", "Error: ${it.message}")
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = red),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Add Staff", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
