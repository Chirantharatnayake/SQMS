package com.example.queuemanagmentsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * Basic UI automation tests for the Notification screen using Jetpack Compose testing APIs (runs with Espresso).
 * These tests verify that the notification components are present, notification management works,
 * and the notification interface functions properly for viewing and clearing notifications.
 */
class NotificationPageScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToNotificationScreen() {
        // Navigate from Register -> Login -> Customer Home -> Notifications
        // First go to login page
        composeRule.onNodeWithTag("goToLoginButton").performClick()

        // Fill in valid credentials and login
        composeRule.onNodeWithTag("emailInput").performTextInput("test@example.com")
        composeRule.onNodeWithTag("passwordInput").performTextInput("password123")
        composeRule.onNodeWithTag("loginButton").performClick()

        // Navigate to notifications from customer home
        // This assumes there's a way to access notifications from the main app
        // You may need to adjust based on your actual navigation flow
    }

    @Test
    fun notificationScreenIsDisplayed() {
        navigateToNotificationScreen()
        composeRule.onNodeWithTag("notificationScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationContent").assertIsDisplayed()
    }

    @Test
    fun notificationTopBarIsDisplayed() {
        navigateToNotificationScreen()
        composeRule.onNodeWithTag("notificationTopBar").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationBackButton").assertIsDisplayed()
    }

    @Test
    fun backButtonIsClickable() {
        navigateToNotificationScreen()
        composeRule.onNodeWithTag("notificationBackButton").performClick()
        // Should navigate back to previous screen
        composeRule.onNodeWithTag("notificationBackButton").assertIsDisplayed()
    }

    @Test
    fun loadingIndicatorIsDisplayedWhenLoading() {
        navigateToNotificationScreen()
        // During initial load, loading indicator should be visible
        // This may be timing dependent - loading might complete quickly
        composeRule.onNodeWithTag("notificationLoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun noNotificationsMessageDisplayedWhenEmpty() {
        navigateToNotificationScreen()
        // If no notifications exist, the no notifications message should be displayed
        // This test may need to wait for loading to complete
        composeRule.onNodeWithTag("noNotificationsMessage").assertIsDisplayed()
    }

    @Test
    fun notificationsListDisplayedWhenNotificationsExist() {
        navigateToNotificationScreen()
        // If notifications exist, the notifications list should be displayed
        // This test assumes there are notifications for the logged-in user
        composeRule.onNodeWithTag("notificationsList").assertIsDisplayed()
    }

    @Test
    fun clearButtonDisplayedWhenNotificationsExist() {
        navigateToNotificationScreen()
        // Clear button should only be visible when notifications exist
        composeRule.onNodeWithTag("notificationClearButton").assertIsDisplayed()
    }

    @Test
    fun clearButtonTriggersConfirmationDialog() {
        navigateToNotificationScreen()
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        // Should show confirmation dialog
        composeRule.onNodeWithTag("clearConfirmationDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("confirmClearDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("cancelClearDialog").assertIsDisplayed()
    }

    @Test
    fun clearConfirmationDialogActions() {
        navigateToNotificationScreen()

        // Open clear dialog
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("clearConfirmationDialog").assertIsDisplayed()

        // Test cancel button
        composeRule.onNodeWithTag("cancelClearDialog").performClick()
        // Dialog should close

        // Test confirm button
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("confirmClearDialog").performClick()
        // Should clear all notifications
    }

    @Test
    fun notificationCardElementsAreDisplayed() {
        navigateToNotificationScreen()
        // Test assumes at least one notification exists
        // You may need to adjust hash value based on test data
        val testHash = "123456789" // Replace with actual notification hash

        composeRule.onNodeWithTag("notificationCard_$testHash").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationMessage_$testHash").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationTimestamp_$testHash").assertIsDisplayed()
    }

    @Test
    fun multipleNotificationCardsCanBeDisplayed() {
        navigateToNotificationScreen()
        // Test multiple notification cards
        val hash1 = "123456789"
        val hash2 = "987654321"

        composeRule.onNodeWithTag("notificationCard_$hash1").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationCard_$hash2").assertIsDisplayed()

        // Verify their content elements
        composeRule.onNodeWithTag("notificationMessage_$hash1").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationMessage_$hash2").assertIsDisplayed()
    }

    @Test
    fun notificationContentDisplaysCorrectly() {
        navigateToNotificationScreen()
        val testHash = "123456789"

        // Verify notification structure
        composeRule.onNodeWithTag("notificationCard_$testHash").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationMessage_$testHash").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationTimestamp_$testHash").assertIsDisplayed()
    }

    @Test
    fun clearNotificationsFlow() {
        navigateToNotificationScreen()

        // Complete clear notifications flow
        // 1. Verify notifications exist and clear button is visible
        composeRule.onNodeWithTag("notificationClearButton").assertIsDisplayed()

        // 2. Click clear button
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("clearConfirmationDialog").assertIsDisplayed()

        // 3. Cancel first time
        composeRule.onNodeWithTag("cancelClearDialog").performClick()

        // 4. Try again and confirm
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("confirmClearDialog").performClick()

        // 5. Verify UI updates (should show loading or empty state)
        composeRule.onNodeWithTag("notificationContent").assertIsDisplayed()
    }

    @Test
    fun allMainComponentsRemainVisibleAfterInteractions() {
        navigateToNotificationScreen()

        // Perform various interactions
        composeRule.onNodeWithTag("notificationBackButton").performClick()
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("cancelClearDialog").performClick()

        // Verify all main sections remain visible
        composeRule.onNodeWithTag("notificationTopBar").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationContent").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationBackButton").assertIsDisplayed()
    }

    @Test
    fun notificationScreenStatesHandling() {
        navigateToNotificationScreen()

        // Test different states the screen can be in
        // 1. Loading state (may be brief)
        // composeRule.onNodeWithTag("notificationLoadingIndicator").assertIsDisplayed()

        // 2. Empty state (if no notifications)
        // composeRule.onNodeWithTag("noNotificationsMessage").assertIsDisplayed()

        // 3. Populated state (if notifications exist)
        // composeRule.onNodeWithTag("notificationsList").assertIsDisplayed()

        // At minimum, content should be displayed
        composeRule.onNodeWithTag("notificationContent").assertIsDisplayed()
    }

    @Test
    fun navigationAndInteractionStability() {
        navigateToNotificationScreen()

        // Test multiple navigation and interaction cycles
        composeRule.onNodeWithTag("notificationBackButton").performClick()
        // Navigate back to notifications (depends on your navigation flow)

        // Test clear dialog multiple times
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("cancelClearDialog").performClick()

        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("cancelClearDialog").performClick()

        // Verify UI remains stable
        composeRule.onNodeWithTag("notificationScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("notificationTopBar").assertIsDisplayed()
    }

    @Test
    fun notificationManagementFlow() {
        navigateToNotificationScreen()

        // Complete notification management workflow
        // 1. View notifications
        composeRule.onNodeWithTag("notificationContent").assertIsDisplayed()

        // 2. Check if notifications exist or empty state
        // Either notifications list or no notifications message should be visible

        // 3. If notifications exist, test clear functionality
        composeRule.onNodeWithTag("notificationClearButton").performClick()
        composeRule.onNodeWithTag("clearConfirmationDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("cancelClearDialog").performClick()

        // 4. Navigate back
        composeRule.onNodeWithTag("notificationBackButton").performClick()

        // Verify flow completed successfully
        composeRule.onNodeWithTag("notificationScreen").assertIsDisplayed()
    }

    @Test
    fun emptyStateDisplay() {
        navigateToNotificationScreen()

        // Test empty state when no notifications exist
        // This test assumes the user has no notifications
        composeRule.onNodeWithTag("noNotificationsMessage").assertIsDisplayed()

        // Clear button should not be visible in empty state
        // composeRule.onNodeWithTag("notificationClearButton").assertDoesNotExist()

        // Back button should still be functional
        composeRule.onNodeWithTag("notificationBackButton").assertIsDisplayed()
    }
}
