package com.king.ultraswiperefresh.indicator.classic

import androidx.compose.ui.text.intl.Locale
import java.text.SimpleDateFormat

actual class SimpleDateFormatImpl actual constructor(
    private val pattern: String,
    private val locale: Locale
) {
    private val sdf = SimpleDateFormat(pattern, java.util.Locale(locale.language, locale.region))
    actual fun format(time: Long): String {
        return sdf.format(time)
    }
}
