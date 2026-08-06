package com.misaventuras

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.misaventuras.data.ChildProfileEntity
import com.misaventuras.ui.HomeState
import com.misaventuras.ui.WelcomeScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {@get:Rule val compose=createComposeRule();@Test fun selectingProfileIsAccessible(){var selected=0L;compose.setContent{MaterialTheme{WelcomeScreen(HomeState(profiles=listOf(ChildProfileEntity(1,"Lola",primaryColor=0,secondaryColor=0))),{selected=it})}};compose.onNodeWithText("Lola").performClick();assertEquals(1L,selected)}}

class AppLaunchTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStartsAndLoadsSeedProfiles() {
        compose.onNodeWithText("Mis Aventuras").assertIsDisplayed()
        compose.onNodeWithText("Lola").assertIsDisplayed()
        compose.onNodeWithText("Olivia").assertIsDisplayed()
    }
}
