package com.king.ultraswiperefresh.indicator.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.king.ultraswiperefresh.UltraSwipeRefreshState
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ultraswiperefresh.refresh_indicator_lottie.generated.resources.Res

/**
 * Lottie动画指示器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LottieRefreshFooter(
    state: UltraSwipeRefreshState,
    modifier: Modifier = Modifier,
    spec: (suspend () -> LottieCompositionSpec)? = null,
    height: Dp = 60.dp,
    alignment: Alignment = Alignment.Center,
    speed: Float = 1f,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val composition by rememberLottieComposition {
        spec?.invoke() ?: LottieCompositionSpec.JsonString(
            Res.lottieBytes("lottie_refresh_footer")
                .decodeToString()
        )
    }
    LottieRefreshIndicator(
        state = state,
        isFooter = true,
        composition = composition,
        modifier = modifier,
        height = height,
        alignment = alignment,
        speed = speed,
        contentScale = contentScale
    )
}
