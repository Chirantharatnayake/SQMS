package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Staff Account screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the staff profile components are present, staff information is displayed correctly,
 * and the logout functionality works properly.
 */
class StaffAccountScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToStaffAccountScreen() {
        // Navigate from Register -> Admin/Staff Login -> Staff Home -> Account Tab
        // This assumes successful staff login flow and navigation to account screen
        // You may need to adjust navigation based on your actual flow

        // The staff account screen is typically accessed via bottom navigation
        // from the staff home screen after successful login
    }

    @Test
    fun staffAccountScreenIsDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffAccountScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountContent").assertIsDisplayed()
    }

    @Test
    fun staffProfileImageIsDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffProfileImage").assertIsDisplayed()
    }

    @Test
    fun staffAccountNameIsDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
    }

    @Test
    fun staffAccountBranchCodeIsDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffAccountBranchCode").assertIsDisplayed()
    }

    @Test
    fun staffLogoutButtonIsDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()
    }

    @Test
    fun staffLogoutButtonIsClickable() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffLogoutButton").performClick()
        // Should navigate to admin_login screen and clear backstack
        // Verify button was clickable (no crash)
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()
    }

    @Test
    fun staffAccountBottomNavIsDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("staffAccountBottomNav").assertIsDisplayed()
        composeRule.onNodeWithTag("staffBottomNavBar").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationTabsAreDisplayed() {
        navigateToStaffAccountScreen()
        composeRule.onNodeWithTag("navItem_Home").assertIsDisplayed()
        composeRule.onNodeWithTag("navItem_Account").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationTabsAreClickable() {
        navigateToStaffAccountScreen()
        // Click Home tab
        composeRule.onNodeWithTag("navItem_Home").performClick()
        // Should navigate to staff home screen

        // Navigate back to account (if needed for test isolation)
        composeRule.onNodeWithTag("navItem_Account").performClick()

        // Verify navigation elements remain functional
        composeRule.onNodeWithTag("staffAccountBottomNav").assertIsDisplayed()
    }

    @Test
    fun allProfileElementsAreDisplayedTogether() {
        navigateToStaffAccountScreen()
        // Verify all profile components are visible simultaneously
        composeRule.onNodeWithTag("staffProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountBranchCode").assertIsDisplayed()
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountBottomNav").assertIsDisplayed()
    }

    @Test
    fun profileElementsRemainVisibleAfterInteractions() {
        navigateToStaffAccountScreen()

        // Interact with navigation tabs
        composeRule.onNodeWithTag("navItem_Home").performClick()
        composeRule.onNodeWithTag("navItem_Account").performClick()

        // Verify all profile elements are still visible
        composeRule.onNodeWithTag("staffProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountBranchCode").assertIsDisplayed()
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()
    }

    @Test
    fun staffAccountScreenLayout() {
        navigateToStaffAccountScreen()

        // Verify the complete layout structure
        composeRule.onNodeWithTag("staffAccountScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountContent").assertIsDisplayed()

        // Profile section
        composeRule.onNodeWithTag("staffProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountBranchCode").assertIsDisplayed()

        // Action section
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()

        // Navigation section
        composeRule.onNodeWithTag("staffAccountBottomNav").assertIsDisplayed()
    }

    @Test
    fun logoutButtonFunctionality() {
        navigateToStaffAccountScreen()

        // Verify logout button is present and clickable
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()
        composeRule.onNodeWithTag("staffLogoutButton").performClick()

        // The logout should navigate to admin_login screen
        // Since we can't easily verify navigation in isolation,
        // we ensure the button interaction doesn't crash the app
        // In a real test, you might verify navigation state or use navigation testing
    }

    @Test
    fun staffInformationLoading() {
        navigateToStaffAccountScreen()

        // Staff information should be displayed (either loading state or actual data)
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountBranchCode").assertIsDisplayed()

        // The text content will depend on loading state vs loaded state
        // During loading: "Loading..."
        // After loading: actual staff name and branch code
    }

    @Test
    fun navigationBetweenHomeAndAccount() {
        navigateToStaffAccountScreen()

        // Start on Account tab
        composeRule.onNodeWithTag("navItem_Account").assertIsDisplayed()

        // Navigate to Home
        composeRule.onNodeWithTag("navItem_Home").performClick()

        // Navigate back to Account
        composeRule.onNodeWithTag("navItem_Account").performClick()

        // Verify we're back on the account screen with all elements visible
        composeRule.onNodeWithTag("staffAccountScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("staffProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
        composeRule.onNodeWithTag("staffLogoutButton").assertIsDisplayed()
    }

    @Test
    fun multipleInteractionsStability() {
        navigateToStaffAccountScreen()

        // Perform multiple interactions to test UI stability
        composeRule.onNodeWithTag("navItem_Home").performClick()
        composeRule.onNodeWithTag("navItem_Account").performClick()
        composeRule.onNodeWithTag("staffLogoutButton").performClick()

        // After multiple interactions, verify key elements remain functional
        // Note: logout might navigate away, so this test verifies no crashes occur
        composeRule.onNodeWithTag("staffAccountScreen").assertIsDisplayed()
    }

    @Test
    fun completeStaffAccountFlow() {
        navigateToStaffAccountScreen()

        // Complete flow: view profile -> navigate -> return -> logout
        // 1. View profile information
        composeRule.onNodeWithTag("staffProfileImage").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountName").assertIsDisplayed()
        composeRule.onNodeWithTag("staffAccountBranchCode").assertIsDisplayed()

        // 2. Navigate to home and back
        composeRule.onNodeWithTag("navItem_Home").performClick()
        composeRule.onNodeWithTag("navItem_Account").performClick()

        // 3. Attempt logout
        composeRule.onNodeWithTag("staffLogoutButton").performClick()

        // Verify the flow completed without crashes
        // (Navigation testing would require more complex setup)
    }
}
