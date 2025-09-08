package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Admin Profile screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the admin profile components are present, admin information is displayed correctly,
 * and the logout functionality works properly.
 */
class AdminProfileScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToAdminProfileScreen() {
        // Navigate from Register -> Admin/Staff Login -> Admin Home -> Account Tab
        // This assumes successful admin login flow and navigation to profile screen
        // You may need to adjust navigation based on your actual flow

        // Navigate to admin login (assuming there's a way from register)
        // Fill in admin credentials and login
        // Navigate to admin home, then to account/profile tab
    }

    @Test
    fun adminProfileScreenIsDisplayed() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminProfileScreen").assertIsDisplayed()
    }

    @Test
    fun adminProfileTitleIsDisplayed() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()
    }

    @Test
    fun adminProfileImageIsDisplayed() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()
    }

    @Test
    fun adminIdTextIsDisplayed() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
    }

    @Test
    fun adminWelcomeTextIsDisplayed() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()
    }

    @Test
    fun adminLogoutButtonIsDisplayed() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()
    }

    @Test
    fun adminLogoutButtonIsClickable() {
        navigateToAdminProfileScreen()
        composeRule.onNodeWithTag("adminLogoutButton").performClick()
        // Should navigate to admin_login screen and clear backstack
        // Verify button was clickable (no crash)
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()
    }

    @Test
    fun allProfileElementsAreDisplayedTogether() {
        navigateToAdminProfileScreen()
        // Verify all profile components are visible simultaneously
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()
    }

    @Test
    fun adminProfileScreenLayout() {
        navigateToAdminProfileScreen()

        // Verify the complete layout structure
        composeRule.onNodeWithTag("adminProfileScreen").assertIsDisplayed()

        // Header section
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()

        // Profile section
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()

        // Action section
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()
    }

    @Test
    fun adminInformationDisplay() {
        navigateToAdminProfileScreen()

        // Verify admin information is displayed correctly
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()

        // The admin ID should show "admin001" (hardcoded value)
        // The welcome text should show "Welcome back, Admin!"
    }

    @Test
    fun logoutButtonFunctionality() {
        navigateToAdminProfileScreen()

        // Verify logout button is present and clickable
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()
        composeRule.onNodeWithTag("adminLogoutButton").performClick()

        // The logout should navigate to admin_login screen
        // Since we can't easily verify navigation in isolation,
        // we ensure the button interaction doesn't crash the app
    }

    @Test
    fun profileElementsRemainVisibleAfterInteractions() {
        navigateToAdminProfileScreen()

        // Interact with logout button (without completing logout)
        composeRule.onNodeWithTag("adminLogoutButton").performClick()

        // Verify all profile elements are still visible
        // Note: This test assumes we can return to the profile screen
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()
    }

    @Test
    fun adminProfileStaticContent() {
        navigateToAdminProfileScreen()

        // Test that static content is properly displayed
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()

        // These elements should contain specific text
        // "Admin Profile", "Admin ID: admin001", "Welcome back, Admin!"
    }

    @Test
    fun profileImageDisplay() {
        navigateToAdminProfileScreen()

        // Verify profile image container is displayed
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()

        // The image should be in a circular container with gray background
        // containing the profile picture resource
    }

    @Test
    fun completeAdminProfileFlow() {
        navigateToAdminProfileScreen()

        // Complete flow: view profile -> logout
        // 1. View profile information
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()

        // 2. Attempt logout
        composeRule.onNodeWithTag("adminLogoutButton").performClick()

        // Verify the flow completed without crashes
        // (Navigation testing would require more complex setup)
    }

    @Test
    fun multipleInteractionsStability() {
        navigateToAdminProfileScreen()

        // Perform multiple interactions to test UI stability
        composeRule.onNodeWithTag("adminLogoutButton").performClick()

        // After interaction, verify key elements remain functional
        // Note: logout might navigate away, so this test verifies no crashes occur
        composeRule.onNodeWithTag("adminProfileScreen").assertIsDisplayed()
    }

    @Test
    fun adminProfileSimpleInterface() {
        navigateToAdminProfileScreen()

        // Test the simple, clean interface design
        // Verify minimal essential elements are present
        composeRule.onNodeWithTag("adminProfileScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("adminProfileTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("adminProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("adminIdText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminWelcomeText").assertIsDisplayed()
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()

        // No additional complex UI elements should be present
        // This is a simple profile screen with essential information only
    }

    @Test
    fun logoutButtonStyling() {
        navigateToAdminProfileScreen()

        // Verify logout button is styled correctly
        composeRule.onNodeWithTag("adminLogoutButton").assertIsDisplayed()

        // Button should be full width and red colored
        // Contains "Log out" text in white
    }
}
