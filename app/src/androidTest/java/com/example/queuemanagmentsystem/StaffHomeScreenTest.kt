package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Staff Home screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the staff dashboard components are present, appointment management works,
 * and the staff interface functions properly for managing customer appointments.
 */
class StaffHomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToStaffHomeScreen() {
        // Navigate from Register -> Admin/Staff Login -> Staff Home
        // This assumes successful staff login flow
        // You may need to adjust navigation based on your actual flow

        // Navigate to admin/staff login (assuming there's a way from register)
        // Fill in staff credentials and login
        // This should result in navigation to staff_home/{staffName}
    }

    @Test
    fun staffHomeScreenIsDisplayed() {
        navigateToStaffHomeScreen()
        composeRule.onNodeWithTag("staffHomeScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("staffHomeContent").assertIsDisplayed()
    }

    @Test
    fun staffHomeHeaderElementsAreDisplayed() {
        navigateToStaffHomeScreen()
        composeRule.onNodeWithTag("staffHomeLogo").assertIsDisplayed()
        composeRule.onNodeWithTag("staffWelcomeText").assertIsDisplayed()
        composeRule.onNodeWithTag("staffServiceType").assertIsDisplayed()
        composeRule.onNodeWithTag("staffBranchCode").assertIsDisplayed()
    }

    @Test
    fun appointmentsTitleIsDisplayed() {
        navigateToStaffHomeScreen()
        composeRule.onNodeWithTag("appointmentsTitle").assertIsDisplayed()
    }

    @Test
    fun loadingIndicatorIsDisplayedWhenLoading() {
        navigateToStaffHomeScreen()
        // During initial load, loading indicator should be visible
        // This may be timing dependent - loading might complete quickly
        composeRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    fun noAppointmentsMessageDisplayedWhenEmpty() {
        navigateToStaffHomeScreen()
        // If no appointments exist, the no appointments message should be displayed
        // This test may need to wait for loading to complete
        composeRule.onNodeWithTag("noAppointmentsMessage").assertIsDisplayed()
    }

    @Test
    fun appointmentsListDisplayedWhenAppointmentsExist() {
        navigateToStaffHomeScreen()
        // If appointments exist, the appointments list should be displayed
        // This test assumes there are appointments for the logged-in staff
        composeRule.onNodeWithTag("appointmentsList").assertIsDisplayed()
    }

    @Test
    fun appointmentCardElementsAreDisplayed() {
        navigateToStaffHomeScreen()
        // Test assumes at least one appointment exists with a specific token
        // You may need to adjust token value based on test data
        val testToken = "123456" // Replace with actual test token

        composeRule.onNodeWithTag("appointmentCard_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("customerName_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("customerPhone_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("tokenCard_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("appointmentService_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("appointmentDate_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("appointmentTime_$testToken").assertIsDisplayed()
    }

    @Test
    fun appointmentActionButtonsAreDisplayed() {
        navigateToStaffHomeScreen()
        val testToken = "123456" // Replace with actual test token

        composeRule.onNodeWithTag("completeButton_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("cancelButton_$testToken").assertIsDisplayed()
    }

    @Test
    fun completeButtonIsClickable() {
        navigateToStaffHomeScreen()
        val testToken = "123456" // Replace with actual test token

        composeRule.onNodeWithTag("completeButton_$testToken").performClick()
        // Should mark appointment as complete and move to completed section
        composeRule.onNodeWithTag("completeButton_$testToken").assertIsDisplayed()
    }

    @Test
    fun cancelButtonTriggersConfirmationDialog() {
        navigateToStaffHomeScreen()
        val testToken = "123456" // Replace with actual test token

        composeRule.onNodeWithTag("cancelButton_$testToken").performClick()
        // Should show confirmation dialog
        composeRule.onNodeWithTag("cancelConfirmationDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmCancelDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("dismissCancelDialog").assertIsDisplayed()
    }

    @Test
    fun cancelConfirmationDialogActions() {
        navigateToStaffHomeScreen()
        val testToken = "123456" // Replace with actual test token

        // Open cancel dialog
        composeRule.onNodeWithTag("cancelButton_$testToken").performClick()
        composeRule.onNodeWithTag("cancelConfirmationDialog").assertIsDisplayed()

        // Test dismiss button
        composeRule.onNodeWithTag("dismissCancelDialog").performClick()
        // Dialog should close (not visible anymore)

        // Test confirm button
        composeRule.onNodeWithTag("cancelButton_$testToken").performClick()
        composeRule.onNodeWithTag("confirmCancelDialog").performClick()
        // Should cancel the appointment
    }

    @Test
    fun completedBookingsSectionIsDisplayed() {
        navigateToStaffHomeScreen()
        // If completed appointments exist, completed section should be visible
        composeRule.onNodeWithTag("completedBookingsTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("completedAppointmentsList").assertIsDisplayed()
    }

    @Test
    fun completedAppointmentDeleteButton() {
        navigateToStaffHomeScreen()
        val testToken = "789012" // Replace with actual completed appointment token

        composeRule.onNodeWithTag("deleteCompletedButton_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("deleteCompletedButton_$testToken").performClick()
        // Should delete the completed appointment
    }

    @Test
    fun staffBottomNavigationIsDisplayed() {
        navigateToStaffHomeScreen()
        composeRule.onNodeWithTag("bottomNavContainer").assertIsDisplayed()
        composeRule.onNodeWithTag("staffBottomNavBar").assertIsDisplayed()
        composeRule.onNodeWithTag("navItem_Home").assertIsDisplayed()
        composeRule.onNodeWithTag("navItem_Account").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationTabsAreClickable() {
        navigateToStaffHomeScreen()
        composeRule.onNodeWithTag("navItem_Home").performClick()
        composeRule.onNodeWithTag("navItem_Account").performClick()
        // Should navigate between staff home and account pages
        composeRule.onNodeWithTag("staffBottomNavBar").assertIsDisplayed()
    }

    @Test
    fun multipleAppointmentCardsCanBeInteracted() {
        navigateToStaffHomeScreen()
        // Test interaction with multiple appointment cards
        val token1 = "123456"
        val token2 = "654321"

        composeRule.onNodeWithTag("completeButton_$token1").performClick()
        composeRule.onNodeWithTag("cancelButton_$token2").performClick()
        composeRule.onNodeWithTag("dismissCancelDialog").performClick()

        // Verify UI remains stable after multiple interactions
        composeRule.onNodeWithTag("appointmentsList").assertIsDisplayed()
    }

    @Test
    fun allMainComponentsRemainVisibleAfterInteractions() {
        navigateToStaffHomeScreen()
        val testToken = "123456"

        // Perform various interactions
        composeRule.onNodeWithTag("completeButton_$testToken").performClick()
        composeRule.onNodeWithTag("navItem_Account").performClick()
        composeRule.onNodeWithTag("navItem_Home").performClick()

        // Verify all main sections remain visible
        composeRule.onNodeWithTag("staffHomeLogo").assertIsDisplayed()
        composeRule.onNodeWithTag("staffWelcomeText").assertIsDisplayed()
        composeRule.onNodeWithTag("appointmentsTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("staffBottomNavBar").assertIsDisplayed()
    }

    @Test
    fun appointmentManagementFlow() {
        navigateToStaffHomeScreen()
        val testToken = "123456"

        // Complete appointment management flow
        // 1. View appointment details
        composeRule.onNodeWithTag("appointmentCard_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("customerName_$testToken").assertIsDisplayed()
        composeRule.onNodeWithTag("appointmentService_$testToken").assertIsDisplayed()

        // 2. Try to cancel (then dismiss)
        composeRule.onNodeWithTag("cancelButton_$testToken").performClick()
        composeRule.onNodeWithTag("cancelConfirmationDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("dismissCancelDialog").performClick()

        // 3. Complete appointment
        composeRule.onNodeWithTag("completeButton_$testToken").performClick()

        // Verify UI remains functional
        composeRule.onNodeWithTag("appointmentsTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("staffBottomNavBar").assertIsDisplayed()
    }

    @Test
    fun staffInfoDisplayedCorrectly() {
        navigateToStaffHomeScreen()

        // Verify staff information is displayed
        composeRule.onNodeWithTag("staffWelcomeText").assertIsDisplayed()
        composeRule.onNodeWithTag("staffServiceType").assertIsDisplayed()
        composeRule.onNodeWithTag("staffBranchCode").assertIsDisplayed()

        // Staff info should remain visible throughout the session
        composeRule.onNodeWithTag("navItem_Account").performClick()
        composeRule.onNodeWithTag("navItem_Home").performClick()
        composeRule.onNodeWithTag("staffWelcomeText").assertIsDisplayed()
    }
}
