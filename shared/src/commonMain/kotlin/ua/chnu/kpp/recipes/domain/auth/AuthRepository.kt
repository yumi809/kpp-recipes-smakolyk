package ua.chnu.kpp.recipes.domain.auth

import kotlinx.coroutines.flow.Flow
import ua.chnu.kpp.recipes.domain.common.Result

data class UserProfile(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val avatarUrl: String,
)

interface AuthRepository {
    /** Signs in and securely stores the received tokens. */
    suspend fun signIn(username: String, password: String): Result<UserProfile>

    /** `true` when a refresh token is stored on the device. */
    suspend fun hasSession(): Boolean

    /** Authorized call (`/auth/me`); the access token is refreshed automatically when it expires. */
    suspend fun currentUser(): Result<UserProfile>

    suspend fun signOut()

    /** Emits when the refresh token was rejected and the stored session was dropped. */
    val sessionExpired: Flow<Unit>
}
