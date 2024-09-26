package com.king.ultraswiperefresh.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.king.ultraswiperefresh.app.navigation.NavRoute
import com.king.ultraswiperefresh.app.navigation.noteNavGraph
import com.king.ultraswiperefresh.app.ui.theme.RefreshLayoutTheme
import org.jetbrains.compose.resources.stringResource
import ultraswiperefresh.composeapp.generated.resources.Res
import ultraswiperefresh.composeapp.generated.resources.app_name

@Composable
fun MainScreen() {
    RefreshLayoutTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
        ) {
            Column {
                TopAppBar(title = {
                    Text(
                        text = stringResource(Res.string.app_name),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                })

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavRoute.Root.name
                ) {
                    noteNavGraph(navController)
                }
            }
        }
    }
}
