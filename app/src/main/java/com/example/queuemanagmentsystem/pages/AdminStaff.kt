package com.example.queuemanagmentsystem.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.queuemanagmentsystem.R
import com.google.firebase.firestore.FirebaseFirestore

// Enhanced color palette for People's Bank theme
private val PrimaryYellow = Color(0xFFF9A825)
private val SecondaryYellow = Color(0xFFFDD835)
private val PrimaryRed = Color(0xFFC62828)
private val SecondaryRed = Color(0xFFD32F2F)
private val White = Color.White
private val LightGray = Color(0xFFF5F5F5)
private val DarkGray = Color(0xFF424242)
private val SoftShadow = Color(0x1A000000)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AdminLoginScreen(navController: NavHostController, onBack: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var selectedRole by remember { mutableStateOf("Admin") }
    val roles = listOf("Admin", "Staff")
    var expanded by remember { mutableStateOf(false) }

    var adminOrStaffId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var branchCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryYellow,
                        SecondaryYellow.copy(alpha = 0.8f),
                        PrimaryYellow.copy(alpha = 0.9f)
                    )
                )
            )
            .testTag("adminStaffScreen")
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .testTag("adminStaffContent"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Animated logo section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -100 },
                    animationSpec = tween(800, easing = EaseOutBounce)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Card(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = SoftShadow,
                            spotColor = SoftShadow
                        )
                        .testTag("logoCard"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bank_logo_rmv),
                            contentDescription = "People's Bank Logo",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated title section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(1000, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.testTag("titleSection")) {
                    Text(
                        text = "Admin & Staff Portal",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("titleText")
                    )
                    Text(
                        text = "Secure access for authorized personnel",
                        fontSize = 16.sp,
                        color = DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp).testTag("subtitleText")
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login form card
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { 500 },
                    animationSpec = tween(1200, delayMillis = 400, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(1200, delayMillis = 400))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = SoftShadow,
                            spotColor = SoftShadow
                        )
                        .testTag("loginFormCard"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Role selection dropdown
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("roleSelectionCard"),
                            colors = CardDefaults.cardColors(
                                containerColor = LightGray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.padding(4.dp).testTag("roleDropdownBox")
                            ) {
                                OutlinedTextField(
                                    value = selectedRole,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = {
                                        Text(
                                            "Select Role",
                                            color = DarkGray.copy(alpha = 0.7f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (selectedRole == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.Badge,
                                            contentDescription = null,
                                            tint = PrimaryRed.copy(alpha = 0.7f)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("roleDropdownField"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryRed,
                                        unfocusedBorderColor = DarkGray.copy(alpha = 0.3f),
                                        focusedLabelColor = PrimaryRed,
                                        unfocusedLabelColor = DarkGray.copy(alpha = 0.7f)
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(
                                        White,
                                        RoundedCornerShape(12.dp)
                                    ).testTag("roleDropdownMenu")
                                ) {
                                    roles.forEach { role ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = if (role == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.Badge,
                                                        contentDescription = null,
                                                        tint = PrimaryRed,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        role,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedRole = role
                                                expanded = false
                                                // Clear branch code when switching to Admin
                                                if (role == "Admin") {
                                                    branchCode = ""
                                                }
                                            },
                                            modifier = Modifier.testTag("roleOption_$role")
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ID field
                        EnhancedAdminTextField(
                            value = adminOrStaffId,
                            onValueChange = { adminOrStaffId = it },
                            label = if (selectedRole == "Admin") "Admin ID" else "Staff ID",
                            icon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text,
                            testTag = "idField"
                        )

                        // Branch code field (only for staff)
                        AnimatedVisibility(
                            visible = selectedRole == "Staff",
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Column(modifier = Modifier.testTag("branchCodeSection")) {
                                Spacer(modifier = Modifier.height(20.dp))
                                EnhancedAdminTextField(
                                    value = branchCode,
                                    onValueChange = { branchCode = it },
                                    label = "Branch Code",
                                    icon = Icons.Default.Business,
                                    keyboardType = KeyboardType.Text,
                                    testTag = "branchCodeField"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Password field
                        EnhancedAdminPasswordField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            isVisible = passwordVisible,
                            onToggleVisibility = { passwordVisible = !passwordVisible },
                            testTag = "passwordField",
                            toggleTag = "passwordToggle"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Login button with loading state
                        Button(
                            onClick = {
                                isLoading = true
                                Log.d("LOGIN_DEBUG", "Role=$selectedRole | ID=$adminOrStaffId | Branch=$branchCode")

                                if (selectedRole == "Admin") {
                                    db.collection("admins")
                                        .whereEqualTo("id", adminOrStaffId)
                                        .get()
                                        .addOnSuccessListener { result ->
                                            isLoading = false
                                            Log.d("LOGIN_DEBUG", "Admin query result: ${result.documents}")

                                            if (!result.isEmpty) {
                                                val user = result.documents[0]
                                                if (user.getString("password") == password) {
                                                    Toast.makeText(context, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("admin_home")
                                                    return@addOnSuccessListener
                                                } else {
                                                    Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show()
                                                    return@addOnSuccessListener
                                                }
                                            } else {
                                                Toast.makeText(context, "Admin not found", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                            Log.e("LOGIN_DEBUG", "Admin query failed: ${it.message}")
                                            Toast.makeText(context, "Error connecting to database", Toast.LENGTH_SHORT).show()
                                        }

                                } else {
                                    db.collection("staff")
                                        .whereEqualTo("id", adminOrStaffId)
                                        .whereEqualTo("branchCode", branchCode)
                                        .get()
                                        .addOnSuccessListener { result ->
                                            isLoading = false
                                            Log.d("LOGIN_DEBUG", "Staff query result: ${result.documents}")

                                            if (!result.isEmpty) {
                                                val user = result.documents[0]
                                                if (user.getString("password") == password) {
                                                    val staffName = user.getString("staffName") ?: "Unknown"
                                                    Log.d("LOGIN_SUCCESS", "Staff Login Successful - Name: $staffName, ID: $adminOrStaffId, Branch: $branchCode")
                                                    Toast.makeText(context, "Staff Login Successful", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("staff_home/${staffName}")
                                                    return@addOnSuccessListener
                                                } else {
                                                    Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show()
                                                    return@addOnSuccessListener
                                                }
                                            } else {
                                                Toast.makeText(context, "Staff not found or incorrect branch", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                            Log.e("LOGIN_DEBUG", "Staff query failed: ${it.message}")
                                            Toast.makeText(context, "Error connecting to database", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .shadow(
                                    elevation = if (isLoading) 4.dp else 12.dp,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .testTag("loginButton"),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryRed,
                                disabledContainerColor = PrimaryRed.copy(alpha = 0.6f)
                            )
                        ) {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Authenticating...",
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Login,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sign In",
                                        color = White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Back navigation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 800))
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(White.copy(alpha = 0.3f))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .testTag("backButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back to Customer Login",
                        color = PrimaryRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EnhancedAdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType,
    testTag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = DarkGray.copy(alpha = 0.7f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryRed.copy(alpha = 0.7f)
            )
        },
        modifier = Modifier.fillMaxWidth().let { base ->
            if (testTag != null) base.testTag(testTag) else base
        },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryRed,
            unfocusedBorderColor = DarkGray.copy(alpha = 0.3f),
            focusedLabelColor = PrimaryRed,
            unfocusedLabelColor = DarkGray.copy(alpha = 0.7f),
            cursorColor = PrimaryRed,
            focusedLeadingIconColor = PrimaryRed,
            unfocusedLeadingIconColor = DarkGray.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
private fun EnhancedAdminPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    testTag: String? = null,
    toggleTag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = DarkGray.copy(alpha = 0.7f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = PrimaryRed.copy(alpha = 0.7f)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onToggleVisibility,
                modifier = if (toggleTag != null) Modifier.testTag(toggleTag) else Modifier
            ) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle password visibility",
                    tint = DarkGray.copy(alpha = 0.7f)
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().let { base ->
            if (testTag != null) base.testTag(testTag) else base
        },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryRed,
            unfocusedBorderColor = DarkGray.copy(alpha = 0.3f),
            focusedLabelColor = PrimaryRed,
            unfocusedLabelColor = DarkGray.copy(alpha = 0.7f),
            cursorColor = PrimaryRed,
            focusedLeadingIconColor = PrimaryRed,
            unfocusedLeadingIconColor = DarkGray.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true
    )
}
