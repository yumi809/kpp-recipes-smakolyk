package ua.chnu.kpp.recipes

import com.russhwolf.settings.MapSettings
import ua.chnu.kpp.recipes.data.local.SettingsOnboardingRepository
import ua.chnu.kpp.recipes.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.kpp.recipes.domain.onboarding.IsOnboardingPassedUseCase
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingUseCasesTest {

    @Test
    fun completingOnboardingIsPersisted() {
        val repository = SettingsOnboardingRepository(MapSettings())
        val isPassed = IsOnboardingPassedUseCase(repository)

        assertFalse(isPassed())
        CompleteOnboardingUseCase(repository)()
        assertTrue(isPassed())
    }
}
