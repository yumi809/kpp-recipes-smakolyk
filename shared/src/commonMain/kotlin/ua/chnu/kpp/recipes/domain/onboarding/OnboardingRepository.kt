package ua.chnu.kpp.recipes.domain.onboarding

/** Domain contract for onboarding progress. Implemented in the data layer with multiplatform-settings. */
interface OnboardingRepository {
    fun isOnboardingPassed(): Boolean
    fun setOnboardingPassed(passed: Boolean)
}
