package ua.chnu.kpp.recipes.domain.auth

import ua.chnu.kpp.recipes.domain.common.Result

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<UserProfile> =
        repository.signIn(username = username.trim(), password = password)
}

class IsUserSignedInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Boolean = repository.hasSession()
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<UserProfile> = repository.currentUser()
}

class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}

class ObserveSessionExpiredUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.sessionExpired
}
