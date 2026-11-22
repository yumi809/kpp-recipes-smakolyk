package ua.chnu.kpp.recipes.ui.login

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

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
            else -> {
                log.i { "Sign-in form is valid for '${current.username.trim()}'" }
                _effects.trySend(LoginEffect.FormAccepted)
            }
        }
    }
}
