package com.djyoo.shortform.data

import android.content.Context
import com.djyoo.shortform.domain.model.Video
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetVideoRepository(
    private val context: Context,
    private val gson: Gson = Gson(),
) : VideoRepository {
    override suspend fun getVideos(): List<Video> {
        return withContext(Dispatchers.IO) {
            val json = context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
            val response = gson.fromJson(json, VideosResponse::class.java)
            response.videos.orEmpty().mapNotNull { it.toDomainOrNull() }
        }
    }

    private data class VideosResponse(
        @SerializedName("videos") val videos: List<VideoDto>?,
    )

    private data class VideoDto(
        @SerializedName("id") val id: String?,
        @SerializedName("videoUrl") val videoUrl: String?,
        @SerializedName("thumbnailUrl") val thumbnailUrl: String?,
        @SerializedName("duration") val durationSeconds: Int?,
        @SerializedName("title") val title: String?,
        @SerializedName("preload") val preload: Boolean?,
    ) {
        fun toDomainOrNull(): Video? {
            val safeId = id?.takeIf { it.isNotBlank() } ?: return null
            val safeVideoUrl = videoUrl?.takeIf { it.isNotBlank() } ?: return null
            return Video(
                id = safeId,
                videoUrl = safeVideoUrl,
                thumbnailUrl = thumbnailUrl?.takeIf { it.isNotBlank() },
                durationSeconds = durationSeconds,
                title = title?.takeIf { it.isNotBlank() },
                preload = preload ?: false,
            )
        }
    }

    private companion object {
        private const val ASSET_FILE_NAME = "videos.json"
    }
}
