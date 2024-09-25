package com.king.ultraswiperefresh.indicator.lottie

import org.jetbrains.compose.resources.ExperimentalResourceApi
import ultraswiperefresh.refresh_indicator_lottie.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
internal suspend fun Res.lottieBytes(name: String): ByteArray {
    return readBytes("files/$name.json")
}
