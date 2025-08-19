package com.example.queuemanagmentsystem.pages

import android.util.Log
import android.util.Patterns
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// -------- Constants & Helpers (no behavior change) --------
private val Yellow = Color(0xFFF9A825)
private val Red = Color(0xFFC62828)
private val White = Color.White

private val OLD_NIC_REGEX = Regex("^[0-9]{9}[VvXx]$")
private val NEW_NIC_REGEX = Regex("^19[0-9]{10}|20[0-9]{10}$") // kept identical to your code
private val LK_PHONE_REGEX = Regex("^\\+94[0-9]{9}$")

private const val ERR_REQUIRED = "All fields are required"
private const val ERR_EMAIL = "Invalid email format"
private const val ERR_PHONE = "Phone number must be in Sri Lankan format: +947XXXXXXXX"
private const val ERR_PWD_LEN = "Password must be at least 6 characters"
private const val ERR_PWD_MATCH = "Passwords do not match"
private const val ERR_NIC = "Invalid NIC. Use 123456789V or 199912345678 format"

private fun isValidNIC(nic: String) = OLD_NIC_REGEX.matches(nic) || NEW_NIC_REGEX.matches(nic)
private fun isValidSriLankanPhone(phone: String) = LK_PHONE_REGEX.matches(phone)

@Composable
fun RegisterScreen(
    navToLogin: () -> Unit,
    navToAdminLogin: () -> Unit = {}
) {
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

    fun validateInputs(): Boolean {
        return when {
            username.isBlank() || email.isBlank() || nic.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                Toast.makeText(context, ERR_REQUIRED, Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, ERR_EMAIL, Toast.LENGTH_SHORT).show()
                false
            }
            !isValidSriLankanPhone(phone) -> {
                Toast.makeText(context, ERR_PHONE, Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(context, ERR_PWD_LEN, Toast.LENGTH_SHORT).show()
                false
            }
            password != confirmPassword -> {
                Toast.makeText(context, ERR_PWD_MATCH, Toast.LENGTH_SHORT).show()
                false
            }
            !isValidNIC(nic) -> {
                Toast.makeText(context, ERR_NIC, Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    val inputModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Red,
        unfocusedBorderColor = Red,
        focusedLabelColor = Red
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Yellow) {
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

            Text("Register", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Red)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = inputModifier,
                colors = fieldColors
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = inputModifier,
                colors = fieldColors
            )

            OutlinedTextField(
                value = nic,
                onValueChange = { nic = it },
                label = { Text("NIC") },
                modifier = inputModifier,
                colors = fieldColors
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number (+947XXXXXXXX)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = inputModifier,
                colors = fieldColors
            )

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                modifier = inputModifier,
                colors = fieldColors
            )

            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                isVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                modifier = inputModifier,
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateInputs()) {
                        registerUser(
                            auth = auth,
                            firestore = firestore,
                            email = email,
                            password = password,
                            username = username,
                            nic = nic,
                            phone = phone,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Verification email sent. Please check your inbox.",
                                    Toast.LENGTH_LONG
                                ).show()
                                navToLogin()
                            },
                            onError = { msg ->
                                Log.e("REGISTER", msg)
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Register", color = White, fontSize = 18.sp)
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

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier,
    colors: TextFieldColors
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = icon, contentDescription = "Toggle $label")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier,
        colors = colors
    )
}

private fun registerUser(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    email: String,
    password: String,
    username: String,
    nic: String,
    phone: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError(task.exception?.message ?: "Firebase auth failed")
                return@addOnCompleteListener
            }
            val userId = task.result.user?.uid
            if (userId == null) {
                onError("User ID is null after registration")
                return@addOnCompleteListener
            }

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
                            if (verifyTask.isSuccessful) onSuccess()
                            else onError("Failed to send verification email")
                        }
                }
                .addOnFailureListener { e ->
                    onError("Failed to save user data: ${e.message}")
                }
        }
}


