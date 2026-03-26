package com.djyoo.shortform.data

import android.content.Context
import com.djyoo.shortform.domain.model.Video

class AssetVideoRepository(
    private val context: Context,
) : VideoRepository {
    override suspend fun getVideos(): List<Video> {
        return emptyList()
    }
}

