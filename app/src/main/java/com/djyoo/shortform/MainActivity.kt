package com.djyoo.shortform

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.djyoo.shortform.data.AssetVideoRepository
import com.djyoo.shortform.player.ExoPlayerController
import com.djyoo.shortform.ui.ShortformApp

@SuppressLint("SourceLockedOrientationActivity")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        val repository = AssetVideoRepository(applicationContext)
        val playerController = ExoPlayerController(applicationContext)
        setContent {
            ShortformApp(
                repository = repository,
                playerController = playerController,
            )
        }
    }
}
