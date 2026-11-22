package ua.chnu.kpp.recipes.data.remote

import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import ua.chnu.kpp.recipes.data.auth.AuthTokens
import ua.chnu.kpp.recipes.data.auth.SessionEvents
import ua.chnu.kpp.recipes.data.auth.TokenStorage
import ua.chnu.kpp.recipes.data.auth.dto.RefreshRequestDto
import ua.chnu.kpp.recipes.data.auth.dto.RefreshResponseDto

private val log = KermitLogger.withTag("Http")

/**
 * Ktor client with the Auth (bearer) plugin:
 * - attaches the stored access token to every API call except login/refresh;
 * - on 401 calls `/auth/refresh` once, stores the new pair and retries the original request;
 * - if refresh is rejected, clears the session and notifies [SessionEvents].
 *
 * [engine] is optional so tests can pass a MockEngine; otherwise the platform engine is used
 * (OkHttp/Android, Darwin, CIO).
 */
internal fun createHttpClient(
    tokenStorage: TokenStorage,
    sessionEvents: SessionEvents,
    engine: HttpClientEngine? = null,
): HttpClient {
    val config: io.ktor.client.HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) = log.d { message }
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    tokenStorage.read()?.let { BearerTokens(it.accessToken, it.refreshToken) }
                }

                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken ?: tokenStorage.read()?.refreshToken
                    if (refreshToken.isNullOrBlank()) {
                        sessionEvents.notifyExpired()
                        return@refreshTokens null
                    }
                    log.i { "Access token expired, refreshing" }
                    val response = try {
                        client.post(ApiConfig.BASE_URL + ApiConfig.REFRESH_PATH) {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshRequestDto(refreshToken, ApiConfig.ACCESS_TOKEN_TTL_MINUTES))
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        // offline: keep the session, the request just fails with a network error
                        log.w(e) { "Token refresh failed (network)" }
                        return@refreshTokens null
                    }

                    if (!response.status.isSuccess()) {
                        log.w { "Refresh token rejected (${response.status}), signing out" }
                        tokenStorage.clear()
                        sessionEvents.notifyExpired()
                        return@refreshTokens null
                    }

                    val body = response.body<RefreshResponseDto>()
                    val tokens = AuthTokens(body.accessToken, body.refreshToken)
                    tokenStorage.save(tokens)
                    log.i { "Tokens refreshed" }
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                }

                // send the token proactively (not only after a 401) to our API, but never to login/refresh
                sendWithoutRequest { request ->
                    val path = request.url.encodedPath
                    request.url.host == "dummyjson.com" &&
                        !path.endsWith(ApiConfig.LOGIN_PATH) &&
                        !path.endsWith(ApiConfig.REFRESH_PATH)
                }
            }
        }
    }
    return if (engine != null) HttpClient(engine, config) else HttpClient(config)
}
