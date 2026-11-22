package ua.chnu.kpp.recipes.ui.login

import androidx.compose.runtime.Immutable

@Immutable
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    /** Errors are shown only after the field was edited once or the form was submitted. */
    val usernameError: UsernameError? = null,
    val passwordError: PasswordError? = null,
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean get() = !isLoading
}

sealed interface LoginIntent {
    data class UsernameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    /** Sent by the "Sign in" button and by the keyboard "Done" action on the password field. */
    data object Submit : LoginIntent
}

sealed interface LoginEffect {
    /** Moves focus to the field that failed validation. */
    data class FocusField(val field: LoginField) : LoginEffect
    data object NavigateToHome : LoginEffect
    /** One-off event rendered as a snackbar. */
    data class ShowError(val reason: LoginFailure) : LoginEffect
}

enum class LoginFailure { INVALID_CREDENTIALS, NO_CONNECTION, SERVER, UNKNOWN }

enum class LoginField { USERNAME, PASSWORD }
