package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Register screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the main input fields are present, accept text, and that interactive
 * elements like the password visibility toggles and register button exist and can be clicked.
 */
class RegisterScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fieldsAreDisplayed() {
        composeRule.onNodeWithTag("usernameField").assertIsDisplayed()
        composeRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeRule.onNodeWithTag("nicField").assertIsDisplayed()
        composeRule.onNodeWithTag("phoneField").assertIsDisplayed()
        composeRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmPasswordField").assertIsDisplayed()
        composeRule.onNodeWithTag("registerButton").assertIsDisplayed()
    }

    @Test
    fun canTypeIntoAllFields() {
        composeRule.onNodeWithTag("usernameField").performTextInput("Test User")
        composeRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeRule.onNodeWithTag("nicField").performTextInput("123456789V")
        composeRule.onNodeWithTag("phoneField").performTextInput("+94712345678")
        composeRule.onNodeWithTag("passwordField").performTextInput("Password1")
        composeRule.onNodeWithTag("confirmPasswordField").performTextInput("Password1")
        // If no exceptions, input worked.
    }

    @Test
    fun togglePasswordVisibility() {
        composeRule.onNodeWithTag("passwordVisibilityToggle").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("confirmPasswordVisibilityToggle").assertIsDisplayed().performClick()
    }

    @Test
    fun clickRegisterWithEmptyFields_showsButtonStill() {
        // Click register without filling to ensure no crash; toast can't be asserted directly here.
        composeRule.onNodeWithTag("registerButton").performClick()
        composeRule.onNodeWithTag("registerButton").assertIsDisplayed()
    }
}

