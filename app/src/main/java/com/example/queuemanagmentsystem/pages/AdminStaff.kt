package com.example.queuemanagmentsystem.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.queuemanagmentsystem.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(navController: NavHostController, onBack: () -> Unit) {

    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val white = Color.White
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var selectedRole by remember { mutableStateOf("Admin") }
    val roles = listOf("Admin", "Staff")
    var expanded by remember { mutableStateOf(false) }

    var adminOrStaffId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var branchCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = yellow
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.bank_logo_rmv),
                contentDescription = "People's Bank Logo",
                modifier = Modifier
                    .height(90.dp)
                    .padding(bottom = 4.dp)
            )

            Text(
                text = "Admin / Staff Login",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = red
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = red,
                        unfocusedBorderColor = red,
                        focusedLabelColor = red
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                selectedRole = role
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = adminOrStaffId,
                onValueChange = { adminOrStaffId = it },
                label = { Text(if (selectedRole == "Admin") "Admin ID" else "Staff ID") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = red,
                    unfocusedBorderColor = red,
                    focusedLabelColor = red
                )
            )

            if (selectedRole == "Staff") {
                OutlinedTextField(
                    value = branchCode,
                    onValueChange = { branchCode = it },
                    label = { Text("Branch Code") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = red,
                        unfocusedBorderColor = red,
                        focusedLabelColor = red
                    )
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = description,
                            tint = red
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = red,
                    unfocusedBorderColor = red,
                    focusedLabelColor = red
                )
            )

            Button(
                onClick = {
                    Log.d("LOGIN_DEBUG", "Role=$selectedRole | ID=$adminOrStaffId | Branch=$branchCode")

                    if (selectedRole == "Admin") {
                        db.collection("admins")
                            .whereEqualTo("id", adminOrStaffId)
                            .get()
                            .addOnSuccessListener { result ->
                                Log.d("LOGIN_DEBUG", "Admin query result: ${result.documents}")

                                if (!result.isEmpty) {
                                    val user = result.documents[0]
                                    if (user.getString("password") == password) {
                                        Toast.makeText(context, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("admin_home")
                                    } else {
                                        Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Admin not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Log.e("LOGIN_DEBUG", "Admin query failed: ${it.message}")
                                Toast.makeText(context, "Error connecting to database", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        db.collection("staff")
                            .whereEqualTo("id", adminOrStaffId)
                            .whereEqualTo("branchCode", branchCode)
                            .get()
                            .addOnSuccessListener { result ->
                                Log.d("LOGIN_DEBUG", "Staff query result: ${result.documents}")

                                if (!result.isEmpty) {
                                    val user = result.documents[0]
                                    if (user.getString("password") == password) {
                                        Toast.makeText(context, "Staff Login Successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("staff_home")
                                    } else {
                                        Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Staff not found or incorrect branch", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Log.e("LOGIN_DEBUG", "Staff query failed: ${it.message}")
                                Toast.makeText(context, "Error connecting to database", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = red),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 12.dp)
            ) {
                Text("Login", color = white, fontSize = 18.sp)
            }

            TextButton(onClick = onBack) {
                Text("Back to Register", color = Color.Blue)
            }
        }
    }
}
