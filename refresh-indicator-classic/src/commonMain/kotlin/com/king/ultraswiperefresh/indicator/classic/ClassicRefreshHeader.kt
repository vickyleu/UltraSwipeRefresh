package com.king.ultraswiperefresh.indicator.classic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.king.ultraswiperefresh.UltraSwipeHeaderState
import com.king.ultraswiperefresh.UltraSwipeRefreshState
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ultraswiperefresh.refresh_indicator_classic.generated.resources.Res
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_classic_arrow
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_classic_refreshing
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_last_refresh_time
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_pull_down_to_refresh
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_refresh_completed
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_refreshing
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_release_to_refresh
import ultraswiperefresh.refresh_indicator_classic.generated.resources.usr_time_format_pattern

/**
 * 经典样式的指示器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Composable
fun ClassicRefreshHeader(
    state: UltraSwipeRefreshState,
    modifier: Modifier = Modifier,
    tipContent: @Composable () -> String = {
        obtainHeaderTipContent(state)
    },
    tipTime: @Composable () -> String = {
        obtainLastRefreshTime(state)
    },
    tipContentStyle: TextStyle = TextStyle.Default.copy(
        fontSize = 15.sp,
        color = Color(0xFF666666)
    ),
    tipTimeStyle: TextStyle = TextStyle.Default.copy(
        fontSize = 12.sp,
        color = Color(0xFF999999)
    ),
    tipTimeVisible: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(12.dp),
    arrowIconPainter: Painter = painterResource(Res.drawable.usr_classic_arrow),
    loadingIconPainter: Painter = painterResource(Res.drawable.usr_classic_refreshing),
    iconSize: Dp = 24.dp,
    iconColorFilter: ColorFilter? = null,
) {
    ClassicRefreshIndicator(
        state = state,
        isFooter = false,
        tipContent = tipContent(),
        tipTime = tipTime(),
        modifier = modifier,
        tipContentStyle = tipContentStyle,
        tipTimeStyle = tipTimeStyle,
        tipTimeVisible = tipTimeVisible,
        paddingValues = paddingValues,
        arrowIconPainter = arrowIconPainter,
        loadingIconPainter = loadingIconPainter,
        iconSize = iconSize,
        iconColorFilter = iconColorFilter,
        label = "HeaderIndicator"
    )
}

/**
 * 根据[UltraSwipeRefreshState]获取提示内容
 */
@Composable
private fun obtainHeaderTipContent(state: UltraSwipeRefreshState): String {
    val textRes = when (state.headerState) {
        UltraSwipeHeaderState.PullDownToRefresh -> Res.string.usr_pull_down_to_refresh
        UltraSwipeHeaderState.ReleaseToRefresh -> Res.string.usr_release_to_refresh
        UltraSwipeHeaderState.Refreshing -> {
            if (state.isFinishing) {
                Res.string.usr_refresh_completed
            } else {
                Res.string.usr_refreshing
            }
        }
    }
    return stringResource(textRes)
}

/**
 * 获取上次刷新时间
 */
@Composable
private fun obtainLastRefreshTime(state: UltraSwipeRefreshState): String {
    var lastRefreshTime by remember("lastRefreshTime") {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }
    LaunchedEffect(state.headerState) {
        if (state.headerState == UltraSwipeHeaderState.Refreshing) {
            lastRefreshTime = Clock.System.now().toEpochMilliseconds()
        }
    }
    val pattern = stringResource(Res.string.usr_time_format_pattern)
    val dateFormat = remember { SimpleDateFormatImpl(pattern) }
    return "${stringResource(Res.string.usr_last_refresh_time)}${dateFormat.format(lastRefreshTime)}"
//     "${context.getString(R.string.usr_last_refresh_time)}${dateFormat.format(lastRefreshTime)}"
}
