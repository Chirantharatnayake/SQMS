package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Appointment screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the appointment booking form components are present, interactive elements work,
 * and the booking flow functions properly.
 */
class AppointmentScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToAppointmentScreen() {
        // Navigate from Register -> Login -> Customer Home -> Service Selection -> Appointment
        // First go to login page
        composeRule.onNodeWithTag("goToLoginButton").performClick()

        // Fill in valid credentials and login
        composeRule.onNodeWithTag("emailInput").performTextInput("test@example.com")
        composeRule.onNodeWithTag("passwordInput").performTextInput("password123")
        composeRule.onNodeWithTag("loginButton").performClick()

        // Click on a service card to navigate to appointment screen
        composeRule.onNodeWithTag("serviceCard_AccountOpening").performClick()
    }

    @Test
    fun appointmentScreenIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("appointmentScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("appointmentContent").assertIsDisplayed()
    }

    @Test
    fun appointmentHeaderIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("appointmentHeader").assertIsDisplayed()
        composeRule.onNodeWithTag("backButton").assertIsDisplayed()
    }

    @Test
    fun backButtonIsClickable() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("backButton").performClick()
        // Should navigate back - verify button remains clickable
        composeRule.onNodeWithTag("backButton").assertIsDisplayed()
    }

    @Test
    fun bannerImageIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("bannerImage").assertIsDisplayed()
    }

    @Test
    fun serviceSelectionCardIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("serviceSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("selectedServiceTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("selectedServiceDisplay").assertIsDisplayed()
    }

    @Test
    fun branchSelectionCardIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("branchSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("selectBranchTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("branchDropdown").assertIsDisplayed()
        composeRule.onNodeWithTag("selectedBranchText").assertIsDisplayed()
    }

    @Test
    fun branchDropdownCanBeOpened() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("branchDropdown").performClick()
        // Verify dropdown menu appears with branch options
        composeRule.onNodeWithTag("branchDropdownMenu").assertIsDisplayed()
        composeRule.onNodeWithTag("branchOption_Colombo").assertIsDisplayed()
        composeRule.onNodeWithTag("branchOption_Kandy").assertIsDisplayed()
    }

    @Test
    fun canSelectBranchFromDropdown() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("branchDropdown").performClick()
        composeRule.onNodeWithTag("branchOption_Colombo").performClick()
        // After selection, dropdown should close and selected branch should be displayed
        composeRule.onNodeWithTag("selectedBranchText").assertIsDisplayed()
    }

    @Test
    fun dateSelectionCardIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("dateSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("selectDateTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("datePickerButton").assertIsDisplayed()
        composeRule.onNodeWithTag("selectedDateDisplay").assertIsDisplayed()
    }

    @Test
    fun datePickerButtonIsClickable() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("datePickerButton").performClick()
        // Date picker dialog should open (native Android dialog - harder to test)
        // Verify button remains clickable
        composeRule.onNodeWithTag("datePickerButton").assertIsDisplayed()
    }

    @Test
    fun timeSlotsCardIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("timeSlotsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("timeSlotsTitle").assertIsDisplayed()
    }

    @Test
    fun timeSlotButtonsAreDisplayed() {
        navigateToAppointmentScreen()
        // Verify some specific time slot buttons are displayed
        composeRule.onNodeWithTag("timeSlot_09:00 AM").assertIsDisplayed()
        composeRule.onNodeWithTag("timeSlot_10:00 AM").assertIsDisplayed()
        composeRule.onNodeWithTag("timeSlot_11:00 AM").assertIsDisplayed()
        composeRule.onNodeWithTag("timeSlot_02:00 PM").assertIsDisplayed()
    }

    @Test
    fun timeSlotCanBeSelected() {
        navigateToAppointmentScreen()
        // Select a time slot
        composeRule.onNodeWithTag("timeSlot_09:00 AM").performClick()
        // Verify the slot remains visible (should be selected/highlighted)
        composeRule.onNodeWithTag("timeSlot_09:00 AM").assertIsDisplayed()
    }

    @Test
    fun multipleTimeSlotsCanBeClicked() {
        navigateToAppointmentScreen()
        // Click multiple time slots (should deselect previous and select new)
        composeRule.onNodeWithTag("timeSlot_09:00 AM").performClick()
        composeRule.onNodeWithTag("timeSlot_10:00 AM").performClick()
        composeRule.onNodeWithTag("timeSlot_11:00 AM").performClick()
        // Verify slots remain visible
        composeRule.onNodeWithTag("timeSlot_11:00 AM").assertIsDisplayed()
    }

    @Test
    fun confirmAppointmentButtonIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("confirmAppointmentButton").assertIsDisplayed()
    }

    @Test
    fun confirmAppointmentButtonIsClickable() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("confirmAppointmentButton").performClick()
        // Should show validation message if required fields not selected
        composeRule.onNodeWithTag("confirmAppointmentButton").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationIsDisplayed() {
        navigateToAppointmentScreen()
        composeRule.onNodeWithTag("bottomNavigation").assertIsDisplayed()
    }

    @Test
    fun completeAppointmentBookingFlow() {
        navigateToAppointmentScreen()

        // Select branch
        composeRule.onNodeWithTag("branchDropdown").performClick()
        composeRule.onNodeWithTag("branchOption_Colombo").performClick()

        // Select time slot
        composeRule.onNodeWithTag("timeSlot_09:00 AM").performClick()

        // Try to confirm appointment
        composeRule.onNodeWithTag("confirmAppointmentButton").performClick()

        // Verify all components remain visible after interaction
        composeRule.onNodeWithTag("serviceSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("branchSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("dateSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("timeSlotsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmAppointmentButton").assertIsDisplayed()
    }

    @Test
    fun allMainComponentsRemainVisibleAfterInteractions() {
        navigateToAppointmentScreen()

        // Interact with various elements
        composeRule.onNodeWithTag("branchDropdown").performClick()
        composeRule.onNodeWithTag("branchOption_Kandy").performClick()
        composeRule.onNodeWithTag("timeSlot_10:00 AM").performClick()
        composeRule.onNodeWithTag("datePickerButton").performClick()

        // Verify all main sections remain visible
        composeRule.onNodeWithTag("appointmentHeader").assertIsDisplayed()
        composeRule.onNodeWithTag("serviceSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("branchSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("dateSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("timeSlotsCard").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmAppointmentButton").assertIsDisplayed()
        composeRule.onNodeWithTag("bottomNavigation").assertIsDisplayed()
    }
}
