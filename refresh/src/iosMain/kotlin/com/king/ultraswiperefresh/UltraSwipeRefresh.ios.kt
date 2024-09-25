package com.king.ultraswiperefresh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

actual abstract class VibratorImpl {
    actual abstract fun hasVibrator(): Boolean
}


class IOSVibrator : VibratorImpl() {
    private val feedbackGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle
        .UIImpactFeedbackStyleMedium)

    override fun hasVibrator(): Boolean {
        // iOS设备没有明确的振动硬件检测, 默认返回true
        return true
    }

    fun vibrate() {
        feedbackGenerator.prepare() // 预先准备振动反馈
        feedbackGenerator.impactOccurred()
    }
}
/**
 * Vibrator
 */
@Composable
@Suppress("DEPRECATION")
internal actual fun rememberVibrator(): VibratorImpl {
    return remember { IOSVibrator() }
}

internal actual fun VibratorImpl.vibrate() {
    (this as? IOSVibrator)?.vibrate()
}
