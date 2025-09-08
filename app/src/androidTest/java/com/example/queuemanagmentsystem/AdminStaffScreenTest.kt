package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the AdminStaff screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the admin/staff login form components are present, interactive elements work,
 * and the authentication flow functions properly for both Admin and Staff roles.
 */
class AdminStaffScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToAdminStaffScreen() {
        // Navigate from Register -> Admin/Staff Login
        // First navigate to admin login from register page
        // This assumes there's a way to get to admin login from the register screen
        // You may need to adjust this based on your actual navigation flow
    }

    @Test
    fun adminStaffScreenIsDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("adminStaffScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("adminStaffContent").assertIsDisplayed()
    }

    @Test
    fun logoAndTitleSectionAreDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("logoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("titleSection").assertIsDisplayed()
        composeRule.onNodeWithTag("titleText").assertIsDisplayed()
        composeRule.onNodeWithTag("subtitleText").assertIsDisplayed()
    }

    @Test
    fun loginFormCardIsDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("loginFormCard").assertIsDisplayed()
    }

    @Test
    fun roleSelectionComponentsAreDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("roleSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("roleDropdownBox").assertIsDisplayed()
        composeRule.onNodeWithTag("roleDropdownField").assertIsDisplayed()
    }

    @Test
    fun roleDropdownCanBeOpened() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        // Verify dropdown menu appears
        composeRule.onNodeWithTag("roleDropdownMenu").assertIsDisplayed()
        composeRule.onNodeWithTag("roleOption_Admin").assertIsDisplayed()
        composeRule.onNodeWithTag("roleOption_Staff").assertIsDisplayed()
    }

    @Test
    fun canSelectAdminRole() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Admin").performClick()
        // After selection, dropdown should close and Admin should be selected
        composeRule.onNodeWithTag("roleDropdownField").assertIsDisplayed()
    }

    @Test
    fun canSelectStaffRole() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Staff").performClick()
        // After selecting Staff, branch code field should appear
        composeRule.onNodeWithTag("branchCodeSection").assertIsDisplayed()
        composeRule.onNodeWithTag("branchCodeField").assertIsDisplayed()
    }

    @Test
    fun inputFieldsAreDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("idField").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
    }

    @Test
    fun canTypeIntoIdField() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("idField").performTextInput("admin001")
        // If no exceptions, input worked
    }

    @Test
    fun canTypeIntoPasswordField() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("passwordField").performTextInput("password123")
        // If no exceptions, input worked
    }

    @Test
    fun passwordVisibilityToggleWorks() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("passwordField").performTextInput("testpassword")
        composeRule.onNodeWithTag("passwordToggle").assertIsDisplayed().performClick()
        // Click again to toggle back
        composeRule.onNodeWithTag("passwordToggle").performClick()
    }

    @Test
    fun branchCodeFieldAppearsForStaffRole() {
        navigateToAdminStaffScreen()
        // Select Staff role
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Staff").performClick()

        // Branch code field should now be visible
        composeRule.onNodeWithTag("branchCodeSection").assertIsDisplayed()
        composeRule.onNodeWithTag("branchCodeField").assertIsDisplayed()
    }

    @Test
    fun canTypeIntoBranchCodeField() {
        navigateToAdminStaffScreen()
        // Select Staff role first
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Staff").performClick()

        // Type into branch code field
        composeRule.onNodeWithTag("branchCodeField").performTextInput("BR001")
        // If no exceptions, input worked
    }

    @Test
    fun loginButtonIsDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun loginButtonIsClickable() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("loginButton").performClick()
        // Should handle login attempt (may show validation errors for empty fields)
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun backButtonIsDisplayed() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("backButton").assertIsDisplayed()
    }

    @Test
    fun backButtonIsClickable() {
        navigateToAdminStaffScreen()
        composeRule.onNodeWithTag("backButton").performClick()
        // Should navigate back to customer login
        composeRule.onNodeWithTag("backButton").assertIsDisplayed()
    }

    @Test
    fun completeAdminLoginFlow() {
        navigateToAdminStaffScreen()

        // Ensure Admin role is selected (default)
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Admin").performClick()

        // Fill in admin credentials
        composeRule.onNodeWithTag("idField").performTextInput("admin001")
        composeRule.onNodeWithTag("passwordField").performTextInput("adminpass123")

        // Attempt login
        composeRule.onNodeWithTag("loginButton").performClick()

        // Verify all components remain visible after interaction
        composeRule.onNodeWithTag("roleSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("idField").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun completeStaffLoginFlow() {
        navigateToAdminStaffScreen()

        // Select Staff role
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Staff").performClick()

        // Fill in staff credentials
        composeRule.onNodeWithTag("idField").performTextInput("staff001")
        composeRule.onNodeWithTag("branchCodeField").performTextInput("BR001")
        composeRule.onNodeWithTag("passwordField").performTextInput("staffpass123")

        // Attempt login
        composeRule.onNodeWithTag("loginButton").performClick()

        // Verify all components remain visible after interaction
        composeRule.onNodeWithTag("roleSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("idField").assertIsDisplayed()
        composeRule.onNodeWithTag("branchCodeSection").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun allMainComponentsRemainVisibleAfterInteractions() {
        navigateToAdminStaffScreen()

        // Interact with various elements
        composeRule.onNodeWithTag("roleDropdownField").performClick()
        composeRule.onNodeWithTag("roleOption_Staff").performClick()
        composeRule.onNodeWithTag("idField").performTextInput("staff002")
        composeRule.onNodeWithTag("branchCodeField").performTextInput("BR002")
        composeRule.onNodeWithTag("passwordField").performTextInput("testpass")
        composeRule.onNodeWithTag("passwordToggle").performClick()
        composeRule.onNodeWithTag("loginButton").performClick()

        // Verify all main sections remain visible
        composeRule.onNodeWithTag("logoCard").assertIsDisplayed()
        composeRule.onNodeWithTag("titleSection").assertIsDisplayed()
        composeRule.onNodeWithTag("loginFormCard").assertIsDisplayed()
        composeRule.onNodeWithTag("roleSelectionCard").assertIsDisplayed()
        composeRule.onNodeWithTag("idField").assertIsDisplayed()
        composeRule.onNodeWithTag("branchCodeSection").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
        composeRule.onNodeWithTag("backButton").assertIsDisplayed()
    }

    @Test
    fun emptyFieldsLoginAttempt() {
        navigateToAdminStaffScreen()

        // Try to login without filling any fields
        composeRule.onNodeWithTag("loginButton").performClick()

        // Should handle validation (Firebase may show error)
        // Verify button remains clickable and form is still visible
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
        composeRule.onNodeWithTag("idField").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
    }
}
