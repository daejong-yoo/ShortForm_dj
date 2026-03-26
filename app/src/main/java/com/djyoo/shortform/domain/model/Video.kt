package com.djyoo.shortform.domain.model

data class Video(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String?,
    val durationSeconds: Int?,
    val title: String?,
    val preload: Boolean,
)

