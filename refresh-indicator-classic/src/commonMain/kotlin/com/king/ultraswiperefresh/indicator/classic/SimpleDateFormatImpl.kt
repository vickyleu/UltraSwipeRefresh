package com.king.ultraswiperefresh.indicator.classic

import androidx.compose.ui.text.intl.Locale

expect class SimpleDateFormatImpl(pattern:String,locale: Locale = Locale.current) {
    fun format(time:Long):String
}
