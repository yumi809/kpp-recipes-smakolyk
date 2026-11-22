package ua.chnu.kpp.recipes.domain.common

/** Outcome of an operation that can fail; independent from Ktor and other data-layer types. */
sealed interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Failure(val error: DataError) : Result<Nothing>
}

sealed interface DataError {
    /** No connection, timeout, DNS... */
    data object Network : DataError
    /** 400/401/403 on sign-in: wrong username or password. */
    data object InvalidCredentials : DataError
    /** Refresh token is no longer valid, the user has to sign in again. */
    data object Unauthorized : DataError
    data class Server(val code: Int) : DataError
    data class Unknown(val message: String) : DataError
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onFailure(action: (DataError) -> Unit): Result<T> {
    if (this is Result.Failure) action(error)
    return this
}
