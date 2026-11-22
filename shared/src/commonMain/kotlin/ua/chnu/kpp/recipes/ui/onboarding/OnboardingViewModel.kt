package ua.chnu.kpp.recipes.ui.onboarding

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import ua.chnu.kpp.recipes.domain.onboarding.CompleteOnboardingUseCase

class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboardingUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Onboarding")

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects: Flow<OnboardingEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageSettled -> {
                log.d { "Page ${intent.page + 1}/${_state.value.pageCount}" }
                _state.update { it.copy(currentPage = intent.page) }
            }

            OnboardingIntent.NextClicked -> {
                val state = _state.value
                if (state.isLastPage) {
                    finish(skipped = false)
                } else {
                    _effects.trySend(OnboardingEffect.ScrollToPage(state.currentPage + 1))
                }
            }

            OnboardingIntent.SkipClicked -> finish(skipped = true)
        }
    }

    private fun finish(skipped: Boolean) {
        log.i { if (skipped) "Onboarding skipped on page ${_state.value.currentPage + 1}" else "Onboarding completed" }
        completeOnboarding()
        _effects.trySend(OnboardingEffect.NavigateToLogin)
    }
}
