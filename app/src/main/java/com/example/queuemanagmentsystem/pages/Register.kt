package com.example.queuemanagmentsystem.pages

import android.util.Log
import android.util.Patterns
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queuemanagmentsystem.R
import com.google.firebase.auth.FirebaseAuth
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

// Validation constants remain the same
private val OLD_NIC_REGEX = Regex("^[0-9]{9}[VvXx]$")
private val NEW_NIC_REGEX = Regex("^19[0-9]{10}|20[0-9]{10}$")
private val LK_PHONE_REGEX = Regex("^\\+94[0-9]{9}$")

private const val ERR_REQUIRED = "All fields are required"
private const val ERR_EMAIL = "Invalid email format"
private const val ERR_PHONE = "Phone number must be in Sri Lankan format: +947XXXXXXXX"
private const val ERR_PWD_LEN = "Password must be at least 6 characters"
private const val ERR_PWD_MATCH = "Passwords do not match"
private const val ERR_NIC = "Invalid NIC. Use 123456789V or 199912345678 format"

private fun isValidNIC(nic: String) = OLD_NIC_REGEX.matches(nic) || NEW_NIC_REGEX.matches(nic)
private fun isValidSriLankanPhone(phone: String) = LK_PHONE_REGEX.matches(phone)

@OptIn(ExperimentalAnimationApi::class)
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
    var isLoading by remember { mutableStateOf(false) }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

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
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                            elevation = 12.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = SoftShadow,
                            spotColor = SoftShadow
                        ),
                    shape = RoundedCornerShape(20.dp),
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

            // Animated title
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(1000, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Create Account",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Join People's Bank Digital Services",
                        fontSize = 16.sp,
                        color = DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Registration form card
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
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = SoftShadow,
                            spotColor = SoftShadow
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Form fields with icons
                        EnhancedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text,
                            testTag = "usernameField"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EnhancedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            testTag = "emailField"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EnhancedTextField(
                            value = nic,
                            onValueChange = { nic = it },
                            label = "NIC Number",
                            icon = Icons.Default.Badge,
                            keyboardType = KeyboardType.Text,
                            testTag = "nicField"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EnhancedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Phone (+947XXXXXXXX)",
                            icon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone,
                            testTag = "phoneField"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EnhancedPasswordField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            isVisible = passwordVisible,
                            onToggleVisibility = { passwordVisible = !passwordVisible },
                            fieldTag = "passwordField",
                            toggleTag = "passwordVisibilityToggle"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EnhancedPasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            isVisible = confirmPasswordVisible,
                            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                            fieldTag = "confirmPasswordField",
                            toggleTag = "confirmPasswordVisibilityToggle"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Register button with loading state
                        Button(
                            onClick = {
                                if (validateInputs()) {
                                    isLoading = true
                                    registerUser(
                                        auth = auth,
                                        firestore = firestore,
                                        email = email,
                                        password = password,
                                        username = username,
                                        nic = nic,
                                        phone = phone,
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Verification email sent. Please check your inbox.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navToLogin()
                                        },
                                        onError = { msg ->
                                            isLoading = false
                                            Log.e("REGISTER", msg)
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = if (isLoading) 4.dp else 8.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("registerButton"),
                            shape = RoundedCornerShape(16.dp),
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
                                        modifier = Modifier.size(20.dp),
                                        color = White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Creating Account...",
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Text(
                                    text = "Create Account",
                                    color = White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom navigation links
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(
                        onClick = navToLogin,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(White.copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Already have an account? Sign In",
                            color = PrimaryRed,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = navToAdminLogin,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(White.copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin/Staff Login",
                            color = DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EnhancedTextField(
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
        shape = RoundedCornerShape(16.dp),
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
private fun EnhancedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    fieldTag: String? = null,
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
            if (fieldTag != null) base.testTag(fieldTag) else base
        },
        shape = RoundedCornerShape(16.dp),
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
