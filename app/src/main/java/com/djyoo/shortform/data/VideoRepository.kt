package com.djyoo.shortform.data

import com.djyoo.shortform.domain.model.Video

interface VideoRepository {
    suspend fun getVideos(): List<Video>
}

