package com.king.ultraswiperefresh.indicator.classic

import androidx.compose.ui.text.intl.Locale

actual class SimpleDateFormatImpl actual constructor(val pattern: String,val locale: Locale) {

    actual fun format(time: Long): String {
        TODO("Not yet implemented")
    }
}
