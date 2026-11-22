package ua.chnu.kpp.recipes.data.local

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import ua.chnu.kpp.recipes.domain.onboarding.OnboardingRepository

/**
 * Stores onboarding progress with multiplatform-settings
 * (SharedPreferences on Android, NSUserDefaults on iOS, java.util.prefs on desktop).
 */
internal class SettingsOnboardingRepository(
    private val settings: Settings,
) : OnboardingRepository {

    private val log = Logger.withTag("OnboardingRepository")

    override fun isOnboardingPassed(): Boolean = settings[KEY_ONBOARDING_PASSED] ?: false

    override fun setOnboardingPassed(passed: Boolean) {
        log.d { "Onboarding passed = $passed" }
        settings[KEY_ONBOARDING_PASSED] = passed
    }

    private companion object {
        const val KEY_ONBOARDING_PASSED = "is_onboarding_passed"
    }
}
