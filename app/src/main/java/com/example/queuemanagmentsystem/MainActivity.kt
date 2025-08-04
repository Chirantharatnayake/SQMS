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

        composable("register") {
            RegisterScreen(
                navToLogin = { navController.navigate("login") },
                navToAdminLogin = { navController.navigate("admin_login") }
            )
        }

        composable("login") {
            LoginScreen(
                navToRegister = { navController.navigate("register") },
                navToLanding = { navController.navigate("landing") }
            )
        }

        composable("admin_login") {
            AdminLoginScreen(
                navController = navController,
                onBack = { navController.navigate("register") }
            )
        }

        composable("admin_home") {
            AdminScreen()
        }

        composable("staff_home") {
            StaffScreen()
        }

        composable("landing") {
            LandingScreen(navController)
        }

        composable(
            route = "appointment/{service}",
            arguments = listOf(navArgument("service") { type = NavType.StringType })
        ) { backStackEntry ->
            val service = backStackEntry.arguments?.getString("service") ?: "Service"
            AppointmentScreen(navController, service)
        }

        composable("slots") {
            SlotScreen(navController)
        }

        composable("account") {
            CustomerProfileScreen(navController)
        }
    }
}
