package ua.chnu.kpp.recipes.data.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ua.chnu.kpp.recipes.data.auth.dto.LoginRequestDto
import ua.chnu.kpp.recipes.data.auth.dto.LoginResponseDto
import ua.chnu.kpp.recipes.data.auth.dto.UserDto
import ua.chnu.kpp.recipes.data.remote.ApiConfig
import ua.chnu.kpp.recipes.data.remote.safeCall
import ua.chnu.kpp.recipes.domain.common.Result

internal class AuthApi(private val client: HttpClient) {

    suspend fun login(username: String, password: String): Result<LoginResponseDto> = safeCall {
        client.post(ApiConfig.BASE_URL + ApiConfig.LOGIN_PATH) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password, ApiConfig.ACCESS_TOKEN_TTL_MINUTES))
        }
    }

    /** Bearer token is attached (and refreshed if needed) by the Ktor Auth plugin. */
    suspend fun me(): Result<UserDto> = safeCall {
        client.get(ApiConfig.BASE_URL + ApiConfig.ME_PATH)
    }
}
