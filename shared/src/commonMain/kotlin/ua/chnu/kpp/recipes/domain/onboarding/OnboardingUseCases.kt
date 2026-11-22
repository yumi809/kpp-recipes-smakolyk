package ua.chnu.kpp.recipes.domain.onboarding

/** Saves the fact that the user has finished (or skipped) the onboarding. */
class CompleteOnboardingUseCase(private val repository: OnboardingRepository) {
    operator fun invoke() = repository.setOnboardingPassed(true)
}

/** Answers whether the onboarding has already been passed, used to pick the start screen. */
class IsOnboardingPassedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isOnboardingPassed()
}
