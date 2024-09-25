package com.king.ultraswiperefresh.app.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import  ultraswiperefresh. app. generated. resources.Res

@Composable
expect fun showToast(msg: String)

@OptIn(ExperimentalResourceApi::class)
internal suspend fun Res.lottieBytes(name: String): ByteArray {
    return readBytes("files/$name.json")
}
