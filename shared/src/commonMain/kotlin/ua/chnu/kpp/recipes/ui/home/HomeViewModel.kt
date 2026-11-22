package ua.chnu.kpp.recipes.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.chnu.kpp.recipes.domain.auth.GetCurrentUserUseCase
import ua.chnu.kpp.recipes.domain.auth.SignOutUseCase
import ua.chnu.kpp.recipes.domain.auth.UserProfile
import ua.chnu.kpp.recipes.domain.common.Result

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val user: UserProfile? = null,
    val hasError: Boolean = false,
)

sealed interface HomeIntent {
    data object Retry : HomeIntent
    data object SignOut : HomeIntent
}

sealed interface HomeEffect {
    data object SignedOut : HomeEffect
}

/** Placeholder home screen; it makes an authorized `/auth/me` call to prove the token flow works. */
class HomeViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    init {
        loadUser()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Retry -> loadUser()
            HomeIntent.SignOut -> viewModelScope.launch {
                signOut()
                _effects.send(HomeEffect.SignedOut)
            }
        }
    }

    private fun loadUser() {
        _state.update { it.copy(isLoading = true, hasError = false) }
        viewModelScope.launch {
            when (val result = getCurrentUser()) {
                is Result.Success -> _state.update { HomeState(isLoading = false, user = result.data) }
                is Result.Failure -> _state.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }
}
