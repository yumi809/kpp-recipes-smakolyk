package ua.chnu.kpp.recipes.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.chnu.kpp.recipes.domain.auth.SignInUseCase
import ua.chnu.kpp.recipes.domain.common.DataError
import ua.chnu.kpp.recipes.domain.common.Result

class LoginViewModel(
    private val signIn: SignInUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Login")

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
    val effects: Flow<LoginEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UsernameChanged -> _state.update {
                it.copy(
                    username = intent.value,
                    // re-validate live only if an error is already shown
                    usernameError = if (it.usernameError != null) LoginValidator.validateUsername(intent.value) else null,
                )
            }

            is LoginIntent.PasswordChanged -> _state.update {
                it.copy(
                    password = intent.value,
                    passwordError = if (it.passwordError != null) LoginValidator.validatePassword(intent.value) else null,
                )
            }

            LoginIntent.TogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _state.value
        if (!current.canSubmit) return

        val usernameError = LoginValidator.validateUsername(current.username)
        val passwordError = LoginValidator.validatePassword(current.password)
        _state.update { it.copy(usernameError = usernameError, passwordError = passwordError) }

        when {
            usernameError != null -> _effects.trySend(LoginEffect.FocusField(LoginField.USERNAME))
            passwordError != null -> _effects.trySend(LoginEffect.FocusField(LoginField.PASSWORD))
            else -> signIn(current.username, current.password)
        }
    }

    private fun signIn(username: String, password: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = signIn.invoke(username, password)
            _state.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> _effects.send(LoginEffect.NavigateToHome)
                is Result.Failure -> {
                    log.w { "Sign-in failed: ${result.error}" }
                    _effects.send(LoginEffect.ShowError(result.error.toLoginFailure()))
                }
            }
        }
    }
}

internal fun DataError.toLoginFailure(): LoginFailure = when (this) {
    DataError.InvalidCredentials, DataError.Unauthorized -> LoginFailure.INVALID_CREDENTIALS
    DataError.Network -> LoginFailure.NO_CONNECTION
    is DataError.Server -> LoginFailure.SERVER
    is DataError.Unknown -> LoginFailure.UNKNOWN
}
