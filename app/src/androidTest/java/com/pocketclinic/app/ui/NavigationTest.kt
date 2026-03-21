package com.pocketclinic.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.pocketclinic.app.ui.navigation.AppNavHost
import com.pocketclinic.app.ui.theme.PocketClinicTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            PocketClinicTheme {
                AppNavHost()
            }
        }
    }

    @Test
    fun command_tab_is_selected_by_default() {
        composeRule
            .onNodeWithContentDescription("Command")
            .assertIsSelected()
    }

    @Test
    fun tapping_tests_tab_navigates_to_tests_screen() {
        composeRule
            .onNodeWithContentDescription("Tests")
            .performClick()
        composeRule
            .onNodeWithTag("tests_screen")
            .assertIsDisplayed()
    }

    @Test
    fun tapping_reports_tab_navigates_to_reports_screen() {
        composeRule
            .onNodeWithContentDescription("Reports")
            .performClick()
        composeRule
            .onNodeWithTag("reports_screen")
            .assertIsDisplayed()
    }

    @Test
    fun tapping_profile_tab_navigates_to_profile_screen() {
        composeRule
            .onNodeWithContentDescription("Profile")
            .performClick()
        composeRule
            .onNodeWithTag("profile_screen")
            .assertIsDisplayed()
    }

    @Test
    fun back_press_from_tests_screen_returns_to_command() {
        composeRule
            .onNodeWithContentDescription("Tests")
            .performClick()
        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeRule
            .onNodeWithTag("command_screen")
            .assertIsDisplayed()
    }
}
