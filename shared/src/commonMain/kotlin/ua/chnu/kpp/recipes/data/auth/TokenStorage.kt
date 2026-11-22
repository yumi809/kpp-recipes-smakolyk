package ua.chnu.kpp.recipes.data.auth

import eu.anifantakis.lib.ksafe.KSafe

data class AuthTokens(val accessToken: String, val refreshToken: String)

interface TokenStorage {
    suspend fun read(): AuthTokens?
    suspend fun save(tokens: AuthTokens)
    suspend fun clear()
}

/**
 * Tokens are kept in KSafe: encrypted with a key from Android Keystore / iOS Keychain
 * (and an app-scoped key file on desktop), never in plain preferences.
 */
internal class KSafeTokenStorage(
    private val ksafe: KSafe,
) : TokenStorage {

    override suspend fun read(): AuthTokens? {
        val access = ksafe.get(KEY_ACCESS, "")
        val refresh = ksafe.get(KEY_REFRESH, "")
        return if (access.isBlank() || refresh.isBlank()) null else AuthTokens(access, refresh)
    }

    override suspend fun save(tokens: AuthTokens) {
        ksafe.put(KEY_ACCESS, tokens.accessToken)
        ksafe.put(KEY_REFRESH, tokens.refreshToken)
    }

    override suspend fun clear() {
        ksafe.put(KEY_ACCESS, "")
        ksafe.put(KEY_REFRESH, "")
    }

    private companion object {
        const val KEY_ACCESS = "auth_access_token"
        const val KEY_REFRESH = "auth_refresh_token"
    }
}
