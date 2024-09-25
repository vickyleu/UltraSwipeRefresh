package com.king.ultraswiperefresh.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.king.ultraswiperefresh.app.ext.showToast

@Composable
actual fun showToast(msg: String) {
    val context  = LocalContext.current
    context.showToast(msg)
}
