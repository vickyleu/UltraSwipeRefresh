package com.king.ultraswiperefresh

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual typealias VibratorImpl = android.os.Vibrator

/**
 * Vibrator
 */
@Composable
@Suppress("DEPRECATION")
internal actual  fun rememberVibrator(): VibratorImpl {
    val context = LocalContext.current
    return remember("Vibrator") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as VibratorImpl
        }
    }
}

/**
 * 振动
 */

@SuppressLint("ObsoleteSdkInt")
internal actual fun VibratorImpl.vibrate(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(
            VibrationEffect.createOneShot(
                VibrationDurationMs,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrate(VibrationDurationMs)
    }
}

private const val VibrationDurationMs = 40L
