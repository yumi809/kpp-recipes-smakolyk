package ua.chnu.kpp.recipes.data.auth

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.flow.Flow
import ua.chnu.kpp.recipes.data.auth.dto.LoginResponseDto
import ua.chnu.kpp.recipes.data.auth.dto.UserDto
import ua.chnu.kpp.recipes.domain.auth.AuthRepository
import ua.chnu.kpp.recipes.domain.auth.UserProfile
import ua.chnu.kpp.recipes.domain.common.Result
import ua.chnu.kpp.recipes.domain.common.map
import ua.chnu.kpp.recipes.domain.common.onSuccess

internal class RemoteAuthRepository(
    private val api: AuthApi,
    private val client: HttpClient,
    private val tokenStorage: TokenStorage,
    private val sessionEvents: SessionEvents,
) : AuthRepository {

    private val log = Logger.withTag("AuthRepository")

    override val sessionExpired: Flow<Unit> get() = sessionEvents.expired

    override suspend fun signIn(username: String, password: String): Result<UserProfile> =
        api.login(username, password)
            .onSuccess { response ->
                tokenStorage.save(AuthTokens(response.accessToken, response.refreshToken))
                // drop tokens cached by the Auth plugin, the next call loads the new ones
                resetCachedTokens()
                log.i { "Signed in as ${response.username}" }
            }
            .map { it.toDomain() }

    override suspend fun hasSession(): Boolean = tokenStorage.read() != null

    override suspend fun currentUser(): Result<UserProfile> = api.me().map { it.toDomain() }

    override suspend fun signOut() {
        tokenStorage.clear()
        resetCachedTokens()
        log.i { "Signed out" }
    }

    private fun resetCachedTokens() {
        client.authProvider<BearerAuthProvider>()?.clearToken()
    }
}

private fun LoginResponseDto.toDomain() = UserProfile(id, username, firstName, lastName, email, image)

private fun UserDto.toDomain() = UserProfile(id, username, firstName, lastName, email, image)
