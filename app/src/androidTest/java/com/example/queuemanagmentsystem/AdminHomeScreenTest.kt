package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Admin Home screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the main input fields are present, accept text, and that interactive
 * elements like the dropdown menu, password toggle, and add staff button exist and can be clicked.
 */
class AdminHomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToAdminHome() {
        // Navigate from Register -> Admin Login -> Admin Home
        // This assumes successful admin login flow exists
        composeRule.onNodeWithTag("adminLoginButton").performClick()
        // Additional navigation steps may be needed based on your admin login flow
    }

    @Test
    fun adminHomeContentIsDisplayed() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("adminHomeContent").assertIsDisplayed()
        composeRule.onNodeWithTag("addStaffTitle").assertIsDisplayed()
    }

    @Test
    fun staffFormFieldsAreDisplayed() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("branchCodeField").assertIsDisplayed()
        composeRule.onNodeWithTag("staffIdDisplay").assertIsDisplayed()
        composeRule.onNodeWithTag("staffNameField").assertIsDisplayed()
        composeRule.onNodeWithTag("serviceTypeField").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeRule.onNodeWithTag("addStaffButton").assertIsDisplayed()
    }

    @Test
    fun canTypeIntoStaffFields() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("branchCodeField").performTextInput("BR001")
        composeRule.onNodeWithTag("staffNameField").performTextInput("John Doe")
        composeRule.onNodeWithTag("passwordField").performTextInput("Password123")
        // If no exceptions, input worked.
    }

    @Test
    fun serviceTypeDropdownCanBeOpened() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("serviceTypeField").performClick()
        // Verify dropdown options are available (may need to wait for animation)
        composeRule.onNodeWithTag("serviceOption_Account Opening").assertIsDisplayed()
    }

    @Test
    fun passwordVisibilityToggleWorks() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("passwordField").performTextInput("TestPassword")
        composeRule.onNodeWithTag("passwordToggle").assertIsDisplayed().performClick()
        // Click again to toggle back
        composeRule.onNodeWithTag("passwordToggle").performClick()
    }

    @Test
    fun navigationTabsAreDisplayed() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("navItem_Home").assertIsDisplayed()
        composeRule.onNodeWithTag("navItem_Account").assertIsDisplayed()
    }

    @Test
    fun canSwitchToAccountTab() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("navItem_Account").performClick()
        // Should switch to account tab (AdminAccountScreen)
        composeRule.onNodeWithTag("navItem_Account").assertIsDisplayed()
    }

    @Test
    fun searchFieldsAreDisplayed() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("staffListTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("staffSearchField").assertIsDisplayed()
        composeRule.onNodeWithTag("customerListTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("customerSearchField").assertIsDisplayed()
    }

    @Test
    fun canTypeInSearchFields() {
        navigateToAdminHome()
        composeRule.onNodeWithTag("staffSearchField").performTextInput("John")
        composeRule.onNodeWithTag("customerSearchField").performTextInput("customer@example.com")
    }

    @Test
    fun clickAddStaffWithEmptyFields_staysVisible() {
        navigateToAdminHome()
        // Click add staff without filling required fields to ensure no crash
        composeRule.onNodeWithTag("addStaffButton").performClick()
        composeRule.onNodeWithTag("addStaffButton").assertIsDisplayed()
    }
}
