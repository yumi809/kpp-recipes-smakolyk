package ua.chnu.kpp.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.chnu.kpp.recipes.domain.auth.IsUserSignedInUseCase
import ua.chnu.kpp.recipes.domain.auth.ObserveSessionExpiredUseCase
import ua.chnu.kpp.recipes.domain.onboarding.IsOnboardingPassedUseCase
import ua.chnu.kpp.recipes.navigation.Route

/**
 * Main (root) view model: picks the start screen from two flags —
 * onboarding passed? and signed in (tokens in secure storage)? —
 * and reports when the session expires so the app can return to login.
 */
class AppViewModel(
    private val isOnboardingPassed: IsOnboardingPassedUseCase,
    private val isUserSignedIn: IsUserSignedInUseCase,
    observeSessionExpired: ObserveSessionExpiredUseCase,
) : ViewModel() {

    /** `null` while the secure storage is being read (a blank splash is shown). */
    private val _startDestination = MutableStateFlow<Route?>(null)
    val startDestination: StateFlow<Route?> = _startDestination.asStateFlow()

    val sessionExpired: Flow<Unit> = observeSessionExpired()

    init {
        viewModelScope.launch {
            _startDestination.value = when {
                !isOnboardingPassed() -> Route.Onboarding
                !isUserSignedIn() -> Route.Login
                else -> Route.Home
            }
        }
    }
}
