// File: AdminScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.queuemanagementapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavController
import com.example.queuemanagmentsystem.R
import com.example.queuemanagmentsystem.pages.AdminAccountScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun AdminScreen(navController: NavController) {
    val red = Color(0xFFC62828)
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var selectedIndex by remember { mutableStateOf(0) }

    val screens = listOf("Home", "Account")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Person)

    var branchCode by remember { mutableStateOf("") }
    var staffId by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var staffList by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var userList by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var staffSearch by remember { mutableStateOf("") }
    var userSearch by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var openDeleteStaffDialog by remember { mutableStateOf(false) }
    var openDeleteUserDialog by remember { mutableStateOf(false) }
    var staffIdToDelete by remember { mutableStateOf("") }
    var userEmailToDelete by remember { mutableStateOf("") }

    val serviceOptions = listOf("Account Opening", "Loan Consultation", "Bank Card Services", "Locker Access")

    fun fetchNextStaffId() {
        firestore.collection("staff")
            .orderBy("id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                staffId = if (result.isEmpty) {
                    "staff001"
                } else {
                    val lastStaffId = result.documents.first().getString("id") ?: "staff000"
                    val currentStaffNumber = lastStaffId.removePrefix("staff").toInt()
                    "staff${(currentStaffNumber + 1).toString().padStart(3, '0')}"
                }
            }
    }

    fun fetchStaffAndUsers() {
        firestore.collection("staff").get().addOnSuccessListener { result ->
            staffList = result.documents.mapNotNull { it.data }
        }
        firestore.collection("users").get().addOnSuccessListener { result ->
            userList = result.documents.mapNotNull { it.data }
        }
    }

    fun deleteStaff(staffId: String) {
        firestore.collection("staff").document(staffId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Staff deleted", Toast.LENGTH_SHORT).show()
                fetchStaffAndUsers()
            }
    }

    fun deleteUserByEmail(email: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val docId = result.documents[0].id
                    firestore.collection("users").document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                            fetchStaffAndUsers()
                        }
                }
            }
    }

    LaunchedEffect(Unit) {
        fetchNextStaffId()
        fetchStaffAndUsers()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFFA726)) {
                screens.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = label,
                                tint = if (selectedIndex == index) Color.Red else Color.Black
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                color = if (selectedIndex == index) Color.Red else Color.Black
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        if (selectedIndex == 0) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .padding(16.dp)
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

                Text("Add Staff Member", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = branchCode,
                    onValueChange = { branchCode = it },
                    label = { Text("Branch Code") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Text(
                    "Staff ID: $staffId",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = staffName,
                    onValueChange = { staffName = it },
                    label = { Text("Staff Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = serviceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Service Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        serviceOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    serviceType = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Button(
                    onClick = {
                        if (branchCode.isBlank() || staffName.isBlank() || serviceType.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        } else {
                            val email = "$staffId@peoplebank.lk"
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { authResult ->
                                    val uid = authResult.user?.uid
                                    if (uid != null) {
                                        val staffData = hashMapOf(
                                            "branchCode" to branchCode.trim(),
                                            "email" to email.trim(),
                                            "id" to staffId.trim(),
                                            "password" to password.trim(),
                                            "serviceType" to serviceType.trim(),
                                            "staffName" to staffName.trim()
                                        )
                                        firestore.collection("staff").document(staffId)
                                            .set(staffData)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Staff added", Toast.LENGTH_SHORT).show()
                                                branchCode = ""
                                                staffName = ""
                                                serviceType = ""
                                                password = ""
                                                fetchNextStaffId()
                                                fetchStaffAndUsers()
                                            }
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    Text("Add Staff", color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Registered Staff Members", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = red)

                OutlinedTextField(
                    value = staffSearch,
                    onValueChange = { staffSearch = it },
                    label = { Text("Search Staff...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                staffList.filter {
                    it["staffName"].toString().contains(staffSearch, true) ||
                            it["id"].toString().contains(staffSearch, true)
                }.forEach { staff ->
                    val id = staff["id"].toString()
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ID: $id", fontWeight = FontWeight.Bold)
                                Text("Name: ${staff["staffName"]}")
                                Text("Service: ${staff["serviceType"]}")
                                Text("Branch: ${staff["branchCode"]}")
                            }
                            Button(
                                onClick = {
                                    staffIdToDelete = id
                                    openDeleteStaffDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = red)
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Registered Customers", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = red)

                OutlinedTextField(
                    value = userSearch,
                    onValueChange = { userSearch = it },
                    label = { Text("Search Customers...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                userList.filter {
                    it["username"].toString().contains(userSearch, true) ||
                            it["email"].toString().contains(userSearch, true)
                }.forEach { user ->
                    val email = user["email"].toString()
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Username: ${user["username"]}", fontWeight = FontWeight.Bold)
                                Text("Phone: ${user["phone"]}")
                                Text("Email: $email")
                            }
                            Button(
                                onClick = {
                                    userEmailToDelete = email
                                    openDeleteUserDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = red)
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Confirmation dialogs
            if (openDeleteStaffDialog) {
                AlertDialog(
                    onDismissRequest = { openDeleteStaffDialog = false },
                    title = { Text("Delete Staff Member") },
                    text = { Text("Are you sure you want to delete this staff member permanently?") },
                    confirmButton = {
                        TextButton(onClick = {
                            deleteStaff(staffIdToDelete)
                            openDeleteStaffDialog = false
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDeleteStaffDialog = false }) { Text("No") }
                    }
                )
            }

            if (openDeleteUserDialog) {
                AlertDialog(
                    onDismissRequest = { openDeleteUserDialog = false },
                    title = { Text("Delete Customer") },
                    text = { Text("Are you sure you want to delete this customer permanently?") },
                    confirmButton = {
                        TextButton(onClick = {
                            deleteUserByEmail(userEmailToDelete)
                            openDeleteUserDialog = false
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDeleteUserDialog = false }) { Text("No") }
                    }
                )
            }
        } else {
            // Account tab → show profile with Logout
            AdminAccountScreen(navController)
        }
    }
}
