package ua.chnu.kpp.recipes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import ua.chnu.kpp.recipes.data.local.UserPreferences
import ua.chnu.kpp.recipes.ui.home.HomeScreen
import ua.chnu.kpp.recipes.ui.onboarding.OnboardingScreen
import ua.chnu.kpp.recipes.ui.theme.SmakolykTheme

@Composable
@Preview
fun App() {
    SmakolykTheme {
        val preferences = remember { UserPreferences() }
        var onboardingPassed by remember { mutableStateOf(preferences.isOnboardingPassed) }

        if (onboardingPassed) {
            HomeScreen(onReplayOnboarding = {
                preferences.isOnboardingPassed = false
                onboardingPassed = false
            })
        } else {
            OnboardingScreen(onFinished = {
                preferences.isOnboardingPassed = true
                onboardingPassed = true
            })
        }
    }
}
