package ua.chnu.kpp.recipes

import androidx.lifecycle.ViewModel
import ua.chnu.kpp.recipes.domain.onboarding.IsOnboardingPassedUseCase
import ua.chnu.kpp.recipes.navigation.Route

/** Main (root) view model: decides which screen the app starts with. */
class AppViewModel(
    isOnboardingPassed: IsOnboardingPassedUseCase,
) : ViewModel() {

    val startDestination: Route = if (isOnboardingPassed()) Route.Login else Route.Onboarding
}
