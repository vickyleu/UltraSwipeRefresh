@file:OptIn(ExperimentalFoundationApi::class)

package com.king.ultraswiperefresh.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * 无过度滚动效果
 */
@Composable
internal actual fun NoOverscrollEffect(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null, content = content)
}
