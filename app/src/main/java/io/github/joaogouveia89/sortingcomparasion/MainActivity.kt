package io.github.joaogouveia89.sortingcomparasion

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.joaogouveia89.sortingcomparasion.ui.theme.SortingComparasionTheme
import io.github.joaogouveia89.sortingcomparasion.ui.theme.colorsChart
import kotlin.math.pow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: MainViewModel by viewModels()
        val totalRam = getTotalRam()

        setContent {
            SortingComparasionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val configuration = LocalConfiguration.current
                    val density = LocalDensity.current

                    val screenHeight = configuration.screenHeightDp.dp
                    val screenWidth = configuration.screenWidthDp.dp
                    val screenHeightInPx = with(density) { screenHeight.toPx() }
                    val boxesWidth = with(density) { screenWidth.toPx() / 30.0 }.toFloat()

                    val bargraphHeight = screenHeight * 7 / 10
                    val bargraphHeightInPx = screenHeightInPx * 7 / 10
                    val bargraphWidth = with(density) { (colorsChart.size * (boxesWidth + 2)).toDp() }

                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        viewModel.initList(colorsChart, bargraphHeightInPx)
                    }

                    ScreenContent(
                        innerPadding = innerPadding,
                        boxesWidth = boxesWidth,
                        startStopSorting = viewModel::startStopSorting,
                        uiState = uiState,
                        onSortingAlgorithmChange = viewModel::changeSortAlgorithm,
                        totalRam = totalRam,
                        bargraphHeight = bargraphHeight,
                        bargraphHeightInPx = bargraphHeightInPx,
                        bargraphtWidth = bargraphWidth
                    )
                }
            }
        }
    }

    private fun getTotalRam(): Double {
        val memInfo = MemoryInfo()
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / 10.0.pow(9.0)
    }
}