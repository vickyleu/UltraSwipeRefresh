package com.king.ultraswiperefresh.theme

import androidx.compose.runtime.Composable

/**
 * 无过度滚动效果
 */
@Composable
internal actual fun NoOverscrollEffect(content: @Composable () -> Unit) {
    content()
}
