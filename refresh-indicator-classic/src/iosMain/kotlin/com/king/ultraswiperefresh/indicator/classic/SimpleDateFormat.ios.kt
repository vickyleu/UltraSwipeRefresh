package com.king.ultraswiperefresh.indicator.classic

import androidx.compose.ui.text.intl.Locale
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

actual class SimpleDateFormatImpl actual constructor(
    private val pattern: String,
    private val locale: Locale
) {
    private val dateFormatter: NSDateFormatter = NSDateFormatter().apply {
        setDateFormat(this@SimpleDateFormatImpl.pattern)
        setLocale(NSLocale(this@SimpleDateFormatImpl.locale.language))
    }

    actual fun format(time: Long): String {
        val date = NSDate(time / 1000.0)  // 将毫秒转换为秒
        return dateFormatter.stringFromDate(date)
    }
}
