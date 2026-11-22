package ua.chnu.kpp.recipes.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import ua.chnu.kpp.recipes.domain.common.DataError
import ua.chnu.kpp.recipes.domain.common.Result

/** Runs a Ktor call and converts HTTP statuses and exceptions into a domain [Result]. */
internal suspend inline fun <reified T> safeCall(block: () -> HttpResponse): Result<T> {
    val response = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        return Result.Failure(DataError.Network)
    } catch (e: Exception) {
        return Result.Failure(DataError.Unknown(e.message ?: e::class.simpleName.orEmpty()))
    }

    return when (response.status.value) {
        in 200..299 -> try {
            Result.Success(response.body<T>())
        } catch (e: SerializationException) {
            Result.Failure(DataError.Unknown("Bad response: ${e.message}"))
        }
        400, 403 -> Result.Failure(DataError.InvalidCredentials)
        401 -> Result.Failure(DataError.Unauthorized)
        else -> Result.Failure(DataError.Server(response.status.value))
    }
}
