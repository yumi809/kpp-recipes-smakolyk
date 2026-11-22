package ua.chnu.kpp.recipes.data.local

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

/**
 * User preferences stored with multiplatform-settings
 * (SharedPreferences on Android, NSUserDefaults on iOS, java.util.prefs on desktop).
 */
internal class UserPreferences(
    private val settings: Settings = Settings(),
) {
    private val log = Logger.withTag("UserPreferences")

    var isOnboardingPassed: Boolean
        get() = settings[KEY_ONBOARDING_PASSED] ?: false
        set(value) {
            log.d { "isOnboardingPassed = $value" }
            settings[KEY_ONBOARDING_PASSED] = value
        }

    private companion object {
        const val KEY_ONBOARDING_PASSED = "is_onboarding_passed"
    }
}
