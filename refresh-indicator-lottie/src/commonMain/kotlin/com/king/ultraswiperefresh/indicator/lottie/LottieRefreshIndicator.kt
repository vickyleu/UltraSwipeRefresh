package com.king.ultraswiperefresh.indicator.lottie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.king.ultraswiperefresh.UltraSwipeFooterState
import com.king.ultraswiperefresh.UltraSwipeHeaderState
import com.king.ultraswiperefresh.UltraSwipeRefreshState
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCancellationBehavior
import io.github.alexzhirkevich.compottie.LottieComposition
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottiePainter

/**
 * Lottie动画指示器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Composable
internal fun LottieRefreshIndicator(
    state: UltraSwipeRefreshState,
    isFooter: Boolean,
    composition: LottieComposition?,
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    alignment: Alignment = Alignment.Center,
    speed: Float = 1f,
    contentScale: ContentScale = ContentScale.Fit,
) {

    val isPlaying by remember {
        derivedStateOf {
            if (isFooter) {
                state.footerState == UltraSwipeFooterState.Loading
            } else {
                state.headerState == UltraSwipeHeaderState.Refreshing
            }
        }
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        restartOnPlay = true,
        speed = speed,
        iterations = Compottie.IterateForever,
        cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
    )

    val alphaState = remember {
        derivedStateOf {
            if ((!isFooter && state.indicatorOffset > 0f) || (isFooter && state.indicatorOffset < 0f)) {
                1f
            } else {
                0f
            }
        }
    }

    Box(
        modifier = Modifier
            .alpha(alphaState.value)
            .fillMaxWidth()
            .height(height),
        contentAlignment = alignment,
    ) {
        Image(
            rememberLottiePainter(composition = composition,
                progress = { progress }), null, modifier = modifier,
            contentScale = contentScale
        )
    }
}
