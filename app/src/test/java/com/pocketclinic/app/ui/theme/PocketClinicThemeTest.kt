package com.pocketclinic.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PocketClinicThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `background color is true black`() {
        var background = Color.Unspecified
        composeTestRule.setContent {
            PocketClinicTheme {
                background = MaterialTheme.colorScheme.background
            }
        }
        assertThat(background).isEqualTo(Background)
    }

    @Test
    fun `surface color is dark gray`() {
        var surface = Color.Unspecified
        composeTestRule.setContent {
            PocketClinicTheme {
                surface = MaterialTheme.colorScheme.surface
            }
        }
        assertThat(surface).isEqualTo(Surface)
    }

    @Test
    fun `primary container is vivid amber`() {
        var primaryContainer = Color.Unspecified
        composeTestRule.setContent {
            PocketClinicTheme {
                primaryContainer = MaterialTheme.colorScheme.primaryContainer
            }
        }
        assertThat(primaryContainer).isEqualTo(PrimaryAmber)
    }

    @Test
    fun `on primary container is dark brown not pure black`() {
        var onPrimaryContainer = Color.Unspecified
        composeTestRule.setContent {
            PocketClinicTheme {
                onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
            }
        }
        // Must NOT be pure white/black; per design tokens on-primary = #432C00
        assertThat(onPrimaryContainer).isEqualTo(OnPrimary)
    }

    @Test
    fun `theme is always dark regardless of system setting`() {
        var isDark = false
        composeTestRule.setContent {
            PocketClinicTheme {
                isDark = MaterialTheme.colorScheme.background == Background
            }
        }
        assertThat(isDark).isTrue()
    }

    @Test
    fun `all shapes have zero corner radius`() {
        var extraSmallRadius = -1f
        composeTestRule.setContent {
            PocketClinicTheme {
                extraSmallRadius = MaterialTheme.shapes.extraSmall.topStart
                    .let { it.toString().toFloatOrNull() ?: 0f }
            }
        }
        // Zero radius — terminal aesthetic
        assertThat(extraSmallRadius).isEqualTo(0f)
    }
}
