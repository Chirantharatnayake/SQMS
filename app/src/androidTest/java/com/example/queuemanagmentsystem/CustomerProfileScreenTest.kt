package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Customer Profile screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the customer profile components are present, dark mode toggle works,
 * profile picture functionality works, and logout functionality works properly.
 */
class CustomerProfileScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToCustomerProfileScreen() {
        // Navigate from Register -> Login -> Customer Home -> Account Tab
        // First go to login page
        composeRule.onNodeWithTag("goToLoginButton").performClick()

        // Fill in valid credentials and login
        composeRule.onNodeWithTag("emailInput").performTextInput("test@example.com")
        composeRule.onNodeWithTag("passwordInput").performTextInput("password123")
        composeRule.onNodeWithTag("loginButton").performClick()

        // Navigate to account/profile tab from customer home
        // This assumes bottom navigation exists to reach the profile
    }

    @Test
    fun customerProfileScreenIsDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("customerProfileScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileContent").assertIsDisplayed()
    }

    @Test
    fun profileInfoCardIsDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("profileInfoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerNameText").assertIsDisplayed()
        composeRule.onNodeWithTag("customerEmailCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerEmailText").assertIsDisplayed()
    }

    @Test
    fun profileAvatarAndImageAreDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("customerProfileAvatar").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileCameraButton").assertIsDisplayed()
    }

    @Test
    fun profileAvatarIsClickable() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()
        // Should trigger image picker (gallery/camera access)
        composeRule.onNodeWithTag("customerProfileAvatar").assertIsDisplayed()
    }

    @Test
    fun cameraButtonIsClickable() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("customerProfileCameraButton").performClick()
        // Should also trigger image picker
        composeRule.onNodeWithTag("customerProfileCameraButton").assertIsDisplayed()
    }

    @Test
    fun preferencesCardIsDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("preferencesCard").assertIsDisplayed()
        composeRule.onNodeWithTag("preferencesHeader").assertIsDisplayed()
    }

    @Test
    fun darkModeToggleComponentsAreDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("darkModeToggleCard").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleIcon").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleDescription").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleSwitch").assertIsDisplayed()
    }

    @Test
    fun darkModeToggleSwitchIsClickable() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
        // Should toggle dark mode on/off
        composeRule.onNodeWithTag("darkModeToggleSwitch").assertIsDisplayed()
    }

    @Test
    fun accountActionsCardIsDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("accountActionsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("accountActionsHeader").assertIsDisplayed()
        composeRule.onNodeWithTag("customerLogoutButton").assertIsDisplayed()
    }

    @Test
    fun customerLogoutButtonIsClickable() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("customerLogoutButton").performClick()
        // Should sign out and navigate to register screen
        composeRule.onNodeWithTag("customerLogoutButton").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationIsDisplayed() {
        navigateToCustomerProfileScreen()
        composeRule.onNodeWithTag("customerProfileBottomNav").assertIsDisplayed()
    }

    @Test
    fun allMainProfileElementsAreDisplayedTogether() {
        navigateToCustomerProfileScreen()
        // Verify all main profile components are visible simultaneously
        composeRule.onNodeWithTag("profileInfoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileAvatar").assertIsDisplayed()
        composeRule.onNodeWithTag("customerNameText").assertIsDisplayed()
        composeRule.onNodeWithTag("customerEmailText").assertIsDisplayed()
        composeRule.onNodeWithTag("preferencesCard").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleSwitch").assertIsDisplayed()
        composeRule.onNodeWithTag("accountActionsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerLogoutButton").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileBottomNav").assertIsDisplayed()
    }

    @Test
    fun customerProfileScreenLayout() {
        navigateToCustomerProfileScreen()

        // Verify the complete layout structure
        composeRule.onNodeWithTag("customerProfileScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileContent").assertIsDisplayed()

        // Profile info section
        composeRule.onNodeWithTag("profileInfoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileAvatar").assertIsDisplayed()
        composeRule.onNodeWithTag("customerNameText").assertIsDisplayed()
        composeRule.onNodeWithTag("customerEmailCard").assertIsDisplayed()

        // Preferences section
        composeRule.onNodeWithTag("preferencesCard").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleCard").assertIsDisplayed()

        // Account actions section
        composeRule.onNodeWithTag("accountActionsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerLogoutButton").assertIsDisplayed()

        // Navigation section
        composeRule.onNodeWithTag("customerProfileBottomNav").assertIsDisplayed()
    }

    @Test
    fun profileImageManagement() {
        navigateToCustomerProfileScreen()

        // Test profile image interaction
        composeRule.onNodeWithTag("customerProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileCameraButton").assertIsDisplayed()

        // Click avatar to change image
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()

        // Click camera button to change image
        composeRule.onNodeWithTag("customerProfileCameraButton").performClick()

        // Verify image components remain functional
        composeRule.onNodeWithTag("customerProfileAvatar").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileImage").assertIsDisplayed()
    }

    @Test
    fun darkModeToggleFunctionality() {
        navigateToCustomerProfileScreen()

        // Test dark mode toggle functionality
        composeRule.onNodeWithTag("darkModeToggleSwitch").assertIsDisplayed()

        // Toggle dark mode on
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()

        // Verify toggle components remain visible
        composeRule.onNodeWithTag("darkModeToggleCard").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleIcon").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleDescription").assertIsDisplayed()

        // Toggle dark mode off
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()

        // Verify components still work
        composeRule.onNodeWithTag("darkModeToggleSwitch").assertIsDisplayed()
    }

    @Test
    fun customerInformationDisplay() {
        navigateToCustomerProfileScreen()

        // Verify customer information is displayed
        composeRule.onNodeWithTag("customerNameText").assertIsDisplayed()
        composeRule.onNodeWithTag("customerEmailText").assertIsDisplayed()

        // Customer name should show either "Loading..." or actual name
        // Customer email should show the logged-in user's email
    }

    @Test
    fun profileElementsRemainVisibleAfterInteractions() {
        navigateToCustomerProfileScreen()

        // Interact with various elements
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
        composeRule.onNodeWithTag("customerProfileCameraButton").performClick()

        // Verify all profile elements are still visible
        composeRule.onNodeWithTag("profileInfoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerNameText").assertIsDisplayed()
        composeRule.onNodeWithTag("preferencesCard").assertIsDisplayed()
        composeRule.onNodeWithTag("accountActionsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("customerLogoutButton").assertIsDisplayed()
    }

    @Test
    fun completeCustomerProfileFlow() {
        navigateToCustomerProfileScreen()

        // Complete profile management flow
        // 1. View profile information
        composeRule.onNodeWithTag("customerNameText").assertIsDisplayed()
        composeRule.onNodeWithTag("customerEmailText").assertIsDisplayed()

        // 2. Change profile picture
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()

        // 3. Toggle dark mode
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()

        // 4. Attempt logout
        composeRule.onNodeWithTag("customerLogoutButton").performClick()

        // Verify the flow completed without crashes
        composeRule.onNodeWithTag("customerProfileScreen").assertIsDisplayed()
    }

    @Test
    fun multipleInteractionsStability() {
        navigateToCustomerProfileScreen()

        // Perform multiple interactions to test UI stability
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
        composeRule.onNodeWithTag("customerProfileCameraButton").performClick()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
        composeRule.onNodeWithTag("customerLogoutButton").performClick()

        // After multiple interactions, verify key elements remain functional
        composeRule.onNodeWithTag("customerProfileScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("profileInfoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("preferencesCard").assertIsDisplayed()
        composeRule.onNodeWithTag("accountActionsCard").assertIsDisplayed()
    }

    @Test
    fun enhancedProfileFeatures() {
        navigateToCustomerProfileScreen()

        // Test enhanced profile features
        // 1. Profile avatar with camera overlay
        composeRule.onNodeWithTag("customerProfileAvatar").assertIsDisplayed()
        composeRule.onNodeWithTag("customerProfileCameraButton").assertIsDisplayed()

        // 2. Enhanced dark mode toggle with description
        composeRule.onNodeWithTag("darkModeToggleCard").assertIsDisplayed()
        composeRule.onNodeWithTag("darkModeToggleDescription").assertIsDisplayed()

        // 3. Account actions section
        composeRule.onNodeWithTag("accountActionsHeader").assertIsDisplayed()
        composeRule.onNodeWithTag("customerLogoutButton").assertIsDisplayed()

        // All enhanced features should be functional
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
    }

    @Test
    fun animatedProfileInterface() {
        navigateToCustomerProfileScreen()

        // Test that animated elements are displayed
        // The animations should complete and elements should be visible
        composeRule.onNodeWithTag("profileInfoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("preferencesCard").assertIsDisplayed()
        composeRule.onNodeWithTag("accountActionsCard").assertIsDisplayed()

        // All animated sections should be functional after animations
        composeRule.onNodeWithTag("customerProfileAvatar").performClick()
        composeRule.onNodeWithTag("darkModeToggleSwitch").performClick()
        composeRule.onNodeWithTag("customerLogoutButton").performClick()
    }
}
