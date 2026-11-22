package ua.chnu.kpp.recipes

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import ua.chnu.kpp.recipes.data.auth.AuthApi
import ua.chnu.kpp.recipes.data.auth.AuthTokens
import ua.chnu.kpp.recipes.data.auth.SessionEvents
import ua.chnu.kpp.recipes.data.auth.TokenStorage
import ua.chnu.kpp.recipes.data.remote.createHttpClient
import ua.chnu.kpp.recipes.domain.common.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

private class InMemoryTokenStorage(var tokens: AuthTokens?) : TokenStorage {
    override suspend fun read() = tokens
    override suspend fun save(tokens: AuthTokens) { this.tokens = tokens }
    override suspend fun clear() { tokens = null }
}

private val json = headersOf(HttpHeaders.ContentType, "application/json")

class TokenRefreshTest {

    @Test
    fun expiredAccessTokenIsRefreshedAndRequestRetried() = runTest {
        val storage = InMemoryTokenStorage(AuthTokens("old-access", "old-refresh"))
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/auth/me" ->
                    if (request.headers[HttpHeaders.Authorization] == "Bearer new-access") {
                        respond("""{"id":1,"username":"emilys","firstName":"Emily"}""", HttpStatusCode.OK, json)
                    } else {
                        respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
                    }
                "/auth/refresh" ->
                    respond("""{"accessToken":"new-access","refreshToken":"new-refresh"}""", HttpStatusCode.OK, json)
                else -> error("Unexpected ${request.url}")
            }
        }
        val api = AuthApi(createHttpClient(storage, SessionEvents(), engine))

        val result = api.me()

        assertIs<Result.Success<*>>(result)
        assertEquals(AuthTokens("new-access", "new-refresh"), storage.tokens)
    }

    @Test
    fun rejectedRefreshTokenClearsSessionAndNotifies() = runTest {
        val storage = InMemoryTokenStorage(AuthTokens("old-access", "old-refresh"))
        val events = SessionEvents()
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/auth/me" -> respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
                "/auth/refresh" -> respond("""{"message":"Invalid refresh token"}""", HttpStatusCode.Forbidden, json)
                else -> error("Unexpected ${request.url}")
            }
        }
        val api = AuthApi(createHttpClient(storage, events, engine))
        val expired = async { events.expired.first() }

        val result = api.me()

        assertIs<Result.Failure>(result)
        assertNull(storage.tokens)
        expired.await()
    }
}
