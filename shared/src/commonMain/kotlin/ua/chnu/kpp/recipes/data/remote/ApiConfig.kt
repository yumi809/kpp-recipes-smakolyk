package ua.chnu.kpp.recipes.data.remote

internal object ApiConfig {
    const val BASE_URL = "https://dummyjson.com"
    const val LOGIN_PATH = "/auth/login"
    const val REFRESH_PATH = "/auth/refresh"
    const val ME_PATH = "/auth/me"

    /** Short access-token lifetime, so the automatic refresh is easy to see in the logs. */
    const val ACCESS_TOKEN_TTL_MINUTES = 2
    const val REFRESH_TOKEN_TTL_MINUTES = 60 * 24 * 7
}
