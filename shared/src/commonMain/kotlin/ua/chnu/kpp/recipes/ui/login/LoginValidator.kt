package ua.chnu.kpp.recipes.ui.login

enum class UsernameError { EMPTY, TOO_SHORT, INVALID_CHARS }

enum class PasswordError { EMPTY, TOO_SHORT }

/** Pure validation rules for the sign-in form (no Compose, easy to unit test). */
object LoginValidator {
    const val USERNAME_MIN_LENGTH = 3
    const val PASSWORD_MIN_LENGTH = 6

    private val usernameAllowed = Regex("^[A-Za-z0-9._-]+$")

    fun validateUsername(username: String): UsernameError? {
        val value = username.trim()
        return when {
            value.isEmpty() -> UsernameError.EMPTY
            value.length < USERNAME_MIN_LENGTH -> UsernameError.TOO_SHORT
            !usernameAllowed.matches(value) -> UsernameError.INVALID_CHARS
            else -> null
        }
    }

    fun validatePassword(password: String): PasswordError? = when {
        password.isEmpty() -> PasswordError.EMPTY
        password.length < PASSWORD_MIN_LENGTH -> PasswordError.TOO_SHORT
        else -> null
    }
}
