package com.hexxotest.spoolcompanion.network

import com.hexxotest.spoolcompanion.network.data.SpoolItem
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URI

@kotlinx.serialization.Serializable
data class SpoolLocationUpdate(
    val location: String?
)

fun normalizeSpoolmanUrl(rawUrl: String): String {
    var url = rawUrl.trim().trimEnd('/')
    if (url.isEmpty()) {
        return url
    }
    if (!url.matches(Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$"))) {
        url = "http://$url"
    }

    val parsedUrl = URI(url)
    val port = if (parsedUrl.port == -1 && parsedUrl.scheme.equals("http", ignoreCase = true)) {
        7912
    } else {
        parsedUrl.port
    }
    return URI(
        parsedUrl.scheme,
        parsedUrl.userInfo,
        parsedUrl.host ?: parsedUrl.authority,
        port,
        parsedUrl.path,
        parsedUrl.query,
        parsedUrl.fragment
    ).toString().trimEnd('/')
}

class SpoolApi(
    baseUrl: String = ""
) {

    interface SpoolApiService {
        // Spoolman v1 endpoint: /api/v1/spool
        @GET("spool")
        suspend fun getSpoolList(
            @Query("allow_archived") allowArchived: Boolean = false
        ): List<SpoolItem>

        @GET("location")
        suspend fun getLocations(): List<String>

        @PATCH("spool/{spoolId}")
        suspend fun updateSpoolLocation(
            @Path("spoolId") spoolId: Int,
            @Body request: SpoolLocationUpdate
        ): SpoolItem
    }

    // Ignore unknown keys to tolerate newer Spoolman fields without breaking parsing.
    private val json = Json { ignoreUnknownKeys = true }

    // baseUrl is expected to be the server root (without trailing /api/v1).
    private val normalizedBaseUrl = normalizeSpoolmanUrl(baseUrl)
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl("$normalizedBaseUrl/api/v1/")
        .build()

    val retrofitService: SpoolApiService by lazy {
        retrofit.create(SpoolApiService::class.java)
    }
}
