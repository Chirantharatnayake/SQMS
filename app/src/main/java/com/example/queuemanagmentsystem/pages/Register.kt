package com.example.queuemanagmentsystem.pages

import android.util.Patterns
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    navToLogin: () -> Unit,
    navToAdminLogin: () -> Unit = {}
) {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val white = Color.White

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nic by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    fun isValidNIC(nic: String): Boolean {
        val oldNIC = Regex("^[0-9]{9}[VvXx]$")
        val newNIC = Regex("^19[0-9]{10}|20[0-9]{10}$")
        return oldNIC.matches(nic) || newNIC.matches(nic)
    }

    fun isValidSriLankanPhone(phone: String): Boolean {
        val sriLankaPhonePattern = Regex("^\\+94[0-9]{9}$")
        return sriLankaPhonePattern.matches(phone)
    }

    fun validateInputs(): Boolean {
        return when {
            username.isBlank() || email.isBlank() || nic.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                false
            }
            !isValidSriLankanPhone(phone) -> {
                Toast.makeText(context, "Phone number must be in Sri Lankan format: +947XXXXXXXX", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                false
            }
            password != confirmPassword -> {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                false
            }
            !isValidNIC(nic) -> {
                Toast.makeText(context, "Invalid NIC. Use 123456789V or 199912345678 format", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = yellow) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.bank_logo_rmv),
                contentDescription = "Bank Logo",
                modifier = Modifier.height(100.dp)
            )

            Text("Register", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = red)

            Spacer(modifier = Modifier.height(16.dp))

            fun inputModifier() = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = red,
                unfocusedBorderColor = red,
                focusedLabelColor = red
            )

            OutlinedTextField(
                value = username, onValueChange = { username = it },
                label = { Text("Username") },
                modifier = inputModifier(),
                colors = fieldColors
            )

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = inputModifier(),
                colors = fieldColors
            )

            OutlinedTextField(
                value = nic, onValueChange = { nic = it },
                label = { Text("NIC") },
                modifier = inputModifier(),
                colors = fieldColors
            )

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Phone Number (+947XXXXXXXX)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = inputModifier(),
                colors = fieldColors
            )

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle Password")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = inputModifier(),
                colors = fieldColors
            )

            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle Confirm Password")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = inputModifier(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateInputs()) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = task.result.user?.uid
                                    if (userId != null) {
                                        val userData = mapOf(
                                            "username" to username,
                                            "email" to email,
                                            "nic" to nic,
                                            "phone" to phone
                                        )
                                        firestore.collection("users").document(userId)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Log.d("FIRESTORE", "User data saved")
                                                auth.currentUser?.sendEmailVerification()
                                                    ?.addOnCompleteListener { verifyTask ->
                                                        if (verifyTask.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Verification email sent. Please check your inbox.",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navToLogin()
                                                        } else {
                                                            Log.e("EMAIL", "Failed to send verification: ${verifyTask.exception?.message}")
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to send verification email",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            }
                                            .addOnFailureListener {
                                                Log.e("FIRESTORE", "Failed to save user data: ${it.message}")
                                                Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "User ID is null after registration", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Log.e("FIREBASE_REGISTER", "Register failed: ${task.exception?.message}")
                                    Toast.makeText(context, "Firebase auth failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = red),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Register", color = white, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = navToLogin) {
                Text("Already have an account? Login", color = Color.Blue)
            }

            TextButton(onClick = navToAdminLogin) {
                Text("Admin/Staff Login", color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
