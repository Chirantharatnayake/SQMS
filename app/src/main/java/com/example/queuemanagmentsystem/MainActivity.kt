package com.example.queuemanagmentsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.queuemanagementapp.ui.screens.AdminScreen
import com.example.queuemanagementapp.ui.screens.StaffScreen
import com.example.queuemanagementapp.ui.screens.StaffAccountScreen
import com.example.queuemanagmentsystem.pages.*
import com.example.queuemanagmentsystem.ui.theme.QueueManagmentSystemTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            QueueManagmentSystemTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "register") {

        // ✅ Register screen
        composable("register") {
            RegisterScreen(
                navToLogin = { navController.navigate("login") },
                navToAdminLogin = { navController.navigate("admin_login") }
            )
        }

        // ✅ Login screen
        composable("login") {
            LoginScreen(
                navToRegister = { navController.navigate("register") },
                navToLanding = { navController.navigate("landing") }
            )
        }

        // ✅ Admin login
        composable("admin_login") {
            AdminLoginScreen(
                navController = navController,
                onBack = { navController.navigate("register") }
            )
        }

        // ✅ Admin home
        composable("admin_home") {
            AdminScreen()
        }

        // ✅ Customer landing page
        composable("landing") {
            LandingScreen(navController)
        }

        // ✅ Appointment booking
        composable(
            route = "appointment/{service}",
            arguments = listOf(navArgument("service") { type = NavType.StringType })
        ) { backStackEntry ->
            val service = backStackEntry.arguments?.getString("service") ?: "Service"
            AppointmentScreen(navController, service)
        }

        // ✅ Slot booking screen
        composable("slots") {
            SlotScreen(navController)
        }

        // ✅ Customer account/profile screen
        composable("account") {
            CustomerProfileScreen(navController)
        }

        // ✅ Notification page
        composable("notifications") {
            NotificationsScreen(navController)
        }

        // ✅ Staff home screen (dynamic route)
        composable("staff_home/{staffName}") { backStackEntry ->
            val staffName = backStackEntry.arguments?.getString("staffName") ?: "Unknown"
            StaffScreen(staffName = staffName, navController = navController)
        }

        // ✅ Staff account screen (dynamic route)
        composable("staff_account/{staffName}") { backStackEntry ->
            val staffName = backStackEntry.arguments?.getString("staffName") ?: "Unknown"
            StaffAccountScreen(staffName = staffName, navController = navController)
        }
    }
}
